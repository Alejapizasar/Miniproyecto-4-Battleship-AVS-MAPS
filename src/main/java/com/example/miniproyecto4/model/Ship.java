package com.example.miniproyecto4.model;

import java.io.Serializable;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 * Represents a ship that belongs to a player's fleet.
 * A ship stores its name, size, occupied coordinates, orientation,
 * and the coordinates that have been hit during the match. It also
 * provides operations to determine whether the ship has been placed
 * and whether it has been completely sunk.
 *
 * @author Alejandro Valencia Sandoval
 * @author Maria Alejandra Pizarro Sarria
 */
public class Ship implements Serializable
{
    /**
     * Serialization identifier for this class.
     */
    private static final long serialVersionUID = 1L;

    /**
     * Name of the ship.
     */
    private final String name;

    /**
     * Number of cells occupied by the ship.
     */
    private final int size;

    /**
     * Coordinates currently occupied by the ship.
     */
    private final List<Coordinate> cells;

    /**
     * Coordinates of the ship that have already been hit.
     */
    private final Set<Coordinate> hitCells;

    /**
     * Current orientation of the ship.
     */
    private Orientation orientation;

    /**
     * Creates a new ship with the specified name and size.
     *
     * @param name the name of the ship.
     * @param size the number of cells occupied by the ship.
     * @throws com.example.miniproyecto4.exceptions.InvalidShipSizeException
     *         if the specified size is less than or equal to zero.
     */
    public Ship(String name, int size)
    {
        if (size <= 0)
        {
            throw new com.example.miniproyecto4.exceptions.InvalidShipSizeException(
                    "Ship \"" + name + "\" must have a positive size, got " + size);
        }
        this.name = name;
        this.size = size;
        this.cells = new LinkedList<>();
        this.hitCells = new HashSet<>();
        this.orientation = Orientation.HORIZONTAL;
    }

    /**
     * Returns the name of the ship.
     *
     * @return the ship name.
     */
    public String getName()
    {
        return this.name;
    }

    /**
     * Returns the size of the ship.
     *
     * @return the number of cells occupied by the ship.
     */
    public int getSize()
    {
        return this.size;
    }

    /**
     * Returns the current orientation of the ship.
     *
     * @return the ship orientation.
     */
    public Orientation getOrientation()
    {
        return this.orientation;
    }

    /**
     * Returns the coordinates occupied by the ship.
     *
     * @return the list of occupied coordinates.
     */
    public List<Coordinate> getCells()
    {
        return this.cells;
    }

    /**
     * Determines whether the ship has already been placed
     * on the game board.
     *
     * @return {@code true} if the ship occupies one or more cells;
     *         {@code false} otherwise.
     */
    public boolean isPlaced()
    {
        return !this.cells.isEmpty();
    }

    /**
     * Assigns the coordinates and orientation of the ship after
     * a valid placement has been performed.
     *
     * @param newCells the coordinates occupied by the ship.
     * @param newOrientation the orientation assigned to the ship.
     */
    void assignCells(List<Coordinate> newCells, Orientation newOrientation)
    {
        this.cells.clear();
        this.cells.addAll(newCells);
        this.orientation = newOrientation;
    }

    /**
     * Removes the ship's placement information and clears
     * all registered hits.
     */
    public void clearPlacement()
    {
        this.cells.clear();
        this.hitCells.clear();
    }

    /**
     * Registers a hit on the specified coordinate if it belongs
     * to this ship.
     *
     * @param coordinate the coordinate where the ship was hit.
     */
    public void registerHit(Coordinate coordinate)
    {
        if (this.cells.contains(coordinate))
        {
            this.hitCells.add(coordinate);
        }
    }

    /**
     * Determines whether all occupied coordinates of the ship
     * have been hit.
     *
     * @return {@code true} if the ship has been sunk;
     *         {@code false} otherwise.
     */
    public boolean isSunk()
    {
        return this.isPlaced() && this.hitCells.containsAll(this.cells);
    }
}