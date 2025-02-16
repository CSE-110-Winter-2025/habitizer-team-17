package edu.ucsd.cse110.habitizer.lib.domain;

import org.junit.Test;
import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import edu.ucsd.cse110.habitizer.lib.data.InMemoryDataSource;

public class UserStoriesTest {

    @Test
    public void testCheckedOffTaskStoresElapsedTime() {
        var task = new Task(10, "Example Task");
        var routine = new Routine(1, "Morning Routine", List.of(task), 30);
        var activeTask = new ActiveTask(task, false, 0);
        var activeRoutine = new ActiveRoutine(routine, List.of(activeTask));

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

        long minutes = fiveMinutesMs / 60000;
        String display = minutes + "m";

        assertEquals(5, minutes);
        assertEquals("5m", display);
    }

    @Test
    public void testRenameTaskInRepository() {
        var dataSource = new InMemoryDataSource();
        var taskRepo = new TaskRepository(dataSource);

        var originalTask = new Task(5, "Original Name");
        taskRepo.save(originalTask);
        taskRepo.rename(5, "New Name");
        Task updatedTask = taskRepo.find(5).getValue();
        assertNotNull(updatedTask);
        assertEquals("New Name", updatedTask.name());
    }

    @Test
    public void testRenameTaskInRoutine() {
        var dataSource = new InMemoryDataSource();
        var taskRepo = new TaskRepository(dataSource);
        var routineRepo = new RoutineRepository(dataSource);

        var task = new Task(101, "Old Name");
        taskRepo.save(task);

        var routine = new Routine(201, "My Routine", List.of(task), 20);
        routineRepo.save(routine);

        taskRepo.rename(task.id(), "Brand New Name");

        Routine updatedRoutine = routineRepo.find(201).getValue();
        assertNotNull(updatedRoutine);

        var refreshedTasks = new ArrayList<Task>();
        for (Task t : updatedRoutine.tasks()) {
            var latest = taskRepo.find(t.id()).getValue();
            refreshedTasks.add(latest);
        }
        var finalRoutine = updatedRoutine.withTasks(refreshedTasks);

        Task updatedTask = finalRoutine.tasks().get(0);
        assertEquals("Brand New Name", updatedTask.name());
    }

}
