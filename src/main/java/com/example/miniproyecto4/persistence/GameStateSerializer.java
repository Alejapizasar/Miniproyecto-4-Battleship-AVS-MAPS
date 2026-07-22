package com.example.miniproyecto4.persistence;

import com.example.miniproyecto4.exceptions.PersistenceException;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Writes and reads a {@link SerializableGameState} to/from disk using
 * plain Java serialization, so a paused match can be resumed exactly
 * where it was left (both boards, fleets, shots already fired, and the
 * player's stats).
 *
 * @author Alejandro Valencia Sandoval
 */
public class GameStateSerializer
{
    public void save(SerializableGameState state, Path path) throws PersistenceException
    {
        try (ObjectOutputStream output = new ObjectOutputStream(Files.newOutputStream(path)))
        {
            output.writeObject(state);
        }
        catch (IOException exception)
        {
            throw new PersistenceException("Could not serialize game state to " + path, exception);
        }
    }

    public SerializableGameState load(Path path) throws PersistenceException
    {
        try (ObjectInputStream input = new ObjectInputStream(Files.newInputStream(path)))
        {
            return (SerializableGameState) input.readObject();
        }
        catch (IOException | ClassNotFoundException exception)
        {
            throw new PersistenceException("Could not load game state from " + path, exception);
        }
    }
}
