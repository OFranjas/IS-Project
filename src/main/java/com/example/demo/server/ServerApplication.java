package com.example.demo.server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.example.demo.server.utils.LoggerUtil;

@SpringBootApplication
public class ServerApplication {

    public static void main(String[] args) {

        try {
            SpringApplication.run(ServerApplication.class, args);
            LoggerUtil.info(ServerApplication.class.getName(), "ServerApplication started successfully.");
        } catch (Exception e) {
            System.out.println("ServerApplication failed to start.");
            System.out.println(e.getMessage());
        }

    }
}
