package com.example.demo.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ServerApplication {

    private static final Logger logger = LoggerFactory.getLogger(ServerApplication.class);

    public static void main(String[] args) {

        try {
            SpringApplication.run(ServerApplication.class, args);
            logger.info("ServerApplication started.");
        } catch (Exception e) {
            logger.error(ServerApplication.class.getName(), "ServerApplication failed to start.", e);
        }

    }
}
