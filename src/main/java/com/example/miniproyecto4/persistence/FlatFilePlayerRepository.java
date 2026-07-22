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
 * Plain-text record of every match played: one line per game with
 * {@code nickname;shipsSunk}, matching the semicolon-separated,
 * comment-friendly format used elsewhere in this project's file inputs.
 * This is intentionally NOT serialized Java state (that is
 * {@link GameStateSerializer}'s job) so it stays human-readable and easy
 * to append to across runs.
 *
 * @author Alejandro Valencia Sandoval
 * @author Maria Alejandra Pizarro Sarria
 */
public class FlatFilePlayerRepository
{
    private static final String SEPARATOR = ";";
    private static final String COMMENT_PREFIX = "#";

    private final Path filePath;

    public FlatFilePlayerRepository(Path filePath)
    {
        this.filePath = filePath;
    }

    /**
     * Appends one line for this match's result. Creates the file (with a
     * header comment) the first time it is called.
     *
     * @param playerData the stats to record
     * @throws PersistenceException if the file cannot be written
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
     * @return every recorded match as {@code [nickname, shipsSunk]} pairs,
     *         skipping blank lines and {@code #} comments
     * @throws PersistenceException if the file cannot be read
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
