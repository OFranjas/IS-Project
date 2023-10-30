package com.example.demo.client;

import java.io.File;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.web.reactive.function.client.WebClient;

import com.example.demo.client.config.WebClientConfig;
import com.example.demo.client.service.OwnerServiceClient;
import com.example.demo.client.service.PetServiceClient;
import com.example.demo.server.model.Owner;
import com.example.demo.server.model.Pet;
import com.example.demo.client.utils.FileOutputUtil;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;
import reactor.util.function.Tuples;

public class Tasks {

    private final WebClient webClient;

    public Tasks(WebClient webClient) {
        this.webClient = webClient;
    }

    /**
     * Task 1: Get owner names and phone numbers and print them.
     * 
     * @param webClient The configured WebClient instance.
     */
    public Runnable OwnersNamesPhones() {

        return () -> {
            // Create the service client
            OwnerServiceClient ownerServiceClient = new OwnerServiceClient(webClient);

            // Define the file path
            String filePath = "Task1_ownersNamesPhones.txt";

            // Clear the file
            FileOutputUtil.clearFile(filePath);

            // Subscribe to getAllOwners and print the data when it's available
            ownerServiceClient.getAllOwners()
                    .subscribe(owner -> {
                        // Handle each received owner here
                        String ownerDetails = "Owner Name: " + owner.getName() + " -> phone number: "
                                + owner.getPhone_number();
                        // System.out.println(ownerDetails); // Optional: Print to console
                        FileOutputUtil.writeToFile(filePath, ownerDetails); // Write to file
                    },
                            error -> {
                                // Handle errors if they occur
                                System.err.println("Error fetching owners: " + error.getMessage());
                            });

        };

    }

    /**
     * Task 2: Get the number of pets.
     * 
     * @param webClient The configured WebClient instance.
     */
    public Runnable NumberOfPets() {

        return () -> {

            // Create the service client
            PetServiceClient petServiceClient = new PetServiceClient(webClient);

            // Define the file path
            String filePath = "Task2_totalPets.txt";

            // Clear the file
            FileOutputUtil.clearFile(filePath);

            // Subscribe to getNumberOfPets and write the data to the file when it's
            // available
            petServiceClient.getAllPets()
                    .reduce(0L, (count, pet) -> count + 1)
                    .subscribe(
                            count -> {
                                // Handle the received pet count here
                                String petCount = "Total number of pets: " + count;
                                // System.out.println(petCount); // Optional: Print to console
                                FileOutputUtil.writeToFile(filePath, petCount); // Write to file
                            },
                            error -> {
                                // Handle errors if they occur
                                System.err.println("Error fetching pet count: " + error.getMessage());
                            });
        };
    }

    /**
     * Task 3: Get the number of dogs.
     * 
     * @param webClient The configured WebClient instance.
     */
    public Runnable NumberOfDogs() {

        return () -> {
            // Create the service client
            PetServiceClient petServiceClient = new PetServiceClient(webClient);

            // Define the path
            String filePath = "Task3_totalDogs.txt";

            // Clear the file
            FileOutputUtil.clearFile(filePath);

            // Subscribe to pets with species "dog" and print the count when it's available
            petServiceClient.getAllPets()
                    .filter(pet -> pet.getSpecies().equals("dog"))
                    .count()
                    .subscribe(count -> {
                        // Handle the count here
                        // System.out.println("Number of dogs: " + count);
                        FileOutputUtil.writeToFile(filePath, "Number of dogs: " + count);
                    },
                            error -> {
                                // Handle errors if they occur
                                System.err.println("Error fetching pets: " + error.getMessage());
                            });

        };

    }

    /**
     * Task 4: Get pets sorted by weight.
     * 
     * @param webClient The configured WebClient instance.
     */
    public Runnable PetsSortedByWeight() {

        return () -> {

            // Create the service client
            PetServiceClient petServiceClient = new PetServiceClient(webClient);

            // Define the path
            String filePath = "Task4_sortedWeight.txt";

            // Clear the file
            FileOutputUtil.clearFile(filePath);

            // Create a list to store pets
            List<Pet> sortedPetsList = new ArrayList<>();

            // Subscribe to getAllPets and add pets to the list when they arrive
            petServiceClient.getAllPets()
                    .filter(pet -> pet.getWeight() > 10) // Only keep pets with weight greater than 10
                    .subscribe(
                            pet -> sortedPetsList.add(pet),
                            error -> {
                                // Handle errors if they occur
                                System.err.println("Error fetching pets: " + error.getMessage());
                            },
                            () -> {
                                // When all pets have arrived, sort the list by weight
                                sortedPetsList.sort(Comparator.comparingDouble(Pet::getWeight));

                                // Process and write pets to the file
                                sortedPetsList.forEach(pet -> {
                                    // Print each pet as it arrives
                                    // System.out.println("Pet Name: " + pet.getName());
                                    // System.out.println("Pet Weight: " + pet.getWeight());

                                    // Write each pet to the file
                                    String petDetails = "Pet Name: " + pet.getName() + " -> weight: " + pet.getWeight();
                                    FileOutputUtil.writeToFile(filePath, petDetails);
                                    // Add more pet details as needed
                                });
                            });

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

            // Create the service client
            PetServiceClient petServiceClient = new PetServiceClient(webClient);

            // Define the path
            String filePath = "Task5_stdDevWeights.txt";

            // Clear the file
            FileOutputUtil.clearFile(filePath);

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

                            // System.out.printf("Average Weight: %.3f\n", average);
                            // System.out.printf("Standard Deviation: %.3f\n", stdDev);

                            FileOutputUtil.writeToFile(filePath, "Average Weight: " + average);
                            FileOutputUtil.writeToFile(filePath, "Standard Deviation: " + stdDev);
                        } else {
                            System.out.println("No pets found.");
                        }

                        return Mono.empty();
                    })
                    .subscribe();

        };

    }

    /**
     * Task 6: Get the name of the eldest pet.
     *
     * @param webClient The WebClient to use for making requests.
     */
    public Runnable NameOfEldestPet() {

        return () -> {

            // Create the service client
            PetServiceClient petServiceClient = new PetServiceClient(webClient);

            // Define the path
            String filePath = "Task6_eldestPet.txt";

            // Clear the file
            FileOutputUtil.clearFile(filePath);

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
                                // System.out.println("Name of the eldest pet: " + eldestPet.getName());
                                FileOutputUtil.writeToFile(filePath, "Name of the eldest pet: " + eldestPet.getName());
                            },
                            error -> {
                                // Handle errors if they occur
                                System.err.println("Error fetching pets: " + error.getMessage());
                            });

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

            // Create the service client
            PetServiceClient petServiceClient = new PetServiceClient(webClient);

            // Define the path
            String filePath = "Task7_averagePetsOwner.txt";

            // Clear the file
            FileOutputUtil.clearFile(filePath);

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

                        double average = (double) totalPets / totalOwners;
                        return Mono.just(average);
                    })
                    .subscribe(
                            avg -> {
                                FileOutputUtil.writeToFile(filePath, "Average number of pets per owner: " + avg);
                            },
                            error -> {
                                System.err.println("Error computing average: " + error.getMessage());
                            });

        };

    }

    /**
     * Task 8: Get the Owner Names and the number of Pets they have,
     *
     * This function fetches all owners and their associated pets' ids from the
     * server,
     * then writes the sorted (by number of pets) output to a file. The data
     * transformation mostly happens on the client side due to limitations on the
     * server.
     * 
     * @param webClient The WebClient to use for making requests.
     */
    public Runnable ownerNamesAndPetCountsSorted() {

        return () -> {
            // Create the service clients
            OwnerServiceClient ownerServiceClient = new OwnerServiceClient(webClient);
            PetServiceClient petServiceClient = new PetServiceClient(webClient);

            // Define the path
            String filePath = "Task8_ownerNamesPetCountsSorted.txt";

            // Clear the file
            FileOutputUtil.clearFile(filePath);

            // Fetch all owners
            ownerServiceClient.getAllOwners()
                    .flatMap(owner -> petServiceClient.getPetIdsByOwnerId(owner.getIdentifier())
                            .count()
                            .map(count -> Tuples.of(owner, count))) // This maps the Mono<Long> to a Mono<Tuple2<Owner,
                                                                    // Long>>
                    .sort((tuple1, tuple2) -> Long.compare(tuple2.getT2(), tuple1.getT2())) // Sort by pet count
                    .subscribe(
                            tuple -> {
                                Owner owner = tuple.getT1();
                                long count = tuple.getT2();

                                String ownerDetails = "Owner Name: " + owner.getName() + " -> Number of Pets: " + count;
                                FileOutputUtil.writeToFile(filePath, ownerDetails);
                            },
                            error -> System.err.println("Error fetching owner and pet details: " + error.getMessage()));
        };

    }

    /**
     * Task 9: Get the owner names and pet names sorted by pet count
     * 
     * This function fetches all owners and their associated pets' names from the
     * server,
     * then writes the sorted (by number of pets) output to a file. The data
     * transformation mostly happens on the client side due to limitations on the
     * server.
     *
     * @param webClient The WebClient instance for making the requests.
     */
    public Runnable ownerNamesAndPetNamesForTask9() {

        return () -> {

            // Create the service clients
            OwnerServiceClient ownerServiceClient = new OwnerServiceClient(webClient);
            PetServiceClient petServiceClient = new PetServiceClient(webClient);

            // Define the path
            String filePath = "Task9_ownerNamesAndPetNamesSorted.txt";

            // Clear the file
            FileOutputUtil.clearFile(filePath);

            // Work the magic
            ownerServiceClient.getAllOwners()
                    .flatMap(owner -> petServiceClient.getPetIdsByOwnerId(owner.getIdentifier())
                            .flatMap(petId -> petServiceClient.getPetById(petId).map(Pet::getName))
                            .map(name -> Tuples.of(owner, name)))
                    .groupBy(tuple -> tuple.getT1(), tuple -> tuple.getT2()) // Group by owner
                    .flatMap(groupedFlux -> groupedFlux.reduce(new ArrayList<String>(), (list, name) -> {
                        list.add(name);
                        return list;
                    }).map(names -> Tuples.of(groupedFlux.key(), names)))
                    .sort((tuple1, tuple2) -> Integer.compare(tuple2.getT2().size(), tuple1.getT2().size()))
                    .subscribe(
                            tuple -> {
                                Owner owner = tuple.getT1();
                                List<String> petNames = tuple.getT2();

                                String ownerDetails = "Owner Name: " + owner.getName() + " -> Pets: "
                                        + String.join(", ", petNames);
                                FileOutputUtil.writeToFile(filePath, ownerDetails);
                            },
                            error -> System.err.println("Error fetching owner and pet details: " + error.getMessage()));

        };

    }

}
