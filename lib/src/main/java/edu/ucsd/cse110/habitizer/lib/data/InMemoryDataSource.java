package edu.ucsd.cse110.habitizer.lib.data;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.ucsd.cse110.habitizer.lib.domain.Task;
import edu.ucsd.cse110.habitizer.lib.util.Subject;
public class InMemoryDataSource {
    private final Map<Integer, Task> tasks = new HashMap<>();
    private final Map<Integer, Subject<Task>> taskSubjects = new HashMap<>();
    private final Subject<List<Task>> allTasksSubject = new Subject<>();

    public InMemoryDataSource() {

    }

    public List<Task> getTasks() {
        return List.copyOf(tasks.values());
    }

    public Task getTask(int id) {
        return tasks.get(id);
    }

    public Subject<Task> getFlashcardSubject(int id) {
        if (!taskSubjects.containsKey(id)) {
            var subject = new Subject<Task>();
            subject.setValue(getTask(id));
            taskSubjects.put(id, subject);
        }
        return taskSubjects.get(id);
    }

    public Subject<List<Task>> getAllFlashcardsSubject() {
        return allTasksSubject;
    }

    public void putTask(Task flashcard) {
        tasks.put(flashcard.id(), flashcard);
        if (taskSubjects.containsKey(flashcard.id())) {
            taskSubjects.get(flashcard.id()).setValue(flashcard);
        }
        allTasksSubject.setValue(getTasks());
    }

    public final static List<Task> DEFAULT_CARDS = List.of();

    public static InMemoryDataSource fromDefault() {
        var data = new InMemoryDataSource();
        for (Task flashcard : DEFAULT_CARDS) {
            data.putTask(flashcard);
        }
        return data;
    }
}
