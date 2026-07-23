package com.example.miniproyecto4.concurrency;

import com.example.miniproyecto4.exceptions.InvalidShotException;
import com.example.miniproyecto4.model.Board;
import com.example.miniproyecto4.model.Coordinate;
import com.example.miniproyecto4.model.MachinePlayer;
import com.example.miniproyecto4.model.ShotResult;

import javafx.application.Platform;

import java.util.function.BiConsumer;

/**
 * Background thread responsible for executing a single machine turn.
 * The thread waits for a short delay to simulate the machine thinking,
 * selects a target coordinate, performs the shot on the human player's
 * board, and reports the result on the JavaFX Application Thread.
 * Synchronization is performed using a shared lock to prevent race
 * conditions between the human and machine turns.
 *
 * @author Alejandro Valencia Sandoval
 * @author Maria Alejandra Pizarro Sarria
 */
public class MachineTurnThread extends Thread
{
    /**
     * Delay in milliseconds used to simulate the machine's thinking time
     * before taking a shot.
     */
    private static final long THINKING_DELAY_MILLIS = 700;

    /**
     * Machine player responsible for selecting the next target.
     */
    private final MachinePlayer machine;

    /**
     * Board representing the human player's fleet.
     */
    private final Board humanBoard;

    /**
     * Shared lock used to synchronize the execution of player turns.
     */
    private final Object turnLock;

    /**
     * Callback invoked after the machine's shot has been resolved.
     * It receives the target coordinate and the corresponding shot result.
     */
    private final BiConsumer<Coordinate, ShotResult> onShotResolved;

    /**
     * Creates a new thread responsible for executing a single machine turn.
     *
     * @param machine the machine player that selects the target coordinate.
     * @param humanBoard the board where the machine performs its shot.
     * @param turnLock the synchronization object shared with the game controller.
     * @param onShotResolved callback executed after the shot has been processed.
     */
    public MachineTurnThread(MachinePlayer machine, Board humanBoard, Object turnLock,
                             BiConsumer<Coordinate, ShotResult> onShotResolved)
    {
        this.machine = machine;
        this.humanBoard = humanBoard;
        this.turnLock = turnLock;
        this.onShotResolved = onShotResolved;
        this.setDaemon(true);
        this.setName("machine-turn-thread");
    }

    /**
     * Executes the machine turn by waiting for the configured delay,
     * selecting a target coordinate, resolving the shot against the
     * human board, and notifying the JavaFX Application Thread with
     * the resulting coordinate and shot outcome.
     */
    @Override
    public void run()
    {
        try
        {
            Thread.sleep(THINKING_DELAY_MILLIS);
        }
        catch (InterruptedException exception)
        {
            Thread.currentThread().interrupt();
            return;
        }

        synchronized (this.turnLock)
        {
            Coordinate target = this.machine.chooseShot(this.humanBoard);
            try
            {
                ShotResult result = this.humanBoard.receiveShot(target);
                Platform.runLater(() -> this.onShotResolved.accept(target, result));
            }
            catch (InvalidShotException exception)
            {
                // RandomShotStrategy already filters out shot cells, so this
                // is only a defensive guard; nothing to report if it happens.
            }
        }
    }
}