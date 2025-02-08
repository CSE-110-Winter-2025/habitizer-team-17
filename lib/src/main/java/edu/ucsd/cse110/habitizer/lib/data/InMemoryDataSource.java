package edu.ucsd.cse110.habitizer.lib.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.ucsd.cse110.habitizer.lib.domain.RoutineList;
import edu.ucsd.cse110.habitizer.lib.domain.Task;
import edu.ucsd.cse110.habitizer.lib.domain.Routine;
import edu.ucsd.cse110.habitizer.lib.util.Subject;
public class InMemoryDataSource {
    private final Map<Integer, Task> tasks = new HashMap<>();
    private final Map<Integer, Subject<Task>> taskSubjects = new HashMap<>();
    private final Subject<List<Task>> allTasksSubject = new Subject<>();

//    private final Map<Integer, Routine> routines = new HashMap<>();
//    private final Map<Integer, Subject<Routine>> routineSubjects = new HashMap<>();
//    private final Subject<List<Routine>> allRoutinesSubject = new Subject<>();
//
//    private final Subject<RoutineList> routineListSubject = new Subject<>();



    public InMemoryDataSource() {

    }

    public List<Task> getTasks() {
        return List.copyOf(tasks.values());
    }

    public Task getTask(int id) {
        return tasks.get(id);
    }

    public Subject<Task> getTaskSubject(int id) {
        if (!taskSubjects.containsKey(id)) {
            var subject = new Subject<Task>();
            subject.setValue(getTask(id));
            taskSubjects.put(id, subject);
        }
        return taskSubjects.get(id);
    }

    public Subject<List<Task>> getAllTasksSubject() {
        return allTasksSubject;
    }

    public void putTask(Task task) {
        tasks.put(task.id(), task);
        if (taskSubjects.containsKey(task.id())) {
            taskSubjects.get(task.id()).setValue(task);
        }
        allTasksSubject.setValue(getTasks());
    }

//    public List<Routine> getRoutines() {
//        return List.copyOf(routines.values());
//    }
//
//    public Routine getRoutine(int id) {
//        return routines.get(id);
//    }
//
//    public Subject<Routine> getRoutineSubject(int id) {
//        if (!routineSubjects.containsKey(id)) {
//            var subject = new Subject<Routine>();
//            subject.setValue(getRoutine(id));
//            routineSubjects.put(id, subject);
//        }
//        return routineSubjects.get(id);
//    }
//
//    public Subject<List<Routine>> getAllRoutinesSubject() {
//        return allRoutinesSubject;
//    }
//
//    public void putRoutine(Routine routine) {
//        routines.put(routine.id(), routine);
//        if (routineSubjects.containsKey(routine.id())) {
//            routineSubjects.get(routine.id()).setValue(routine);
//        }
//        allRoutinesSubject.setValue(getRoutines());
//    }
//
//    public void putRoutineList(RoutineList routineList) {
//        routineListSubject.setValue(routineList);
//    };

    public final static List<Task> DEFAULT_TASKS = List.of(
            new Task("Shower", 0, 0),
            new Task("Brush Teeth", 1, 0),
            new Task("Dress", 2, 0),
            new Task("Make Coffee", 3, 0),
            new Task("Make Lunch", 4, 0),
            new Task("Dinner Prep", 5, 0),
            new Task("Pack Bag", 6, 0),
            new Task("Pack Balls", 7, 1)
    );
    public final static Routine DEFAULT_ROUTINE = new Routine(DEFAULT_TASKS, "Morning Routine", 0);
//
//    public final static RoutineList DEFAULT_ROUTINE_LIST = new RoutineList(List.of(DEFAULT_ROUTINE));

    public static InMemoryDataSource fromDefault() {
        var data = new InMemoryDataSource();
        for (Task task : DEFAULT_TASKS) {
            data.putTask(task);
        }
        return data;
    }
}
