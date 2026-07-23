package com.example.miniproyecto4.strategy;

import com.example.miniproyecto4.exceptions.PlacementException;
import com.example.miniproyecto4.model.Board;
import com.example.miniproyecto4.model.Coordinate;
import com.example.miniproyecto4.model.Orientation;
import com.example.miniproyecto4.model.Ship;
import com.example.miniproyecto4.model.interfaces.ShipPlacementStrategy;

/**
 * Placement strategy that positions a ship on the board using
 * the coordinate and orientation selected by the human player.
 * The strategy delegates the validation and placement process
 * to the game board.
 *
 * @author Alejandro Valencia Sandoval
 * @author Maria Alejandra Pizarro Sarria
 */
public class ManualPlacementStrategy implements ShipPlacementStrategy
{
    /**
     * Places the specified ship on the board using the provided
     * origin coordinate and orientation.
     *
     * @param board the board where the ship will be placed.
     * @param ship the ship to be placed.
     * @param origin the starting coordinate of the ship.
     * @param orientation the orientation of the ship.
     * @throws PlacementException if the ship cannot be placed at
     *                            the specified position.
     */
    @Override
    public void placeShip(Board board, Ship ship, Coordinate origin, Orientation orientation) throws PlacementException
    {
        board.placeShip(ship, origin, orientation);
    }
}