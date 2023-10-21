package com.example.demo.server.service;

import com.example.demo.server.model.Owner;
import com.example.demo.server.repository.OwnerRepository;
import com.example.demo.server.repository.PetRepository;
import org.springframework.beans.factory.annotation.Autowired;
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
        return ownerRepository.save(owner);
    }

    /**
     * Retrieve all owners from the database.
     * 
     * @return A reactive stream (Flux) of all owners.
     */
    public Flux<Owner> getAllOwners() {
        return ownerRepository.findAll();
    }

    /**
     * Retrieve a specific owner by its identifier.
     * 
     * @param id The identifier of the owner to retrieve.
     * @return A reactive stream (Mono) containing the owner or empty if not found.
     */
    public Mono<Owner> getOwnerById(Long id) {
        return ownerRepository.findById(id).switchIfEmpty(Mono.empty());
    }

    /**
     * Update an existing owner in the database.
     * 
     * @param owner The owner entity with updated values.
     * @return A reactive stream (Mono) containing the updated owner.
     */
    public Mono<Owner> updateOwner(Long id, Owner updatedOwner) {
        // Check if the owner with the given id exists
        return ownerRepository.findById(id)
                .flatMap(existingOwner -> {
                    // Update the existing owner with the data from updatedOwner
                    existingOwner.setName(updatedOwner.getName());
                    existingOwner.setPhoneNumber(updatedOwner.getPhoneNumber());

                    // Save the updated owner
                    return ownerRepository.save(existingOwner);
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
        return petRepository.findByOwnerId(id)
                .hasElements()
                .flatMap(hasPets -> {
                    if (hasPets) {
                        return Mono.error(new RuntimeException("Cannot delete owner with associated pets."));
                    } else {
                        return ownerRepository.deleteById(id);
                    }
                });
    }
}
