package com.example.miniproyecto4.model.interfaces;

import com.example.miniproyecto4.model.Board;
import com.example.miniproyecto4.model.Coordinate;

/**
 * Decides which coordinate the machine fires at next. Mirrors
 * {@code ShipPlacementStrategy} so the same Strategy pattern already
 * used for placement is reused for shooting (swap in a smarter
 * hunt/target strategy later without touching the caller).
 *
 * @author Alejandro Valencia Sandoval
 * @author Maria Alejandra Pizarro Sarria
 */
public interface ShotStrategy
{
    /**
     * @param targetBoard the enemy board being fired at
     * @return a coordinate inside {@code targetBoard} that has not been shot yet
     */
    Coordinate chooseShot(Board targetBoard);
}
