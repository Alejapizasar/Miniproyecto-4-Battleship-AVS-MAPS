package com.example.miniproyecto4.view;

/**
 * Represents the possible visual states of a single cell on a game board.
 * Used by {@link BoardCellView} to decide which 2D graphic to render.
 *
 * @author Alejandro Valencia Sandoval
 */
public enum CellState
{
    // Untouched water, nothing has happened here yet.
    WATER,

    // A ship segment sits here and has not been hit (only rendered on the
    // owner's own board, never on the enemy board unless in debug/verify mode).
    SHIP,

    // A shot was fired here and there was no ship: water splash / X mark.
    MISS,

    // A shot hit a ship segment that belongs to a ship with more than
    // one remaining segment.
    HIT,

    // The last remaining segment of a ship was hit: the whole ship is sunk.
    SUNK
}
