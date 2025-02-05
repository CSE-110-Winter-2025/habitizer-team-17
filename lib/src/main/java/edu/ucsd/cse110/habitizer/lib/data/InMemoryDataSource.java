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

    private final Map<Integer, Task> routine = new HashMap<>();
    private final Map<Integer, Subject<Task>> routineSubjects = new HashMap<>();
    private final Subject<List<Task>> allRoutinesSubject = new Subject<>();

    private final Map<Integer, Task> routineList = new HashMap<>();
    private final Subject<RoutineList> routineListSubject = new Subject<>();



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

    public void putTask(Task flashcard) {
        tasks.put(flashcard.id(), flashcard);
        if (taskSubjects.containsKey(flashcard.id())) {
            taskSubjects.get(flashcard.id()).setValue(flashcard);
        }
        allTasksSubject.setValue(getTasks());
    }

    public void putRoutine(Routine routine) {};

    public void putRoutineList(RoutineList routineList) {
        routineListSubject.setValue(routineList);
    };

    public final static List<Task> DEFAULT_TASKS = List.of(
            new Task("Shower", 0),
            new Task("Brush Teeth", 1),
            new Task("Dress", 2),
            new Task("Make Coffee", 3),
            new Task("Make Lunch", 4),
            new Task("Dinner Prep", 5),
            new Task("Pack Bag", 6)
    );
    public final static Routine DEFAULT_ROUTINE = new Routine(DEFAULT_TASKS, "routine_1", 0);

    public final static RoutineList DEFAULT_ROUTINE_LIST = new RoutineList(List.of(DEFAULT_ROUTINE));

    public static InMemoryDataSource fromDefault() {
        var data = new InMemoryDataSource();
        data.putRoutineList(DEFAULT_ROUTINE_LIST);
        return data;
    }
}
