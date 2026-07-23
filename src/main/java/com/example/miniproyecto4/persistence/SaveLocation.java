package com.example.miniproyecto4.persistence;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Utility class that defines the default location used to store
 * the application's persistence files. This class provides access
 * to the save directory as well as the files used for serialized
 * game states and player records.
 *
 * @author Maria Alejandra Pizarro Sarria
 */
public final class SaveLocation
{
    /**
     * Directory where all application persistence files are stored.
     */
    private static final Path SAVE_DIRECTORY = Path.of(System.getProperty("user.home"), ".battleship");

    /**
     * Path of the serialized game state file.
     */
    public static final Path GAME_STATE_FILE = SAVE_DIRECTORY.resolve("battleship_save.dat");

    /**
     * Path of the player records file.
     */
    public static final Path PLAYERS_FILE = SAVE_DIRECTORY.resolve("players.txt");

    /**
     * Prevents the instantiation of this utility class.
     */
    private SaveLocation()
    {
    }

    /**
     * Creates the save directory if it does not already exist.
     *
     * @throws IOException if the directory cannot be created.
     */
    public static void ensureDirectoryExists() throws IOException
    {
        if (Files.notExists(SAVE_DIRECTORY))
        {
            Files.createDirectories(SAVE_DIRECTORY);
        }
    }
}