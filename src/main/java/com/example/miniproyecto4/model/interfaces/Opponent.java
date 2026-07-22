package com.example.miniproyecto4.model.interfaces;

import com.example.miniproyecto4.exceptions.InvalidShotException;
import com.example.miniproyecto4.model.Board;
import com.example.miniproyecto4.model.Coordinate;
import com.example.miniproyecto4.model.Ship;
import com.example.miniproyecto4.model.ShotResult;

import java.util.List;

/**
 * Common contract for either side of a match (human or machine), so
 * {@code GameController} and the turn logic can work with either one
 * without knowing which is which.
 * @author Alejandro Valencia Sandoval
 * @author Maria Alejandra Pizarro Sarria
 */
public interface Opponent
{
    Board getBoard();

    List<Ship> getFleet();

    boolean hasLost();

    ShotResult receiveShotAt(Coordinate coordinate) throws InvalidShotException;
}
