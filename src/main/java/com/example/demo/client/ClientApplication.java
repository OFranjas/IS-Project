package com.example.demo.client;

import org.springframework.web.reactive.function.client.WebClient;
import com.example.demo.client.config.WebClientConfig;
import com.example.demo.client.service.OwnerServiceClient;
import com.example.demo.client.service.PetServiceClient;
import com.example.demo.server.model.Owner;
import com.example.demo.server.model.Pet;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.CountDownLatch;

public class ClientApplication {

    private final WebClient webClient;
    private final Tasks tasks;

    private Flux<Owner> allOwners;
    private Flux<Pet> allPets;

    private final PetServiceClient petServiceClient;
    private final OwnerServiceClient ownerServiceClient;

    public static void main(String[] args) {
        ClientApplication app = new ClientApplication();
        app.run();
    }

    public ClientApplication() {
        WebClientConfig webClientConfig = new WebClientConfig();
        this.webClient = webClientConfig.webClient();
        this.tasks = new Tasks(webClient);
        this.allOwners = webClient.get().uri("http://localhost:8080/owner").retrieve().bodyToFlux(Owner.class);
        this.allPets = webClient.get().uri("http://localhost:8080/pet").retrieve().bodyToFlux(Pet.class);
        this.petServiceClient = new PetServiceClient(webClient);
        this.ownerServiceClient = new OwnerServiceClient(webClient);
    }

    /**
     * The entry point of the client application.
     * It initializes the web client and concurrently executes various tasks.
     */
    public void run() {

        // Combine all tasks into a single Mono
        Mono<Void> combinedTasks = Mono.when(
                tasks.ownersNamesPhones(this.allOwners).then(),
                tasks.numberOfPets(this.allPets).then(),
                tasks.numberOfDogs(this.allPets).then(),
                tasks.petsSortedByWeight(this.allPets).then(),
                tasks.averageAndStdDevOfWeights(this.allPets).then(),
                tasks.nameOfEldestPet(this.allPets).then(),
                tasks.averagePetsPerOwner(this.allPets).then(),
                tasks.ownerNamesAndPetCountsSorted(this.allOwners, this.petServiceClient).then(),
                tasks.ownerNamesAndPetNamesForTask9(this.allOwners, this.petServiceClient).then(),
                tasks.taskGetPetByIdWithRetry(this.allPets, this.petServiceClient));

        // Subscribe to the Mono and wait for it to complete before exiting
        combinedTasks.doOnTerminate(() -> {

        }).block(); // Block until all tasks are complete

    }
}
