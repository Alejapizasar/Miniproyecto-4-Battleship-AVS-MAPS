package com.example.miniproyecto4.model;

import java.io.Serializable;

/**
 * Represents the statistical information of a player during a
 * Battleship match. This class records the player's name, the
 * number of shots fired, successful hits, missed shots, and
 * ships sunk throughout the game.
 *
 * @author Alejandro Valencia Sandoval
 * @author Maria Alejandra Pizarro Sarria
 */
public class PlayerData implements Serializable
{
    /**
     * Serialization identifier for this class.
     */
    private static final long serialVersionUID = 1L;

    /**
     * Name of the player.
     */
    private final String name;

    /**
     * Total number of shots fired by the player.
     */
    private int shotsFired;

    /**
     * Total number of successful hits.
     */
    private int hits;

    /**
     * Total number of missed shots.
     */
    private int misses;

    /**
     * Total number of ships sunk by the player.
     */
    private int shipsSunk;

    /**
     * Creates a new player statistics object with the specified name.
     * If the provided name is null or blank, a default name is assigned.
     *
     * @param name the player's name.
     */
    public PlayerData(String name)
    {
        this.name = (name == null || name.isBlank()) ? "Jugador" : name;
        this.shotsFired = 0;
        this.hits = 0;
        this.misses = 0;
        this.shipsSunk = 0;
    }

    /**
     * Updates the player's statistics according to the result
     * of the most recent shot.
     *
     * @param result the outcome of the shot.
     */
    public void registerShot(ShotResult result)
    {
        this.shotsFired++;
        switch (result)
        {
            case MISS:
                this.misses++;
                break;
            case HIT:
                this.hits++;
                break;
            case SUNK:
                this.hits++;
                this.shipsSunk++;
                break;
            default:
                break;
        }
    }

    /**
     * Returns the player's name.
     *
     * @return the player's name.
     */
    public String getName()
    {
        return this.name;
    }

    /**
     * Returns the total number of shots fired.
     *
     * @return the number of shots fired.
     */
    public int getShotsFired()
    {
        return this.shotsFired;
    }

    /**
     * Returns the total number of successful hits.
     *
     * @return the number of successful hits.
     */
    public int getHits()
    {
        return this.hits;
    }

    /**
     * Returns the total number of missed shots.
     *
     * @return the number of missed shots.
     */
    public int getMisses()
    {
        return this.misses;
    }

    /**
     * Returns the total number of ships sunk by the player.
     *
     * @return the number of ships sunk.
     */
    public int getShipsSunk()
    {
        return this.shipsSunk;
    }
}