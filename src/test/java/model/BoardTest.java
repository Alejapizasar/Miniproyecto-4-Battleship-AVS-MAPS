package model;

import com.example.miniproyecto4.exceptions.InvalidShotException;
import com.example.miniproyecto4.exceptions.PlacementException;
import com.example.miniproyecto4.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link Board}: placement bounds/overlap rules and the
 * shot resolution (MISS/HIT/SUNK, duplicate/out-of-bounds shots).
 *
 * @author Alejandro Valencia Sandoval
 */
class BoardTest
{
    private Board board;

    @BeforeEach
    void setUp()
    {
        this.board = new Board();
    }

    @Test
    void placeShipInsideBoundsSucceeds() throws PlacementException
    {
        Ship frigate = new Ship("Frigate", 1);
        this.board.placeShip(frigate, new Coordinate(0, 0), Orientation.HORIZONTAL);

        assertTrue(frigate.isPlaced());
        assertEquals(1, this.board.getPlacedShips().size());
    }

    @Test
    void placeShipOutOfBoundsThrows()
    {
        Ship destroyer = new Ship("Destroyer", 2);
        assertThrows(PlacementException.class,
                () -> this.board.placeShip(destroyer, new Coordinate(0, 9), Orientation.HORIZONTAL));
    }

    @Test
    void placeShipOverlappingAnotherShipThrows() throws PlacementException
    {
        Ship first = new Ship("Frigate", 1);
        Ship second = new Ship("Frigate", 1);

        this.board.placeShip(first, new Coordinate(3, 3), Orientation.HORIZONTAL);

        assertThrows(PlacementException.class,
                () -> this.board.placeShip(second, new Coordinate(3, 3), Orientation.HORIZONTAL));
    }

    @Test
    void receiveShotOnEmptyCellIsMiss() throws InvalidShotException
    {
        ShotResult result = this.board.receiveShot(new Coordinate(5, 5));
        assertEquals(ShotResult.MISS, result);
    }

    @Test
    void receiveShotOnShipIsHitThenSunkOnLastSegment() throws PlacementException, InvalidShotException
    {
        Ship destroyer = new Ship("Destroyer", 2);
        this.board.placeShip(destroyer, new Coordinate(1, 1), Orientation.HORIZONTAL);

        ShotResult first = this.board.receiveShot(new Coordinate(1, 1));
        ShotResult second = this.board.receiveShot(new Coordinate(1, 2));

        assertEquals(ShotResult.HIT, first);
        assertEquals(ShotResult.SUNK, second);
        assertTrue(destroyer.isSunk());
    }

    @Test
    void receiveShotOnSameCellTwiceThrows() throws InvalidShotException
    {
        this.board.receiveShot(new Coordinate(2, 2));
        assertThrows(InvalidShotException.class, () -> this.board.receiveShot(new Coordinate(2, 2)));
    }

    @Test
    void receiveShotOutOfBoundsThrows()
    {
        assertThrows(InvalidShotException.class, () -> this.board.receiveShot(new Coordinate(-1, 0)));
    }

    @Test
    void areAllShipsSunkFalseWhenBoardHasNoShips()
    {
        assertFalse(this.board.areAllShipsSunk());
    }
}
