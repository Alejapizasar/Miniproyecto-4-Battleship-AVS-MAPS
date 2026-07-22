package model;

import com.example.miniproyecto4.model.Board;
import com.example.miniproyecto4.model.Coordinate;
import com.example.miniproyecto4.model.Orientation;
import com.example.miniproyecto4.model.Ship;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link Ship}: placement state and hit/sunk tracking,
 * independent of {@link Board} (uses the package-private assignCells
 * indirectly is not possible from here, so these go through a real
 * Board placement instead, which is how the class is used in practice).
 *
 * @author Alejandro Valencia Sandoval
 */
class ShipTest
{
    @Test
    void newShipIsNotPlaced()
    {
        Ship ship = new Ship("Frigate", 1);
        assertFalse(ship.isPlaced());
    }

    @Test
    void placedShipReportsItsCells() throws com.example.miniproyecto4.exceptions.PlacementException
    {
        Board board = new Board();
        Ship submarine = new Ship("Submarine", 3);

        board.placeShip(submarine, new Coordinate(4, 4), Orientation.VERTICAL);

        List<Coordinate> expectedCells = List.of(
                new Coordinate(4, 4), new Coordinate(5, 4), new Coordinate(6, 4));
        assertEquals(expectedCells, submarine.getCells());
        assertTrue(submarine.isPlaced());
    }

    @Test
    void oneCellFrigateSinksOnFirstHit() throws com.example.miniproyecto4.exceptions.PlacementException
    {
        Board board = new Board();
        Ship frigate = new Ship("Frigate", 1);
        board.placeShip(frigate, new Coordinate(0, 0), Orientation.HORIZONTAL);

        frigate.registerHit(new Coordinate(0, 0));

        assertTrue(frigate.isSunk());
    }

    @Test
    void largerShipNotSunkUntilEverySegmentIsHit() throws com.example.miniproyecto4.exceptions.PlacementException
    {
        Board board = new Board();
        Ship destroyer = new Ship("Destroyer", 2);
        board.placeShip(destroyer, new Coordinate(2, 2), Orientation.HORIZONTAL);

        destroyer.registerHit(new Coordinate(2, 2));
        assertFalse(destroyer.isSunk());

        destroyer.registerHit(new Coordinate(2, 3));
        assertTrue(destroyer.isSunk());
    }

    @Test
    void clearPlacementResetsCellsAndHits() throws com.example.miniproyecto4.exceptions.PlacementException
    {
        Board board = new Board();
        Ship frigate = new Ship("Frigate", 1);
        board.placeShip(frigate, new Coordinate(7, 7), Orientation.HORIZONTAL);
        frigate.registerHit(new Coordinate(7, 7));

        frigate.clearPlacement();

        assertFalse(frigate.isPlaced());
        assertFalse(frigate.isSunk());
    }
}
