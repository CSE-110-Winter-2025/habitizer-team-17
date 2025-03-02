package edu.ucsd.cse110.habitizer.lib.domain;

import org.junit.Test;
import static org.junit.Assert.*;

import java.util.List;

import edu.ucsd.cse110.habitizer.lib.data.InMemoryDataSource;

/**
 * Tests for US6 (tracking checked-off task time) and US10 (renaming tasks).
 */
public class UserStoriesTest {

    @Test
    public void testCheckedOffTaskStoresElapsedTime() {
        var task = new Task(10, "Example Task");
        var routine = new Routine(1, "Morning Routine", List.of(task), 30, 0);
        var activeTask = new ActiveTask(task, false, 0);
        var activeRoutine = new ActiveRoutine(routine, List.of(activeTask), 0L);
        long fiveMinutesMs = 5 * 60 * 1000;

        var updatedTask = activeTask.withChecked(true, fiveMinutesMs);
        var updatedActiveRoutine = activeRoutine.withActiveTask(updatedTask);

        assertTrue(updatedActiveRoutine.activeTasks().get(0).checked());
        assertEquals(fiveMinutesMs,
                updatedActiveRoutine.activeTasks().get(0).checkedElapsedTime());
    }
    @Test
    public void testDisplayMinutesForCheckedOffTask() {
        long fiveMinutesMs = 5 * 60 * 1000;
        long minutes = fiveMinutesMs / 60_000;
        String display = minutes + "m";

        assertEquals(5, minutes);
        assertEquals("5m", display);
    }


    @Test
    public void testRenameTaskInRoutine() {
        var dataSource = new InMemoryDataSource();
        var routineRepo = new SimpleRoutineRepository(dataSource);

        var task = new Task(101, "Old Name");
        var routine = new Routine(201, "My Routine", List.of(task), 20, 0);
        routineRepo.save(routine);

        var updatedRoutine = routine.withRenamedTask(101, "Brand New Name");
        routineRepo.save(updatedRoutine);

        Routine fetched = routineRepo.find(201).getValue();
        assertNotNull(fetched);

        var renamedTask = fetched.tasks().get(0);
        assertEquals("Brand New Name", renamedTask.name());
    }

    @Test
    public void testRenameOneTaskAmongMany() {
        var dataSource = new InMemoryDataSource();
        var routineRepo = new SimpleRoutineRepository(dataSource);

        var task1 = new Task(1, "First Task");
        var task2 = new Task(2, "Second Task");
        var task3 = new Task(3, "Third Task");
        var routine = new Routine(999, "Multi-task Routine",
                List.of(task1, task2, task3), 30, 0);
        routineRepo.save(routine);

        var updatedRoutine = routine.withRenamedTask(2, "Renamed Second Task");
        routineRepo.save(updatedRoutine);

        Routine fetched = routineRepo.find(999).getValue();
        assertNotNull(fetched);
        var tasks = fetched.tasks();
        assertEquals("First Task", tasks.get(0).name());
        assertEquals("Renamed Second Task", tasks.get(1).name());
        assertEquals("Third Task", tasks.get(2).name());
    }
}
