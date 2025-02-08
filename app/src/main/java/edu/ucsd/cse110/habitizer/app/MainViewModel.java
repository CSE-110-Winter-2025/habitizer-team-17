package edu.ucsd.cse110.habitizer.app;

import static androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY;

import androidx.lifecycle.ViewModel;
import androidx.lifecycle.viewmodel.ViewModelInitializer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Timer;

import edu.ucsd.cse110.habitizer.lib.domain.Task;
import edu.ucsd.cse110.habitizer.lib.domain.TaskRepository;
import edu.ucsd.cse110.habitizer.lib.util.Subject;


public class MainViewModel extends ViewModel {
    private static final String LOG_TAG = "MainViewModel";

    // Domain state (true "Model" state)
    private final TaskRepository taskRepository;

    // UI state
    private final Subject<List<Integer>> taskOrdering;
    private final Subject<List<Task>> orderedTasks;
    private final Subject<Boolean> isShowingMorning;
    private final Subject<String> title;
    private final Subject<String> completedTime;
    private final Subject<Boolean> isTimerRunning;

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
        this.orderedTasks = new Subject<>();
        this.isShowingMorning = new Subject<>();
        this.title = new Subject<>();

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
    private void updateTitle(boolean isShowingMorning) {
        String routineTitle = isShowingMorning ? "Morning Routine" : "Evening Routine";
        String timeDisplay = completedTime.getValue();

        if (timeDisplay != null && !timeDisplay.isEmpty()) {
            routineTitle += " - " + timeDisplay;
        }

        title.setValue(routineTitle);
    }

    // Timer control methods
    public void startTimer() {
        completedTime.setValue("");
        updateTitle(isShowingMorning.getValue());
        timer.start();
        isTimerRunning.setValue(true);
    }
    public void stopTimer() {
        if (isTimerRunning.getValue()) {
            timer.stop();
            isTimerRunning.setValue(false);
            // Update completed time when stopping
            String finalTime = timer.getFormattedTime();
            completedTime.setValue(finalTime);
            updateTitle(isShowingMorning.getValue());
        }
    }

    public void forwardTimer() {
        if (!timer.isRunning()) {
            timer.setMockMode(true);
            timer.forward();
            String currentTime = timer.getFormattedTime();
            completedTime.setValue(currentTime);
            updateTitle(isShowingMorning.getValue());
        }
    }


    public Subject<List<Task>> getOrderedTasks() {
        return orderedTasks;
    }

    public void nextRoutine() {
        var isShowingMorning = this.isShowingMorning.getValue();
        if (isShowingMorning == null) return;
        this.isShowingMorning.setValue(!isShowingMorning);
    }

    public Subject<Boolean> getIsShowingMorning() {
        return this.isShowingMorning;
    }

    public Subject<String> getTitle() {
        return this.title;
    }

    public Subject<Boolean> getIsTimerRunning() {
        return isTimerRunning;
    }

    public Subject<String> getCompletedTime() {
        return completedTime;
    }
}
