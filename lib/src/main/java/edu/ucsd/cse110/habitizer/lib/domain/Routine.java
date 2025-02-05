package edu.ucsd.cse110.habitizer.lib.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Routine {
    private List<Task> tasks;
    private String name;
    private final Integer id;

    public Routine(List<Task> tasks, String name, Integer id) {
        if (tasks == null) {
            throw new IllegalArgumentException("Tasks must not be null; pass empty list if no tasks " +
                    "are present");
        }
        if (name == null ) {
            throw new IllegalArgumentException("Routine Name must not be null");
        }
        if (name.isEmpty()) {
            throw new IllegalArgumentException("Routine Name must not be empty");
        }
        this.name = name;
        this.tasks = tasks;
        this.id = id;
    }

    public Integer id() {
        return this.id;
    }
    public List<Task> getTasks() {
        return this.tasks;
    }

    public String getName() {
        return this.name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Routine routine = (Routine) o;
        return Objects.equals(tasks, routine.tasks) && Objects.equals(name, routine.name) && Objects.equals(id, routine.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(tasks, name, id);
    }
}
