package edu.ucsd.cse110.habitizer.app;

import android.app.Application;

import edu.ucsd.cse110.habitizer.app.data.db.DatabaseProvider;
import edu.ucsd.cse110.habitizer.app.data.db.HabitizerDatabase;
import edu.ucsd.cse110.habitizer.app.data.db.RoomRoutineRepository;
import edu.ucsd.cse110.habitizer.lib.domain.RoutineRepository;

public class HabitizerApplication extends Application {
    private static HabitizerApplication instance;
    private RoutineRepository routineRepository;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;

        // Initialize the database
        HabitizerDatabase database = DatabaseProvider.getInstance(this);

        // Create repository with Room DAO
        routineRepository = new RoomRoutineRepository(database.routineDao());
    }

    public static HabitizerApplication getInstance() {
        return instance;
    }

    public RoutineRepository getRoutineRepository() {
        return routineRepository;
    }
}