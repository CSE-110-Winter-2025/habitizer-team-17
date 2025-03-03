package edu.ucsd.cse110.habitizer.app.data.db;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import java.util.List;
import java.util.concurrent.Executors;

import edu.ucsd.cse110.habitizer.lib.data.InMemoryDataSource;
import edu.ucsd.cse110.habitizer.lib.domain.Routine;

/**
 * Helper class for managing the database and prepopulating it with default routines.
 */
public class DatabaseProvider {
    private static final String DATABASE_NAME = "habitizer-database";
    private static volatile HabitizerDatabase instance;

    /**
     * Gets the singleton instance of the database.
     */
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
                                    // Prepopulate the database with default routines when first created
                                    Executors.newSingleThreadExecutor().execute(() -> {
                                        // Get default routines
                                        List<Routine> defaultRoutines = getDefaultRoutines();
                                        // Convert to entities
                                        List<RoutineEntity> routineEntities = defaultRoutines.stream()
                                                .map(RoutineEntity::fromDomain)
                                                .toList();
                                        // Insert into database
                                        getInstance(context).routineDao().insertAll(routineEntities);
                                    });
                                }
                            })
                            .build();
                }
            }
        }
        return instance;
    }

    /**
     * Gets the default routines from InMemoryDataSource.
     */
    private static List<Routine> getDefaultRoutines() {
        return List.of(
                InMemoryDataSource.MORNING_ROUTINE,
                InMemoryDataSource.EVENING_ROUTINE
        );
    }
}