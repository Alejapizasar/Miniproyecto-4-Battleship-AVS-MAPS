package com.example.miniproyecto4.exceptions;

/**
 * Unchecked exception thrown when a {@link com.example.miniproyecto4.model.Ship}
 * is constructed with a non-positive size.
 *
 * <p>Unlike {@link PlacementException} or {@link InvalidShotException}
 * (checked — expected situations a caller must decide how to recover
 * from, e.g. "the player clicked an invalid cell"), this represents a
 * programmer error: a fleet definition that should never be malformed in
 * the first place. Callers are not expected to catch this; it is meant
 * to fail loudly during development instead of silently building a
 * broken ship.</p>
 * @author Alejandro Valencia Sandoval
 * @author Maria Alejandra Pizarro Sarria
 */
public class InvalidShipSizeException extends RuntimeException
{
    public InvalidShipSizeException(String message)
    {
        super(message);
    }
}
