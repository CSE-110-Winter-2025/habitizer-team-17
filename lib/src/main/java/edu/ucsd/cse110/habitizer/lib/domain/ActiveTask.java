package edu.ucsd.cse110.habitizer.lib.domain;

import androidx.annotation.NonNull;

import java.util.Objects;

public class ActiveTask {
    private final @NonNull Task task;
    private final boolean checked;
    private final long checkedElapsedTime;

    public ActiveTask(@NonNull Task task, boolean checked, long checkedElapsedTime) {
        this.task = task;
        this.checked = checked;
        this.checkedElapsedTime = checkedElapsedTime;
    }

    public @NonNull Task task() {
        return task;
    }

    public boolean checked() {
        return this.checked;
    }
    public long checkedElapsedTime() {
        return checkedElapsedTime;
    }

    public ActiveTask withTask(@NonNull Task task) {
        return new ActiveTask(task, checked(), checkedElapsedTime);
    }

    public ActiveTask withChecked(boolean checked, long currentElapsedTime) {
        return new ActiveTask(task(), checked, checked ? currentElapsedTime : 0);
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ActiveTask that = (ActiveTask) o;
        return checked == that.checked && Objects.equals(task, that.task);
    }

    @Override
    public int hashCode() {
        return Objects.hash(task, checked);
    }

}
