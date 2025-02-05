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
            new Task("Shower", 0),
            new Task("Brush Teeth", 1),
            new Task("Dress", 2),
            new Task("Make Coffee", 3),
            new Task("Make Lunch", 4),
            new Task("Dinner Prep", 5),
            new Task("Pack Bag", 6)
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
}
