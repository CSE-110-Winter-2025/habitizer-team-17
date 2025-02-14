package edu.ucsd.cse110.habitizer.app;

import android.app.Application;

import androidx.lifecycle.MutableLiveData;

import edu.ucsd.cse110.habitizer.lib.data.InMemoryDataSource;
import edu.ucsd.cse110.habitizer.lib.domain.RoutineRepository;
import edu.ucsd.cse110.habitizer.lib.domain.TaskRepository;

public class HabitizerApplication extends Application {
    private InMemoryDataSource dataSource;
    private RoutineRepository routineRepository;

    private TaskRepository taskRepository;

    private MutableLiveData<Screen> screen;

    @Override
    public void onCreate() {
        super.onCreate();

        this.dataSource = InMemoryDataSource.fromDefault();
        this.routineRepository = new RoutineRepository(dataSource);
        this.taskRepository = new TaskRepository(dataSource);
        this.screen = new MutableLiveData<>(Screen.PREVIEW_SCREEN);
    }

    public RoutineRepository getRoutineRepository() {
        return routineRepository;
    }

    public TaskRepository getTaskRepository() {
        return taskRepository;
    }

    public MutableLiveData<Screen> getScreen() {
        return screen;
    }

}
