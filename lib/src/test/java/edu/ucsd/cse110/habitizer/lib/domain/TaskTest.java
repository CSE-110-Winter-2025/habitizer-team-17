package edu.ucsd.cse110.habitizer.lib.domain;

import static org.junit.Assert.*;
import static org.hamcrest.MatcherAssert.assertThat;

import org.junit.Before;
import org.junit.Test;

public class TaskTest {

    Task TASK_A;

    @Before
    public void setup() {
        TASK_A = new Task(1,"A");
    }

    @Test
    public void getNameTest()  {
        var expected = "A";
        assertEquals(expected, TASK_A.name());
    }

    @Test
    public void withNameTest() {
        var expected = "B";
        var newTask = TASK_A.withName("B");
        assertEquals(expected, newTask.name());
    }

    @Test
    public void getIdTest()  {
        Integer expected = 1;
        assertEquals(expected, TASK_A.id());
    }

    @Test
    public void nullNameTest() {
        assertThrows(IllegalArgumentException.class, () -> new Task(2,null));
    }

    @Test
    public void emptyNameTest() {
        assertThrows(IllegalArgumentException.class, () -> new Task(2,""));
    }
}

/*

 */