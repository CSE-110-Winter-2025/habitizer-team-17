package edu.ucsd.cse110.habitizer.lib.domain;

import static org.junit.Assert.*;

import org.junit.Test;

public class ActiveTaskTest {
    @Test
    public void testGetters() {
        var task = new Task(0, "task");
        var activeTask = new ActiveTask(task, false,0);
        assertEquals(task, activeTask.task());
        assertFalse(activeTask.checked());
    }

    @Test
    public void testWithChecked() {
        var task = new Task(0, "task");
        var activeTask = new ActiveTask(task, false,0);
        var newActiveTask = activeTask.withChecked(true,0);
        assertTrue(newActiveTask.checked());
    }
}
