package edu.ucsd.cse110.habitizer.lib.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.ucsd.cse110.habitizer.lib.domain.Task;
import edu.ucsd.cse110.habitizer.lib.domain.Routine;
import edu.ucsd.cse110.observables.MutableSubject;
import edu.ucsd.cse110.observables.PlainMutableSubject;

public class InMemoryDataSource {
    private int nextId = 0;
    private final Map<Integer, Routine> routines = new HashMap<>();
    private final Map<Integer, MutableSubject<Routine>> routineSubjects = new HashMap<>();
    private final MutableSubject<List<Routine>> allRoutinesSubject = new PlainMutableSubject<>();

    public InMemoryDataSource() {

    }

    public Routine getRoutine(int id) {
        return routines.get(id);
    }

    public List<Routine> getRoutines() {
        return List.copyOf(routines.values());
    }

    public MutableSubject<Routine> getRoutineSubject(int id) {
        if (!routineSubjects.containsKey(id)) {
            var subject = new PlainMutableSubject<Routine>();
            subject.setValue(getRoutine(id));
            routineSubjects.put(id, subject);
        }
        return routineSubjects.get(id);
    }

    public MutableSubject<List<Routine>> getAllRoutinesSubject() {
        return allRoutinesSubject;
    }


    public void putRoutine(Routine routine) {
        routine = preInsert(routine);
        routines.put(routine.id(), routine);
        if (routineSubjects.containsKey(routine.id())) {
            routineSubjects.get(routine.id()).setValue(routine);
        }
        allRoutinesSubject.setValue(getRoutines());
    }

    // assign ids to newly created tasks that don't have ids yet
    // temporary solution until we can persist data
    private Routine preInsert(Routine routine) {
        var newTasks = new ArrayList<Task>();
        for (var task : routine.tasks()) {
            var id = task.id();
            if (id == null) {
                newTasks.add(task.withId(getNextAvailableId()));
            } else {
                newTasks.add(task);
            }
        }
        return routine.withTasks(newTasks);
    }

    private int getNextAvailableId() {
        while (routines.containsKey(nextId)) nextId++;
        return nextId;
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
    public final static Routine MORNING_ROUTINE = new Routine(
            0, "Morning Routine", MORNING_TASKS, 20, 0
    );
    public final static Routine EVENING_ROUTINE = new Routine(
            1, "Evening Routine", EVENING_TASKS, 10, 1
    );

    public static InMemoryDataSource fromDefault() {
        var data = new InMemoryDataSource();

        data.putRoutine(MORNING_ROUTINE);
        data.putRoutine(EVENING_ROUTINE);

        return data;
    }
}
