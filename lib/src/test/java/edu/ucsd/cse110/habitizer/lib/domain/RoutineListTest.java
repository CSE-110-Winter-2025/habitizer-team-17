package edu.ucsd.cse110.habitizer.lib.domain;

import static org.junit.Assert.*;

import org.junit.Test;

import java.util.List;

public class RoutineListTest {
    public final static List<Task> tasks = List.of(
            new Task("Shower", 0),
            new Task("Brush Teeth", 1),
            new Task("Dress", 2),
            new Task("Make Coffee", 3),
            new Task("Make Lunch", 4),
            new Task("Dinner Prep", 5),
            new Task("Pack Bag", 6)
    );
    public final static Routine routine = new Routine(tasks, "routine_1", 0);

    public final static RoutineList expectedRoutineList = new RoutineList(List.of(routine));

    @Test
    public void getExpectedRoutines() {
        assertEquals(routine, expectedRoutineList.getRoutines().get(0));
    }
}
