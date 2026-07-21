package com.example.miniproyecto4.model;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 * A single ship of the fleet. Knows its own cells once placed and tracks
 * which of those cells have been hit, so it can report whether it is sunk.
 *
 * @author Alejandro Valencia Sandoval
 */
public class Ship
{
    private final String name;
    private final int size;
    private final List<Coordinate> cells;
    private final Set<Coordinate> hitCells;
    private Orientation orientation;

    public Ship(String name, int size)
    {
        this.name = name;
        this.size = size;
        this.cells = new LinkedList<>();
        this.hitCells = new HashSet<>();
        this.orientation = Orientation.HORIZONTAL;
    }

    public String getName()
    {
        return this.name;
    }

    public int getSize()
    {
        return this.size;
    }

    public Orientation getOrientation()
    {
        return this.orientation;
    }

    public List<Coordinate> getCells()
    {
        return this.cells;
    }

    public boolean isPlaced()
    {
        return !this.cells.isEmpty();
    }

    // Called only by Board once a placement has been validated.
    void assignCells(List<Coordinate> newCells, Orientation newOrientation)
    {
        this.cells.clear();
        this.cells.addAll(newCells);
        this.orientation = newOrientation;
    }

    public void clearPlacement()
    {
        this.cells.clear();
        this.hitCells.clear();
    }

    public void registerHit(Coordinate coordinate)
    {
        if (this.cells.contains(coordinate))
        {
            this.hitCells.add(coordinate);
        }
    }

    public boolean isSunk()
    {
        return this.isPlaced() && this.hitCells.containsAll(this.cells);
    }
}
