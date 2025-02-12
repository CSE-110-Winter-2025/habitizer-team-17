package edu.ucsd.cse110.habitizer.lib.domain;
import java.util.Timer;
import java.util.TimerTask;

public class CustomTimer {
    private Timer timer;
    private long elapsedTime;
    private boolean isRunning;
    private boolean isMocked;
    private final int FORWARD_SECONDS = 30;
    private final int Final_Seconds = 1000;
    private TimerTask timerTask;
    private String completedTime;
    public CustomTimer() {
        this.elapsedTime = 0;
        this.isRunning = false;
        this.isMocked = false;
        this.completedTime = "00:00";
    }

    private void startTimer() {
        timer = new Timer();
        timerTask = new TimerTask() {
            @Override
            public void run() {
                elapsedTime += Final_Seconds;
            }
        };
        timer.scheduleAtFixedRate(timerTask, 0, 1000);
    }

    public void start() {
        if (!isRunning) {
            reset();
            startTimer();
            isRunning = true;
        } else {
        }
    }



    public void stop() {
        if (isRunning) {
            if (timer != null) {
                timer.cancel();
                timer.purge();
                timer = null;
            }
            isRunning = false;
            completedTime = getFormattedTime();
        } else {
        }
    }

    public boolean forward() {
        if (!isMocked) {
            return false;
        }


        elapsedTime += (FORWARD_SECONDS * Final_Seconds);


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
        long totalSeconds = elapsedTime / Final_Seconds;
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
            timer = null;
        }
        elapsedTime = 0;
        isRunning = false;
        completedTime = "00:00";
    }
}