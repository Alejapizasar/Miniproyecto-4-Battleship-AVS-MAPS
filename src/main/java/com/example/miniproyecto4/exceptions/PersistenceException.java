package com.example.miniproyecto4.exceptions;

/**
 * Exception thrown when an error occurs while saving or loading
 * application data.
 * This exception is used to report failures related to the persistence
 * layer, including serialized game states and player records. It wraps
 * the underlying exception so that callers only need to handle a single
 * exception type defined by the application.
 *
 * @author Alejandro Valencia Sandoval
 * @author Maria Alejandra Pizarro Sarria
 */
public class PersistenceException extends Exception
{
    /**
     * Creates a new exception with the specified detail message.
     *
     * @param message the detail message describing the persistence error.
     */
    public PersistenceException(String message)
    {
        super(message);
    }

    /**
     * Creates a new exception with the specified detail message and cause.
     *
     * @param message the detail message describing the persistence error.
     * @param cause the underlying cause of the exception.
     */
    public PersistenceException(String message, Throwable cause)
    {
        super(message, cause);
    }
}