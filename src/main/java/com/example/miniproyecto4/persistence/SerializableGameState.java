package com.example.miniproyecto4.persistence;

import com.example.miniproyecto4.model.Board;
import com.example.miniproyecto4.model.PlayerData;

import java.io.Serializable;

/**
 * Represents a complete snapshot of a Battleship match that can be
 * serialized and restored later. This object stores the boards of
 * both players, the human player's statistics, and the current turn,
 * allowing the game to continue from the exact point where it was saved.
 *
 * @author Alejandro Valencia Sandoval
 * @author Maria Alejandra Pizarro Sarria
 */
public class SerializableGameState implements Serializable
{
    /**
     * Serialization identifier for this class.
     */
    private static final long serialVersionUID = 1L;

    /**
     * Board belonging to the human player.
     */
    private final Board humanBoard;

    /**
     * Board belonging to the machine player.
     */
    private final Board machineBoard;

    /**
     * Statistics associated with the human player.
     */
    private final PlayerData playerData;

    /**
     * Indicates whether it is currently the human player's turn.
     */
    private final boolean humanTurn;

    /**
     * Creates a new serializable game state containing all the
     * information required to resume a match.
     *
     * @param humanBoard the human player's board.
     * @param machineBoard the machine player's board.
     * @param playerData the human player's statistics.
     * @param humanTurn indicates whether it is the human player's turn.
     */
    public SerializableGameState(Board humanBoard, Board machineBoard, PlayerData playerData, boolean humanTurn)
    {
        this.humanBoard = humanBoard;
        this.machineBoard = machineBoard;
        this.playerData = playerData;
        this.humanTurn = humanTurn;
    }

    /**
     * Indicates whether it is currently the human player's turn.
     *
     * @return {@code true} if it is the human player's turn;
     *         {@code false} otherwise.
     */
    public boolean isHumanTurn()
    {
        return this.humanTurn;
    }

    /**
     * Returns the board belonging to the human player.
     *
     * @return the human player's board.
     */
    public Board getHumanBoard()
    {
        return this.humanBoard;
    }

    /**
     * Returns the board belonging to the machine player.
     *
     * @return the machine player's board.
     */
    public Board getMachineBoard()
    {
        return this.machineBoard;
    }

    /**
     * Returns the statistics associated with the human player.
     *
     * @return the player's statistics.
     */
    public PlayerData getPlayerData()
    {
        return this.playerData;
    }
}