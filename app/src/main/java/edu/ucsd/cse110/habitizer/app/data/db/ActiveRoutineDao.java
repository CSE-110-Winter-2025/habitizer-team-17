package edu.ucsd.cse110.habitizer.app.data.db;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;

import java.util.List;
import java.util.Map;

@Dao
public interface ActiveRoutineDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(ActiveRoutineEntity activeRoutine);

    @Query("SELECT * FROM active_routines JOIN routines ON active_routines.routine_id = routines" +
            ".id")
    Map<ActiveRoutineEntity, RoutineEntity> find();

    @Query("SELECT * FROM active_routines JOIN routines ON active_routines.routine_id = routines" +
            ".id")
    LiveData<Map<ActiveRoutineEntity, RoutineEntity>> findAsLiveData();

    @Query("DELETE FROM active_routines")
    void delete();

    @Transaction
    default void replace(ActiveRoutineEntity activeRoutine) {
        delete();
        insert(activeRoutine);
    }
}
