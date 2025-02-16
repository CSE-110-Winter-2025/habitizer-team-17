package edu.ucsd.cse110.habitizer.lib.domain;

import java.util.Timer;
import java.util.TimerTask;

public class CustomTimer {
    protected Timer timer;
    protected long elapsedTimeMilliSeconds;
    protected boolean isRunning;
    public static final int MILLISECONDS_PER_SECOND = 1000;
    protected TimerTask timerTask;


    public CustomTimer() {
        this.elapsedTimeMilliSeconds = 0;
        this.isRunning = false;
    }

    private void startTimer() {
        if (timer != null) {
            timer.cancel();
            timer.purge();
        }
        timer = new Timer();
        timerTask = new TimerTask() {
            @Override
            public void run() {
                elapsedTimeMilliSeconds += MILLISECONDS_PER_SECOND;
            }
        };
        timer.schedule(timerTask, MILLISECONDS_PER_SECOND, MILLISECONDS_PER_SECOND); // Schedule with a delay of 1s, then repeat every 1s
    }

    public void start() {
        if (!isRunning) {
            startTimer(); // Start from the current elapsed time
            isRunning = true;
        }
    }

    public boolean isRunning() {
        return isRunning;
    }


    public long getElapsedTimeInMilliSeconds(){
        return elapsedTimeMilliSeconds;
    }

    public void stop() {
        if (isRunning) {
            if (timer != null) {
                timer.cancel();
                timer.purge();
                timer = null;
            }
            isRunning = false;
        }
    }

    public void reset() {
        if (timer != null) {
            timer.cancel();
            timer.purge();
            timer = null;
        }
        elapsedTimeMilliSeconds = 0;
        isRunning = false;
    }
}
