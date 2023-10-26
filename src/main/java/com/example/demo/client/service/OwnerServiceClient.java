package com.example.demo.client.service;

import com.example.demo.client.exceptions.ClientException;
import com.example.demo.server.model.Owner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service class to interact with the Owner-related endpoints of the server.
 * Utilizes WebClient to make reactive HTTP calls.
 */
@Service
public class OwnerServiceClient {

    private final WebClient webClient;

    /**
     * Constructor to inject the WebClient.
     *
     * @param webClient The WebClient instance for making HTTP calls.
     */
    @Autowired
    public OwnerServiceClient(WebClient webClient) {
        this.webClient = webClient;
    }

    /**
     * Fetches all the owners from the server.
     *
     * @return A Flux of Owner objects.
     */
    public Flux<Owner> getAllOwners() {
        return webClient.get()
                .uri("/owners")
                .retrieve()
                .onStatus(status -> !status.is2xxSuccessful(),
                        response -> Mono.error(new ClientException("Error fetching owners")))
                .bodyToFlux(Owner.class);
    }

    /**
     * Fetches a specific owner by their ID from the server.
     *
     * @param id The ID of the owner.
     * @return A Mono of Owner object.
     */
    public Mono<Owner> getOwnerById(Long id) {
        return webClient.get()
                .uri("/owners/{id}", id)
                .retrieve()
                .onStatus(status -> !status.is2xxSuccessful(),
                        response -> Mono.error(new ClientException("Error fetching owner with ID: " + id)))
                .bodyToMono(Owner.class);
    }
}
