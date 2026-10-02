package com.example.inventory_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling

public class InventoryServiceApplication {

	public static void main(String[] args) {

        System.setProperty("spring.datasource.url", "jdbc:postgresql://localhost:5432/ticket_db");
        System.setProperty("spring.datasource.username", "postgres");
        System.setProperty("spring.datasource.password", "mysecretpassword");

        System.setProperty("spring.jpa.hibernate.ddl-auto", "update");
        System.setProperty("spring.jpa.show-sql", "true");
		SpringApplication.run(InventoryServiceApplication.class, "--server.port=8082");
	}

}
