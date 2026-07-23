package com.example.miniproyecto4.model.interfaces;

import com.example.miniproyecto4.exceptions.InvalidShotException;
import com.example.miniproyecto4.model.Board;
import com.example.miniproyecto4.model.Coordinate;
import com.example.miniproyecto4.model.Ship;
import com.example.miniproyecto4.model.ShotResult;

import java.util.List;

/**
 * Defines the common behavior required by any participant in a Battleship
 * match. Both human and machine players implement this interface so that
 * the game controller can interact with either type of opponent through
 * the same set of operations.
 *
 * @author Alejandro Valencia Sandoval
 * @author Maria Alejandra Pizarro Sarria
 */
public interface Opponent
{
    /**
     * Returns the board associated with this opponent.
     *
     * @return the opponent's game board.
     */
    Board getBoard();

    /**
     * Returns the fleet owned by this opponent.
     *
     * @return the list of ships that make up the opponent's fleet.
     */
    List<Ship> getFleet();

    /**
     * Determines whether all ships belonging to this opponent
     * have been sunk.
     *
     * @return {@code true} if the opponent has lost the match;
     *         {@code false} otherwise.
     */
    boolean hasLost();

    /**
     * Processes a shot fired at the specified coordinate.
     *
     * @param coordinate the target coordinate of the shot.
     * @return the result of the shot.
     * @throws InvalidShotException if the specified coordinate is invalid
     *                              or has already been targeted.
     */
    ShotResult receiveShotAt(Coordinate coordinate) throws InvalidShotException;
}