package com.example.miniproyecto4.strategy;

import com.example.miniproyecto4.exceptions.PlacementException;
import com.example.miniproyecto4.model.Board;
import com.example.miniproyecto4.model.Coordinate;
import com.example.miniproyecto4.model.Orientation;
import com.example.miniproyecto4.model.Ship;
import com.example.miniproyecto4.model.interfaces.ShipPlacementStrategy;

import java.util.Random;

/**
 * Ignores the origin/orientation it receives and instead searches for a
 * random valid spot for the ship, retrying a bounded number of times.
 *
 * @author Alejandro Valencia Sandoval
 * @author Maria Alejandra Pizarro Sarria
 */
public class RandomPlacementStrategy implements ShipPlacementStrategy
{
    private static final int MAX_ATTEMPTS = 200;

    private final Random random;

    public RandomPlacementStrategy()
    {
        this.random = new Random();
    }

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
