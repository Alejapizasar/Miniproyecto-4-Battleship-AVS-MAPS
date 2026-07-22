package com.example.miniproyecto4.model.interfaces;


import com.example.miniproyecto4.model.Board;
import com.example.miniproyecto4.model.Coordinate;
import com.example.miniproyecto4.model.Orientation;
import com.example.miniproyecto4.exceptions.PlacementException;
import com.example.miniproyecto4.model.Ship;

/**
 * Strategy for deciding exactly where a ship ends up on the board.
 * ManualPlacementStrategy honors the origin/orientation given by the
 * player; RandomPlacementStrategy computes its own and ignores them.
 *
 * @author Alejandro Valencia Sandoval
 * @author Maria Alejandra Pizarro Sarria
 */
public interface ShipPlacementStrategy
{
    void placeShip(Board board, Ship ship, Coordinate origin, Orientation orientation) throws PlacementException;
}
