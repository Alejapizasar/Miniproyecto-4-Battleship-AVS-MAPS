package com.example.miniproyecto4.exceptions;

/**
 * Exception thrown when an attempt is made to create a ship with
 * an invalid size.
 * This exception represents a programming error caused by defining
 * a ship with a non-positive length. Since ship sizes are expected
 * to be validated during development, this exception extends
 * {@code RuntimeException} and is not intended to be handled during
 * normal application execution.
 *
 * @author Alejandro Valencia Sandoval
 * @author Maria Alejandra Pizarro Sarria
 */
public class InvalidShipSizeException extends RuntimeException
{
    /**
     * Creates a new exception with the specified detail message.
     *
     * @param message the detail message describing the cause of the exception.
     */
    public InvalidShipSizeException(String message)
    {
        super(message);
    }
}