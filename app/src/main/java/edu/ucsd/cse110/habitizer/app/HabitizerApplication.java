package edu.ucsd.cse110.habitizer.app;

import android.app.Application;

import edu.ucsd.cse110.habitizer.lib.data.InMemoryDataSource;
import edu.ucsd.cse110.habitizer.lib.domain.ActiveRoutineRepository;
import edu.ucsd.cse110.habitizer.lib.domain.ActiveTaskRepository;
import edu.ucsd.cse110.habitizer.lib.domain.Routine;
import edu.ucsd.cse110.habitizer.lib.domain.RoutineRepository;
import edu.ucsd.cse110.habitizer.lib.domain.Task;
import edu.ucsd.cse110.habitizer.lib.domain.TaskRepository;

public class HabitizerApplication extends Application {
    private InMemoryDataSource dataSource;
    private RoutineRepository routineRepository;

    private TaskRepository taskRepository;

    private ActiveRoutineRepository activeRoutineRepository;

    private ActiveTaskRepository activeTaskRepository;

    @Override
    public void onCreate() {
        super.onCreate();

        this.dataSource = InMemoryDataSource.fromDefault();
        this.routineRepository = new RoutineRepository(dataSource);
        this.taskRepository = new TaskRepository(dataSource);
        this.activeRoutineRepository = new ActiveRoutineRepository(dataSource);
        this.activeTaskRepository = new ActiveTaskRepository(dataSource);
    }

    public RoutineRepository getRoutineRepository() {
        return routineRepository;
    }
    public TaskRepository getTaskRepository(){return taskRepository; }

    public ActiveRoutineRepository getActiveRoutineRepository(){return activeRoutineRepository; }

    public ActiveTaskRepository getActiveTaskRepository() {
        return activeTaskRepository;
    }
}
