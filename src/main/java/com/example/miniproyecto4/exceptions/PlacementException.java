package com.example.miniproyecto4.exceptions;

/**
 * Exception thrown when a ship cannot be placed on the game board.
 * This exception is used to indicate that the requested ship placement
 * is invalid because it exceeds the board boundaries, overlaps another
 * ship, or no valid position can be found during automatic placement.
 *
 * @author Alejandro Valencia
 * @author Maria Alejandra Pizarro Sarria
 */
public class PlacementException extends Exception
{
    /**
     * Creates a new exception with the specified detail message.
     *
     * @param message the detail message describing the placement error.
     */
    public PlacementException(String message)
    {
        super(message);
    }

    /**
     * Creates a new exception with the specified detail message and cause.
     *
     * @param message the detail message describing the placement error.
     * @param cause the underlying cause of the exception.
     */
    public PlacementException(String message, Throwable cause)
    {
        super(message, cause);
    }
}