package com.example.demo.server.repository;

import com.example.demo.server.model.Pet;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

/**
 * Repository interface for CRUD operations on the Pet entity.
 * This repository extends ReactiveCrudRepository to provide reactive CRUD
 * operations.
 */
@Repository
public interface PetRepository extends ReactiveCrudRepository<Pet, Long> {

    /**
     * Custom query method to find all pets by a specific owner's ID.
     *
     * How it works:
     * 1. The method starts with "findBy", which tells Spring Data you want to
     * perform a query based on some property.
     * 2. "OwnerId" refers to the "ownerId" attribute in the Pet model.
     * 3. When this method is called with an owner's ID, Spring Data generates a
     * query to retrieve all Pet records
     * where the ownerId matches the provided value.
     * 4. The resulting pets are returned as a Flux<Pet>, a reactive stream type
     * from Project Reactor.
     *
     * @param ownerId The ID of the owner whose pets we want to retrieve.
     * @return A reactive stream (Flux) of pets belonging to the specified owner.
     */
    Flux<Pet> findByOwnerId(Long ownerId);
}
 