package com.example.demo.server.service;

import com.example.demo.server.model.Pet;
import com.example.demo.server.repository.PetRepository;
import com.example.demo.server.repository.OwnerRepository;

import java.time.Duration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service class responsible for handling business logic related to the Pet
 * entity.
 * 
 * This service interacts with the PetRepository to access and manipulate Pet
 * data.
 * Provides CRUD methods and ensures the operations align with the project
 * requirements.
 */
@Service
public class PetService {

    private static final Logger logger = LoggerFactory.getLogger(PetService.class);

    private final PetRepository petRepository;
    private final OwnerRepository ownerRepository;

    public PetService(PetRepository petRepository, OwnerRepository ownerRepository) {
        this.petRepository = petRepository;
        this.ownerRepository = ownerRepository;
    }

    /**
     * Create a new pet in the database.
     * 
     * @param pet The pet entity to be created.
     * @return A reactive stream (Mono) containing the created pet.
     */
    public Mono<Pet> createPet(Pet pet) {

        // Check if required parameters are missing
        if (pet.getName() == null || pet.getSpecies() == null || pet.getBirth_date() == null
                || pet.getWeight() == null || pet.getOwnerid() == null) {
            // Log a warning and return an error Mono
            logger.warn("Missing required parameters for creating a pet.");
            return Mono.empty();
        }

        // Ensure the ID is null to indicate an insert operation
        pet.setIdentifier(null);

        logger.info("Creating pet with name: " + pet.getName());

        return petRepository.save(pet).onErrorResume(e -> {
            logger.error("Error creating pet with name: " + pet.getName(), e);
            return Mono.error(e);
        });
    }

    /**
     * Retrieve all pets from the database.
     * 
     * @return A reactive stream (Flux) of all pets.
     */
    public Flux<Pet> getAllPets() {
        logger.info("Retrieving all pets");

        return petRepository
                .findAll()
                .onErrorResume(e -> {
                    logger.error("Error retrieving all pets", e);
                    return Flux.error(e);
                });

    }

    /**
     * Retrieve a specific pet by its identifier.
     * If the pet is not found, an empty Mono is returned.
     * 
     * @param id The identifier of the pet to retrieve.
     * @return A reactive stream (Mono) containing the pet or empty if not found.
     */
    public Mono<Pet> getPetById(Long id) {
        logger.info("Retrieving pet with id: " + id);

        return petRepository.findById(id)
                .switchIfEmpty(Mono.defer(() -> {
                    logger.warn("No pet found with id: " + id);
                    return Mono.empty();
                }))
                .onErrorResume(e -> {
                    logger.error("Error retrieving pet with id: " + id, e);
                    return Mono.error(e);
                });
    }

    /**
     * Retrieve a specific pet by its identifier with a simulated delay.
     * If the pet is not found, an empty Mono is returned.
     * 
     * @param id The identifier of the pet to retrieve.
     * @return A reactive stream (Mono) containing the pet or empty if not found.
     */
    public Mono<Pet> getPetByIdWithDelay(Long id) {
        logger.info("Retrieving pet with id: " + id + " with delay");

        return petRepository.findById(id)
                .switchIfEmpty(Mono.defer(() -> {
                    logger.warn("No pet found with id: " + id);
                    return Mono.empty();
                }))
                .delayElement(Duration.ofSeconds(2))
                .onErrorResume(e -> {
                    logger.error("Error retrieving pet with id: " + id, e);
                    return Mono.error(e);
                });
    }

    /**
     * Update an existing pet in the database.
     * 
     * @param pet The pet entity with updated values.
     * @return A reactive stream (Mono) containing the updated pet.
     */
    public Mono<Pet> updatePet(Long id, Pet updatedPet) {

        logger.info("Updating pet with id: " + id);

        // Check if the pet with the given ID exists
        return petRepository.findById(id)
                .flatMap(existingPet -> {
                    if (existingPet != null) {
                        // Update the existing pet with the data from updatedPet
                        existingPet.setName(updatedPet.getName());
                        existingPet.setSpecies(updatedPet.getSpecies());
                        existingPet.setBirth_date(updatedPet.getBirth_date());
                        existingPet.setWeight(updatedPet.getWeight());
                        existingPet.setOwnerid(updatedPet.getOwnerid());

                        // Save the updated pet
                        return petRepository.save(existingPet);
                    } else {
                        // Log an error if the pet with the given id doesn't exist
                        logger.warn("Pet with id " + id + " not found.");
                        // Return an error Mono with a custom error message
                        return Mono.error(new RuntimeException("Pet with id " + id + " not found."));
                    }
                })
                .onErrorResume(e -> {
                    // Handle any errors that occur during the update operation
                    logger.error("Error updating pet with id " + id, e);
                    return Mono.error(e);
                });
    }

    /**
     * Delete a specific pet by its identifier.
     * 
     * @param id The identifier of the pet to delete.
     * @return A reactive stream (Mono) indicating the completion of the deletion.
     */
    public Mono<Void> deletePet(Long id) {

        logger.info("Deleting pet with id: " + id);

        return petRepository.findById(id)
                .switchIfEmpty(Mono.defer(() -> {
                    // Log a warning if the pet with the given id doesn't exist
                    logger.warn("Pet with id " + id + " does not exist.");
                    // Return an empty Mono to indicate no action was taken
                    return Mono.empty();
                }))
                .flatMap(existingPet -> {
                    // Delete the pet if it exists
                    return petRepository.deleteById(id);
                })
                .onErrorResume(e -> {
                    // Handle any errors that occur during the delete operation
                    logger.error("Error deleting pet with id " + id, e);
                    return Mono.error(e);
                });

    }

    public Flux<Long> getPetIdsByOwnerId(Long ownerId) {
        logger.info("Retrieving pet IDs for owner with id: " + ownerId);

        // Check if the owner with the given id exists
        return ownerRepository.findById(ownerId)
                .flatMapMany(owner -> {
                    // If the owner exists, retrieve the pet IDs
                    return petRepository.findByOwnerid(ownerId)
                            .map(Pet::getIdentifier);
                })
                .switchIfEmpty(Flux.defer(() -> {
                    // Log a warning if the owner doesn't exist
                    logger.warn("Owner with id " + ownerId + " does not exist.");
                    return Flux.empty();
                }))
                .onErrorResume(e -> {
                    // Handle any errors that occur during the retrieval
                    logger.error("Error retrieving pet ids for owner with id " + ownerId, e);
                    return Flux.error(e);
                });
    }

}
