package edu.ucsd.cse110.habitizer.lib.domain;

import androidx.annotation.Nullable;

import java.util.Objects;

public class Task {

    private String name;
    private final Integer id;
    // some sort of time field, not ready for this yet
    // ... more fields to come
    public Task(String name, Integer id) {
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return Objects.equals(name, task.name) && Objects.equals(id, task.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, id);
    }
}
