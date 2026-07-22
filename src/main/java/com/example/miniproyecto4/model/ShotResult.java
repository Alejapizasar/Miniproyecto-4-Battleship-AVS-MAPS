package com.example.miniproyecto4.model;

/**
 * Outcome of firing at a single coordinate on a {@link Board}. Kept in the
 * model package (as opposed to {@code view.CellState}) so domain logic
 * never depends on the JavaFX layer.
 *
 * @author Alejandro Valencia Sandoval
 */
public enum ShotResult
{
    // No ship occupied the coordinate.
    MISS,

    // A ship was hit but still has at least one un-hit segment left.
    HIT,

    // The hit finished off every segment of the ship: it is now sunk.
    SUNK
}
