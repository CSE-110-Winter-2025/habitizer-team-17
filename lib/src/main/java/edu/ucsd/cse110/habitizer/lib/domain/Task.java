package edu.ucsd.cse110.habitizer.lib.domain;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.Objects;

public class Task {

    private final @Nullable Integer id;
    private @NonNull String name;
    private boolean completed;
    // some sort of time field, not ready for this yet
    // ... more fields to come
    public Task(@Nullable Integer id, @NonNull String name) {
        if (name == null ) {
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

    public String getName() {
        return this.name;
    }
    public void setName(String newName) {
        this.name = newName;
    }
    public boolean isCompleted() {
        return completed;
    }
    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return Objects.equals(id, task.id) && Objects.equals(getName(), task.getName());
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, getName());
    }
}
