package edu.ucsd.cse110.habitizer.lib.domain;

import androidx.annotation.Nullable;

import java.util.Objects;

public class Task {

    private String name;
    private final Integer taskId;
    private final Integer routineId;
    // some sort of time field, not ready for this yet
    // ... more fields to come
    public Task(String name, Integer taskId, Integer routineId) {
        if (name == null ) {
            throw new IllegalArgumentException("Task Name must not be null");
        }
        if (name.isEmpty()) {
            throw new IllegalArgumentException("Task Name must not be empty");
        }
        this.name = name;
        this.taskId = taskId;
        this.routineId = routineId;
    }

    public @Nullable Integer id() {
        return this.taskId;
    }
    public @Nullable Integer routineId() { return this.routineId; }
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
        return Objects.equals(name, task.name) && Objects.equals(taskId, task.taskId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, taskId);
    }
}
