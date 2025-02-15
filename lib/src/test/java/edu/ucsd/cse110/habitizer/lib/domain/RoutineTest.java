package edu.ucsd.cse110.habitizer.lib.domain;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import java.util.List;

public class RoutineTest {
    private List<Task> tasks;
    private Routine routine;

    @Before
    public void setup() {
        tasks = List.of(
                new Task(0, "Shower"),
                new Task(1, "Brush Teeth"),
                new Task(2, "Dress"),
                new Task(3, "Make Coffee"),
                new Task(4, "Make Lunch"),
                new Task(5, "Dinner Prep"),
                new Task(6, "Pack Bag")
        );

        routine = new Routine(0, "Morning", tasks, 45, 1);
    }

    @Test
    public void testGetters() {
        assertEquals(Integer.valueOf(0), routine.id());
        assertEquals("Morning", routine.name());
        assertEquals(tasks, routine.tasks());
        assertEquals(Integer.valueOf(45), routine.goalTime());
        assertEquals(Integer.valueOf(1), routine.sortOrder());
    }

    @Test
    public void testWithId() {
        var expected = new Routine(2,"Morning", tasks, 45, 1);
        var actual = routine.withId(2);
        assertEquals(expected, actual);
    }

    @Test
    public void testWithName() {
        var expected = new Routine(0,"Evening", tasks, 45, 1);
        var actual = routine.withName("Evening");
        assertEquals(expected, actual);
    }

    @Test
    public void testWithTasks() {
        List<Task> expectedTasks = List.of(new Task(7, "Pack Balls"));
        var expected = new Routine(0,"Morning", expectedTasks, 45, 1);
        var actual = routine.withTasks(expectedTasks);
        assertEquals(expected, actual);
    }

    @Test
    public void testWithGoalTime() {
        var expected = new Routine(0,"Morning", tasks, 60, 1);
        var actual = routine.withGoalTime(60);
        assertEquals(expected, actual);
    }

    @Test
    public void testWithSortOrder() {
        var expected = new Routine(0,"Morning", tasks, 45, 3);
        var actual = routine.withSortOrder(3);
        assertEquals(expected, actual);
    }

    @Test
    public void testEquals() {
        var routine1 = new Routine(0, "test", List.of(), 0, 0);
        var routine2 = new Routine(0, "test", List.of(), 0, 0);
        var routine3 = new Routine(0, "test", List.of(), 0, 0);
        assertEquals(routine1, routine2);
        assertEquals(routine1, routine3);
    }
}
