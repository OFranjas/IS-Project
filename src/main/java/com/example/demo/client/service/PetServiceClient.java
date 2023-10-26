package com.example.demo.client.service;

import com.example.demo.client.exceptions.ClientException;
import com.example.demo.server.model.Pet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service class to interact with the Pet-related endpoints of the server.
 * Utilizes WebClient to make reactive HTTP calls.
 */
@Service
public class PetServiceClient {

    private final WebClient webClient;

    /**
     * Constructor to inject the WebClient.
     *
     * @param webClient The WebClient instance for making HTTP calls.
     */
    @Autowired
    public PetServiceClient(WebClient webClient) {
        this.webClient = webClient;
    }

    /**
     * Fetches all the pets from the server.
     *
     * @return A Flux of Pet objects.
     */
    public Flux<Pet> getAllPets() {
        return webClient.get()
                .uri("/pets")
                .retrieve()
                .onStatus(status -> !status.is2xxSuccessful(),
                        response -> Mono.error(new ClientException("Error fetching pets")))
                .bodyToFlux(Pet.class);
    }

    /**
     * Fetches a specific pet by their ID from the server.
     *
     * @param id The ID of the pet.
     * @return A Mono of Pet object.
     */
    public Mono<Pet> getPetById(Long id) {
        return webClient.get()
                .uri("/pets/{id}", id)
                .retrieve()
                .onStatus(status -> !status.is2xxSuccessful(),
                        response -> Mono.error(new ClientException("Error fetching pet with ID: " + id)))
                .bodyToMono(Pet.class);
    }
}