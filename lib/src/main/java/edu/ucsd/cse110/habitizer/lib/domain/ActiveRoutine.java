package edu.ucsd.cse110.habitizer.lib.domain;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ActiveRoutine {

    private final @NonNull Routine routine;
    private final @NonNull List<ActiveTask> activeTasks;

    private final @NonNull Long  previousTaskEndTime;

    public ActiveRoutine(@NonNull Routine routine, @NonNull List<ActiveTask> activeTasks, @NonNull Long previousTaskEndTime) {
        this.routine = routine;
        this.activeTasks = activeTasks;
        this.previousTaskEndTime = previousTaskEndTime;
    }

    public @NonNull Routine routine() {
        return routine;
    }

    public @NonNull List<ActiveTask> activeTasks() {
        return activeTasks;
    }

    public ActiveRoutine withActiveTask(ActiveTask activeTask) {
        var newActiveTasks = new ArrayList<ActiveTask>();
        for (var oldactiveTask : activeTasks) {
            if (Objects.equals(oldactiveTask.task().id(), activeTask.task().id())) {
                newActiveTasks.add(activeTask);
            } else {
                newActiveTasks.add(oldactiveTask);
            }
        }
        return new ActiveRoutine(routine(), newActiveTasks, this.previousTaskEndTime);
    }

    public ActiveRoutine withPreviousTaskEndTime(@NonNull Long previousTaskEndTime) {
        return new ActiveRoutine(routine(), activeTasks, previousTaskEndTime);
    }

    public long previousTaskEndTime() {
        return previousTaskEndTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ActiveRoutine that = (ActiveRoutine) o;
        return Objects.equals(routine, that.routine) && Objects.equals(activeTasks, that.activeTasks);
    }

    @Override
    public int hashCode() {
        return Objects.hash(routine, activeTasks);
    }
}
