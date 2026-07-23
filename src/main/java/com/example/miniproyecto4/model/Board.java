package com.example.miniproyecto4.model;

import com.example.miniproyecto4.exceptions.InvalidShotException;
import com.example.miniproyecto4.exceptions.PlacementException;

import java.io.Serializable;
import java.util.*;

/**
 * Represents a Battleship game board.
 * This class manages ship placement, shot processing, occupied
 * coordinates, and the overall state of the board during a match.
 * It also provides the information required to determine whether
 * all ships have been destroyed.
 *
 * @author Alejandro Valencia Sandoval
 * @author Maria Alejandra Pizarro Sarria
 */
public class Board implements Serializable
{
    /**
     * Serialization identifier for this class.
     */
    private static final long serialVersionUID = 1L;

    /**
     * Number of rows and columns of the game board.
     */
    public static final int SIZE = 10;

    /**
     * Associates each occupied coordinate with the ship that occupies it.
     */
    private final Map<Coordinate, Ship> occupiedCells;

    /**
     * Collection of ships currently placed on the board.
     */
    private final List<Ship> placedShips;

    /**
     * Set containing all coordinates that have already been targeted.
     */
    private final Set<Coordinate> shotCells;

    /**
     * Creates an empty game board.
     */
    public Board()
    {
        this.occupiedCells = new HashMap<>();
        this.placedShips = new ArrayList<>();
        this.shotCells = new HashSet<>();
    }

    /**
     * Determines whether the specified coordinate belongs to the board.
     *
     * @param coordinate the coordinate to evaluate.
     * @return {@code true} if the coordinate is inside the board;
     *         {@code false} otherwise.
     */
    public boolean isInsideBounds(Coordinate coordinate)
    {
        return coordinate.getRow() >= 0 && coordinate.getRow() < SIZE
                && coordinate.getColumn() >= 0 && coordinate.getColumn() < SIZE;
    }

    /**
     * Computes all board coordinates that would be occupied by the
     * specified ship if placed at the given origin and orientation.
     *
     * @param ship the ship whose occupied cells are calculated.
     * @param origin the starting coordinate of the ship.
     * @param orientation the ship orientation.
     * @return the list of coordinates that the ship would occupy.
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

    /**
     * Determines whether a ship can be placed at the specified position.
     *
     * @param ship the ship to be placed.
     * @param origin the starting coordinate.
     * @param orientation the orientation of the ship.
     * @return {@code true} if the ship can be placed;
     *         {@code false} otherwise.
     */
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

    /**
     * Places the specified ship on the board.
     *
     * @param ship the ship to be placed.
     * @param origin the starting coordinate of the ship.
     * @param orientation the orientation of the ship.
     * @throws PlacementException if the ship cannot be placed at the
     *                            specified position.
     */
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
     * Processes a shot fired at the specified coordinate.
     *
     * @param coordinate the coordinate where the shot is fired.
     * @return the result of the shot.
     * @throws InvalidShotException if the coordinate is outside the board
     *                              or has already been targeted.
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

    /**
     * Returns the ship occupying the specified coordinate.
     *
     * @param coordinate the coordinate to inspect.
     * @return the ship occupying the coordinate, or {@code null} if
     *         the cell is empty.
     */
    public Ship getShipAt(Coordinate coordinate)
    {
        return this.occupiedCells.get(coordinate);
    }

    /**
     * Determines whether every ship placed on the board has been sunk.
     *
     * @return {@code true} if all ships have been destroyed;
     *         {@code false} otherwise.
     */
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

    /**
     * Returns all coordinates that have already been targeted.
     *
     * @return an unmodifiable set containing all fired coordinates.
     */
    public Set<Coordinate> getShotCells()
    {
        return Collections.unmodifiableSet(this.shotCells);
    }

    /**
     * Removes all ships and recorded shots from the board.
     */
    public void clear()
    {
        this.occupiedCells.clear();
        this.placedShips.clear();
        this.shotCells.clear();
    }

    /**
     * Returns the list of ships currently placed on the board.
     *
     * @return the placed ships.
     */
    public List<Ship> getPlacedShips()
    {
        return this.placedShips;
    }
}