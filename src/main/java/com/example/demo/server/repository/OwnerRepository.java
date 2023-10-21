package com.example.demo.server.repository;

import com.example.demo.server.model.Owner;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for CRUD operations on the Owner entity.
 * This repository extends ReactiveCrudRepository to provide reactive CRUD
 * operations.
 *
 * In the context of this application:
 * 1. The Owner entity represents the individual or entity that owns one or more
 * pets.
 * 2. CRUD operations provided by ReactiveCrudRepository allow for the creation,
 * retrieval,
 * update, and deletion of Owner records in a non-blocking, reactive manner.
 */
@Repository
public interface OwnerRepository extends ReactiveCrudRepository<Owner, Long> {
    // Currently, this repository does not have custom query methods.
    // However, custom queries can be added in the future if needed,
    // following the method naming conventions of Spring Data.
}
