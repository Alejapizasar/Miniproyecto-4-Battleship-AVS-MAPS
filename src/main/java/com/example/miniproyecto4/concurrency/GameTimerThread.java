package com.example.miniproyecto4.concurrency;

import javafx.application.Platform;

import java.util.function.LongConsumer;

/**
 * Background thread that ticks once per second for the whole match and
 * reports the elapsed seconds back to the JavaFX Application Thread.
 * Runs independently of the turn logic (it is not guarded by the same
 * turn lock as {@link MachineTurnThread}) since it only reads a counter
 * and never touches the boards.
 *
 * @author Alejandro Valencia Sandoval
 * @author Maria Alejandra Pizarro Sarria
 */
public class GameTimerThread extends Thread
{
    private final LongConsumer onTick;
    private volatile boolean running;
    private volatile boolean paused;

    public GameTimerThread(LongConsumer onTick)
    {
        this.onTick = onTick;
        this.running = true;
        this.paused = false;
        this.setDaemon(true);
        this.setName("game-timer-thread");
    }

    @Override
    public void run()
    {
        long elapsedSeconds = 0;
        while (this.running)
        {
            try
            {
                Thread.sleep(1000);
            }
            catch (InterruptedException exception)
            {
                Thread.currentThread().interrupt();
                return;
            }

            if (this.paused)
            {
                // Skip this tick entirely: no increment, no report, so the
                // displayed clock genuinely stops instead of just freezing
                // its label while still counting underneath.
                continue;
            }

            elapsedSeconds++;
            long secondsToReport = elapsedSeconds;
            Platform.runLater(() -> this.onTick.accept(secondsToReport));
        }
    }

    /** Freezes the clock (no more ticks reported) until {@link #resumeTimer()}. */
    public void pauseTimer()
    {
        this.paused = true;
    }

    /** Resumes reporting ticks after a {@link #pauseTimer()} call. */
    public void resumeTimer()
    {
        this.paused = false;
    }

    /**
     * Stops the timer after the current tick finishes; safe to call from
     * the JavaFX Application Thread (e.g. on victory/defeat or exit).
     */
    public void stopTimer()
    {
        this.running = false;
        this.interrupt();
    }
}
