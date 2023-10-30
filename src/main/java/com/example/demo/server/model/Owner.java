package com.example.demo.server.model;

import org.springframework.data.annotation.Id;

import lombok.Data;

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
@Data
public class Owner {

    @Id
    private Long identifier;

    private String name;
    private String phone_number;

    // Constructors
    public Owner() {
    }

    public Owner(Long identifier, String name, String phone_number) {
        this.identifier = identifier;
        this.name = name;
        this.phone_number = phone_number;
    }

}
