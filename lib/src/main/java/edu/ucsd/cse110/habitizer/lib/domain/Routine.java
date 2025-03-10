package edu.ucsd.cse110.habitizer.lib.domain;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Routine {
    private final @Nullable Integer id;
    private final @NonNull String name;
    private final @NonNull List<Task> tasks;
    private final @NonNull Integer goalTime;
    private final @NonNull Integer sortOrder;

    public Routine(@Nullable Integer id, @NonNull String name, @NonNull List<Task> tasks,
                   @NonNull Integer goalTime, @NonNull Integer sortOrder) {
        this.id = id;
        this.name = name;
        this.tasks = tasks;
        this.goalTime = goalTime;
        this.sortOrder = sortOrder;
    }

    public @Nullable Integer id() {
        return id;
    }

    public @NonNull String name() {
        return name;
    }

    public @NonNull List<Task> tasks() {
        return tasks;
    }

    public @NonNull Integer goalTime() {
        return goalTime;
    }

    public @NonNull Integer sortOrder() {
        return sortOrder;
    }

    public Routine withId(@Nullable Integer id) {
        return new Routine(id, name(), tasks(), goalTime(), sortOrder());
    }

    public Routine withName(@NonNull String name) {
        return new Routine(id(), name, tasks(), goalTime(), sortOrder());
    }

    public Routine withTasks(@NonNull List<Task> tasks) {
        return new Routine(id(), name(), tasks, goalTime(), sortOrder());
    }

    public Routine withoutTask(int id) {
        var newTasks = new ArrayList<>(tasks);
        newTasks.removeIf(task -> task.id() != null && task.id() == id);
        return withTasks(newTasks);
    }

    public Routine withGoalTime(@NonNull Integer goalTime) {
        return new Routine(id(), name(), tasks(), goalTime, sortOrder());
    }

    public Routine withSortOrder(@NonNull Integer sortOrder) {
        return new Routine(id(), name(), tasks(), goalTime(), sortOrder);
    }

    public Routine withAppendedTask(Task task) {
        var newTasks = new ArrayList<>(tasks);
        newTasks.add(task);
        return withTasks(newTasks);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Routine routine = (Routine) o;
        return Objects.equals(id, routine.id)
                && Objects.equals(name, routine.name)
                && Objects.equals(tasks, routine.tasks)
                && Objects.equals(goalTime, routine.goalTime)
                && Objects.equals(sortOrder, routine.sortOrder);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, tasks, goalTime, sortOrder);
    }

    public Routine withRenamedTask(int taskId, String newName) {
        var newTasks = new ArrayList<>(tasks);
        for (int i = 0; i < newTasks.size(); i++) {
            Task task = newTasks.get(i);
            if (task.id() != null && task.id() == taskId) {
                newTasks.set(i, task.withName(newName));
                break;
            }
        }
        return withTasks(newTasks);
    }


    public Routine moveTaskOrdering(int taskId, int direction){ //1 is up, 0 is down
        List<Task> newTasks = this.tasks();
        for(var task: newTasks){
            if(task.id() == taskId){
                if(direction == 1){
                    if(newTasks.indexOf(task) != 0){
                        Task placeHolder = newTasks.get(newTasks.indexOf(task) - 1);
                        int taskIndex = newTasks.indexOf(task);
                        newTasks.set(taskIndex-1, task);
                        newTasks.set(taskIndex, placeHolder);
                        return withTasks(newTasks);
                    }
                } else {
                    if(newTasks.indexOf(task) != newTasks.size() - 1){
                        Task placeHolder = newTasks.get(newTasks.indexOf(task) + 1);
                        newTasks.set(newTasks.indexOf(task) + 1, task);
                        newTasks.set(newTasks.indexOf(task), placeHolder);
                        return withTasks(newTasks);
                    }
                }
            }
        }
        return this;
    }
}
