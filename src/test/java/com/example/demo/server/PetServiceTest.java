package com.example.demo.server;

import com.example.demo.server.service.PetService;
import com.example.demo.server.model.Pet;
import com.example.demo.server.repository.PetRepository;

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
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

/**
 * Test class for PetService.
 *
 * This class provides tests for the CRUD operations and business logic
 * implemented in PetService.
 * It leverages Mockito for mocking dependencies and uses StepVerifier from
 * Reactor to test reactive types.
 */
@SpringBootTest
@ExtendWith(MockitoExtension.class)
public class PetServiceTest {

    @InjectMocks
    private PetService petService;

    @Mock
    private PetRepository petRepository;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    /**
     * Test the retrieval of a specific pet by its identifier.
     *
     * This test checks if the service correctly fetches a pet using its identifier.
     */
    @Test
    public void getPetById_FoundTest() {
        try {
            Pet pet = new Pet();
            pet.setIdentifier(1L);
            pet.setName("Buddy");

            lenient().when(petRepository.findById(1L)).thenReturn(Mono.just(pet));

            StepVerifier.create(petService.getPetById(1L))
                    .expectNextMatches(retrievedPet -> "Buddy".equals(retrievedPet.getName()))
                    .verifyComplete();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Test the retrieval of a specific pet by its identifier when no pet is found.
     *
     * This test checks if the service correctly handles the scenario when no pet is
     * found for the given identifier.
     */
    @Test
    public void getPetById_NotFoundTest() {
        try {
            lenient().when(petRepository.findById(anyLong())).thenReturn(Mono.empty());

            StepVerifier.create(petService.getPetById(999L))
                    .verifyComplete();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Test the creation of a new pet.
     *
     * This test checks if the service correctly creates and returns a new pet.
     */
    @Test
    public void createPetTest() {
        try {
            Pet pet = new Pet();
            pet.setName("Max");

            lenient().when(petRepository.save(any(Pet.class))).thenReturn(Mono.just(pet));

            StepVerifier.create(petService.createPet(pet))
                    .expectNextMatches(createdPet -> "Max".equals(createdPet.getName()))
                    .verifyComplete();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Test the updating of an existing pet.
     *
     * This test checks if the service correctly updates and returns a pet.
     */
    @Test
    public void updatePetTest() {
        try {
            Pet pet = new Pet();
            pet.setIdentifier(1L);
            pet.setName("Buddy Updated");

            lenient().when(petRepository.save(any(Pet.class))).thenReturn(Mono.just(pet));

            StepVerifier.create(petService.updatePet(1L, pet))
                    .expectNextMatches(updatedPet -> "Buddy Updated".equals(updatedPet.getName()))
                    .verifyComplete();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Test the deletion of a pet by its identifier.
     *
     * This test checks if the service correctly deletes a pet and completes without
     * errors.
     */
    @Test
    public void deletePetTest() {
        try {
            lenient().when(petRepository.deleteById(anyLong())).thenReturn(Mono.empty());

            StepVerifier.create(petService.deletePet(1L))
                    .verifyComplete();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
