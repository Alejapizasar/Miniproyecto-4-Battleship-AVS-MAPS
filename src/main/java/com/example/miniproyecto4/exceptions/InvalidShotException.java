package com.example.miniproyecto4.exceptions;

/**
 * Checked exception thrown when a shot cannot be resolved: coordinate
 * outside the board, or a cell that was already fired at before.
 *
 * @author Alejandro Valencia Sandoval
 * @author Maria Alejandra Pizarro Sarria
 */
public class InvalidShotException extends Exception
{
    public InvalidShotException(String message)
    {
        super(message);
    }

    public InvalidShotException(String message, Throwable cause)
    {
        super(message, cause);
    }
}
