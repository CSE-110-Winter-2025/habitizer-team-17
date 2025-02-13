package edu.ucsd.cse110.habitizer.lib.domain;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.Objects;

public class Task {
    private final @Nullable Integer id;
    private final @NonNull String name;

    public Task(@Nullable Integer id, @NonNull String name) {
        if (name == null) {
            throw new IllegalArgumentException("Task Name must not be null");
        }
        if (name.isEmpty()) {
            throw new IllegalArgumentException("Task Name must not be empty");
        }
        this.name = name;
        this.id = id;
    }

    public @Nullable Integer id() {
        return this.id;
    }

    public @NonNull String name() {
        return this.name;
    }

    public Task withId(@Nullable Integer id) {
        return new Task(id, name());
    }

    public Task withName(@NonNull String name) {
        if (name == null) {
            throw new IllegalArgumentException("Task Name must not be null");
        }
        if (name.isEmpty()) {
            throw new IllegalArgumentException("Task Name must not be empty");
        }
        return new Task(id(), name);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return Objects.equals(id, task.id) && Objects.equals(name, task.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name);
    }
}
