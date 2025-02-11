package edu.ucsd.cse110.habitizer.lib.domain;

import java.util.ArrayList;
import java.util.List;

public class ActiveRoutine {

    Routine routine;

    List<ActiveTask> activeTasks;

    public ActiveRoutine(Routine routine, List<ActiveTask> activeTasks){
        this.routine = routine;
        this.activeTasks = activeTasks;
    }

    public ActiveRoutine setActiveTask(ActiveTask activeTask){
        var newActiveTasks = new ArrayList<ActiveTask>();
        for(var oldactiveTask: activeTasks) {
            if (oldactiveTask.task().id() == activeTask.task().id()) {
                newActiveTasks.add(activeTask);
            } else {
                newActiveTasks.add(oldactiveTask);
            }
        }
        return new ActiveRoutine(routine(), newActiveTasks);
    }

    public List<ActiveTask> activeTasks(){
        return activeTasks;
    }

    public Routine routine(){
        return routine;
    }
}
