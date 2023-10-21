package com.example.demo.server.controller;

import com.example.demo.server.model.Pet;
import com.example.demo.server.service.PetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Controller class for handling Pet-related HTTP requests.
 */
@RestController
@RequestMapping("/pet") // Mapping for Pet actions
public class PetController {

    @Autowired
    private PetService petService;

    /**
     * Retrieve all pets.
     *
     * @return A list of all pets.
     */
    @GetMapping
    public Flux<Pet> getAllPets() {
        return petService.getAllPets();
    }

    /**
     * Retrieve a pet by its identifier.
     *
     * @param id The identifier of the pet to retrieve.
     * @return The pet with the specified identifier.
     */
    @GetMapping("/{id}")
    public Mono<Pet> getPetById(@PathVariable Long id) {
        return petService.getPetById(id);
    }

    /**
     * Create a new pet.
     *
     * @param pet The pet to create.
     * @return The created pet.
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<Pet> createPet(@RequestBody Pet pet) {
        return petService.createPet(pet);
    }

    /**
     * Update an existing pet.
     *
     * @param id  The identifier of the pet to update.
     * @param pet The updated pet data.
     * @return The updated pet.
     */
    @PutMapping("/{id}")
    public Mono<Pet> updatePet(@PathVariable Long id, @RequestBody Pet pet) {
        return petService.updatePet(id, pet);
    }

    /**
     * Delete a pet by its identifier.
     *
     * @param id The identifier of the pet to delete.
     * @return A completion signal.
     */
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> deletePet(@PathVariable Long id) {
        return petService.deletePet(id);
    }
}
