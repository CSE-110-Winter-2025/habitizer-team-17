package edu.ucsd.cse110.habitizer.app;

import android.app.Application;

import androidx.lifecycle.MutableLiveData;

import edu.ucsd.cse110.habitizer.lib.data.InMemoryDataSource;
import edu.ucsd.cse110.habitizer.lib.domain.RoutineRepository;

public class HabitizerApplication extends Application {
    private InMemoryDataSource dataSource;
    private RoutineRepository routineRepository;

    private MutableLiveData<Screen> screen;

    @Override
    public void onCreate() {
        super.onCreate();

        this.dataSource = InMemoryDataSource.fromDefault();
        this.routineRepository = new RoutineRepository(dataSource);
        this.screen = new MutableLiveData<>(Screen.PREVIEW_SCREEN);
    }

    public RoutineRepository getRoutineRepository() {
        return routineRepository;
    }

    public MutableLiveData<Screen> getScreen() {
        return screen;
    }

}
