package com.example.order_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@SpringBootApplication
@EnableScheduling

public class OrderServiceApplication {

	public static void main(String[] args) {

		SpringApplication.run(OrderServiceApplication.class, "--server.port=8081");
	}

}
