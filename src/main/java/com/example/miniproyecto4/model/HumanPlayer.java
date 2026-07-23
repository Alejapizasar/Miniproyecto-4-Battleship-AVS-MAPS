package com.example.miniproyecto4.model;

import com.example.miniproyecto4.exceptions.InvalidShotException;
import com.example.miniproyecto4.model.interfaces.Opponent;

import java.util.List;

/**
 * Represents the human player participating in a Battleship match.
 * This class stores the player's board and fleet, and provides the
 * operations defined by the {@link Opponent} interface to allow the
 * game controller to interact with the player during the match.
 *
 * @author Alejandro Valencia Sandoval
 * @author Maria Alejandra Pizarro Sarria
 */
public class HumanPlayer implements Opponent
{
    /**
     * Board containing the player's fleet and shot information.
     */
    private final Board board;

    /**
     * Fleet controlled by the player.
     */
    private final List<Ship> fleet;

    /**
     * Creates a new human player with the specified board and fleet.
     *
     * @param board the board assigned to the player.
     * @param fleet the fleet placed on the player's board.
     */
    public HumanPlayer(Board board, List<Ship> fleet)
    {
        this.board = board;
        this.fleet = fleet;
    }

    /**
     * Returns the board associated with this player.
     *
     * @return the player's board.
     */
    @Override
    public Board getBoard()
    {
        return this.board;
    }

    /**
     * Returns the fleet controlled by this player.
     *
     * @return the player's fleet.
     */
    @Override
    public List<Ship> getFleet()
    {
        return this.fleet;
    }

    /**
     * Determines whether the player has lost the match.
     *
     * @return {@code true} if all ships have been sunk;
     *         {@code false} otherwise.
     */
    @Override
    public boolean hasLost()
    {
        return this.board.areAllShipsSunk();
    }

    /**
     * Processes a shot fired at the player's board.
     *
     * @param coordinate the coordinate where the shot is received.
     * @return the result of the shot.
     * @throws InvalidShotException if the specified coordinate is invalid
     *                              or has already been targeted.
     */
    @Override
    public ShotResult receiveShotAt(Coordinate coordinate) throws InvalidShotException
    {
        return this.board.receiveShot(coordinate);
    }
}