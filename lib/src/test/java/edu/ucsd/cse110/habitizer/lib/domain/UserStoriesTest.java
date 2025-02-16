package edu.ucsd.cse110.habitizer.lib.domain;

import org.junit.Test;
import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import edu.ucsd.cse110.habitizer.lib.data.InMemoryDataSource;

public class UserStoriesTest {

    @Test
    public void testCheckedOffTaskStoresElapsedTime() {
        // 1. Create a routine and tasks
        var task = new Task(10, "Example Task");
        var routine = new Routine(1, "Morning Routine", List.of(task), 30);

        // 2. Convert to ActiveRoutine
        var activeTask = new ActiveTask(task, false, 0);
        var activeRoutine = new ActiveRoutine(routine, List.of(activeTask));

        // 3. Simulate that 5 minutes (300000 ms) have passed in your timer
        long fiveMinutesMs = 5 * 60 * 1000;

        // 4. Check off the task
        var updatedTask = activeTask.withChecked(true, fiveMinutesMs);
        var updatedActiveRoutine = activeRoutine.withActiveTask(updatedTask);

        // 5. Check that the checkedElapsedTime is exactly 5 minutes
        assertTrue(updatedActiveRoutine.activeTasks().get(0).checked());
        assertEquals(fiveMinutesMs,
                updatedActiveRoutine.activeTasks().get(0).checkedElapsedTime());
    }

    /**
     * Story A.2: Convert stored ms into a user-friendly display (e.g., “5m”).
     * In reality, you might do this in a helper method or UI code.
     */
    @Test
    public void testDisplayMinutesForCheckedOffTask() {
        // Suppose your code wants to display the time in minutes.
        long fiveMinutesMs = 5 * 60 * 1000;

        // Convert to minutes
        long minutes = fiveMinutesMs / 60000;
        // Format as “5m”
        String display = minutes + "m";

        // Check
        assertEquals(5, minutes);
        assertEquals("5m", display);
    }

    /**
     * Story B.1: Rename a task in the repository to ensure it is persisted.
     */
    @Test
    public void testRenameTaskInRepository() {
        // Create an in-memory data source and a task repository
        var dataSource = new InMemoryDataSource();
        var taskRepo = new TaskRepository(dataSource);

        // 1. Save original task
        var originalTask = new Task(5, "Original Name");
        taskRepo.save(originalTask);

        // 2. Rename
        taskRepo.rename(5, "New Name");

        // 3. Fetch from repository
        Task updatedTask = taskRepo.find(5).getValue();
        assertNotNull(updatedTask);
        assertEquals("New Name", updatedTask.name());
    }

    /**
     * Story B.2: Rename a task that is part of a routine, and verify the routine
     * sees the updated name (assuming you re-fetch tasks from the repo).
     */
    @Test
    public void testRenameTaskInRoutine() {
        var dataSource = new InMemoryDataSource();
        var taskRepo = new TaskRepository(dataSource);
        var routineRepo = new RoutineRepository(dataSource);

        // 1. Create & save Task
        var task = new Task(101, "Old Name");
        taskRepo.save(task);

        // 2. Create & save Routine that references it
        var routine = new Routine(201, "My Routine", List.of(task), 20);
        routineRepo.save(routine);

        // 3. Rename Task in the repository
        taskRepo.rename(task.id(), "Brand New Name");

        // 4. Re-fetch the routine from the repository (still has the old Task reference)
        Routine updatedRoutine = routineRepo.find(201).getValue();
        assertNotNull(updatedRoutine);

        // 5. Manually refresh tasks from the repository to get the new name
        var refreshedTasks = new ArrayList<Task>();
        for (Task t : updatedRoutine.tasks()) {
            var latest = taskRepo.find(t.id()).getValue();
            refreshedTasks.add(latest);
        }
        var finalRoutine = updatedRoutine.withTasks(refreshedTasks);

        // 6. Now the name should be the new one
        Task updatedTask = finalRoutine.tasks().get(0);
        assertEquals("Brand New Name", updatedTask.name());
    }

}
