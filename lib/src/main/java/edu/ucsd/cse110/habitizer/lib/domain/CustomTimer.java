package edu.ucsd.cse110.habitizer.lib.domain;
import java.util.Timer;
import java.util.TimerTask;

public class CustomTimer {
    private Timer timer;
    private long elapsedTime;
    private boolean isRunning;
    private boolean isMocked;
    private final int FORWARD_SECONDS = 30;
    private TimerTask timerTask;
    private TimerUpdateListener updateListener;

    // Interface for updating UI
    public interface TimerUpdateListener {
        void onTimerUpdate(String time);
    }

    public CustomTimer(TimerUpdateListener listener) {
        this.elapsedTime = 0;
        this.isRunning = false;
        this.isMocked = false;
        this.updateListener = listener;
    }

    /**
     * Starts the timer
     */
    private void startTimer() {
        timer = new Timer();
        timerTask = new TimerTask() {
            @Override
            public void run() {
                elapsedTime += 1000; // Increment by 1 second (1000ms)
                if (updateListener != null) {
                    updateListener.onTimerUpdate(getFormattedTime());
                }
            }
        };
        timer.scheduleAtFixedRate(timerTask, 0, 1000); // Run every second
    }

    /**
     * Starts the timer if it's not already running
     */
    public void start() {
        if (!isRunning && !isMocked) {
            startTimer();
            isRunning = true;
        }
    }

    /**
     * Stops the timer
     */
    public void stop() {
        if (isRunning) {
            timer.cancel();
            timer.purge();
            isRunning = false;
        }
    }

    /**
     * Advances the timer by 30 seconds (only works in mock mode)
     * @return boolean indicating if the operation was successful
     */
    public boolean forward() {
        if (!isMocked) {
            return false;
        }
        elapsedTime += (FORWARD_SECONDS * 1000); // Add 30 seconds
        if (updateListener != null) {
            updateListener.onTimerUpdate(getFormattedTime());
        }
        return true;
    }

    /**
     * Enables or disables mock mode
     * @param mock boolean to set mock mode
     */
    public void setMockMode(boolean mock) {
        if (mock != isMocked) {
            stop(); // Stop the timer when switching modes
            isMocked = mock;
        }
    }

    /**
     * Gets formatted time string in HH:MM or MM:SS format depending on duration
     * If time is less than an hour: shows "MM:SS"
     * If time is more than an hour: shows "HH:MM"
     * @return String representing elapsed time
     */
    public String getFormattedTime() {
        long totalSeconds = elapsedTime / 1000;
        long hours = totalSeconds / 3600;
        long minutes = (totalSeconds % 3600) / 60;
        long seconds = totalSeconds % 60;

        if (hours > 0) {
            // Format as HH:MM for times over an hour
            return String.format("%d:%02d", hours, minutes);
        } else {
            // Format as MM:SS for times under an hour
            return String.format("%d:%02d", minutes, seconds);
        }
    }

    /**
     * Resets the timer to 0
     */
    public void reset() {
        stop();
        elapsedTime = 0;
        if (updateListener != null) {
            updateListener.onTimerUpdate("0:00");
        }
    }

    /**
     * Cleans up timer resources when done
     */
    public void cleanup() {
        if (timer != null) {
            timer.cancel();
            timer.purge();
        }
    }
}
