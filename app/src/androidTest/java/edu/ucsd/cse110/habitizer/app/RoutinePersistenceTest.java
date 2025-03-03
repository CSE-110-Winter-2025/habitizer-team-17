package edu.ucsd.cse110.habitizer.app;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import android.content.Context;

import androidx.room.Room;
import androidx.test.core.app.ApplicationProvider;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import edu.ucsd.cse110.habitizer.app.data.db.HabitizerDatabase;
import edu.ucsd.cse110.habitizer.app.data.db.RoutineDao;
import edu.ucsd.cse110.habitizer.app.data.db.RoutineEntity;
import edu.ucsd.cse110.habitizer.lib.domain.Routine;
import edu.ucsd.cse110.habitizer.lib.domain.Task;

public class RoutinePersistenceTest {
    private HabitizerDatabase database;
    private RoutineDao routineDao;

    @Before
    public void initDb() {
        Context context = ApplicationProvider.getApplicationContext();

        database = Room.inMemoryDatabaseBuilder(context, HabitizerDatabase.class)
                .allowMainThreadQueries()  // For simple synchronous tests
                .build();
        routineDao = database.routineDao();

        Routine nightRoutine = new Routine(1, "Night time routine", new ArrayList<>(), 0, 0);
        RoutineEntity nightEntity = RoutineEntity.fromDomain(nightRoutine);
        routineDao.insert(nightEntity);
    }

    @After
    public void closeDb() {
        database.close();
    }

    @Test
    public void testAddTaskPersistence() {
        RoutineEntity entity = routineDao.findByIdSync(1);
        Routine routine = entity.toDomain();

        Task newTask = new Task(null, "Put on Slippers");
        Routine updatedRoutine = routine.withAppendedTask(newTask);

        routineDao.insert(RoutineEntity.fromDomain(updatedRoutine));

        database.close();
        Context context = ApplicationProvider.getApplicationContext();
        database = Room.inMemoryDatabaseBuilder(context, HabitizerDatabase.class)
                .allowMainThreadQueries()
                .build();
        routineDao = database.routineDao();

        routineDao.insert(RoutineEntity.fromDomain(updatedRoutine));

        RoutineEntity persistedEntity = routineDao.findByIdSync(1);
        Routine persistedRoutine = persistedEntity.toDomain();

        boolean taskFound = persistedRoutine.tasks().stream()
                .anyMatch(task -> "Put on Slippers".equals(task.name()));
        assertTrue("Task 'Put on Slippers' should persist after restart", taskFound);
    }

    @Test
    public void testRemoveTaskPersistence() {
        List<Task> initialTasks = new ArrayList<>();
        initialTasks.add(new Task(101, "Brush teeth"));
        Routine morningRoutine = new Routine(2, "Morning time routine", initialTasks, 0, 1);
        routineDao.insert(RoutineEntity.fromDomain(morningRoutine));

        RoutineEntity entityBeforeRemoval = routineDao.findByIdSync(2);
        Routine routineBeforeRemoval = entityBeforeRemoval.toDomain();
        Routine updatedRoutine = routineBeforeRemoval.withTasks(
                routineBeforeRemoval.tasks().stream()
                        .filter(task -> !"Brush teeth".equals(task.name()))
                        .collect(Collectors.toList())
        );
        routineDao.insert(RoutineEntity.fromDomain(updatedRoutine));

        RoutineEntity persistedEntity = routineDao.findByIdSync(2);
        Routine persistedRoutine = persistedEntity.toDomain();

        boolean taskFound = persistedRoutine.tasks().stream()
                .anyMatch(task -> "Brush teeth".equals(task.name()));
        assertFalse("Task 'Brush teeth' should be removed and not persist", taskFound);
    }

    @Test
    public void testRenameTaskPersistence() {
        List<Task> initialTasks = new ArrayList<>();
        initialTasks.add(new Task(101, "Brush teeth"));
        Routine morningRoutine = new Routine(3, "Morning time routine", initialTasks, 0, 1);
        routineDao.insert(RoutineEntity.fromDomain(morningRoutine));

        RoutineEntity entityBeforeRename = routineDao.findByIdSync(3);
        Routine routineBeforeRename = entityBeforeRename.toDomain();
        Routine updatedRoutine = routineBeforeRename.withRenamedTask(101, "Take a shower");
        routineDao.insert(RoutineEntity.fromDomain(updatedRoutine));

        RoutineEntity persistedEntity = routineDao.findByIdSync(3);
        Routine persistedRoutine = persistedEntity.toDomain();

        boolean oldNameFound = persistedRoutine.tasks().stream()
                .anyMatch(task -> "Brush teeth".equals(task.name()));
        boolean newNameFound = persistedRoutine.tasks().stream()
                .anyMatch(task -> "Take a shower".equals(task.name()));

        assertFalse("Old task name 'Brush teeth' should not persist", oldNameFound);
        assertTrue("New task name 'Take a shower' should persist", newNameFound);
    }

    @Test
    public void testAddNewRoutinePersistence() {
        Routine newRoutine = new Routine(null, "Afternoon routine", new ArrayList<>(), 0, 2);

        routineDao.insert(RoutineEntity.fromDomain(newRoutine));

        List<RoutineEntity> allEntities = routineDao.findAllSync();
        boolean routineFound = allEntities.stream()
                .map(RoutineEntity::toDomain)
                .anyMatch(r -> "Afternoon routine".equals(r.name()));

        assertTrue("Newly created routine should appear in the list", routineFound);
    }
}
