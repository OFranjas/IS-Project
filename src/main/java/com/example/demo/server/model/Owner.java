package com.example.demo.server.model;

/**
 * Represents an Owner entity in the system.
 *
 * An Owner is characterized by:
 * - An identifier, which is a unique value to distinguish each owner.
 * - A name, representing the owner's full name.
 * - A telephone number, which can be used to contact the owner.
 *
 * The Owner entity has a one-to-many relationship with the Pet entity,
 * signifying that a single owner can have multiple pets. This relationship
 * is established through the ownerId attribute in the Pet entity.
 */
public class Owner {
    private Long identifier;
    private String name;
    private String PhoneNumber;

    // Constructors
    public Owner() {
    }

    public Owner(Long identifier, String name, String PhoneNumber) {
        this.identifier = identifier;
        this.name = name;
        this.PhoneNumber = PhoneNumber;
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

    public String getPhoneNumber() {
        return PhoneNumber;
    }

    public void setPhoneNumber(String PhoneNumber) {
        this.PhoneNumber = PhoneNumber;
    }
}
