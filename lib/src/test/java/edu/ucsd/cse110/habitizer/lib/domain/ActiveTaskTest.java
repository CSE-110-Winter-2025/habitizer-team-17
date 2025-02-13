package edu.ucsd.cse110.habitizer.lib.domain;

import static org.junit.Assert.*;

import org.junit.Test;

public class ActiveTaskTest {
    @Test
    public void testGetters() {
        var task = new Task(0, "task");
        var activeTask = new ActiveTask(task, false);
        assertEquals(task, activeTask.task());
        assertFalse(activeTask.checked());
    }

    @Test
    public void testWithChecked() {
        var task = new Task(0, "task");
        var activeTask = new ActiveTask(task, false);
        var newActiveTask = activeTask.withChecked(true);
        assertTrue(newActiveTask.checked());
    }
}
