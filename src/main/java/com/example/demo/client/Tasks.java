package com.example.demo.client;

import java.time.Duration;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.web.reactive.function.client.WebClient;

import com.example.demo.client.service.OwnerServiceClient;
import com.example.demo.client.service.PetServiceClient;
import com.example.demo.server.model.Owner;
import com.example.demo.server.model.Pet;
import com.example.demo.client.utils.FileOutputUtil;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuples;
import reactor.util.retry.Retry;

public class Tasks {

    private final PetServiceClient petServiceClient;
    private final OwnerServiceClient ownerServiceClient;

    public Tasks(WebClient webClient) {
        this.petServiceClient = new PetServiceClient(webClient);
        this.ownerServiceClient = new OwnerServiceClient(webClient);
    }

    /**
     * Task 1: Get owner names and phone numbers and print them.
     * 
     * @param webClient The configured WebClient instance.
     */
    public Runnable OwnersNamesPhones() {

        return () -> {
            // Define the file path
            String filePath = "Task1_ownersNamesPhones.txt";

            // Clear the file
            FileOutputUtil.clearFile(filePath);

            // Create a StringBuilder to buffer the owner details
            StringBuilder ownerDetailsBuffer = new StringBuilder();

            // Subscribe to getAllOwners
            ownerServiceClient.getAllOwners()
                    .doOnNext(owner -> {
                        // Handle each received owner here
                        String ownerDetails = "Owner Name: " + owner.getName() + " -> phone number: "
                                + owner.getPhone_number();
                        ownerDetailsBuffer.append(ownerDetails).append("\n"); // Append to the buffer with a newline
                    })
                    .doOnError(error -> {
                        // Handle errors if they occur
                        System.err.println("Error fetching owners: " + error.getMessage());
                    })
                    .doOnComplete(() -> {
                        // Write the buffered owner details to the file
                        FileOutputUtil.writeToFile(filePath, ownerDetailsBuffer.toString());
                        System.out.println("Task 1: ✅");
                    })
                    .subscribe(); // This will start the process

        };
    }

    /**
     * Task 2: Get the number of pets.
     * 
     * @param webClient The configured WebClient instance.
     */
    public Runnable NumberOfPets() {

        return () -> {

            // Define the file path
            String filePath = "Task2_totalPets.txt";

            // Clear the file
            FileOutputUtil.clearFile(filePath);

            // Create a buffer for the result string
            StringBuilder resultBuffer = new StringBuilder();

            // Subscribe to getAllPets and count the pets
            petServiceClient.getAllPets()
                    .count() // This returns a Mono<Long> with the count of pets
                    .doOnNext(count -> {
                        // Handle the received pet count here
                        String petCount = "Total number of pets: " + count;
                        resultBuffer.append(petCount); // Store the result in the buffer
                    })
                    .doOnError(error -> {
                        // Handle errors if they occur
                        System.err.println("Error fetching pet count: " + error.getMessage());
                    })
                    .doOnSuccess(count -> {
                        // Write the result to the file
                        FileOutputUtil.writeToFile(filePath, resultBuffer.toString());
                        System.out.println("Task 2: ✅");
                    })
                    .subscribe(); // This will start the process
        };
    }

    /**
     * Task 3: Get the number of dogs.
     * 
     * @param webClient The configured WebClient instance.
     */
    public Runnable NumberOfDogs() {

        return () -> {
            // Define the path
            String filePath = "Task3_totalDogs.txt";

            // Clear the file
            FileOutputUtil.clearFile(filePath);

            // Create a buffer for the result string
            StringBuilder resultBuffer = new StringBuilder();

            // Subscribe to pets with species "dog" and count them
            petServiceClient.getAllPets()
                    .filter(pet -> "dog".equalsIgnoreCase(pet.getSpecies()))
                    .count() // This returns a Mono<Long> with the count of dogs
                    .doOnNext(count -> {
                        // Handle the count here
                        String dogCount = "Number of dogs: " + count;
                        resultBuffer.append(dogCount); // Store the result in the buffer
                    })
                    .doOnError(error -> {
                        // Handle errors if they occur
                        System.err.println("Error fetching number of dogs: " + error.getMessage());
                    })
                    .doOnSuccess(count -> {
                        // Write the result to the file upon successful completion
                        FileOutputUtil.writeToFile(filePath, resultBuffer.toString());
                        System.out.println("Task 3: ✅");
                    })
                    .subscribe(); // This will start the process

        };

    }

    /**
     * Task 4: Get pets sorted by weight.
     * 
     * @param webClient The configured WebClient instance.
     */
    public Runnable PetsSortedByWeight() {

        return () -> {

            // Define the path
            String filePath = "Task4_sortedWeight.txt";

            // Clear the file
            FileOutputUtil.clearFile(filePath);

            // Create a buffer to store pets' details
            StringBuilder petDetailsBuffer = new StringBuilder();

            // Subscribe to getAllPets, filter, and process them
            petServiceClient.getAllPets()
                    .filter(pet -> pet.getWeight() > 10) // Only keep pets with weight greater than 10
                    .sort(Comparator.comparingDouble(Pet::getWeight)) // Sort the pets by weight
                    .doOnNext(pet -> {
                        // Append each pet's details to the buffer
                        String petDetails = "Pet Name: " + pet.getName() + " -> weight: " + pet.getWeight() + "\n";
                        petDetailsBuffer.append(petDetails);
                    })
                    .doOnError(error -> {
                        // Handle errors if they occur
                        System.err.println("Error fetching pets: " + error.getMessage());
                    })
                    .doOnComplete(() -> {
                        // Write the pet details to the file upon completion
                        FileOutputUtil.writeToFile(filePath, petDetailsBuffer.toString());
                        System.out.println("Task 4: ✅");
                    })
                    .subscribe(); // This will start the process
        };
    }

    /**
     * Task 5: Calculate the average and standard deviation of pet
     * weights.
     * 
     * @param webClient The configured WebClient instance.
     */
    public Runnable AverageAndStdDevOfWeights() {

        return () -> {

            // Define the path
            String filePath = "Task5_stdDevWeights.txt";

            // Clear the file
            FileOutputUtil.clearFile(filePath);

            // Create a buffer to store the results
            StringBuilder resultBuffer = new StringBuilder();

            // ? Cache the result of getAllPets() so we call it only once
            Flux<Pet> cachedPets = petServiceClient.getAllPets().cache();

            // Calculate sum of weights
            Mono<Double> sumMono = cachedPets
                    .map(Pet::getWeight)
                    .reduce(0.0, (acc, value) -> acc + value);

            // Calculate sum of squares of weights
            Mono<Double> sumOfSquaresMono = cachedPets
                    .map(pet -> pet.getWeight() * pet.getWeight())
                    .reduce(0.0, (acc, value) -> acc + value);

            // Count the pets
            Mono<Long> countMono = cachedPets.count();

            // Combine the results and calculate average and standard deviation
            Mono.zip(sumMono, sumOfSquaresMono, countMono)
                    .doOnNext(tuple -> {
                        double sum = tuple.getT1();
                        double sumOfSquares = tuple.getT2();
                        long count = tuple.getT3();

                        if (count > 0) {
                            double average = sum / count;
                            double variance = (sumOfSquares / count) - (average * average);
                            double stdDev = Math.sqrt(variance);

                            resultBuffer.append("Average Weight: ").append(String.format("%.2f", average)).append("\n");
                            resultBuffer.append("Standard Deviation: ").append(String.format("%.2f", stdDev))
                                    .append("\n");

                        } else {
                            resultBuffer.append("No pets found.\n");
                        }
                    })
                    .doOnError(error -> {
                        // Handle errors if they occur
                        System.err.println("Error calculating weights: " + error.getMessage());
                    })
                    .doOnSuccess(tuple -> {
                        // Write the results to the file when the operation completes successfully
                        FileOutputUtil.writeToFile(filePath, resultBuffer.toString());
                        System.out.println("Task 5: ✅");
                    })
                    .subscribe(); // This will start the process

        };

    }

    /**
     * Task 6: Get the name of the eldest pet.
     *
     * @param webClient The WebClient to use for making requests.
     */
    public Runnable NameOfEldestPet() {

        return () -> {

            // Define the path
            String filePath = "Task6_eldestPet.txt";

            // Clear the file
            FileOutputUtil.clearFile(filePath);

            StringBuilder resultBuffer = new StringBuilder();

            // Find the eldest pet by comparing birth dates
            petServiceClient.getAllPets()
                    .reduce((pet1, pet2) -> {
                        if (pet1.getBirth_date().isBefore(pet2.getBirth_date())) {
                            return pet1;
                        } else {
                            return pet2;
                        }
                    })
                    .doOnSuccess(eldestPet -> {
                        // Handle the eldest pet here
                        resultBuffer.append("Name of the eldest pet: ").append(eldestPet.getName()).append("\n");
                        FileOutputUtil.writeToFile(filePath, resultBuffer.toString());
                        System.out.println("Task 6: ✅");
                    })
                    .doOnError(error -> {
                        // Handle errors if they occur
                        System.err.println("Error fetching pets: " + error.getMessage());
                    })
                    .subscribe();

        };

    }

    /***
     * Task 7: Get the Average number of Pets per Owner, considering
     * only the owners with more than one animal.
     * 
     * @param webClient The WebClient to use for making requests.
     */
    public Runnable averagePetsPerOwner() {

        return () -> {

            // Define the path
            String filePath = "Task7_averagePetsOwner.txt";

            // Clear the file
            FileOutputUtil.clearFile(filePath);

            StringBuilder resultBuffer = new StringBuilder();

            petServiceClient.getAllPets()
                    .reduce(new HashMap<Long, Long>(), (map, pet) -> {
                        map.put(pet.getOwnerid(), map.getOrDefault(pet.getOwnerid(), 0L) + 1);
                        return map;
                    }) // Aggregate pets count per owner in a map
                    .flatMap(map -> {
                        long totalPets = 0;
                        long totalOwners = 0;

                        for (Map.Entry<Long, Long> entry : map.entrySet()) {
                            if (entry.getValue() > 1) {
                                totalPets += entry.getValue();
                                totalOwners++;
                            }
                        }

                        double average = totalOwners == 0 ? 0 : (double) totalPets / totalOwners;
                        return Mono.just(average);
                    })
                    .doOnSuccess(avg -> {
                        resultBuffer.append("Average number of pets per owner: ").append(String.format("%.2f", avg))
                                .append("\n");
                        FileOutputUtil.writeToFile(filePath, resultBuffer.toString());
                        System.out.println("Task 7: ✅");
                    })
                    .doOnError(error -> {
                        // Handle errors if they occur
                        System.err.println("Error computing average: " + error.getMessage());
                    })
                    .subscribe();

        };

    }

    /**
     * Task 8: Get the Owner Names and the number of Pets they have, sorted by the
     * number of Pets.
     *
     * 
     * @param webClient The WebClient to use for making requests.
     */
    public Runnable ownerNamesAndPetCountsSorted() {

        return () -> {

            // Define the path
            String filePath = "Task8_ownerNamesPetCountsSorted.txt";

            // Clear the file
            FileOutputUtil.clearFile(filePath);

            StringBuilder resultBuffer = new StringBuilder();

            ownerServiceClient.getAllOwners()
                    .flatMap(owner -> petServiceClient.getPetIdsByOwnerId(owner.getIdentifier())
                            .count()
                            .map(count -> Tuples.of(owner, count)), 50)
                    .sort((tuple1, tuple2) -> Long.compare(tuple2.getT2(), tuple1.getT2())) // Sort by pet count
                    .retryWhen(Retry.backoff(3, Duration.ofSeconds(1))
                            .maxBackoff(Duration.ofSeconds(10)))
                    .doOnNext(tuple -> {
                        Owner owner = tuple.getT1();
                        long count = tuple.getT2();
                        resultBuffer.append("Owner Name: ").append(owner.getName()).append(" -> Number of Pets: ")
                                .append(count).append("\n");
                    })
                    .doOnError(error -> {
                        // Handle errors if they occur
                        System.err.println("Error fetching owner and pet details: " + error.getMessage());
                    })
                    .doOnComplete(() -> {
                        FileOutputUtil.writeToFile(filePath, resultBuffer.toString());
                        System.out.println("Task 8: ✅");
                    })
                    .subscribe();

        };

    }

    /**
     * Task 9: Get the owner names and pet names sorted by pet count
     * 
     *
     * @param webClient The WebClient instance for making the requests.
     */
    public Runnable ownerNamesAndPetNamesForTask9() {

        return () -> {

            // Define the path
            String filePath = "Task9_ownerNamesAndPetNamesSorted.txt";

            // Clear the file
            FileOutputUtil.clearFile(filePath);

            // Create a buffer for the result string
            StringBuilder resultBuffer = new StringBuilder();

            // Work the magic
            ownerServiceClient.getAllOwners()
                    .flatMap(owner -> petServiceClient.getPetIdsByOwnerId(owner.getIdentifier())
                            .flatMap(petId -> petServiceClient.getPetById(petId)
                                    .map(Pet::getName), 5) // Adjusted concurrency limit for inner flatMap to avoid
                                                           // system overload
                            .collectList() // Collect pet names per owner
                            .map(names -> Tuples.of(owner, names)), 5) // Also adjusted concurrency limit for system
                                                                       // overload
                    .sort((tuple1, tuple2) -> Integer.compare(tuple2.getT2().size(), tuple1.getT2().size()))
                    .retryWhen(Retry.backoff(3, Duration.ofSeconds(5)).maxBackoff(Duration.ofSeconds(20))) // Retry with
                    .doOnNext(tuple -> {
                        Owner owner = tuple.getT1();
                        List<String> petNames = tuple.getT2();
                        resultBuffer.append("Owner Name: ").append(owner.getName()).append(" -> Pets: ")
                                .append(String.join(", ", petNames)).append("\n");
                    })
                    .doOnError(error -> {
                        System.err.println("Error fetching owner and pet details: " + error.getMessage());
                    })
                    .doOnComplete(() -> {
                        FileOutputUtil.writeToFile(filePath, resultBuffer.toString());
                        System.out.println("Task 9: ✅");
                    })
                    .subscribe();
        };

    }

    /**
     * Task to retrieve a pet by its identifier with retries and delay.
     *
     * @param webClient The WebClient instance for making the requests.
     */
    public Runnable taskGetPetByIdWithRetry() {
        return () -> {

            // Define the path
            String filePath = "Task10_GetPetByIdWithRetry.txt";

            // Clear the file
            FileOutputUtil.clearFile(filePath);

            // Create a buffer for the result string
            StringBuilder resultBuffer = new StringBuilder();

            // Generate random identifier
            long randomId = (long) (Math.random() * 1000);

            // Work the magic with retry
            petServiceClient.getPetByIdWithRetry(randomId)
                    .doOnNext(pet -> {
                        resultBuffer.append("Pet Name: ").append(pet.getName()).append("\n");
                    })
                    .doOnError(error -> {
                        // Handle errors if they occur
                        System.err.println("Error fetching pet details: " + error.getMessage());
                    })
                    .doOnSuccess(pet -> {
                        FileOutputUtil.writeToFile(filePath, resultBuffer.toString());
                        System.out.println("Task Delay: ✅");
                    })
                    .subscribe();
        };
    }
}