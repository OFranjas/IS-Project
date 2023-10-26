package com.example.demo.client.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * Configuration class responsible for creating and configuring the WebClient
 * bean.
 * WebClient is used to make reactive HTTP calls in a non-blocking manner.
 */
@Configuration
public class WebClientConfig {

    /**
     * Base URL of the server fetched from application properties or defaults to
     * "http://localhost:8080".
     */
    @Value("http://localhost:8080")
    private String serverBaseUrl;

    /**
     * Defines and configures a WebClient bean to be used throughout the
     * application.
     *
     * @return A WebClient instance configured with the server's base URL.
     */
    @Bean
    public WebClient webClient() {
        return WebClient.builder()
                .baseUrl(serverBaseUrl)
                // You can add filters, default headers, etc. here if needed
                .build();
    }
}
