package com.example.miniproyecto4.model;

import com.example.miniproyecto4.exceptions.InvalidShotException;
import com.example.miniproyecto4.exceptions.PlacementException;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Domain-level 10x10 board. Tracks which ship occupies each coordinate
 * without using a raw 2D array, so placement and shooting logic only
 * deal with the model, never with the JavaFX nodes. Serializable so a
 * whole board (fleet included, through {@link Ship}) can be saved and
 * later restored as-is.
 *
 * @author Alejandro Valencia Sandoval
 * @author Maria Alejandra Pizarro Sarria
 */
public class Board implements Serializable
{
    private static final long serialVersionUID = 1L;

    public static final int SIZE = 10;

    private final Map<Coordinate, Ship> occupiedCells;
    private final List<Ship> placedShips;
    private final Set<Coordinate> shotCells;

    public Board()
    {
        this.occupiedCells = new HashMap<>();
        this.placedShips = new ArrayList<>();
        this.shotCells = new HashSet<>();
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

    /**
     * Resolves a shot fired at {@code coordinate}: marks the cell as shot,
     * registers the hit on the owning ship (if any) and reports whether it
     * was a miss, a hit, or the final blow that sinks the ship.
     *
     * @param coordinate the target cell
     * @return the outcome of the shot
     * @throws InvalidShotException if the coordinate is out of bounds or
     *                               was already fired at before
     */
    public ShotResult receiveShot(Coordinate coordinate) throws InvalidShotException
    {
        if (!this.isInsideBounds(coordinate))
        {
            throw new InvalidShotException("Shot coordinate " + coordinate + " is out of bounds");
        }
        if (this.shotCells.contains(coordinate))
        {
            throw new InvalidShotException("Coordinate " + coordinate + " was already shot at");
        }

        this.shotCells.add(coordinate);

        Ship ship = this.occupiedCells.get(coordinate);
        if (ship == null)
        {
            return ShotResult.MISS;
        }

        ship.registerHit(coordinate);
        return ship.isSunk() ? ShotResult.SUNK : ShotResult.HIT;
    }

    public Ship getShipAt(Coordinate coordinate)
    {
        return this.occupiedCells.get(coordinate);
    }

    public boolean areAllShipsSunk()
    {
        if (this.placedShips.isEmpty())
        {
            return false;
        }
        for (Ship ship : this.placedShips)
        {
            if (!ship.isSunk())
            {
                return false;
            }
        }
        return true;
    }

    public Set<Coordinate> getShotCells()
    {
        return Collections.unmodifiableSet(this.shotCells);
    }

    public void clear()
    {
        this.occupiedCells.clear();
        this.placedShips.clear();
        this.shotCells.clear();
    }

    public List<Ship> getPlacedShips()
    {
        return this.placedShips;
    }
}
