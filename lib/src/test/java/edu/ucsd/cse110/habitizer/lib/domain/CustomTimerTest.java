package edu.ucsd.cse110.habitizer.lib.domain;
import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

public class CustomTimerTest {
    private CustomTimer timer;

    @Before
    public void setUp() {
        timer = new MockTimer();
    }

    @Test
    public void testStartTimer() throws InterruptedException {
        timer.start();
        Thread.sleep(2050); // Wait for 2 seconds
        timer.stop();

        long elapsedTime = timer.getElapsedTimeInMilliSeconds(); // Store as a String
        assertNotEquals(0, elapsedTime/CustomTimer.MILLISECONDS_PER_SECOND); // Timer should not be at "00:00"
    }


    @Test
    public void testStopTimer() throws InterruptedException {
        timer.start();
        Thread.sleep(2050); // Wait for 2 seconds
        timer.stop();

        // Ensure time is stored correctly
        assertNotEquals(0, timer.getElapsedTimeInMilliSeconds());

        // Ensure stopping the timer prevents further increments
        Thread.sleep(2000);
        assertEquals(2000, timer.getElapsedTimeInMilliSeconds());
    }

    @Test
    public void testFastForward() {
        // Enable mock mode
        MockTimer t = (MockTimer)timer;
        t.forward();
        t.forward();

        // Timer should advance by 30 seconds
        assertEquals(60*CustomTimer.MILLISECONDS_PER_SECOND, t.getElapsedTimeInMilliSeconds());
    }

}