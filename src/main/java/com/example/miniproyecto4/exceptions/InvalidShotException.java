package com.example.miniproyecto4.exceptions;

/**
 * Exception thrown when a shot cannot be processed.
 * This exception is used to indicate that the specified coordinate
 * is outside the game board or that the selected cell has already
 * been targeted during the current match.
 *
 * @author Alejandro Valencia Sandoval
 * @author Maria Alejandra Pizarro Sarria
 */
public class InvalidShotException extends Exception
{
    /**
     * Creates a new exception with the specified detail message.
     *
     * @param message the detail message describing the reason for the exception.
     */
    public InvalidShotException(String message)
    {
        super(message);
    }

    /**
     * Creates a new exception with the specified detail message and cause.
     *
     * @param message the detail message describing the reason for the exception.
     * @param cause the underlying cause of the exception.
     */
    public InvalidShotException(String message, Throwable cause)
    {
        super(message, cause);
    }
}