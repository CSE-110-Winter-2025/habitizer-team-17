package edu.ucsd.cse110.habitizer.lib.domain;

import static org.junit.Assert.*;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

public class RoutineTest {
    public final static List<Task> tasks = List.of(
            new Task("Shower", 0, 0),
            new Task("Brush Teeth", 1, 0),
            new Task("Dress", 2, 0),
            new Task("Make Coffee", 3, 0),
            new Task("Make Lunch", 4, 0),
            new Task("Dinner Prep", 5, 0),
            new Task("Pack Bag", 6, 0),
            new Task("Pack Balls", 7, 1)
    );
    public final static Routine expectedRoutine = new Routine(tasks, "expectedRoutine", 0);

    @Test
    public void getExpectedName() {
        assertEquals("expectedRoutine", expectedRoutine.getName());
    }

    @Test
    public void getExpectedId() {
        Integer expected = 0;
        assertEquals(expected, expectedRoutine.id());
    }

    @Test
    public void getTaskList() {
        assertEquals(tasks, expectedRoutine.getTasks());
    }

    @Test
    public void nullNameTest() {
        assertThrows(IllegalArgumentException.class, () -> new Routine(tasks,null, 2));
    }

    @Test
    public void emptyNameTest() {
        assertThrows(IllegalArgumentException.class, () -> new Routine(tasks,"", 2));
    }

    @Test
    public void nullTasksTest() {
        assertThrows(IllegalArgumentException.class, () -> new Routine(null,"test", 2));
    }
}
