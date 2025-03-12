package edu.ucsd.cse110.habitizer.app.data.db;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;

@Dao
public interface CustomTimerDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(CustomTimerEntity customTimer);

    @Query("SELECT * FROM custom_timer")
    CustomTimerEntity find();

    @Query("SELECT * FROM custom_timer")
    LiveData<CustomTimerEntity> findAsLiveData();

    @Query("DELETE FROM custom_timer")
    void delete();

    @Transaction
    default void replace(CustomTimerEntity customTimer) {
        delete();
        insert(customTimer);
    }
}
