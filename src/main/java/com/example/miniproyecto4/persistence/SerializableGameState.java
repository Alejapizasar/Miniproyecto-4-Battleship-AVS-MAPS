package com.example.miniproyecto4.persistence;

import com.example.miniproyecto4.model.Board;
import com.example.miniproyecto4.model.PlayerData;

import java.io.Serializable;

/**
 * Full snapshot of an in-progress match: both boards (with their fleets
 * and shot cells, through {@link Board}'s own Serializable graph) plus
 * the human player's stats. This is what {@link GameStateSerializer}
 * writes/reads as a single serialized object, so resuming a game means
 * restoring these three references as-is.
 *
 * @author Alejandro Valencia Sandoval
 */
public class SerializableGameState implements Serializable
{
    private static final long serialVersionUID = 1L;

    private final Board humanBoard;
    private final Board machineBoard;
    private final PlayerData playerData;

    public SerializableGameState(Board humanBoard, Board machineBoard, PlayerData playerData)
    {
        this.humanBoard = humanBoard;
        this.machineBoard = machineBoard;
        this.playerData = playerData;
    }

    public Board getHumanBoard()
    {
        return this.humanBoard;
    }

    public Board getMachineBoard()
    {
        return this.machineBoard;
    }

    public PlayerData getPlayerData()
    {
        return this.playerData;
    }
}
