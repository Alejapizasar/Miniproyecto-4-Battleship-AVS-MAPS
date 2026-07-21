package com.example.miniproyecto4.model;

import com.example.miniproyecto4.exceptions.PlacementException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Domain-level 10x10 board. Tracks which ship occupies each coordinate
 * without using a raw 2D array, so placement and later shooting logic
 * only deal with the model, never with the JavaFX nodes.
 *
 * @author Alejandro Valencia Sandoval
 */
public class Board
{
    public static final int SIZE = 10;

    private final Map<Coordinate, Ship> occupiedCells;
    private final List<Ship> placedShips;

    public Board()
    {
        this.occupiedCells = new HashMap<>();
        this.placedShips = new ArrayList<>();
    }

    public boolean isInsideBounds(Coordinate coordinate)
    {
        return coordinate.getRow() >= 0 && coordinate.getRow() < SIZE
                && coordinate.getColumn() >= 0 && coordinate.getColumn() < SIZE;
    }

    /**
     * Computes the cells a ship would occupy starting at origin, without
     * placing it. Used by both canPlace() and the placement strategies.
     */
    public List<Coordinate> computeCells(Ship ship, Coordinate origin, Orientation orientation)
    {
        List<Coordinate> cells = new ArrayList<>();
        for (int offset = 0; offset < ship.getSize(); offset++)
        {
            int row = origin.getRow() + (orientation == Orientation.VERTICAL ? offset : 0);
            int column = origin.getColumn() + (orientation == Orientation.HORIZONTAL ? offset : 0);
            cells.add(new Coordinate(row, column));
        }
        return cells;
    }

    public boolean canPlace(Ship ship, Coordinate origin, Orientation orientation)
    {
        List<Coordinate> cells = this.computeCells(ship, origin, orientation);
        for (Coordinate cell : cells)
        {
            if (!this.isInsideBounds(cell) || this.occupiedCells.containsKey(cell))
            {
                return false;
            }
        }
        return true;
    }

    public void placeShip(Ship ship, Coordinate origin, Orientation orientation) throws PlacementException
    {
        if (!this.canPlace(ship, origin, orientation))
        {
            throw new PlacementException("Cannot place " + ship.getName() + " at " + origin
                    + " with orientation " + orientation);
        }

        List<Coordinate> cells = this.computeCells(ship, origin, orientation);
        ship.assignCells(cells, orientation);
        for (Coordinate cell : cells)
        {
            this.occupiedCells.put(cell, ship);
        }
        this.placedShips.add(ship);
    }

    public void clear()
    {
        this.occupiedCells.clear();
        this.placedShips.clear();
    }

    public List<Ship> getPlacedShips()
    {
        return this.placedShips;
    }
}
