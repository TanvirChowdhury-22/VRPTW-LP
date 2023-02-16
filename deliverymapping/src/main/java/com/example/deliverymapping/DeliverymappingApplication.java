package com.example.deliverymapping;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class DeliverymappingApplication {

    public static void main(String[] args) {
        SpringApplication.run(DeliverymappingApplication.class, args);
    }

}
