package com.example.miniproyecto4.strategy;

import com.example.miniproyecto4.model.Board;
import com.example.miniproyecto4.model.Coordinate;
import com.example.miniproyecto4.model.Orientation;
import com.example.miniproyecto4.exceptions.PlacementException;
import com.example.miniproyecto4.model.Ship;
import com.example.miniproyecto4.model.interfaces.ShipPlacementStrategy;

/**
 * Places a ship exactly where the player clicked, with the orientation
 * currently selected in the UI (toggled via the "Rotar Barco" button).
 *
 * @author Alejandro Valencia Sandoval
 * @author Maria Alejandra Pizarro Sarria
 */
public class ManualPlacementStrategy implements ShipPlacementStrategy
{
    @Override
    public void placeShip(Board board, Ship ship, Coordinate origin, Orientation orientation) throws PlacementException
    {
        board.placeShip(ship, origin, orientation);
    }
}