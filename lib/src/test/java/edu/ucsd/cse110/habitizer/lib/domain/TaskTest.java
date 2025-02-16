package edu.ucsd.cse110.habitizer.lib.domain;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

public class TaskTest {

    private Task task;

    @Before
    public void setup() {
        task = new Task(0, "task");
    }

    @Test
    public void testGetters() {
        assertEquals(Integer.valueOf(0), task.id());
        assertEquals("task", task.name());
    }

    @Test
    public void testWithId() {
        var expected = new Task(2, "task");
        var actual = task.withId(2);
        assertEquals(expected, actual);
    }

    @Test
    public void withNameTest() {
        var expected = new Task(0, "a task");
        var actual = task.withName("a task");
        assertEquals(expected, actual);
    }
}