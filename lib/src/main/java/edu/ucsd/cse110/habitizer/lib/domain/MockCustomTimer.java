package edu.ucsd.cse110.habitizer.lib.domain;

public class MockCustomTimer extends CustomTimer {
    public static final int ADVANCE_TIME_IN_SECONDS = 15;

    public MockCustomTimer() {
        super();
    }

    public MockCustomTimer(TimerState state, long elapsedTimeInMilliseconds) {
        super(state, elapsedTimeInMilliseconds);
    }

    public void advance() {
        if (state != TimerState.RUNNING) {
            throw new IllegalStateException();
        }
        elapsedTimeInMilliseconds += (ADVANCE_TIME_IN_SECONDS * MILLISECONDS_PER_SECOND);
    }

}
