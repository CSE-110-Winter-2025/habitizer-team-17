package edu.ucsd.cse110.habitizer.app.data.db;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import java.util.List;
import java.util.concurrent.Executors;

import edu.ucsd.cse110.habitizer.lib.data.InMemoryDataSource;
import edu.ucsd.cse110.habitizer.lib.domain.Routine;

public class DatabaseProvider {
    private static final String TAG = "DatabaseProvider";
    private static final String DATABASE_NAME = "habitizer-database";
    private static volatile HabitizerDatabase instance;

    public static HabitizerDatabase getInstance(Context context) {
        if (instance == null) {
            synchronized (DatabaseProvider.class) {
                if (instance == null) {
                    instance = Room.databaseBuilder(
                                    context.getApplicationContext(),
                                    HabitizerDatabase.class,
                                    DATABASE_NAME)
                            .addCallback(new RoomDatabase.Callback() {
                                @Override
                                public void onCreate(@NonNull SupportSQLiteDatabase db) {
                                    super.onCreate(db);
                                    Log.d(TAG, "Database created, populating with default data");

                                    Executors.newSingleThreadExecutor().execute(() -> {
                                        try {
                                            // Get default routines
                                            List<Routine> defaultRoutines = getDefaultRoutines();
                                            Log.d(TAG, "Default routines: " + defaultRoutines.size());

                                            // Convert to entities
                                            List<RoutineEntity> routineEntities = defaultRoutines.stream()
                                                    .map(RoutineEntity::fromDomain)
                                                    .toList();

                                            // Insert into database
                                            getInstance(context).routineDao().insertAll(routineEntities);
                                            Log.d(TAG, "Default routines inserted successfully");
                                        } catch (Exception e) {
                                            Log.e(TAG, "Error populating database: " + e.getMessage(), e);
                                        }
                                    });
                                }
                            })
                            .build();
                }
            }
        }
        return instance;
    }

    private static List<Routine> getDefaultRoutines() {
        return List.of(
                InMemoryDataSource.MORNING_ROUTINE,
                InMemoryDataSource.EVENING_ROUTINE
        );
    }
}