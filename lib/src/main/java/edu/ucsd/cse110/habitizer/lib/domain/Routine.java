package edu.ucsd.cse110.habitizer.lib.domain;

import java.util.ArrayList;
import java.util.List;

public class Routine {
    private List<Task> tasks;
    private String name;
    private final Integer id;

    public Routine(List<Task> tasks, String name, Integer id) {
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
}
