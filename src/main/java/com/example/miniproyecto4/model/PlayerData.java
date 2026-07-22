package com.example.miniproyecto4.model;

import java.io.Serializable;

/**
 * Tracks a single match's stats for the human player: nickname, shots
 * fired, hits, misses and ships sunk. Serializable so it travels inside
 * a saved game, and flat-file friendly (see {@code persistence.FlatFilePlayerRepository})
 * for the simpler nickname + ships-sunk record.
 *
 * @author Alejandro Valencia Sandoval
 * @author Maria Alejandra Pizarro Sarria
 */
public class PlayerData implements Serializable
{
    private static final long serialVersionUID = 1L;

    // HU-3: the "view enemy board" verification tool is capped at 3 uses
    // per match, and each use is penalized as if it were a missed shot.
    private static final int MAX_ENEMY_BOARD_VIEWS = 3;

    private final String name;
    private int shotsFired;
    private int hits;
    private int misses;
    private int shipsSunk;
    private int enemyBoardViews;

    public PlayerData(String name)
    {
        this.name = (name == null || name.isBlank()) ? "Jugador" : name;
        this.shotsFired = 0;
        this.hits = 0;
        this.misses = 0;
        this.shipsSunk = 0;
        this.enemyBoardViews = 0;
    }

    /**
     * Updates every counter in one call based on the outcome of a shot the
     * player just fired.
     *
     * @param result outcome returned by {@link Board#receiveShot(Coordinate)}
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
     * @return true while the player still has at least one use left of the
     *         "view enemy board" verification tool ({@link #MAX_ENEMY_BOARD_VIEWS} per match)
     */
    public boolean canViewEnemyBoard()
    {
        return this.enemyBoardViews < MAX_ENEMY_BOARD_VIEWS;
    }

    /**
     * Registers one use of the "view enemy board" tool. Counts as a
     * penalty: it increments the miss counter exactly like a wasted shot,
     * even though no actual shot was fired at the machine's board, so
     * peeking at the enemy fleet always costs the player something.
     */
    public void registerEnemyBoardView()
    {
        this.enemyBoardViews++;
        this.misses++;
    }

    /**
     * @return how many uses of the "view enemy board" tool are left this match
     */
    public int getRemainingEnemyBoardViews()
    {
        return MAX_ENEMY_BOARD_VIEWS - this.enemyBoardViews;
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
}
