package edu.ucsd.cse110.habitizer.lib.domain;

import static org.junit.Assert.*;

import org.junit.Test;

import java.util.List;

public class RoutineTest {
    public final static List<Task> tasks = List.of(
            new Task(0, "Shower"),
            new Task(1, "Brush Teeth"),
            new Task(2, "Dress"),
            new Task(3, "Make Coffee"),
            new Task(4, "Make Lunch"),
            new Task(5, "Dinner Prep"),
            new Task(6, "Pack Bag")
    );
    public final static Routine expectedRoutine = new Routine(0, "expectedRoutine", tasks, 44);

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
<<<<<<< HEAD
        assertThrows(IllegalArgumentException.class, () -> new Routine(2, null, tasks));
=======
        assertThrows(IllegalArgumentException.class, () -> new Routine(2, null,tasks, 44));
>>>>>>> master
    }

    @Test
    public void emptyNameTest() {
<<<<<<< HEAD
        assertThrows(IllegalArgumentException.class, () -> new Routine(2, "", tasks));
=======
        assertThrows(IllegalArgumentException.class, () -> new Routine(2,"",tasks, 44));
>>>>>>> master
    }

    @Test
    public void nullTasksTest() {
<<<<<<< HEAD
        assertThrows(IllegalArgumentException.class, () -> new Routine(2, "test", null));
=======
        assertThrows(IllegalArgumentException.class, () -> new Routine(2,"test", null, 44));
    }


    @Test
    public void getGoalTimeTest() {
        Routine routine = new Routine(2,"test", tasks, 0);
        var expected = 0;
        assertEquals(expected, routine.getGoalTime());
    }

    @Test
    public void setGoalTimeTest() {
        Routine routine = new Routine(2,"test", tasks, 0);
        var expected = 44;
        routine.setGoalTime(44);
        assertEquals(expected, routine.getGoalTime());
>>>>>>> master
    }
}
