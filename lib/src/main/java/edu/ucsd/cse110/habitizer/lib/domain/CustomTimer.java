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
    private String completedTime; // Added missing completedTime field

    public CustomTimer() {
        this.elapsedTime = 0;
        this.isRunning = false;
        this.isMocked = false;
        this.completedTime = "00:00"; // Initialize completedTime
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
        if (!isRunning) {
            reset(); // Ensure timer is reset before starting
            startTimer();
            isRunning = true;
        } else {
        }
    }



    public void stop() {
        if (isRunning) {
            System.out.println("🛑 Stopping Timer...");
            if (timer != null) {
                timer.cancel();
                timer.purge();
                timer = null;
            }
            isRunning = false;

            // Capture final time including fast-forward
            completedTime = getFormattedTime();
        } else {
        }
    }

    public boolean forward() {
        if (!isMocked) {
            return false;
        }


        elapsedTime += (FORWARD_SECONDS * 1000); // ✅ Add 30 seconds


        // ✅ Ensure completedTime updates even if the timer is stopped
        completedTime = getFormattedTime();

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

        completedTime = (hours > 0)
                ? String.format("%d:%02d", hours, minutes)
                : String.format("%02d:%02d", minutes, seconds);

        return completedTime;
    }

    public boolean isRunning() {
        return isRunning;
    }

    public void reset() {
        if (timer != null) {
            timer.cancel();
            timer.purge();
            timer = null; // ✅ Ensure timer is reset
        }
        elapsedTime = 0;
        isRunning = false;
        completedTime = "00:00"; // ✅ Reset completedTime
    }
}
