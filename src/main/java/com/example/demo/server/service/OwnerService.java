package com.example.demo.server.service;

import com.example.demo.server.model.Owner;
import com.example.demo.server.repository.OwnerRepository;
import com.example.demo.server.repository.PetRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service class responsible for handling business logic related to the Owner
 * entity.
 * 
 * This service interacts with the OwnerRepository and PetRepository to access
 * and manipulate Owner and Pet data.
 * Provides CRUD methods, ensuring operations adhere to the project's
 * requirements, especially the constraint
 * related to deleting owners with associated pets.
 */
@Service
public class OwnerService {

    private static final Logger logger = LoggerFactory.getLogger(OwnerService.class);

    private final OwnerRepository ownerRepository;
    private final PetRepository petRepository;

    public OwnerService(OwnerRepository ownerRepository, PetRepository petRepository) {
        this.ownerRepository = ownerRepository;
        this.petRepository = petRepository;
    }

    /**
     * Create a new owner in the database.
     * 
     * @param owner The owner entity to be created.
     * @return A reactive stream (Mono) containing the created owner.
     */
    public Mono<Owner> createOwner(Owner owner) {
        // Ensure the ID is null to indicate an insert operation
        owner.setIdentifier(null);

        logger.debug("Creating owner with name: " + owner.getName());

        return ownerRepository.save(owner).onErrorResume(e -> {
            logger.error("Error creating owner with name: " + owner.getName(), e);
            return Mono.error(e);
        });
    }

    /**
     * Retrieve all owners from the database.
     * 
     * @return A reactive stream (Flux) of all owners.
     */
    public Flux<Owner> getAllOwners() {

        logger.debug("Retrieving all owners");

        return ownerRepository.findAll()
                .onErrorResume(e -> {
                    logger.error("Error retrieving all owners", e);
                    return Flux.error(e);
                });
    }

    /**
     * Retrieve a specific owner by its identifier.
     * 
     * @param id The identifier of the owner to retrieve.
     * @return A reactive stream (Mono) containing the owner or empty if not found.
     */
    public Mono<Owner> getOwnerById(Long id) {
        logger.debug("Retrieving owner with id: " + id);

        return ownerRepository.findById(id)
                .switchIfEmpty(Mono.defer(() -> {
                    // Log a warning when no owner with the given id is found
                    logger.warn("No owner found with id: " + id);
                    return Mono.empty();
                }))
                .onErrorResume(e -> {
                    // Log an error when an error occurs during retrieval
                    logger.error("Error retrieving owner with id: " + id, e);
                    return Mono.empty();
                });
    }

    /**
     * Update an existing owner in the database.
     * 
     * @param owner The owner entity with updated values.
     * @return A reactive stream (Mono) containing the updated owner.
     */
    public Mono<Owner> updateOwner(Long id, Owner updatedOwner) {

        logger.debug("Updating owner with id: " + id);

        // Check if the owner with the given id exists
        return ownerRepository.findById(id)
                .flatMap(existingOwner -> {
                    // Update the existing owner with the data from updatedOwner
                    existingOwner.setName(updatedOwner.getName());
                    existingOwner.setPhone_number(updatedOwner.getPhone_number());

                    logger.debug("Updated owner with id: " + id);

                    // Save the updated owner
                    return ownerRepository.save(existingOwner);
                })
                .onErrorResume(e -> {
                    logger.error("Error updating owner with id: " + id, e);
                    return Mono.error(e);
                });
    }

    /**
     * Delete a specific owner by its identifier.
     * The owner can only be deleted if they are not associated with any pets.
     * 
     * @param id The identifier of the owner to delete.
     * @return A reactive stream (Mono) indicating the completion of the deletion or
     *         an error if constraints are violated.
     */
    public Mono<Void> deleteOwner(Long id) {

        logger.debug("Deleting owner with id: " + id);

        return petRepository.findByOwnerid(id)
                .hasElements()
                .flatMap(hasPets -> {
                    if (hasPets) {
                        logger.error(
                                "Owner with id " + id + " has pets and cannot be deleted.");

                        return Mono.empty();
                    } else {
                        return ownerRepository.deleteById(id);
                    }
                })
                .onErrorResume(e -> {
                    logger.error("Error deleting owner with id: " + id, e);
                    return Mono.error(e);
                });
    }
}
