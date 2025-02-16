package edu.ucsd.cse110.habitizer.lib.domain;

import static org.junit.Assert.*;

import org.junit.Test;

import java.util.List;

public class ActiveRoutineTest {
    @Test
    public void testGetters() {
        var task = new Task(0, "task");
        var tasks = List.of(task);
        var routine = new Routine(0, "routine", tasks, 0, 0);
        var activeTask = new ActiveTask(task, false,0L);
        var activeTasks = List.of(activeTask);
        var activeRoutine = new ActiveRoutine(routine, activeTasks,0L);
        assertEquals(routine, activeRoutine.routine());
        assertEquals(activeTasks, activeRoutine.activeTasks());
    }

    @Test
    public void testWithActiveTask() {
        var task = new Task(0, "task");
        var newTask = new Task(0, "task2");
        var routine = new Routine(0, "routine", List.of(task), 0, 0);
        var activeTask = new ActiveTask(task, false,0L);
        var newActiveTask = new ActiveTask(newTask, true,0L);
        var activeRoutine = new ActiveRoutine(routine, List.of(activeTask), 0L);
        var newActiveRoutine = activeRoutine.withActiveTask(newActiveTask);
        assertEquals(List.of(newActiveTask), newActiveRoutine.activeTasks());
    }
}
