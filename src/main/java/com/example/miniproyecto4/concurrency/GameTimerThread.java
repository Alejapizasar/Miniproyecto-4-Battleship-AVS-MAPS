package com.example.miniproyecto4.concurrency;

import javafx.application.Platform;

import java.util.function.LongConsumer;

/**
 * Background thread responsible for tracking the elapsed game time.
 * The thread increments the elapsed time once every second and notifies
 * the JavaFX Application Thread through the provided callback.
 * This timer runs independently from the turn management threads because
 * it only keeps track of time and does not interact with the game boards
 * or the synchronization mechanisms used during gameplay.
 *
 * @author Alejandro Valencia Sandoval
 * @author Maria Alejandra Pizarro Sarria
 */
public class GameTimerThread extends Thread
{
    /**
     * Callback invoked every second with the current elapsed time.
     */
    private final LongConsumer onTick;

    /**
     * Indicates whether the timer thread should continue running.
     */
    private volatile boolean running;

    /**
     * Indicates whether the timer is currently paused.
     */
    private volatile boolean paused;

    /**
     * Creates a new game timer thread.
     *
     * @param onTick callback that receives the elapsed time in seconds
     *               after each timer tick.
     */
    public GameTimerThread(LongConsumer onTick)
    {
        this.onTick = onTick;
        this.running = true;
        this.paused = false;
        this.setDaemon(true);
        this.setName("game-timer-thread");
    }

    /**
     * Executes the timer loop while the thread is running.
     * The elapsed time is increased once per second unless the timer
     * is paused. Each updated value is reported on the JavaFX
     * Application Thread.
     */
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

    /**
     * Pauses the timer. No additional time is counted or reported
     * until the timer is resumed.
     */
    public void pauseTimer()
    {
        this.paused = true;
    }

    /**
     * Resumes the timer after it has been paused.
     */
    public void resumeTimer()
    {
        this.paused = false;
    }

    /**
     * Stops the timer thread and interrupts its execution if it is
     * currently waiting. This method is intended to be called when
     * the game finishes or the application is closed.
     */
    public void stopTimer()
    {
        this.running = false;
        this.interrupt();
    }
}