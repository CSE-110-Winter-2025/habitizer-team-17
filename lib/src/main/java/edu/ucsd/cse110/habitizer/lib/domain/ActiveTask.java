package edu.ucsd.cse110.habitizer.lib.domain;

import androidx.annotation.NonNull;

import java.util.Objects;

public class ActiveTask {
    private final @NonNull Task task;
    private final boolean checked;

    public ActiveTask(@NonNull Task task, boolean checked) {
        this.task = task;
        this.checked = checked;
    }

    public @NonNull Task task() {
        return task;
    }

    public boolean checked() {
        return this.checked;
    }

    public ActiveTask withTask(@NonNull Task task) {
        return new ActiveTask(task, checked());
    }

    public ActiveTask withChecked(boolean checked) {
        return new ActiveTask(task(), checked);
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
