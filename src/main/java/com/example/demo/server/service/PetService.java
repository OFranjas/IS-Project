package com.example.demo.server.service;

import com.example.demo.server.model.Pet;
import com.example.demo.server.repository.PetRepository;
import com.example.demo.server.utils.LoggerUtil;

import ch.qos.logback.classic.Logger;

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

    private final PetRepository petRepository;

    public PetService(PetRepository petRepository) {
        this.petRepository = petRepository;
    }

    /**
     * Create a new pet in the database.
     * 
     * @param pet The pet entity to be created.
     * @return A reactive stream (Mono) containing the created pet.
     */
    public Mono<Pet> createPet(Pet pet) {
        // Ensure the ID is null to indicate an insert operation
        pet.setIdentifier(null);
        LoggerUtil.info(this.getClass().getName(), "Creating pet with name: " + pet.getName());

        return petRepository.save(pet);
    }

    /**
     * Retrieve all pets from the database.
     * 
     * @return A reactive stream (Flux) of all pets.
     */
    public Flux<Pet> getAllPets() {
        LoggerUtil.info(this.getClass().getName(), "Retrieving all pets");

        return petRepository.findAll();
    }

    /**
     * Retrieve a specific pet by its identifier.
     * If the pet is not found, an empty Mono is returned.
     * 
     * @param id The identifier of the pet to retrieve.
     * @return A reactive stream (Mono) containing the pet or empty if not found.
     */
    public Mono<Pet> getPetById(Long id) {

        LoggerUtil.info(this.getClass().getName(), "Retrieving pet with id: " + id);

        return petRepository.findById(id).switchIfEmpty(Mono.empty());
    }

    /**
     * Update an existing pet in the database.
     * 
     * @param pet The pet entity with updated values.
     * @return A reactive stream (Mono) containing the updated pet.
     */
    public Mono<Pet> updatePet(Long id, Pet UptadetPet) {

        LoggerUtil.info(this.getClass().getName(), "Updating pet with id: " + id);

        // Check if the pet with the given id exists
        return petRepository.findById(id)
                .flatMap(existingPet -> {
                    // Update the existing pet with the data from pet
                    existingPet.setName(UptadetPet.getName());
                    existingPet.setSpecies(UptadetPet.getSpecies());
                    existingPet.setBirth_date(UptadetPet.getBirth_date());
                    existingPet.setWeight(UptadetPet.getWeight());

                    // Save the updated pet
                    return petRepository.save(existingPet);
                });
    }

    /**
     * Delete a specific pet by its identifier.
     * 
     * @param id The identifier of the pet to delete.
     * @return A reactive stream (Mono) indicating the completion of the deletion.
     */
    public Mono<Void> deletePet(Long id) {

        LoggerUtil.info(this.getClass().getName(), "Deleting pet with id: " + id);

        return petRepository.deleteById(id);
    }
}
