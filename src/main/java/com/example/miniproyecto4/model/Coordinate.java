package com.example.miniproyecto4.model;
import java.util.Objects;

/**
 * Immutable pair of (row, column) identifying a single cell on a 10x10
 * board. Used as a Map key throughout the project, so equals/hashCode
 * are based on value, not identity.
 *
 * @author Alejandro Valencia Sandoval
 */
public final class Coordinate
{
    private final int row;
    private final int column;

    public Coordinate(int row, int column)
    {
        this.row = row;
        this.column = column;
    }

    public int getRow()
    {
        return this.row;
    }

    public int getColumn()
    {
        return this.column;
    }

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

    @Override
    public int hashCode()
    {
        return Objects.hash(this.row, this.column);
    }

    @Override
    public String toString()
    {
        return "(" + this.row + ", " + this.column + ")";
    }
}