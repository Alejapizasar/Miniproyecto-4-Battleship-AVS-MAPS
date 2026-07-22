package com.example.miniproyecto4.model;

import com.example.miniproyecto4.exceptions.InvalidShotException;
import com.example.miniproyecto4.exceptions.PlacementException;
import com.example.miniproyecto4.model.interfaces.Opponent;
import com.example.miniproyecto4.model.interfaces.ShipPlacementStrategy;
import com.example.miniproyecto4.model.interfaces.ShotStrategy;
import com.example.miniproyecto4.strategy.RandomPlacementStrategy;
import com.example.miniproyecto4.strategy.RandomShotStrategy;

import java.util.List;

/**
 * The computer opponent: places its own fleet randomly and picks shots
 * through {@link ShotStrategy}, so the "AI" is really just a strategy
 * object that can be swapped for a smarter one later.
 *
 * @author Alejandro Valencia Sandoval
 */
public class MachinePlayer implements Opponent
{
    private final Board board;
    private final List<Ship> fleet;
    private final ShipPlacementStrategy placementStrategy;
    private final ShotStrategy shotStrategy;

    public MachinePlayer()
    {
        this.board = new Board();
        this.fleet = Fleet.standardFleet();
        this.placementStrategy = new RandomPlacementStrategy();
        this.shotStrategy = new RandomShotStrategy();
    }

    /**
     * Rebuilds a machine opponent from an already-placed board and fleet
     * (as restored from a saved game), skipping random placement entirely
     * since the ships are already positioned.
     *
     * @param board the machine's board, already populated with its fleet
     * @param fleet the same ships already placed on that board
     */
    public MachinePlayer(Board board, List<Ship> fleet)
    {
        this.board = board;
        this.fleet = fleet;
        this.placementStrategy = new RandomPlacementStrategy();
        this.shotStrategy = new RandomShotStrategy();
    }

    /**
     * Places every ship of the machine's fleet at a random valid spot.
     * Call this once, right after construction.
     *
     * @throws PlacementException if a spot could not be found for some ship
     *                            (should not normally happen; RandomPlacementStrategy retries)
     */
    public void placeFleetRandomly() throws PlacementException
    {
        for (Ship ship : this.fleet)
        {
            this.placementStrategy.placeShip(this.board, ship, null, null);
        }
    }

    /**
     * @param enemyBoard the human's board, to know which cells are still free
     * @return the coordinate the machine will fire at next
     */
    public Coordinate chooseShot(Board enemyBoard)
    {
        return this.shotStrategy.chooseShot(enemyBoard);
    }

    @Override
    public Board getBoard()
    {
        return this.board;
    }

    @Override
    public List<Ship> getFleet()
    {
        return this.fleet;
    }

    @Override
    public boolean hasLost()
    {
        return this.board.areAllShipsSunk();
    }

    @Override
    public ShotResult receiveShotAt(Coordinate coordinate) throws InvalidShotException
    {
        return this.board.receiveShot(coordinate);
    }
}
