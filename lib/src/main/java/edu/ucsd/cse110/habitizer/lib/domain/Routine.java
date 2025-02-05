package edu.ucsd.cse110.habitizer.lib.domain;

import java.util.ArrayList;

public class Routine {
    private ArrayList<Task> tasks;

    public Routine(ArrayList<Task> tasks) {
        this.tasks = tasks;
    }

    public ArrayList<Task> getTasks() {
        return this.tasks;
    }
}
