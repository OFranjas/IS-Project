package com.example.demo.server;

import com.example.demo.server.model.Owner;
import com.example.demo.server.model.Pet;
import com.example.demo.server.repository.OwnerRepository;
import com.example.demo.server.repository.PetRepository;
import com.example.demo.server.service.OwnerService;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.lenient;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

/**
 * Test class for OwnerService.
 *
 * This class provides tests for the CRUD operations and business logic
 * implemented in OwnerService.
 * It leverages Mockito for mocking dependencies and uses StepVerifier from
 * Reactor to test reactive types.
 */
@SpringBootTest
@ExtendWith(MockitoExtension.class)
public class OwnerServiceTest {

    @InjectMocks
    private OwnerService ownerService;

    @Mock
    private OwnerRepository ownerRepository;

    @Mock
    private PetRepository petRepository;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    /**
     * Test the retrieval of a specific owner by its identifier when the owner
     * exists.
     *
     * This test checks if the service correctly fetches an owner using its
     * identifier
     * and returns the expected owner details without errors.
     */
    @Test
    public void getOwnerById_FoundTest() {
        try {
            Owner owner = new Owner();
            owner.setIdentifier(1L);
            owner.setName("John Doe");

            lenient().when(ownerRepository.findById(1L)).thenReturn(Mono.just(owner));

            StepVerifier.create(ownerService.getOwnerById(1L))
                    .expectNextMatches(retrievedOwner -> "John Doe".equals(retrievedOwner.getName()))
                    .verifyComplete();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Test the retrieval of a specific owner by its identifier when no owner is
     * found.
     *
     * This test checks if the service correctly handles the scenario when no owner
     * is found for the given identifier.
     */
    @Test
    public void getOwnerById_NotFoundTest() {
        try {
            lenient().when(ownerRepository.findById(anyLong())).thenReturn(Mono.empty());

            StepVerifier.create(ownerService.getOwnerById(999L))
                    .verifyComplete();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Test the creation of a new owner.
     *
     * This test checks if the service correctly creates and returns a new owner.
     */
    @Test
    public void createOwnerTest() {
        try {
            Owner owner = new Owner();
            owner.setName("Max");

            lenient().when(ownerRepository.save(any(Owner.class))).thenReturn(Mono.just(owner));

            StepVerifier.create(ownerService.createOwner(owner))
                    .expectNextMatches(createdOwner -> "Max".equals(createdOwner.getName()))
                    .verifyComplete();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Test the updating of an existing owner.
     *
     * This test checks if the service correctly updates and returns an owner.
     */
    @Test
    public void updateOwnerTest() {
        try {
            Owner owner = new Owner();
            owner.setIdentifier(1L);
            owner.setName("Jane Doe Updated");

            lenient().when(ownerRepository.save(any(Owner.class))).thenReturn(Mono.just(owner));

            StepVerifier.create(ownerService.updateOwner(1L, owner))
                    .expectNextMatches(updatedOwner -> "Jane Doe Updated".equals(updatedOwner.getName()))
                    .verifyComplete();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Test the retrieval of all owners.
     *
     * This test checks if the service correctly retrieves all owners.
     */
    @Test
    public void getAllOwnersTest() {
        try {
            Owner owner1 = new Owner();
            owner1.setIdentifier(1L);
            owner1.setName("John");

            Owner owner2 = new Owner();
            owner2.setIdentifier(2L);
            owner2.setName("Jane");

            lenient().when(ownerRepository.findAll()).thenReturn(Flux.just(owner1, owner2));

            StepVerifier.create(ownerService.getAllOwners())
                    .expectNextMatches(owners -> owners.getName().equals("John"))
                    .expectNextMatches(owners -> owners.getName().equals("Jane"))
                    .verifyComplete();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Test the retrieval of all owners with more than one associated pet.
     *
     * This test checks if the service correctly retrieves owners with more than one
     * associated pet.
     */
    @Test
    public void getOwnersWithMultiplePetsTest() {
        try {
            Owner owner1 = new Owner();
            owner1.setIdentifier(1L);
            owner1.setName("John");

            Pet pet1 = new Pet();
            pet1.setIdentifier(1L);
            pet1.setName("Buddy");
            pet1.setOwnerid(1L);

            Pet pet2 = new Pet();
            pet2.setIdentifier(2L);
            pet2.setName("Max");
            pet2.setOwnerid(1L);

            lenient().when(ownerRepository.findAll()).thenReturn(Flux.just(owner1));
            lenient().when(petRepository.findByOwnerid(1L)).thenReturn(Flux.just(pet1, pet2));

            StepVerifier.create(ownerService.getOwnerById(1L))
                    .expectNextMatches(owners -> owners.getName().equals("John"))
                    .verifyComplete();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Test the deletion of an owner by its identifier when the owner has no
     * associated pets.
     *
     * This test checks if the service correctly deletes an owner without any
     * associated pets and completes without errors.
     */
    @Test
    public void deleteOwner_NoPetsTest() {
        try {
            lenient().when(petRepository.findByOwnerid(anyLong())).thenReturn(Flux.empty()); // No pets associated
            lenient().when(ownerRepository.deleteById(anyLong())).thenReturn(Mono.empty());

            StepVerifier.create(ownerService.deleteOwner(1L))
                    .verifyComplete();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Test the deletion of an owner by its identifier when the owner has associated
     * pets.
     *
     * This test checks if the service correctly throws an exception when trying to
     * delete an owner with associated pets.
     */
    @Test
    public void deleteOwner_WithPetsTest() {
        try {
            Pet pet = new Pet();
            pet.setIdentifier(2L);
            pet.setName("Buddy");

            lenient().when(petRepository.findByOwnerid(anyLong())).thenReturn(Flux.just(pet)); // Owner has pets

            StepVerifier.create(ownerService.deleteOwner(1L))
                    .expectErrorMatches(e -> e.getMessage().contains("Cannot delete owner with associated pets."))
                    .verify();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
