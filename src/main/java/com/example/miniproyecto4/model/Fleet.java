package com.example.miniproyecto4.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Factory for the standard set of ships used in a match. Adjust the sizes
 * here once you decide the final rules (right now they are a guess based
 * on the ImageView fx:id names already in PlayerView.fxml).
 *
 * @author Alejandro Valencia Sandoval
 */
public final class Fleet
{
    private Fleet()
    {
    }

    public static List<Ship> standardFleet()
    {
        List<Ship> ships = new ArrayList<>();
        ships.add(new Ship("Aircraft Carrier", 4));  // verylargeShip
        ships.add(new Ship("Battleship", 4));        // largeShip
        ships.add(new Ship("Battleship", 4));         // largeShip2
        ships.add(new Ship("Patrol Boat", 1));        // mediumsizedShip
        ships.add(new Ship("Destroyer", 3));          // smallShip
        ships.add(new Ship("Destroyer", 3));          // smallShip2
        ships.add(new Ship("Destroyer", 3));          // smallShip3
        return ships;
    }
}