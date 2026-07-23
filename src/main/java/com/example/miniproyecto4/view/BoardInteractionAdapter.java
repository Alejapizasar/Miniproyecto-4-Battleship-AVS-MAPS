package com.example.miniproyecto4.view;

import com.example.miniproyecto4.model.Coordinate;

/**
 * Abstract adapter class that provides empty implementations of the
 * methods defined by {@link BoardInteractionListener}. Subclasses may
 * override only the callbacks they require, avoiding the need to
 * implement every method of the interface.
 *
 * @author Alejandro Valencia Sandoval
 * @author Maria Alejandra Pizarro Sarria
 */
public abstract class BoardInteractionAdapter implements BoardInteractionListener
{
    /**
     * Called when a board cell is clicked.
     * This default implementation performs no action.
     *
     * @param coordinate the coordinate of the clicked cell.
     */
    @Override
    public void onCellClicked(Coordinate coordinate)
    {
        // Default: do nothing. Override where a click matters.
    }

    /**
     * Called when the mouse pointer enters a board cell.
     * This default implementation performs no action.
     *
     * @param coordinate the coordinate of the hovered cell.
     */
    @Override
    public void onCellHoverEntered(Coordinate coordinate)
    {
        // Default: do nothing. Override where hover feedback matters.
    }

    /**
     * Called when the mouse pointer leaves a board cell.
     * This default implementation performs no action.
     *
     * @param coordinate the coordinate of the cell.
     */
    @Override
    public void onCellHoverExited(Coordinate coordinate)
    {
        // Default: do nothing.
    }
}