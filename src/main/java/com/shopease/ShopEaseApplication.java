package com.shopease;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Main application class for the ShopEase e-commerce backend.
 *
 * This application provides a robust shopping cart system with user authentication,
 * product management, cart operations, order processing, and discounts.
 */
@SpringBootApplication
@EnableJpaAuditing
@EnableScheduling
public class ShopEaseApplication {

    public static void main(String[] args) {
        SpringApplication.run(ShopEaseApplication.class, args);
    }
}