package com.spo.stock;

import com.spo.stock.model.Stock;
import com.spo.stock.repository.StockRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
@EnableEurekaClient
public class StockServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(StockServiceApplication.class, args);
	}

	@Bean
	public CommandLineRunner loadData(StockRepository stockRepository) {
		return args -> {
			Stock stock = new Stock();
			stock.setSku("123");
			stock.setQuantity(2);

			Stock stock2 = new Stock();
			stock2.setSku("123456");
			stock2.setQuantity(3);

			stockRepository.save(stock);
			stockRepository.save(stock2);

		};
	}

}
