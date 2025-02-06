package edu.ucsd.cse110.habitizer.app;

import static androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY;

import androidx.lifecycle.ViewModel;
import androidx.lifecycle.viewmodel.ViewModelInitializer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import edu.ucsd.cse110.habitizer.lib.domain.Task;
import edu.ucsd.cse110.habitizer.lib.domain.TaskRepository;
import edu.ucsd.cse110.habitizer.lib.util.Subject;

public class MainViewModel extends ViewModel {
    private static final String LOG_TAG = "MainViewModel";

    // Domain state (true "Model" state)
    private final TaskRepository taskRepository;

    // UI state
    private final Subject<List<Integer>> taskOrdering;

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
        this.taskOrdering = new Subject<>();

        taskRepository.findAll().observe(tasks -> {
            if (tasks == null) return;

            var ordering = new ArrayList<Integer>();
            for (int i = 0; i < tasks.size(); i++) {
                ordering.add(i);
            }
            taskOrdering.setValue(ordering);
        });
    }
}
