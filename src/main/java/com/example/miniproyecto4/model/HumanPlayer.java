package com.example.miniproyecto4.model;

import com.example.miniproyecto4.exceptions.InvalidShotException;
import com.example.miniproyecto4.model.interfaces.Opponent;

import java.util.List;

/**
 * Wraps the board and fleet the human already set up in PlayerController,
 * exposing them through the {@link Opponent} contract so GameController
 * can treat both sides of the match the same way.
 *
 * @author Alejandro Valencia Sandoval
 * @author Maria Alejandra Pizarro Sarria
 */
public class HumanPlayer implements Opponent
{
    private final Board board;
    private final List<Ship> fleet;

    public HumanPlayer(Board board, List<Ship> fleet)
    {
        this.board = board;
        this.fleet = fleet;
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
