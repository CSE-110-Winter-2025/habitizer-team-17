package edu.ucsd.cse110.habitizer.lib.domain;

public class MockTimer extends CustomTimer {

    private final int FORWARD_SECONDS = 30;

    public void forward() {
        elapsedTimeMilliSeconds += (FORWARD_SECONDS * MILLISECONDS_PER_SECOND);
    }

}
