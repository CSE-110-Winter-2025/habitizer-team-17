package edu.ucsd.cse110.habitizer.app;

import android.app.Application;
import androidx.room.Room;
import edu.ucsd.cse110.habitizer.app.data.db.HabitizerDatabase;
import edu.ucsd.cse110.habitizer.app.data.db.RoomRoutineRepository;
import edu.ucsd.cse110.habitizer.lib.domain.RoutineRepository;

public class HabitizerApplication extends Application {
    private HabitizerDatabase database;
    private RoutineRepository routineRepository;

    @Override
    public void onCreate() {
        super.onCreate();

        // Build the Room database instance
        database = Room.databaseBuilder(
                getApplicationContext(),
                HabitizerDatabase.class,
                "habitizer_db"
        ).build();

        // Create the Room-backed repository
        routineRepository = new RoomRoutineRepository(database.routineDao());
    }

    public RoutineRepository getRoutineRepository() {
        return routineRepository;
    }
}
