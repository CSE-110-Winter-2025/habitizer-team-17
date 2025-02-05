package edu.ucsd.cse110.habitizer.lib.domain;

public class Task {

    private String name;
    // some sort of time field, not ready for this yet
    // ... more fields to come
    public Task(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String newName) {
        this.name = newName;
    }
}
