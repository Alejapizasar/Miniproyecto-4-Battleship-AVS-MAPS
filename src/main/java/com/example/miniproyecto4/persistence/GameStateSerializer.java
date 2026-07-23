package com.example.miniproyecto4.persistence;

import com.example.miniproyecto4.exceptions.PersistenceException;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Provides the functionality required to save and restore the complete
 * state of a Battleship match using Java object serialization.
 * This class allows a game to be persisted to disk and later restored
 * with all its data, including boards, fleets, player statistics,
 * and game progress.
 *
 * @author Alejandro Valencia Sandoval
 * @author Maria Alejandra Pizarro Sarria
 */
public class GameStateSerializer
{
    /**
     * Serializes the specified game state and stores it in the
     * given file.
     *
     * @param state the game state to be saved.
     * @param path the destination file path.
     * @throws PersistenceException if the game state cannot be written.
     */
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

    /**
     * Restores a previously serialized game state from the
     * specified file.
     *
     * @param path the path of the file containing the saved game.
     * @return the restored game state.
     * @throws PersistenceException if the game state cannot be loaded
     *                              or deserialized.
     */
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