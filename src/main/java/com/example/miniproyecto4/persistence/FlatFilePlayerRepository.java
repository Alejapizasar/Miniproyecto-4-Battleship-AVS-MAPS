package com.example.miniproyecto4.persistence;

import com.example.miniproyecto4.exceptions.PersistenceException;
import com.example.miniproyecto4.model.PlayerData;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;

/**
 * Repository responsible for storing and retrieving player match
 * records using a plain text file. Each record contains the player's
 * name and the number of ships sunk during a completed match.
 *
 * @author Alejandro Valencia Sandoval
 * @author Maria Alejandra Pizarro Sarria
 */
public class FlatFilePlayerRepository
{
    /**
     * Separator used between fields in each record.
     */
    private static final String SEPARATOR = ";";

    /**
     * Prefix used to identify comment lines.
     */
    private static final String COMMENT_PREFIX = "#";

    /**
     * Path of the file containing the player records.
     */
    private final Path filePath;

    /**
     * Creates a new repository that stores player records in
     * the specified file.
     *
     * @param filePath the path of the persistence file.
     */
    public FlatFilePlayerRepository(Path filePath)
    {
        this.filePath = filePath;
    }

    /**
     * Saves the specified player's statistics by appending
     * a new record to the persistence file.
     *
     * @param playerData the player information to be stored.
     * @throws PersistenceException if the record cannot be written.
     */
    public void save(PlayerData playerData) throws PersistenceException
    {
        try
        {
            if (Files.notExists(this.filePath))
            {
                Files.writeString(this.filePath, "# nickname;shipsSunk" + System.lineSeparator(),
                        StandardOpenOption.CREATE);
            }

            String line = playerData.getName() + SEPARATOR + playerData.getShipsSunk() + System.lineSeparator();
            Files.writeString(this.filePath, line, StandardOpenOption.CREATE, StandardOpenOption.APPEND);
        }
        catch (IOException exception)
        {
            throw new PersistenceException("Could not save player record to " + this.filePath, exception);
        }
    }

    /**
     * Loads all player records stored in the persistence file.
     *
     * @return a list containing every stored player record.
     * @throws PersistenceException if the records cannot be read.
     */
    public List<String[]> loadAll() throws PersistenceException
    {
        List<String[]> records = new ArrayList<>();
        if (Files.notExists(this.filePath))
        {
            return records;
        }

        try
        {
            for (String line : Files.readAllLines(this.filePath))
            {
                if (line.isBlank() || line.startsWith(COMMENT_PREFIX))
                {
                    continue;
                }
                records.add(line.split(SEPARATOR));
            }
            return records;
        }
        catch (IOException exception)
        {
            throw new PersistenceException("Could not read player records from " + this.filePath, exception);
        }
    }
}