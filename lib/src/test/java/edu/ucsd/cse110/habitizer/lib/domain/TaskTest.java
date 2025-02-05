package edu.ucsd.cse110.habitizer.lib.domain;

import static org.junit.Assert.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import org.junit.Test;

public class TaskTest {

    Task TASK_A = new Task("A", 1);

    @Test
    public void getNameTest()  {
        var expected = "A";
        assertEquals(expected, TASK_A.getName());
    }

    @Test
    public void setNameTest() {
        var expected = "B";
        TASK_A.setName("B");
        assertEquals(expected, TASK_A.getName());
        TASK_A.setName("A");
    }

    @Test
    public void getIdTest()  {
        Integer expected = 1;
        assertEquals(expected, TASK_A.id());
    }

    @Test
    public void nullNameTest() {
        assertThrows(IllegalArgumentException.class, () -> new Task(null, 2));
    }

    @Test
    public void emptyNameTest() {
        assertThrows(IllegalArgumentException.class, () -> new Task("", 2));
    }
}

/*

 */