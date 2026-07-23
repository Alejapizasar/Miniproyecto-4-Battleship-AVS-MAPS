package com.example.miniproyecto4.model.interfaces;

import com.example.miniproyecto4.exceptions.PlacementException;
import com.example.miniproyecto4.model.Board;
import com.example.miniproyecto4.model.Coordinate;
import com.example.miniproyecto4.model.Orientation;
import com.example.miniproyecto4.model.Ship;

/**
 * Defines the contract for ship placement strategies.
 * Implementations of this interface determine how and where a ship
 * is positioned on the game board. Different strategies may place
 * ships using player-selected coordinates or automatically generated
 * locations.
 *
 * @author Alejandro Valencia Sandoval
 * @author Maria Alejandra Pizarro Sarria
 */
public interface ShipPlacementStrategy
{
    /**
     * Places the specified ship on the given board according to the
     * implementation's placement strategy.
     *
     * @param board the board where the ship will be placed.
     * @param ship the ship to be placed.
     * @param origin the starting coordinate used for the placement.
     * @param orientation the orientation of the ship on the board.
     * @throws PlacementException if the ship cannot be placed at the
     *                            specified position.
     */
    void placeShip(Board board, Ship ship, Coordinate origin, Orientation orientation) throws PlacementException;
}