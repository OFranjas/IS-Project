package com.example.demo.client.exceptions;

/**
 * Custom runtime exception to handle errors during WebClient operations
 * when interacting with the server.
 */
public class ClientException extends RuntimeException {

    /**
     * Constructor with a message parameter.
     *
     * @param message The error message.
     */
    public ClientException(String message) {
        super(message);
    }

    /**
     * Constructor with a message and a cause.
     *
     * @param message The error message.
     * @param cause   The throwable cause.
     */
    public ClientException(String message, Throwable cause) {
        super(message, cause);
    }
}
