package com.example.miniproyecto4.model;

import java.io.Serializable;
import java.util.Objects;

/**
 * Represents an immutable coordinate on the game board.
 * A coordinate identifies the position of a single cell by its row
 * and column values. Instances of this class are commonly used as
 * keys in collections that store board information.
 *
 * @author Alejandro Valencia Sandoval
 * @author Maria Alejandra Pizarro Sarria
 */
public final class Coordinate implements Serializable
{
    /**
     * Serialization identifier for this class.
     */
    private static final long serialVersionUID = 1L;

    /**
     * Row index of the coordinate.
     */
    private final int row;

    /**
     * Column index of the coordinate.
     */
    private final int column;

    /**
     * Creates a new coordinate with the specified row and column.
     *
     * @param row the row index.
     * @param column the column index.
     */
    public Coordinate(int row, int column)
    {
        this.row = row;
        this.column = column;
    }

    /**
     * Returns the row index of this coordinate.
     *
     * @return the row value.
     */
    public int getRow()
    {
        return this.row;
    }

    /**
     * Returns the column index of this coordinate.
     *
     * @return the column value.
     */
    public int getColumn()
    {
        return this.column;
    }

    /**
     * Compares this coordinate with another object for equality.
     * Two coordinates are considered equal if they have the same
     * row and column values.
     *
     * @param other the object to compare with.
     * @return {@code true} if both objects represent the same
     *         coordinate; {@code false} otherwise.
     */
    @Override
    public boolean equals(Object other)
    {
        if (this == other)
        {
            return true;
        }
        if (!(other instanceof Coordinate))
        {
            return false;
        }
        Coordinate that = (Coordinate) other;
        return this.row == that.row && this.column == that.column;
    }

    /**
     * Returns the hash code associated with this coordinate.
     *
     * @return the hash code value.
     */
    @Override
    public int hashCode()
    {
        return Objects.hash(this.row, this.column);
    }

    /**
     * Returns a string representation of this coordinate.
     *
     * @return a string containing the row and column values.
     */
    @Override
    public String toString()
    {
        return "(" + this.row + ", " + this.column + ")";
    }
}