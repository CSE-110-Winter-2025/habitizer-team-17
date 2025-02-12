package edu.ucsd.cse110.habitizer.lib.domain;
import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;
public class CustomerTimerTest {
    private CustomTimer timer;

    @Before
    public void setUp() {
        timer = new CustomTimer();
    }

    @Test
    public void testStartTimer() throws InterruptedException {
        timer.start();
        Thread.sleep(2000); // Wait for 2 seconds
        timer.stop();

        String elapsed = timer.getFormattedTime(); // Store as a String
        assertNotEquals("00:00", elapsed); // Timer should not be at "00:00"
    }


    @Test
    public void testStopTimer() throws InterruptedException {
        timer.start();
        Thread.sleep(2000); // Wait for 2 seconds
        timer.stop();
        String stoppedTime = timer.getFormattedTime();

        // Ensure time is stored correctly
        assertNotEquals("0:00", stoppedTime);

        // Ensure stopping the timer prevents further increments
        Thread.sleep(2000);
        assertEquals(stoppedTime, timer.getFormattedTime());
    }

    @Test
    public void testFastForward() {
        timer.setMockMode(true); // Enable mock mode
        timer.forward();

        // Timer should advance by 30 seconds
        assertEquals("00:30", timer.getFormattedTime());
    }

}