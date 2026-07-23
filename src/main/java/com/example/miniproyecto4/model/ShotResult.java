package com.example.miniproyecto4.model;

/**
 * Represents the possible outcomes of a shot fired during a Battleship
 * match. Each constant indicates the result obtained after targeting
 * a coordinate on the game board.
 *
 * @author Alejandro Valencia Sandoval
 * @author Maria Alejandra Pizarro Sarria
 */
public enum ShotResult
{
    /**
     * Indicates that the targeted coordinate does not contain a ship.
     */
    MISS,

    /**
     * Indicates that a ship has been successfully hit but has not
     * yet been completely destroyed.
     */
    HIT,

    /**
     * Indicates that the shot destroyed the final remaining segment
     * of a ship, causing it to sink.
     */
    SUNK
}