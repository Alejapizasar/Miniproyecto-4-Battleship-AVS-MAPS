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
 * Represents the computer-controlled player in a Battleship match.
 * This class manages the machine's board and fleet, automatically
 * places its ships, selects target coordinates using a shooting
 * strategy, and provides the operations required by the
 * {@link Opponent} interface.
 *
 * @author Alejandro Valencia Sandoval
 * @author Maria Alejandra Pizarro Sarria
 */
public class MachinePlayer implements Opponent
{
    /**
     * Board containing the machine player's fleet.
     */
    private final Board board;

    /**
     * Fleet controlled by the machine player.
     */
    private final List<Ship> fleet;

    /**
     * Strategy used to place ships on the board.
     */
    private final ShipPlacementStrategy placementStrategy;

    /**
     * Strategy used to determine the next shot.
     */
    private final ShotStrategy shotStrategy;

    /**
     * Creates a new machine player with an empty board,
     * a standard fleet, and the default placement and
     * shooting strategies.
     */
    public MachinePlayer()
    {
        this.board = new Board();
        this.fleet = Fleet.standardFleet();
        this.placementStrategy = new RandomPlacementStrategy();
        this.shotStrategy = new RandomShotStrategy();
    }

    /**
     * Creates a machine player using a previously initialized
     * board and fleet. This constructor is primarily intended
     * for restoring a saved game.
     *
     * @param board the machine player's board.
     * @param fleet the fleet already placed on the board.
     */
    public MachinePlayer(Board board, List<Ship> fleet)
    {
        this.board = board;
        this.fleet = fleet;
        this.placementStrategy = new RandomPlacementStrategy();
        this.shotStrategy = new RandomShotStrategy();
    }

    /**
     * Places every ship in the machine player's fleet on the board
     * using the configured placement strategy.
     *
     * @throws PlacementException if one or more ships cannot be placed.
     */
    public void placeFleetRandomly() throws PlacementException
    {
        for (Ship ship : this.fleet)
        {
            this.placementStrategy.placeShip(this.board, ship, null, null);
        }
    }

    /**
     * Selects the next coordinate where the machine player
     * will fire.
     *
     * @param enemyBoard the opponent's board.
     * @return the coordinate selected for the next shot.
     */
    public Coordinate chooseShot(Board enemyBoard)
    {
        return this.shotStrategy.chooseShot(enemyBoard);
    }

    /**
     * Returns the board associated with the machine player.
     *
     * @return the machine player's board.
     */
    @Override
    public Board getBoard()
    {
        return this.board;
    }

    /**
     * Returns the fleet controlled by the machine player.
     *
     * @return the machine player's fleet.
     */
    @Override
    public List<Ship> getFleet()
    {
        return this.fleet;
    }

    /**
     * Determines whether all ships belonging to the machine
     * player have been sunk.
     *
     * @return {@code true} if the machine has lost the match;
     *         {@code false} otherwise.
     */
    @Override
    public boolean hasLost()
    {
        return this.board.areAllShipsSunk();
    }

    /**
     * Processes a shot received at the specified coordinate.
     *
     * @param coordinate the coordinate where the shot is received.
     * @return the result of the shot.
     * @throws InvalidShotException if the specified coordinate is
     *                              invalid or has already been targeted.
     */
    @Override
    public ShotResult receiveShotAt(Coordinate coordinate) throws InvalidShotException
    {
        return this.board.receiveShot(coordinate);
    }
}