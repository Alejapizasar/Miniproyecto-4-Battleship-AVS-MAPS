package com.example.miniproyecto4.model.interfaces;

import com.example.miniproyecto4.model.Board;
import com.example.miniproyecto4.model.Coordinate;

/**
 * Defines the contract for shot selection strategies.
 * Implementations of this interface determine the next coordinate
 * that the machine player will target during the game. Different
 * strategies can be implemented to provide various shooting behaviors
 * without modifying the game logic.
 *
 * @author Alejandro Valencia Sandoval
 * @author Maria Alejandra Pizarro Sarria
 */
public interface ShotStrategy
{
    /**
     * Selects the next coordinate where a shot will be fired.
     *
     * @param targetBoard the opponent's board where the shot will be performed.
     * @return the coordinate selected for the next shot.
     */
    Coordinate chooseShot(Board targetBoard);
}