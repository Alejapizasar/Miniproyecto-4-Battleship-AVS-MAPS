package com.example.miniproyecto4.model;

/**
 * Represents the possible orientations that a ship can have
 * when placed on the game board. The orientation determines
 * whether the ship extends horizontally or vertically from
 * its starting coordinate.
 *
 * @author Alejandro Valencia
 * @author Maria Alejandra Pizarro Sarria
 */
public enum Orientation
{
    /**
     * Indicates that the ship extends along the columns
     * of the board.
     */
    HORIZONTAL,

    /**
     * Indicates that the ship extends along the rows
     * of the board.
     */
    VERTICAL
}