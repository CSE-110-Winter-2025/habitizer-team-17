package edu.ucsd.cse110.habitizer.lib.domain;

public class ActiveTask {
    Task task;
    boolean checked;

    public ActiveTask(Task task, boolean checked){
        this.task = task;
        this.checked = checked;
    }

    public boolean isChecked(){
        return this.checked;
    }

    public ActiveTask setChecked(boolean checked){
        return new ActiveTask(task(), checked);
    }

    public Task task(){
        return task;
    }
}
