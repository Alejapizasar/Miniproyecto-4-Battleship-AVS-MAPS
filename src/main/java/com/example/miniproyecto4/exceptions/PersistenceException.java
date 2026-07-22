package com.example.miniproyecto4.exceptions;

/**
 * Checked exception thrown whenever saving or loading game data fails,
 * whether it is the flat-file player record or the serialized game
 * state. Wraps the underlying {@code IOException}/{@code ClassNotFoundException}
 * so callers only need to catch one type from this project's own hierarchy.
 *
 * @author Alejandro Valencia Sandoval
 * @author Maria Alejandra Pizarro Sarria
 */
public class PersistenceException extends Exception
{
    public PersistenceException(String message)
    {
        super(message);
    }

    public PersistenceException(String message, Throwable cause)
    {
        super(message, cause);
    }
}
