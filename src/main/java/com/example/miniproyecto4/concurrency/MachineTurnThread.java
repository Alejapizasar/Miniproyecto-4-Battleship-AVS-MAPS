package com.example.miniproyecto4.concurrency;

import com.example.miniproyecto4.exceptions.InvalidShotException;
import com.example.miniproyecto4.model.Board;
import com.example.miniproyecto4.model.Coordinate;
import com.example.miniproyecto4.model.MachinePlayer;
import com.example.miniproyecto4.model.ShotResult;

import javafx.application.Platform;

import java.util.function.BiConsumer;

/**
 * Runs the machine's single next shot on a background thread: a short
 * "thinking" delay via {@code Thread.sleep}, then resolves the shot
 * against the human board inside a {@code synchronized} block guarded by
 * a lock shared with the controller, so a human click cannot race with
 * the machine's turn. The result is handed back to the JavaFX
 * Application Thread through {@link Platform#runLater}, since JavaFX
 * nodes may only be touched from that thread.
 *
 * <p>One instance resolves exactly one shot; GameController starts a new
 * one for every consecutive hit the machine scores.</p>
 *
 * @author Alejandro Valencia Sandoval
 * @author Maria Alejandra Pizarro Sarria
 */
public class MachineTurnThread extends Thread
{
    private static final long THINKING_DELAY_MILLIS = 700;

    private final MachinePlayer machine;
    private final Board humanBoard;
    private final Object turnLock;
    private final BiConsumer<Coordinate, ShotResult> onShotResolved;

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
