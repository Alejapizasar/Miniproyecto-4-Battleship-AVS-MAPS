package com.example.miniproyecto4.strategy;

import com.example.miniproyecto4.exceptions.PlacementException;
import com.example.miniproyecto4.model.Board;
import com.example.miniproyecto4.model.Coordinate;
import com.example.miniproyecto4.model.Orientation;
import com.example.miniproyecto4.model.Ship;
import com.example.miniproyecto4.model.interfaces.ShipPlacementStrategy;

import java.util.Random;

/**
 * Placement strategy that automatically positions ships on the
 * board by selecting random valid coordinates and orientations.
 * The strategy continues searching until a valid position is
 * found or the maximum number of attempts is reached.
 *
 * @author Alejandro Valencia Sandoval
 * @author Maria Alejandra Pizarro Sarria
 */
public class RandomPlacementStrategy implements ShipPlacementStrategy
{
    /**
     * Maximum number of attempts allowed to find a valid
     * position for a ship.
     */
    private static final int MAX_ATTEMPTS = 200;

    /**
     * Random number generator used to select positions
     * and orientations.
     */
    private final Random random;

    /**
     * Creates a new random placement strategy.
     */
    public RandomPlacementStrategy()
    {
        this.random = new Random();
    }

    /**
     * Places the specified ship at a randomly selected valid
     * position on the board.
     *
     * @param board the board where the ship will be placed.
     * @param ship the ship to be placed.
     * @param ignoredOrigin ignored parameter required by the interface.
     * @param ignoredOrientation ignored parameter required by the interface.
     * @throws PlacementException if a valid position cannot be found
     *                            after the maximum number of attempts.
     */
    @Override
    public void placeShip(Board board, Ship ship, Coordinate ignoredOrigin, Orientation ignoredOrientation) throws PlacementException
    {
        for (int attempt = 0; attempt < MAX_ATTEMPTS; attempt++)
        {
            Coordinate origin = new Coordinate(this.random.nextInt(Board.SIZE), this.random.nextInt(Board.SIZE));
            Orientation orientation = this.random.nextBoolean() ? Orientation.HORIZONTAL : Orientation.VERTICAL;

            if (board.canPlace(ship, origin, orientation))
            {
                board.placeShip(ship, origin, orientation);
                return;
            }
        }

        throw new PlacementException("Could not find a random spot for " + ship.getName()
                + " after " + MAX_ATTEMPTS + " attempts");
    }
}