package com.example.miniproyecto4.strategy;

import com.example.miniproyecto4.model.Board;
import com.example.miniproyecto4.model.Coordinate;
import com.example.miniproyecto4.model.interfaces.ShotStrategy;

import java.util.Random;

/**
 * Shooting strategy that selects a random coordinate on the
 * opponent's board. The strategy guarantees that the selected
 * coordinate has not been targeted previously.
 *
 * @author Alejandro Valencia Sandoval
 * @author Maria Alejandra Pizarro Sarria
 */
public class RandomShotStrategy implements ShotStrategy
{
    /**
     * Random number generator used to select target coordinates.
     */
    private final Random random;

    /**
     * Creates a new random shooting strategy.
     */
    public RandomShotStrategy()
    {
        this.random = new Random();
    }

    /**
     * Selects a random coordinate that has not been previously
     * targeted on the specified board.
     *
     * @param targetBoard the opponent's board.
     * @return a valid coordinate for the next shot.
     */
    @Override
    public Coordinate chooseShot(Board targetBoard)
    {
        Coordinate candidate;
        do
        {
            candidate = new Coordinate(this.random.nextInt(Board.SIZE), this.random.nextInt(Board.SIZE));
        }
        while (targetBoard.getShotCells().contains(candidate));

        return candidate;
    }
}