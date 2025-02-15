package edu.ucsd.cse110.habitizer.lib.domain;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;
import java.util.Objects;

public class Routine {
    private final @Nullable Integer id;
    private final @NonNull String name;
    private final @NonNull List<Task> tasks;
    private final @NonNull Integer goalTime;
    private final @NonNull Integer sortOrder;

    public Routine(@Nullable Integer id, @NonNull String name, @NonNull List<Task> tasks,
                   @NonNull Integer goalTime, @NonNull Integer sortOrder) {
        this.id = id;
        this.name = name;
        this.tasks = tasks;
        this.goalTime = goalTime;
        this.sortOrder = sortOrder;
    }

    public @Nullable Integer id() {
        return id;
    }

    public @NonNull String name() {
        return name;
    }

    public @NonNull List<Task> tasks() {
        return tasks;
    }

    public @NonNull Integer goalTime() {
        return goalTime;
    }

    public @NonNull Integer sortOrder() {
        return sortOrder;
    }

    public Routine withId(@Nullable Integer id) {
        return new Routine(id, name(), tasks(), goalTime(), sortOrder());
    }

    public Routine withName(@NonNull String name) {
        return new Routine(id(), name, tasks(), goalTime(), sortOrder());
    }

    public Routine withTasks(@NonNull List<Task> tasks) {
        return new Routine(id(), name(), tasks, goalTime(), sortOrder());
    }

    public Routine withGoalTime(@NonNull Integer goalTime) {
        return new Routine(id(), name(), tasks(), goalTime, sortOrder());
    }

    public Routine withSortOrder(@NonNull Integer sortOrder) {
        return new Routine(id(), name(), tasks(), goalTime(), sortOrder);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Routine routine = (Routine) o;
        return Objects.equals(id, routine.id)
                && Objects.equals(name, routine.name)
                && Objects.equals(tasks, routine.tasks)
                && Objects.equals(goalTime, routine.goalTime)
                && Objects.equals(sortOrder, routine.sortOrder);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, tasks, goalTime, sortOrder);
    }
}
