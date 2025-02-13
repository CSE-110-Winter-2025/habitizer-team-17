package edu.ucsd.cse110.habitizer.lib.domain;

import static org.junit.Assert.*;

import org.junit.Test;

import java.util.List;

public class RoutineTest {
    public final static List<Task> tasks = List.of(
            new Task(0,"Shower"),
            new Task(1, "Brush Teeth"),
            new Task(2,"Dress"),
            new Task(3,"Make Coffee"),
            new Task(4,"Make Lunch"),
            new Task(5,"Dinner Prep"),
            new Task(6,"Pack Bag")
    );
    public final static Routine expectedRoutine = new Routine(0, "expectedRoutine", tasks);

    @Test
    public void getExpectedName() {
        assertEquals("expectedRoutine", expectedRoutine.name());
    }

    @Test
    public void getExpectedId() {
        Integer expected = 0;
        assertEquals(expected, expectedRoutine.id());
    }

    @Test
    public void getTaskList() {
        assertEquals(tasks, expectedRoutine.tasks());
    }

    @Test
    public void nullNameTest() {
        assertThrows(IllegalArgumentException.class, () -> new Routine(2, null,tasks));
    }

    @Test
    public void emptyNameTest() {
        assertThrows(IllegalArgumentException.class, () -> new Routine(2,"",tasks));
    }

    @Test
    public void nullTasksTest() {
        assertThrows(IllegalArgumentException.class, () -> new Routine(2,"test", null));
    }
}
