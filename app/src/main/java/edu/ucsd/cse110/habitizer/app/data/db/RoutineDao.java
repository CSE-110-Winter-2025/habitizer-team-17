package edu.ucsd.cse110.habitizer.app.data.db;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface RoutineDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert(RoutineEntity routine);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    List<Long> insertAll(List<RoutineEntity> routines);

    @Query("SELECT * FROM routines WHERE id = :id")
    LiveData<RoutineEntity> findById(int id);

    @Query("SELECT * FROM routines ORDER BY sort_order ASC")
    LiveData<List<RoutineEntity>> findAll();

    @Query("SELECT * FROM routines")
    List<RoutineEntity> findAllSync();

    @Query("SELECT * FROM routines WHERE id = :id LIMIT 1")
    RoutineEntity findByIdSync(int id);

    @Query("UPDATE routines SET sort_order = sort_order - 1 WHERE sort_order >= :sortOrder")
    void findAndChangeAboveOrdering(int sortOrder);

    @Update
    void update(RoutineEntity routine);

    @Delete
    void delete(RoutineEntity routine);
}
