package com.example.miniproyecto4.strategy;

import com.example.miniproyecto4.model.Board;
import com.example.miniproyecto4.model.Coordinate;
import com.example.miniproyecto4.model.interfaces.ShotStrategy;

import java.util.Random;

/**
 * Picks a purely random, not-yet-shot coordinate on the target board.
 * Good enough as the required "computer opponent" baseline; swap this
 * for a hunt/target strategy later without touching any caller, since
 * both implement {@link ShotStrategy}.
 *
 * @author Alejandro Valencia Sandoval
 */
public class RandomShotStrategy implements ShotStrategy
{
    private final Random random;

    public RandomShotStrategy()
    {
        this.random = new Random();
    }

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
