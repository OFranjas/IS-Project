package com.example.demo.client;

import java.time.Duration;
import java.time.Instant;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import org.springframework.web.reactive.function.client.WebClient;

import com.example.demo.client.service.PetServiceClient;
import com.example.demo.server.model.Owner;
import com.example.demo.server.model.Pet;
import com.example.demo.client.utils.FileOutputUtil;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import reactor.util.function.Tuples;
import reactor.util.retry.Retry;

public class Tasks {

    public Tasks(WebClient webClient) {

    }

    /**
     * Task 1: Get owner names and phone numbers and print them.
     * 
     * @param webClient The configured WebClient instance.
     */
    public Mono<Void> ownersNamesPhones(Flux<Owner> allOwners) {
        // Define the file path
        String filePath = "Task1_ownersNamesPhones.txt";

        // Clear the file
        FileOutputUtil.clearFile(filePath);

        // Create a StringBuilder to buffer the owner details
        StringBuilder ownerDetailsBuffer = new StringBuilder();

        // Start the timer
        Instant start = Instant.now();

        // Use a copy of allOwners to avoid multiple subscriptions
        return allOwners
                .doOnNext(owner -> {
                    // Handle each received owner here
                    String ownerDetails = "Owner Name: " + owner.getName() + " -> phone number: "
                            + owner.getPhone_number();
                    ownerDetailsBuffer.append(ownerDetails).append("\n"); // Append to the buffer with a newline
                })
                .then() // Ignore element emission and only react on completion signal
                .doOnError(error -> {
                    // Handle errors if they occur
                    System.err.println("Error fetching owners: " + error.getMessage());
                })
                .doOnSuccess(aVoid -> {
                    // Write the buffered owner details to the file
                    FileOutputUtil.writeToFile(filePath, ownerDetailsBuffer.toString());
                    Duration duration = Duration.between(start, Instant.now());
                    System.out.println("Task 1: ✅ -> " + duration.toMillis() + " ms");
                });
    }

    /**
     * Task 2: Get the number of pets.
     * 
     * @param webClient The configured WebClient instance.
     */
    public Mono<Void> numberOfPets(Flux<Pet> allPets) {

        // Define the file path
        String filePath = "Task2_totalPets.txt";

        // Clear the file
        FileOutputUtil.clearFile(filePath);

        // Create a buffer for the result string
        StringBuilder resultBuffer = new StringBuilder();

        // Start the timer
        Instant start = Instant.now();

        // Chain the operations and return a Mono<Void> that represents the completion
        // of this task
        return allPets
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
                .then() // Use then to return a Mono<Void> after the operation completes
                .doOnSuccess(aVoid -> {
                    // Write the result to the file
                    FileOutputUtil.writeToFile(filePath, resultBuffer.toString());
                    Duration duration = Duration.between(start, Instant.now());
                    System.out.println("Task 2: ✅ -> " + duration.toMillis() + " ms");
                });
    }

    /**
     * Task 3: Get the number of dogs.
     * 
     * @param webClient The configured WebClient instance.
     */
    public Mono<Void> numberOfDogs(Flux<Pet> allPets) {
        // Define the path
        String filePath = "Task3_totalDogs.txt";

        // Clear the file
        FileOutputUtil.clearFile(filePath);

        // Create a buffer for the result string
        StringBuilder resultBuffer = new StringBuilder();

        // Start the timer
        Instant start = Instant.now();

        // Return a Mono<Void> that represents the completion of counting dogs
        return allPets
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
                .then() // Use then to return a Mono<Void> after the operation completes
                .doOnSuccess(aVoid -> {
                    // Write the result to the file upon successful completion
                    FileOutputUtil.writeToFile(filePath, resultBuffer.toString());

                    Duration duration = Duration.between(start, Instant.now());
                    System.out.println("Task 3: ✅ -> " + duration.toMillis() + " ms");
                });
    }

    /**
     * Task 4: Get pets sorted by weight.
     * 
     * @param webClient The configured WebClient instance.
     */
    public Mono<Pet> petsSortedByWeight(Flux<Pet> allPets) {
        // Define the path
        String filePath = "Task4_sortedWeight.txt";

        // Clear the file
        FileOutputUtil.clearFile(filePath);

        // Create a buffer to store pets' details
        StringBuilder petDetailsBuffer = new StringBuilder();

        // Start the timer
        Instant start = Instant.now();

        // Process the allPets flux
        return allPets
                .filter(pet -> pet.getWeight() > 10) // Only keep pets with weight greater than 10
                .sort(Comparator.comparingDouble(Pet::getWeight)) // Sort the pets by weight
                .doOnNext(pet -> {
                    // Append each pet's details to the buffer
                    String petDetails = "Pet Name: " + pet.getName() + " -> weight: " + pet.getWeight() + "\n";
                    petDetailsBuffer.append(petDetails);
                })
                .doOnComplete(() -> {
                    // Write the pet details to the file upon completion
                    FileOutputUtil.writeToFile(filePath, petDetailsBuffer.toString());
                })
                .doOnError(error -> {
                    // Handle errors if they occur
                    System.err.println("Error fetching pets: " + error.getMessage());
                })
                .doFinally(signalType -> {
                    // This is executed after error or completion to log the time
                    Duration duration = Duration.between(start, Instant.now());
                    System.out.println("Task 4: ✅ -> " + duration.toMillis() + " ms");
                })
                .last(); // Return the last pet in the sorted list as a Mono<Pet>
    }

    /**
     * Task 5: Calculate the average and standard deviation of pet
     * weights.
     * 
     * @param webClient The configured WebClient instance.
     */
    public Mono<Void> averageAndStdDevOfWeights(Flux<Pet> allPets) {
        // Define the path
        String filePath = "Task5_stdDevWeights.txt";

        // Clear the file
        FileOutputUtil.clearFile(filePath);

        StringBuilder resultBuffer = new StringBuilder();

        // Start the timer
        Instant start = Instant.now();

        // Calculate sum of weights
        Mono<Double> sumMono = allPets.map(Pet::getWeight)
                .reduce((Double) 0.0, (a, b) -> Double.sum(a.doubleValue(), b.doubleValue()));

        // Calculate sum of squares of weights
        Mono<Double> sumOfSquaresMono = allPets.map(pet -> Math.pow(pet.getWeight(), 2))
                .reduce((Double) 0.0, (a, b) -> Double.sum(a.doubleValue(), b.doubleValue()));

        // Count the pets
        Mono<Long> countMono = allPets.count();

        // Combine the results and calculate average and standard deviation
        return Mono.zip(sumMono, sumOfSquaresMono, countMono)
                .flatMap(tuple -> {
                    double sum = tuple.getT1();
                    double sumOfSquares = tuple.getT2();
                    long count = tuple.getT3();

                    if (count > 0) {
                        double average = sum / count;
                        double variance = (sumOfSquares / count) - (average * average);
                        double stdDev = Math.sqrt(variance);

                        resultBuffer.append("Average Weight: ").append(String.format("%.2f", average)).append("\n");
                        resultBuffer.append("Standard Deviation: ").append(String.format("%.2f", stdDev)).append("\n");
                    } else {
                        return Mono.error(new IllegalStateException("No pets found."));
                    }
                    // No need to return anything as the next step is doOnSuccess
                    return Mono.empty();
                })
                .doOnSuccess(aVoid -> {
                    // This executes if the above flatMap completes without emitting an error
                    FileOutputUtil.writeToFile(filePath, resultBuffer.toString());
                })
                .doOnError(error -> {
                    // Handle errors if they occur
                    System.err.println("Error calculating weights: " + error.getMessage());
                })
                .doFinally(signalType -> {
                    Duration duration = Duration.between(start, Instant.now());
                    System.out.println("Task 5: ✅ -> " + duration.toMillis() + " ms");
                })
                .then(); // Return an empty Mono<Void> to complete the chain
    }

    /**
     * Task 6: Get the name of the eldest pet.
     *
     * @param webClient The WebClient to use for making requests.
     */
    public Mono<Void> nameOfEldestPet(Flux<Pet> allPets) {
        // Define the path
        String filePath = "Task6_eldestPet.txt";
        StringBuilder resultBuffer = new StringBuilder();

        FileOutputUtil.clearFile(filePath);

        // Start the timer
        Instant start = Instant.now();

        // Find the eldest pet by comparing birth dates
        return allPets
                .reduce((pet1, pet2) -> pet1.getBirth_date().isBefore(pet2.getBirth_date()) ? pet1 : pet2)
                .doOnError(error -> {
                    // Handle errors if they occur
                    System.err.println("Error fetching pets: " + error.getMessage());
                })
                .doOnSuccess(eldestPet -> {
                    // Handle the eldest pet here (if there is one)
                    if (eldestPet != null) {
                        resultBuffer.append("Name of the eldest pet: ").append(eldestPet.getName()).append("\n");
                        FileOutputUtil.writeToFile(filePath, resultBuffer.toString());
                    }
                })
                .doFinally(signalType -> {

                    Duration duration = Duration.between(start, Instant.now());
                    System.out.println("Task 6: ✅ -> " + duration.toMillis() + " ms");
                })
                .then(); // Return an empty Mono<Void> to complete the chain
    }

    /***
     * Task 7: Get the Average number of Pets per Owner, considering
     * only the owners with more than one animal.
     * 
     * @param webClient The WebClient to use for making requests.
     */
    public Mono<Void> averagePetsPerOwner(Flux<Pet> allPets) {
        // Define the path
        String filePath = "Task7_averagePetsOwner.txt";
        StringBuilder resultBuffer = new StringBuilder();

        // CLEAR THE FILE
        FileOutputUtil.clearFile(filePath);

        // Start the timer
        Instant start = Instant.now();

        return allPets
                .collect(
                        // Use a collector to aggregate the counts into a map
                        HashMap<Long, Long>::new,
                        (map, pet) -> map.merge(pet.getOwnerid(), 1L, (existing, increment) -> existing + increment))
                .flatMap(map -> {
                    long totalPets = map.values().stream().mapToLong(Long::longValue).sum();
                    long totalOwners = map.size();

                    double average = totalOwners == 0 ? 0 : (double) totalPets / totalOwners;
                    resultBuffer.append("Average number of pets per owner: ").append(String.format("%.2f", average))
                            .append("\n");

                    return Mono.just(resultBuffer.toString());
                })
                .doOnSuccess(content -> FileOutputUtil.writeToFile(filePath, resultBuffer.toString()))
                .doOnError(error -> {
                    // Handle errors if they occur
                    System.err.println("Error computing average: " + error.getMessage());
                })
                .doFinally(signalType -> {
                    Duration duration = Duration.between(start, Instant.now());
                    System.out.println("Task 7: ✅ -> " + duration.toMillis() + " ms");
                })
                .then(); // Return an empty Mono<Void> to complete the chain
    }

    /**
     * Task 8: Get the Owner Names and the number of Pets they have, sorted by the
     * number of Pets.
     *
     * 
     * @param webClient The WebClient to use for making requests.
     */
    public Mono<Void> ownerNamesAndPetCountsSorted(Flux<Owner> allOwners, PetServiceClient petServiceClient) {
        // Define the path
        String filePath = "Task8_ownerNamesPetCountsSorted.txt";

        // Clear the file
        FileOutputUtil.clearFile(filePath);

        StringBuilder resultBuffer = new StringBuilder();

        // Start the timer
        Instant start = Instant.now();

        return allOwners
                .subscribeOn(Schedulers.boundedElastic())
                .flatMap(owner -> petServiceClient.getPetIdsByOwnerId(owner.getIdentifier())
                        .count()
                        .map(count -> Tuples.of(owner, count))
                        .subscribeOn(Schedulers.boundedElastic()))
                .sort((tuple1, tuple2) -> Long.compare(tuple2.getT2(), tuple1.getT2())) // Sort by pet count
                .retryWhen(Retry.backoff(3, Duration.ofSeconds(1)).maxBackoff(Duration.ofSeconds(10)))
                .doOnNext(tuple -> {
                    // Process each tuple and append the results to the buffer
                    Owner owner = tuple.getT1();
                    long count = tuple.getT2();
                    resultBuffer.append("Owner Name: ").append(owner.getName()).append(" -> Number of Pets: ")
                            .append(count).append("\n");
                })
                .doOnError(error -> {
                    System.err.println("Error fetching owner and pet details: " + error.getMessage());
                })
                .doOnComplete(() -> {
                    // Once all the data is collected, write it to the file.
                    FileOutputUtil.writeToFile(filePath, resultBuffer.toString());
                })
                .doFinally(signalType -> {
                    // When the flux completes, calculate the duration and print it.
                    Duration duration = Duration.between(start, Instant.now());
                    System.out.println("Task 8: ✅ -> " + duration.toMillis() + " ms");
                })
                .then(); // Complete the Mono<Void> chain.
    }

    /**
     * Task 9: Get the owner names and pet names sorted by pet count
     * 
     *
     * @param webClient The WebClient instance for making the requests.
     */
    public Mono<Void> ownerNamesAndPetNamesForTask9(Flux<Owner> allOwners, PetServiceClient petServiceClient) {

        // Define the path
        String filePath = "Task9_ownerNamesAndPetNamesSorted.txt";

        // Clear the file
        FileOutputUtil.clearFile(filePath);

        // Create a buffer for the result string
        StringBuilder resultBuffer = new StringBuilder();

        // Start the timer
        Instant start = Instant.now();

        // Begin processing
        return allOwners
                .flatMapSequential(owner -> petServiceClient.getPetIdsByOwnerId(owner.getIdentifier())
                        .flatMapSequential(petId -> petServiceClient.getPetById(petId), 10) // Adjusted concurrency
                        .map(Pet::getName) // Extract pet names
                        .collectList() // Collect pet names per owner into a List<String>
                        .map(names -> Tuples.of(owner, names)), 10) // Create a tuple of owner and List<String>
                .sort((tuple1, tuple2) -> Integer.compare(tuple2.getT2().size(), tuple1.getT2().size()))
                .retryWhen(Retry.backoff(3, Duration.ofSeconds(5)).maxBackoff(Duration.ofSeconds(20)))
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
                    // Once all the data is collected, write it to the file.
                    FileOutputUtil.writeToFile(filePath, resultBuffer.toString());
                })
                .doFinally(signalType -> {
                    // When the flux completes, calculate the duration and print it.
                    Duration duration = Duration.between(start, Instant.now());
                    System.out.println("Task 9: ✅ -> " + duration.toMillis() + " ms");
                })
                .then(); // Signal completion without emitting any elements

    }

    /**
     * Task to retrieve a pet by its identifier with retries and delay.
     *
     * @param webClient The WebClient instance for making the requests.
     */
    public Mono<Void> taskGetPetByIdWithRetry(Flux<Pet> AllPets, PetServiceClient petServiceClient) {
        return Mono.fromRunnable(() -> {

            // Define the path
            String filePath = "Task10_GetPetByIdWithRetry.txt";

            // Clear the file
            FileOutputUtil.clearFile(filePath);

            // Generate random pet identifier from allPets
            long randomId = AllPets.map(Pet::getIdentifier).collectList().block().get(0);

            // Create a buffer for the result string
            StringBuilder resultBuffer = new StringBuilder();

            // Start the timer
            Instant start = Instant.now();

            // Work the magic with retry
            petServiceClient.getPetByIdWithRetry(randomId)
                    .doOnNext(pet -> {
                        // Add pet details to the buffer
                        resultBuffer.append("Pet Name: ").append(pet.getName()).append("\n");
                    })
                    .doOnError(error -> {
                        // Handle errors if they occur
                        System.err.println("Error fetching pet details: " + error.getMessage());
                    })
                    .doOnSuccess(pet -> {
                        // On success, write the buffer to the file
                        FileOutputUtil.writeToFile(filePath, resultBuffer.toString());
                    })
                    .doFinally(signalType -> {
                        // On termination, successful or not, print the duration
                        Duration duration = Duration.between(start, Instant.now());
                        System.out.println("Task Delay: ✅ -> " + duration.toMillis() + " ms");
                    })
                    .subscribe();

        }).then(); // then() converts this to Mono<Void>, completing after the Runnable
    }

}