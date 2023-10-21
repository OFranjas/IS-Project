package com.example.demo.server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
public class ServerApplication {

    public static void main(String[] args) {

        try {
            SpringApplication.run(ServerApplication.class, args);
            System.out.println("ServerApplication started successfully.");
        } catch (Exception e) {
            System.out.println("ServerApplication failed to start.");
            System.out.println(e.getMessage());
        }

    }
}
