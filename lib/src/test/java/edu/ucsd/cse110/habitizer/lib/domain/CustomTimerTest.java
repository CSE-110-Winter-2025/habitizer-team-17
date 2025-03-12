package edu.ucsd.cse110.habitizer.lib.domain;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

public class CustomTimerTest {

    @Test
    public void testStartTimer() throws InterruptedException {
        CustomTimer timer = new CustomTimer();

        timer.start();
        Thread.sleep(1050); // Wait for 1 second

        TimerState state = timer.getState();
        long elapsedTimeInMilliseconds = timer.getElapsedTimeInMilliseconds();

        assertEquals(TimerState.RUNNING, state);
        assertEquals(1000, elapsedTimeInMilliseconds);
    }

    @Test
    public void testPauseTimer() throws InterruptedException {
        CustomTimer timer = new CustomTimer(TimerState.RUNNING, 1000);

        timer.pause();
        Thread.sleep(1050); // Wait for 1 second

        TimerState state = timer.getState();
        long elapsedTimeInMilliseconds = timer.getElapsedTimeInMilliseconds();

        assertEquals(TimerState.PAUSED, state);
        assertEquals(1000, elapsedTimeInMilliseconds);
    }

    @Test
    public void testResumeTimer() throws InterruptedException {
        CustomTimer timer = new CustomTimer(TimerState.PAUSED, 1000);

        timer.resume();
        Thread.sleep(1050); // Wait for 1 second

        TimerState state = timer.getState();
        long elapsedTimeInMilliseconds = timer.getElapsedTimeInMilliseconds();

        assertEquals(TimerState.RUNNING, state);
        assertEquals(2000, elapsedTimeInMilliseconds);
    }

    @Test
    public void testStopTimer() throws InterruptedException {
        CustomTimer timer = new CustomTimer(TimerState.RUNNING, 1000);

        timer.stop();
        Thread.sleep(1050); // Wait for 1 second

        TimerState state = timer.getState();
        long elapsedTimeInMilliseconds = timer.getElapsedTimeInMilliseconds();

        assertEquals(TimerState.STOPPED, state);
        assertEquals(1000, elapsedTimeInMilliseconds);
    }

    @Test
    public void testResetTimer() throws InterruptedException {
        CustomTimer timer = new CustomTimer(TimerState.STOPPED, 1000);

        timer.reset();
        Thread.sleep(1050); // Wait for 1 second

        TimerState state = timer.getState();
        long elapsedTimeInMilliseconds = timer.getElapsedTimeInMilliseconds();

        assertEquals(TimerState.INITIAL, state);
        assertEquals(0, elapsedTimeInMilliseconds);
    }

    @Test
    public void testAdvanceTimer() {
        MockCustomTimer timer = new MockCustomTimer(TimerState.RUNNING, 0);

        timer.advance();

        long elapsedTimeInMilliseconds = timer.getElapsedTimeInMilliseconds();

        assertEquals(MockCustomTimer.ADVANCE_TIME_IN_SECONDS * CustomTimer.MILLISECONDS_PER_SECOND, elapsedTimeInMilliseconds);
    }

}