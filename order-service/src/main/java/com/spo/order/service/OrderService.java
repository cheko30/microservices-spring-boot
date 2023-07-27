package com.spo.order.service;


import com.spo.order.dto.OrderLineItemsDto;
import com.spo.order.dto.OrderRequest;
import com.spo.order.dto.StockResponse;
import com.spo.order.event.OrderPlacedEvent;
import com.spo.order.model.Order;
import com.spo.order.model.OrderLineItems;
import com.spo.order.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.sleuth.Span;
import org.springframework.cloud.sleuth.Tracer;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
public class OrderService {

    @Autowired
    private KafkaTemplate<String, OrderPlacedEvent> kafkaTemplate;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private WebClient.Builder webClientBuilder;

    @Autowired
    private Tracer tracer;

    public String placeOrder(OrderRequest orderRequest) {
        Order order = new Order();
        order.setOrderNumber(UUID.randomUUID().toString());

        List<OrderLineItems> orderLineItems = orderRequest.getOrderLineItemsDtoList()
                .stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());

        order.setOrderLineItems(orderLineItems);

        List<String> sku = order.getOrderLineItems().stream()
                .map(OrderLineItems::getSku)
                .collect(Collectors.toList());


        Span stockServiceLookup = tracer.nextSpan().name("StockServiceLookup");
        try (Tracer.SpanInScope isLookup = tracer.withSpan(stockServiceLookup.start())) {
            stockServiceLookup.tag("call", "StockService");

            StockResponse[] stockResponseArray = webClientBuilder.build().get()
                    .uri("http://stock-service/api/stock", uriBuilder -> uriBuilder.queryParam("sku", sku).build())
                    .retrieve()
                    .bodyToMono(StockResponse[].class)
                    .block();

            boolean allProductsInStock = Arrays.stream(stockResponseArray)
                    .allMatch(StockResponse::isInStock);

            if (allProductsInStock) {
                orderRepository.save(order);
                kafkaTemplate.send("notificationTopic", new OrderPlacedEvent(order.getOrderNumber()));
                return "Order success";
            } else {
                throw new IllegalArgumentException("The product is not in stock");
            }
        } finally {
            stockServiceLookup.end();
        }

    }

    private OrderLineItems mapToDto(OrderLineItemsDto orderLineItemsDto) {
        OrderLineItems orderLineItems = new OrderLineItems();
        orderLineItems.setPrice(orderLineItemsDto.getPrice());
        orderLineItems.setAmount(orderLineItemsDto.getAmount());
        orderLineItems.setSku(orderLineItemsDto.getSku());

        return orderLineItems;
    }
}
