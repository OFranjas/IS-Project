package com.example.demo.server.model;

import java.time.LocalDate;

/**
 * Represents a Pet entity in the system.
 *
 * A Pet is characterized by:
 * - An identifier, which is a unique value to distinguish each pet.
 * - A name, representing the pet's given name.
 * - A species, indicating the type or breed of the pet.
 * - A birth date, indicating when the pet was born.
 * - A weight, specifying the pet's weight.
 * - An ownerId, which establishes a relationship with an Owner entity,
 * signifying the owner of the pet.
 */
public class Pet {
    private Long identifier;
    private String name;
    private String species;
    private LocalDate birthDate;
    private Double weight;
    private Long ownerId;

    // Constructors
    public Pet() {
    }

    public Pet(Long identifier, String name, String species, LocalDate birthDate, Double weight, Long ownerId) {
        this.identifier = identifier;
        this.name = name;
        this.species = species;
        this.birthDate = birthDate;
        this.weight = weight;
        this.ownerId = ownerId;
    }

    // Getters and Setters
    public Long getIdentifier() {
        return identifier;
    }

    public void setIdentifier(Long identifier) {
        this.identifier = identifier;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSpecies() {
        return species;
    }

    public void setSpecies(String species) {
        this.species = species;
    }

    public LocalDate getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(LocalDate birthDate) {
        this.birthDate = birthDate;
    }

    public Double getWeight() {
        return weight;
    }

    public void setWeight(Double weight) {
        this.weight = weight;
    }

    public Long getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(Long ownerId) {
        this.ownerId = ownerId;
    }
}
