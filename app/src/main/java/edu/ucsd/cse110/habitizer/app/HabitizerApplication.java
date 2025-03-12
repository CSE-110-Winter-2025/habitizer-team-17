package edu.ucsd.cse110.habitizer.app;

import android.app.Application;

import edu.ucsd.cse110.habitizer.app.data.db.DatabaseProvider;
import edu.ucsd.cse110.habitizer.app.data.db.HabitizerDatabase;
import edu.ucsd.cse110.habitizer.app.data.db.RoomActiveRoutineRepository;
import edu.ucsd.cse110.habitizer.app.data.db.RoomCustomTimerRepository;
import edu.ucsd.cse110.habitizer.app.data.db.RoomRoutineRepository;
import edu.ucsd.cse110.habitizer.lib.domain.ActiveRoutineRepository;
import edu.ucsd.cse110.habitizer.lib.domain.CustomTimerRepository;
import edu.ucsd.cse110.habitizer.lib.domain.RoutineRepository;

public class HabitizerApplication extends Application {
    private static HabitizerApplication instance;
    private RoutineRepository routineRepository;
    private ActiveRoutineRepository activeRoutineRepository;
    private CustomTimerRepository customTimerRepository;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        HabitizerDatabase database = DatabaseProvider.getInstance(this);
        routineRepository = new RoomRoutineRepository(database.routineDao());
        activeRoutineRepository = new RoomActiveRoutineRepository(database.activeRoutineDao());
        customTimerRepository = new RoomCustomTimerRepository(database.customTimerDao());
    }

    public static HabitizerApplication getInstance() {
        return instance;
    }

    public RoutineRepository getRoutineRepository() {
        return routineRepository;
    }

    public ActiveRoutineRepository getActiveRoutineRepository() {
        return activeRoutineRepository;
    }

    public CustomTimerRepository getCustomTimerRepository() {
        return customTimerRepository;
    }
}