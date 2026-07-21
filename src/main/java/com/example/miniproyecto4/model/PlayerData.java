package com.example.miniproyecto4.model;

import java.io.Serializable;

/**
 * Carries a player's identity and match statistics: name, shots fired,
 * hits, misses, ships sunk, and whether they won the match. Used both
 * for the in-game HUD and later for the plain-text/serialized
 * persistence modules of the project.
 *
 * <p>Implements {@link Serializable} on purpose so a whole match (player
 * vs. machine) can be saved/resumed as-is once the persistence module
 * is built, without needing a second, separate DTO.</p>
 *
 * @author Alejandro Valencia Sandoval
 */
public class PlayerData implements Serializable
{
    private static final long serialVersionUID = 1L;
    private static final String FIELD_SEPARATOR = ";";

    private String name;
    private int shotsFired;
    private int hits;
    private int misses;
    private int shipsSunk;
    private boolean winner;

    public PlayerData(String name)
    {
        this.name = (name == null || name.isBlank()) ? "Player" : name;
        this.shotsFired = 0;
        this.hits = 0;
        this.misses = 0;
        this.shipsSunk = 0;
        this.winner = false;
    }

    public String getName()
    {
        return this.name;
    }

    public int getShotsFired()
    {
        return this.shotsFired;
    }

    public int getHits()
    {
        return this.hits;
    }

    public int getMisses()
    {
        return this.misses;
    }

    public int getShipsSunk()
    {
        return this.shipsSunk;
    }

    public boolean isWinner()
    {
        return this.winner;
    }

    public void setWinner(boolean winner)
    {
        this.winner = winner;
    }

    /**
     * Records the outcome of a single shot fired by this player.
     *
     * @param wasHit true if the shot landed on a ship, false if it hit water
     */
    public void registerShot(boolean wasHit)
    {
        this.shotsFired++;
        if (wasHit)
        {
            this.hits++;
        }
        else
        {
            this.misses++;
        }
    }

    public void registerSunkShip()
    {
        this.shipsSunk++;
    }

    /**
     * @return hits / shotsFired as a percentage, or 0 if no shots were fired yet
     */
    public double accuracyPercentage()
    {
        if (this.shotsFired == 0)
        {
            return 0.0;
        }
        return (this.hits * 100.0) / this.shotsFired;
    }

    /**
     * Serializes this instance to a single flat-file line, used by the
     * plain-text persistence module (leaderboard / stats history).
     *
     * @return a semicolon-separated line: name;shotsFired;hits;misses;shipsSunk;winner
     */
    public String toPlainTextLine()
    {
        return String.join(FIELD_SEPARATOR,
                this.name,
                String.valueOf(this.shotsFired),
                String.valueOf(this.hits),
                String.valueOf(this.misses),
                String.valueOf(this.shipsSunk),
                String.valueOf(this.winner));
    }

    /**
     * Parses a line produced by {@link #toPlainTextLine()} back into a
     * PlayerData instance.
     *
     * @param line a semicolon-separated line matching the format above
     * @return the reconstructed PlayerData
     * @throws IllegalArgumentException if the line does not have the expected number of fields
     */
    public static PlayerData fromPlainTextLine(String line)
    {
        String[] fields = line.split(FIELD_SEPARATOR);
        if (fields.length != 6)
        {
            throw new IllegalArgumentException("Malformed PlayerData line: " + line);
        }

        PlayerData data = new PlayerData(fields[0]);
        data.shotsFired = Integer.parseInt(fields[1]);
        data.hits = Integer.parseInt(fields[2]);
        data.misses = Integer.parseInt(fields[3]);
        data.shipsSunk = Integer.parseInt(fields[4]);
        data.winner = Boolean.parseBoolean(fields[5]);
        return data;
    }
}