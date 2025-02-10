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

    public CustomTimer() {
        this.elapsedTime = 0;
        this.isRunning = false;
        this.isMocked = false;
    }

    private void startTimer() {
        timer = new Timer();
        timerTask = new TimerTask() {
            @Override
            public void run() {
                elapsedTime += 1000;
            }
        };
        timer.scheduleAtFixedRate(timerTask, 0, 1000);
    }

    public void start() {
        if (!isRunning && !isMocked) {
            reset(); // Reset elapsed time
            startTimer(); // Ensure a new timer is created
            isRunning = true;
        }
    }


    public void stop() {
        if (isRunning) {
            timer.cancel();
            timer.purge();
            isRunning = false;
        }
    }

    public boolean forward() {
        if (!isMocked) {
            return false;
        }
        elapsedTime += (FORWARD_SECONDS * 1000);
        return true;
    }

    public void setMockMode(boolean mock) {
        if (mock != isMocked) {
            stop();
            isMocked = mock;
        }
    }

    public String getFormattedTime() {
        long totalSeconds = elapsedTime / 1000;
        long hours = totalSeconds / 3600;
        long minutes = (totalSeconds % 3600) / 60;
        long seconds = totalSeconds % 60;

        if (hours > 0) {
            return String.format("%d:%02d", hours, minutes);
        } else {
            return String.format("%d:%02d", minutes, seconds);
        }
    }

    public boolean isRunning() {
        return isRunning;
    }

    public void reset() {
        if (timer != null) {
            timer.cancel(); // Stop the existing timer
            timer.purge();
        }
        elapsedTime = 0;
        isRunning = false; // Ensure it's not marked as running
    }

}