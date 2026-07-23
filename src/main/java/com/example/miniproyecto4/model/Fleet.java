package com.example.miniproyecto4.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Utility class responsible for creating the default fleet used in
 * a Battleship match. The generated fleet contains the predefined
 * ships and their corresponding sizes according to the game rules.
 *
 * @author Alejandro Valencia Sandoval
 * @author Maria Alejandra Pizarro Sarria
 */
public final class Fleet
{
    /**
     * Prevents the instantiation of this utility class.
     */
    private Fleet()
    {
    }

    /**
     * Creates and returns the standard fleet used during a match.
     *
     * @return a list containing all ships that compose the standard fleet.
     */
    public static List<Ship> standardFleet()
    {
        List<Ship> ships = new ArrayList<>();
        ships.add(new Ship("Aircraft Carrier", 4));  // verylargeShip
        ships.add(new Ship("Submarine", 3));
        ships.add(new Ship("Submarine", 3));
        ships.add(new Ship("Destroyer", 2));
        ships.add(new Ship("Destroyer", 2));
        ships.add(new Ship("Destroyer", 2));
        ships.add(new Ship("Frigate", 1));
        ships.add(new Ship("Frigate", 1));
        ships.add(new Ship("Frigate", 1));
        ships.add(new Ship("Frigate", 1));       // smallShip3
        return ships;
    }
}