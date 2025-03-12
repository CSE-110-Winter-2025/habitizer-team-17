package edu.ucsd.cse110.habitizer.app.data.db;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import edu.ucsd.cse110.habitizer.lib.domain.CustomTimer;
import edu.ucsd.cse110.habitizer.lib.domain.TimerState;

@Entity(tableName = "custom_timer")
public class CustomTimerEntity {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    public Integer id = null;

    @ColumnInfo(name = "timer_state")
    public TimerState timerState;

    @ColumnInfo(name = "elapsed_time")
    public long elapsedTimeInMilliseconds;

    CustomTimerEntity(TimerState timerState, long elapsedTimeInMilliseconds) {
        this.timerState = timerState;
        this.elapsedTimeInMilliseconds = elapsedTimeInMilliseconds;
    }

    public static CustomTimerEntity fromCustomTimer(CustomTimer customTimer) {
        return new CustomTimerEntity(customTimer.getState(), customTimer.getElapsedTimeInMilliseconds());
    }

    public @NonNull CustomTimer toCustomTimer() {
        return new CustomTimer(timerState, elapsedTimeInMilliseconds);
    }
}
