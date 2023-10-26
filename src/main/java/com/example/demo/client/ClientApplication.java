package com.example.demo.client;

import java.util.concurrent.atomic.AtomicReference;

import org.springframework.web.reactive.function.client.WebClient;

import com.example.demo.client.config.WebClientConfig;
import com.example.demo.client.service.OwnerServiceClient;
import com.example.demo.client.service.PetServiceClient;
import com.example.demo.server.model.Owner;
import com.example.demo.server.model.Pet;

import reactor.core.publisher.Mono;

public class ClientApplication {

    public static void main(String[] args) {

        try {
            ClientApplication clientApplication = new ClientApplication();

            clientApplication.run();

        } catch (Exception e) {
            System.out.println("ClientApplication failed to start.");
            System.out.println(e.getMessage());
        }

    }

    public void run() {
        // Entry point of the client application
        System.out.println("ClientApplication started successfully.");

        // Configure the web client
        WebClientConfig webClientConfig = new WebClientConfig();
        WebClient webClient = webClientConfig.webClient();

        // Execute various tasks
        OwnersNamesPhones(webClient); // Task 1: Get owner names and phones
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        NumberOfPets(webClient); // Task 2: Get the number of pets
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        NumberOfDogs(webClient); // Task 3: Get the number of dogs
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        PetsSortedByWeight(webClient); // Task 4: Get and print pets sorted by weight
        // Wait for the previous task to complete before executing the next one
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        AverageAndStdDevOfWeights(webClient); // Task 5: Calculate and print average and std deviation of pet weights
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        NameOfEldestPet(webClient); // Task 6: Print the name of the eldest pet
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * Task 1: Get owner names and phone numbers and print them.
     * 
     * @param webClient The configured WebClient instance.
     */
    public void OwnersNamesPhones(WebClient webClient) {
        // Create the service client
        OwnerServiceClient ownerServiceClient = new OwnerServiceClient(webClient);

        // Subscribe to getAllOwners and print the data when it's available
        ownerServiceClient.getAllOwners()
                .subscribe(owner -> {
                    // Handle each received owner here
                    System.out.println(
                            "Owner Name: " + owner.getName() + " -> phone number: " + owner.getPhone_number());
                    // Add more owner details as needed
                },
                        error -> {
                            // Handle errors if they occur
                            System.err.println("Error fetching owners: " + error.getMessage());
                        });
    }

    /**
     * Task 2: Get the number of pets and print it.
     * 
     * @param webClient The configured WebClient instance.
     */
    public void NumberOfPets(WebClient webClient) {
        // Create the service client
        PetServiceClient petServiceClient = new PetServiceClient(webClient);

        // Subscribe to getNumberOfPets and print the data when it's available
        petServiceClient.getAllPets()
                .reduce(0L, (count, pet) -> count + 1)
                .subscribe(
                        count -> {
                            // Handle the received pet count here
                            System.out.println("Total number of pets: " + count);
                        },
                        error -> {
                            // Handle errors if they occur
                            System.err.println("Error fetching pet count: " + error.getMessage());
                        });
    }

    /**
     * Task 3: Get the number of dogs and print it.
     * 
     * @param webClient The configured WebClient instance.
     */
    public void NumberOfDogs(WebClient webClient) {

        // Create the service client
        PetServiceClient petServiceClient = new PetServiceClient(webClient);

        // Subscribe to pets with species "dog" and print the count when it's available
        petServiceClient.getAllPets()
                .filter(pet -> pet.getSpecies().equals("dog"))
                .count()
                .subscribe(count -> {
                    // Handle the count here
                    System.out.println("Number of dogs: " + count);
                },
                        error -> {
                            // Handle errors if they occur
                            System.err.println("Error fetching pets: " + error.getMessage());
                        });
    }

    /**
     * Task 4: Get and print pets sorted by weight.
     * 
     * @param webClient The configured WebClient instance.
     */
    public void PetsSortedByWeight(WebClient webClient) {
        // Create the service client
        PetServiceClient petServiceClient = new PetServiceClient(webClient);

        // Subscribe to getAllPets and process the data when it's available
        petServiceClient.getAllPets()
                .concatMap(
                        pet -> Mono.just(pet)
                                .doOnNext(sortedPet -> {
                                    // Print each pet as it arrives (already sorted)
                                    System.out.println("Pet Name: " + sortedPet.getName());
                                    System.out.println("Pet Weight: " + sortedPet.getWeight());
                                    // Add more pet details as needed
                                }))
                .subscribe(
                        null,
                        error -> {
                            // Handle errors if they occur
                            System.err.println("Error fetching pets: " + error.getMessage());
                        });
    }

    /**
     * Task 5: Calculate and print the average and standard deviation of pet
     * weights.
     * 
     * @param webClient The configured WebClient instance.
     */
    public void AverageAndStdDevOfWeights(WebClient webClient) {
        // Create the service client
        PetServiceClient petServiceClient = new PetServiceClient(webClient);

        // Calculate sum of weights, sum of squares, and count
        Mono<Double> sumMono = petServiceClient.getAllPets()
                .map(Pet::getWeight)
                .reduce(0.0, (acc, value) -> acc + value);

        Mono<Double> sumOfSquaresMono = petServiceClient.getAllPets()
                .map(pet -> pet.getWeight() * pet.getWeight())
                .reduce(0.0, (acc, value) -> acc + value);

        Mono<Long> countMono = petServiceClient.getAllPets()
                .count();

        // Combine the results and calculate average and standard deviation
        Mono.zip(sumMono, sumOfSquaresMono, countMono)
                .flatMap(tuple -> {
                    double sum = tuple.getT1();
                    double sumOfSquares = tuple.getT2();
                    long count = tuple.getT3();

                    if (count > 0) {
                        double average = sum / count;
                        double variance = (sumOfSquares / count) - (average * average);
                        double stdDev = Math.sqrt(variance);

                        System.out.printf("Average Weight: %.3f\n", average);
                        System.out.printf("Standard Deviation: %.3f\n", stdDev);
                    } else {
                        System.out.println("No pets found.");
                    }

                    return Mono.empty();
                })
                .subscribe();
    }

    /**
     * Task 6: Print the name of the eldest pet.
     *
     * @param webClient The WebClient to use for making requests.
     */
    public void NameOfEldestPet(WebClient webClient) {
        // Create the service client
        PetServiceClient petServiceClient = new PetServiceClient(webClient);

        // Find the eldest pet by comparing birth dates
        petServiceClient.getAllPets()
                .reduce((pet1, pet2) -> {
                    if (pet1.getBirth_date().isBefore(pet2.getBirth_date())) {
                        return pet1;
                    } else {
                        return pet2;
                    }
                })
                .subscribe(
                        eldestPet -> {
                            // Handle the eldest pet here
                            System.out.println("Name of the eldest pet: " + eldestPet.getName());
                        },
                        error -> {
                            // Handle errors if they occur
                            System.err.println("Error fetching pets: " + error.getMessage());
                        });
    }

}
