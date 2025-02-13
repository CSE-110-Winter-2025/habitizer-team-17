package edu.ucsd.cse110.habitizer.lib.data;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.ucsd.cse110.habitizer.lib.domain.ActiveRoutine;
import edu.ucsd.cse110.habitizer.lib.domain.ActiveTask;
import edu.ucsd.cse110.habitizer.lib.domain.Task;
import edu.ucsd.cse110.habitizer.lib.domain.Routine;
import edu.ucsd.cse110.observables.MutableSubject;
import edu.ucsd.cse110.observables.PlainMutableSubject;

public class InMemoryDataSource {

    private final Map<Integer, Task> tasks = new HashMap<>();
    private final Map<Integer, MutableSubject<Task>> taskSubjects = new HashMap<Integer, edu.ucsd.cse110.observables.MutableSubject<Task>>();
    private final MutableSubject<List<Task>> allTasksSubject = new PlainMutableSubject<>();

    private final Map<Integer, Routine> routines = new HashMap<>();

    private final Map<Integer, ActiveTask> activeTasks = new HashMap<>();

    private final Map<Integer, MutableSubject<ActiveTask>> activeTaskSubjects = new HashMap<>();
    private final Map<Integer, MutableSubject<Routine>> routineSubjects = new HashMap<>();
    private final MutableSubject<List<Routine>> allRoutinesSubject = new PlainMutableSubject<>();
    //
    private final MutableSubject<List<Routine>> routineListSubject = new PlainMutableSubject<>();

    private final MutableSubject<List<ActiveTask>> allActiveTasksSubject = new PlainMutableSubject<>();


    public InMemoryDataSource() {

    }

    public List<Task> getTasks() {
        return List.copyOf(tasks.values());
    }

    public Task getTask(int id) {
        return tasks.get(id);
    }

    public MutableSubject<Task> getTaskSubject(int id) {
        if (!taskSubjects.containsKey(id)) {
            var subject = new PlainMutableSubject<Task>();
            subject.setValue(getTask(id));
            taskSubjects.put(id, subject);
        }
        return taskSubjects.get(id);
    }

    public MutableSubject<List<Task>> getAllTasksSubject() {
        return allTasksSubject;
    }

    public void putTask(Task task) {
        tasks.put(task.id(), task);
        if (taskSubjects.containsKey(task.id())) {
            taskSubjects.get(task.id()).setValue(task);
        }
        allTasksSubject.setValue(getTasks());
    }

    public List<Routine> getRoutines() {
        return List.copyOf(routines.values());
    }

    public Routine getRoutine(int id) {
        return routines.get(id);
    }

    public MutableSubject<Routine> getRoutineSubject(int id) {
        if (!routineSubjects.containsKey(id)) {
            var subject = new PlainMutableSubject<Routine>();
            subject.setValue(getRoutine(id));
            routineSubjects.put(id, subject);
        }
        return routineSubjects.get(id);
    }

    public List<ActiveTask> getActiveTasks() {
        return List.copyOf(activeTasks.values());
    }

    public ActiveTask getActiveTask(int id) {
        return activeTasks.get(id);
    }

    public MutableSubject<ActiveTask> getActiveTaskSubject(int id) {
        if (!activeTaskSubjects.containsKey(id)) {
            var subject = new PlainMutableSubject<ActiveTask>();
            subject.setValue(getActiveTask(id));
            activeTaskSubjects.put(id, subject);

        }
        return activeTaskSubjects.get(id);
    }

    public MutableSubject<List<Routine>> getAllRoutinesSubject() {
        return allRoutinesSubject;
    }

    public MutableSubject<List<ActiveTask>> getAllActiveTasksSubject() {
        return allActiveTasksSubject;
    }

    public void putRoutine(Routine routine) {
        routines.put(routine.id(), routine);
        if (routineSubjects.containsKey(routine.id())) {
            routineSubjects.get(routine.id()).setValue(routine);
        }
        allRoutinesSubject.setValue(getRoutines());
    }

    public void putRoutineList(List<Routine> routineList) {
        routineListSubject.setValue(routineList);
    }

    ;

    public void putTaskList(List<Task> tasks) {
        for (var task : tasks) {
            this.putTask(task);
        }
    }

    public void putActiveTask(ActiveTask activeTask) {
        activeTasks.put(activeTask.task().id(), activeTask);
        if (activeTaskSubjects.containsKey(activeTask.task().id())) {
            activeTaskSubjects.get(activeTask.task().id()).setValue(activeTask);
        }
    }

    public void putActiveTaskList(List<ActiveTask> activeTasks) {
        for (var aTask : activeTasks) {
            this.putActiveTask(aTask);
        }
    }

    public void putActiveRoutine(ActiveRoutine activeRoutine) {
        this.activeRoutine = activeRoutine;
        if (activeRoutineMutableSubject != null) {
            activeRoutineMutableSubject.setValue(activeRoutine);
        }
    }

    public ActiveRoutine getActiveRoutine() {
        return activeRoutine;
    }

    public MutableSubject<ActiveRoutine> getActiveRoutineSubject() {
        if (activeRoutineMutableSubject == null) {
            activeRoutineMutableSubject = new PlainMutableSubject<>();
            activeRoutineMutableSubject.setValue(activeRoutine);
        }
        return activeRoutineMutableSubject;
    }


    public final static List<Task> MORNING_TASKS = List.of(
            new Task(0, "Shower"),
            new Task(1, "Brush Teeth"),
            new Task(2, "Dress"),
            new Task(3, "Make Coffee"),
            new Task(4, "Make Lunch"),
            new Task(5, "Dinner Prep"),
            new Task(6, "Pack Bag")
    );

    public final static List<Task> EVENING_TASKS = List.of(
            new Task(7, "Pack Balls")
    );
    public final static Routine MORNING_ROUTINE = new Routine(0, "Morning Routine", MORNING_TASKS);

    public final static Routine EVENING_ROUTINE = new Routine(1, "Evening Routine", EVENING_TASKS);

    private static ActiveRoutine activeRoutine = null;

    private static MutableSubject<ActiveRoutine> activeRoutineMutableSubject;
//
//    public final static RoutineList DEFAULT_ROUTINE_LIST = new RoutineList(List.of(DEFAULT_ROUTINE));

    public static InMemoryDataSource fromDefault() {
        var data = new InMemoryDataSource();
        data.putRoutine(MORNING_ROUTINE);
        data.putRoutine(EVENING_ROUTINE);
        data.putTaskList(MORNING_TASKS);
        data.putTaskList(EVENING_TASKS);

        return data;
    }
}
