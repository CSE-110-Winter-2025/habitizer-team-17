package edu.ucsd.cse110.habitizer.lib.domain;

import java.util.ArrayList;

public class Routine {
    private ArrayList<Task> tasks;
    private String name;
    private final Integer id;

    public Routine(ArrayList<Task> tasks, String name, Integer id) {
        this.name = name;
        this.tasks = tasks;
        this.id = id;
    }

    public Integer id() {
        return this.id;
    }
    public ArrayList<Task> getTasks() {
        return this.tasks;
    }

    public String name() {
        return this.name;
    }
}
