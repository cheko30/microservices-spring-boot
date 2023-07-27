package com.spo.stock.service;

import com.spo.stock.dto.StockResponse;
import com.spo.stock.repository.StockRepository;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class StockService {

    @Autowired
    private StockRepository stockRepository;

    @SneakyThrows
    public List<StockResponse> isInStock(List<String> sku) {
        log.info("Wait started");
        Thread.sleep(10000);
        log.info("Wait end");
        return stockRepository.findBySkuIn(sku)
                .stream()
                .map(stock ->
                    StockResponse.builder()
                            .sku(stock.getSku())
                            .inStock(stock.getQuantity() > 0)
                            .build()
                ).collect(Collectors.toList());
    }
}
