package edu.ucsd.cse110.habitizer.lib.domain;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;
import java.util.Objects;

public class Routine {
    private final @Nullable Integer id;
    private final @NonNull String name;
    private final @NonNull List<Task> tasks;

    public Routine(@Nullable Integer id, @NonNull String name, @NonNull List<Task> tasks) {
        if (name == null) {
            throw new IllegalArgumentException("Routine Name must not be null");
        }
        if (name.isEmpty()) {
            throw new IllegalArgumentException("Routine Name must not be empty");
        }
        if (tasks == null) {
            throw new IllegalArgumentException("Tasks must not be null; pass empty list if no tasks " +
                    "are present");
        }
        this.id = id;
        this.name = name;
        this.tasks = tasks;
    }

    public @Nullable Integer id() {
        return this.id;
    }

    public @NonNull String name() {
        return this.name;
    }

    public @NonNull List<Task> tasks() {
        return this.tasks;
    }

    public Routine withId(@Nullable Integer id) {
        return new Routine(id, name(), tasks());
    }

    public Routine withName(@NonNull String name) {
        if (name == null) {
            throw new IllegalArgumentException("Routine Name must not be null");
        }
        if (name.isEmpty()) {
            throw new IllegalArgumentException("Routine Name must not be empty");
        }
        return new Routine(id(), name, tasks());
    }

    public Routine withTasks(@NonNull List<Task> tasks) {
        if (tasks == null) {
            throw new IllegalArgumentException("Tasks must not be null; pass empty list if no tasks " +
                    "are present");
        }
        return new Routine(id(), name(), tasks);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Routine routine = (Routine) o;
        return Objects.equals(id, routine.id) && Objects.equals(name, routine.name) && Objects.equals(tasks, routine.tasks);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, tasks);
    }
}
