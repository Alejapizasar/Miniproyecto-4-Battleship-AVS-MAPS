package com.example.miniproyecto4.persistence;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Single source of truth for where save files live on disk: a fixed
 * folder inside the user's home directory ({@code ~/.battleship/})
 * instead of a path relative to the current working directory, which
 * resolves differently depending on how the app is launched and can
 * silently point two launches at two different physical files.
 *
 * @author Maria Alejandra Pizarro Sarria
 */
public final class SaveLocation
{
    private static final Path SAVE_DIRECTORY = Path.of(System.getProperty("user.home"), ".battleship");

    public static final Path GAME_STATE_FILE = SAVE_DIRECTORY.resolve("battleship_save.dat");
    public static final Path PLAYERS_FILE = SAVE_DIRECTORY.resolve("players.txt");

    private SaveLocation()
    {
    }

    public static void ensureDirectoryExists() throws IOException
    {
        if (Files.notExists(SAVE_DIRECTORY))
        {
            Files.createDirectories(SAVE_DIRECTORY);
        }
    }
}
