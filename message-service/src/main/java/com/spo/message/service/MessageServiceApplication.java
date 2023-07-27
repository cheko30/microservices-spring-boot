package com.spo.message.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@Slf4j
@SpringBootApplication
public class MessageServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(MessageServiceApplication.class, args);
	}

	@RabbitListener(queues = {"{com.spo.queue.name}"})
	public void receiveMessageWithRabbitMQ(String message) {
		log.info("Receive message {}", message);
	}

}
