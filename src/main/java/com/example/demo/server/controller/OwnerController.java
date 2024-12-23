package com.example.demo.server.controller;

import com.example.demo.server.model.Owner;
import com.example.demo.server.service.OwnerService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Controller class for handling Owner-related HTTP requests.
 */
@RestController
@RequestMapping("/owner") // Mapping for Owner actions
public class OwnerController {

    private static final Logger logger = LoggerFactory.getLogger(OwnerController.class);

    @Autowired
    private OwnerService ownerService;

    /**
     * Retrieve all owners.
     *
     * @return A list of all owners.
     */
    @GetMapping
    public Flux<Owner> getAllOwners() {
        logger.debug("Got request: GET /owner");

        return ownerService.getAllOwners();
    }

    /**
     * Retrieve an owner by its identifier.
     *
     * @param id The identifier of the owner to retrieve.
     * @return The owner with the specified identifier.
     */
    @GetMapping("/{id}")
    public Mono<Owner> getOwnerById(@PathVariable Long id) {
        logger.debug("Got request: GET /owner/" + id);

        return ownerService.getOwnerById(id);
    }

    /**
     * Create a new owner.
     *
     * @param owner The owner to create.
     * @return The created owner.
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<Owner> createOwner(@RequestBody Owner owner) {
        logger.debug("Got request: POST /owner");

        return ownerService.createOwner(owner);
    }

    /**
     * Update an existing owner.
     *
     * @param id    The identifier of the owner to update.
     * @param owner The updated owner data.
     * @return The updated owner.
     */
    @PutMapping("/{id}")
    public Mono<Owner> updateOwner(@PathVariable Long id, @RequestBody Owner owner) {
        logger.debug("Got request: PUT /owner/" + id);

        return ownerService.updateOwner(id, owner);
    }

    /**
     * Delete an owner by its identifier.
     *
     * @param id The identifier of the owner to delete.
     * @return A completion signal.
     */
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> deleteOwner(@PathVariable Long id) {
        logger.debug("Got request: DELETE /owner/" + id);

        return ownerService.deleteOwner(id);
    }
}
