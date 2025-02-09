package edu.ucsd.cse110.habitizer.app;

import static androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY;

import androidx.lifecycle.ViewModel;
import androidx.lifecycle.viewmodel.ViewModelInitializer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import edu.ucsd.cse110.habitizer.lib.domain.Task;
import edu.ucsd.cse110.habitizer.lib.domain.TaskRepository;
import edu.ucsd.cse110.observables.MutableSubject;
import edu.ucsd.cse110.observables.PlainMutableSubject;

public class MainViewModel extends ViewModel {
    private static final String LOG_TAG = "MainViewModel";

    // Domain state (true "Model" state)
    private final TaskRepository taskRepository;

    // UI state
    private final MutableSubject<List<Integer>> taskOrdering;
    private final MutableSubject<List<Task>> orderedTasks;
    private final MutableSubject<Boolean> isShowingMorning;
    private final MutableSubject<String> title;

    public static final ViewModelInitializer<MainViewModel> initializer =
            new ViewModelInitializer<>(
                    MainViewModel.class,
                    creationExtras -> {
                        var app = (HabitizerApplication) creationExtras.get(APPLICATION_KEY);
                        assert app != null;
                        return new MainViewModel((app.getTaskRepository()));
                    }
            );

    public MainViewModel(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;

        // Create the observable objects
        this.taskOrdering = new PlainMutableSubject<>();
        this.orderedTasks = new PlainMutableSubject<>();
        this.isShowingMorning = new PlainMutableSubject<>();
        this.title = new PlainMutableSubject<>();

        // Initialize ordering when tasks are loaded
        taskRepository.findAll().observe(tasks -> {
            if (tasks == null) return;

            var ordering = new ArrayList<Integer>();
            for (int i = 0; i < tasks.size(); i++) {
                ordering.add(i);
            }
            taskOrdering.setValue(ordering);
        });

        isShowingMorning.setValue(true);

        // Update ordered tasks when the ordering changes
        taskOrdering.observe(ordering -> {
            if (ordering == null) return;

            var tasks = new ArrayList<Task>();
            for (var id : ordering) {
                var task = taskRepository.find(id).getValue();
                if (task == null) return;
                tasks.add(task);
            }
            this.orderedTasks.setValue(tasks);
        });

        // When the top routine changes, update the routineId
        isShowingMorning.observe(isShowingMorning -> {
            if (isShowingMorning == null) return;
            title.setValue(isShowingMorning ? "Morning Routine" : "Evening Routine");
        });
    }

    public MutableSubject<List<Task>> getOrderedTasks() {
        return orderedTasks;
    }

    public void nextRoutine() {
        var isShowingMorning = this.isShowingMorning.getValue();
        if (isShowingMorning == null) return;
        this.isShowingMorning.setValue(!isShowingMorning);
    }

    public MutableSubject<Boolean> getIsShowingMorning() {
        return this.isShowingMorning;
    }

    public MutableSubject<String> getTitle() {
        return this.title;
    }
}
