package edu.ucsd.cse110.habitizer.app.data.db;

import android.util.Log;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Transformations;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

import edu.ucsd.cse110.habitizer.lib.domain.Routine;
import edu.ucsd.cse110.habitizer.lib.domain.RoutineRepository;
import edu.ucsd.cse110.habitizer.lib.domain.Task;
import edu.ucsd.cse110.observables.MutableSubject;

public class RoomRoutineRepository implements RoutineRepository {
    private static final String TAG = "RoomRoutineRepository";
    private final RoutineDao routineDao;
    private final Executor executor = Executors.newSingleThreadExecutor();

    // Counter for task IDs
    private static int nextTaskId = 1000;

    public RoomRoutineRepository(RoutineDao routineDao) {
        this.routineDao = routineDao;

        // Find highest task ID during initialization to avoid conflicts
        executor.execute(() -> {
            try {
                List<RoutineEntity> entities = routineDao.findAllSync();
                for (RoutineEntity entity : entities) {
                    List<Task> tasks = tasksFromJson(entity.tasksJson);
                    for (Task task : tasks) {
                        if (task.id() != null && task.id() >= nextTaskId) {
                            nextTaskId = task.id() + 1;
                        }
                    }
                }
                Log.d(TAG, "Initialized nextTaskId to: " + nextTaskId);
            } catch (Exception e) {
                Log.e(TAG, "Error initializing task IDs: " + e.getMessage());
            }
        });
    }

    // Helper to parse tasks from JSON without modifying RoutineEntity
    private List<Task> tasksFromJson(String json) {
        try {
            return RoutineEntity.fromJson(json);
        } catch (Exception e) {
            Log.e(TAG, "Error parsing tasks JSON: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    @Override
    public MutableSubject<Routine> find(int id) {
        LiveData<RoutineEntity> entityLiveData = routineDao.findById(id);
        LiveData<Routine> routineLiveData = Transformations.map(entityLiveData, entity -> {
            return (entity == null) ? null : entity.toDomain();
        });
        return new LiveDataSubjectAdapter<>(routineLiveData);
    }

    @Override
    public MutableSubject<List<Routine>> findAll() {
        LiveData<List<RoutineEntity>> entitiesLiveData = routineDao.findAll();
        LiveData<List<Routine>> routinesLiveData = Transformations.map(entitiesLiveData, entities ->
                entities.stream()
                        .map(RoutineEntity::toDomain)
                        .collect(Collectors.toList())
        );
        return new LiveDataSubjectAdapter<>(routinesLiveData);
    }

    @Override
    public void save(Routine routine) {
        executor.execute(() -> {
            try {
                Log.d(TAG, "Saving routine: " + routine.name() + " (ID: " + routine.id() + ")");

                // Process tasks to ensure they all have valid IDs
                List<Task> tasksWithIds = new ArrayList<>();
                boolean hasTaskUpdates = false;

                synchronized (RoomRoutineRepository.class) {
                    for (Task task : routine.tasks()) {
                        if (task.id() == null) {
                            // Assign new ID to task
                            int id = nextTaskId++;
                            tasksWithIds.add(task.withId(id));
                            hasTaskUpdates = true;
                            Log.d(TAG, "Assigned ID " + id + " to task: " + task.name());
                        } else {
                            tasksWithIds.add(task);
                        }
                    }
                }

                // Create updated routine with proper task IDs if needed
                Routine routineToSave = hasTaskUpdates ? routine.withTasks(tasksWithIds) : routine;

                // Convert to entity and save
                RoutineEntity entity = RoutineEntity.fromDomain(routineToSave);
                long result = routineDao.insert(entity);

                Log.d(TAG, "Routine saved with result: " + result);
            } catch (Exception e) {
                Log.e(TAG, "Error saving routine: " + e.getMessage());
                e.printStackTrace();
            }
        });
    }
}