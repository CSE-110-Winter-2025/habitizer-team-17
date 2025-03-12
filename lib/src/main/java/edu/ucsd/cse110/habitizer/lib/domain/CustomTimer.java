package edu.ucsd.cse110.habitizer.lib.domain;

import java.util.Timer;
import java.util.TimerTask;

public class CustomTimer {
    public static final int MILLISECONDS_PER_SECOND = 1000;
    protected TimerState state;
    protected Timer timer;
    protected long elapsedTimeInMilliseconds;


    public CustomTimer() {
        reset();
    }

    public CustomTimer(TimerState state, long elapsedTimeInMilliseconds) {
        this.timer = null;
        this.state = state;
        this.elapsedTimeInMilliseconds = elapsedTimeInMilliseconds;
        if (state == TimerState.RUNNING) {
            runTimer();
        }
    }

    private void runTimer() {
        timer = new Timer();
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                elapsedTimeInMilliseconds += MILLISECONDS_PER_SECOND;
            }
        };
        // Run every second, starting after 1 second
        timer.schedule(timerTask, MILLISECONDS_PER_SECOND, MILLISECONDS_PER_SECOND);
    }

    public void start() {
        if (state != TimerState.INITIAL) {
            throw new IllegalStateException();
        }
        runTimer();
        state = TimerState.RUNNING;
    }

    public void pause() {
        if (state != TimerState.RUNNING) {
            throw new IllegalStateException();
        }
        timer.cancel();
        state = TimerState.PAUSED;
    }

    public void resume() {
        if (state != TimerState.PAUSED) {
            throw new IllegalStateException();
        }
        runTimer();
        state = TimerState.RUNNING;
    }

    public void stop() {
        if (state != TimerState.RUNNING && state != TimerState.PAUSED) {
            throw new IllegalStateException();
        }
        timer.cancel();
        state = TimerState.STOPPED;
    }

    public void reset() {
        if (timer != null) {
            timer.cancel();
        }
        state = TimerState.INITIAL;
        elapsedTimeInMilliseconds = 0;
    }

    public TimerState getState() {
        return state;
    }

    public long getElapsedTimeInMilliseconds() {
        return elapsedTimeInMilliseconds;
    }
}
