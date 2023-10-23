package com.example.demo.server.model;

import lombok.Data;
import lombok.Lombok;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

import org.springframework.data.annotation.Id;

/**
 * Represents a Pet entity in the system.
 *
 * A Pet is characterized by:
 * - An identifier, which is a unique value to distinguish each pet.
 * - A name, representing the pet's given name.
 * - A species, indicating the type or breed of the pet.
 * - A birth date, indicating when the pet was born.
 * - A weight, specifying the pet's weight.
 * - An ownerid, which establishes a relationship with an Owner entity,
 * signifying the owner of the pet.
 */
@Data
public class Pet {

    @Id
    private Long identifier;

    private String name;
    private String species;
    private LocalDate birth_date;
    private Double weight;
    private Long ownerid;

    // Constructors
    public Pet() {
    }

    public Pet(Long identifier, String name, String species, LocalDate birth_date, Double weight, Long ownerid) {
        this.identifier = identifier;
        this.name = name;
        this.species = species;
        this.birth_date = birth_date;
        this.weight = weight;
        this.ownerid = ownerid;
    }
}
