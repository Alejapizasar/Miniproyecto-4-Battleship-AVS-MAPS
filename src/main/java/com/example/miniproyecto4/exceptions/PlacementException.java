package com.example.miniproyecto4.exceptions;

/**
 * Checked exception thrown whenever a ship cannot be placed on the board:
 * out of bounds, overlapping another ship, or no valid random spot found.
 *
 * @author Alejandro Valencia Sandoval
 */
public class PlacementException extends Exception
{
    public PlacementException(String message)
    {
        super(message);
    }

    public PlacementException(String message, Throwable cause)
    {
        super(message, cause);
    }
}
