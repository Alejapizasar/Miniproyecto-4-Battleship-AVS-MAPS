package com.example.miniproyecto4.view;

/**
 * Represents the different visual states that a board cell can
 * display during a Battleship match. Each state determines the
 * graphical appearance rendered by a {@link BoardCellView}.
 *
 * @author Alejandro Valencia Sandoval
 * @author Maria Alejandra Pizarro Sarria
 */
public enum CellState
{
    /**
     * Indicates that the cell contains only water and has not
     * been targeted.
     */
    WATER,

    /**
     * Indicates that the cell contains a ship segment that has
     * not been hit.
     */
    SHIP,

    /**
     * Indicates that a shot was fired at the cell but no ship
     * was present.
     */
    MISS,

    /**
     * Indicates that the cell contains a ship segment that has
     * been successfully hit.
     */
    HIT,

    /**
     * Indicates that the ship occupying this cell has been
     * completely destroyed.
     */
    SUNK
}