package edu.ucsd.cse110.habitizer.app;

import static androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.lifecycle.ViewModel;
import androidx.lifecycle.viewmodel.ViewModelInitializer;

import java.util.ArrayList;
import java.util.List;

import edu.ucsd.cse110.habitizer.lib.domain.Routine;
import edu.ucsd.cse110.habitizer.lib.domain.RoutineList;
import edu.ucsd.cse110.habitizer.lib.domain.RoutineRepository;
import edu.ucsd.cse110.habitizer.lib.domain.Task;
import edu.ucsd.cse110.habitizer.lib.domain.TaskRepository;
import edu.ucsd.cse110.observables.MutableSubject;
import edu.ucsd.cse110.observables.PlainMutableSubject;
import edu.ucsd.cse110.habitizer.lib.domain.CustomTimer;

public class MainViewModel extends ViewModel {
    private static final String LOG_TAG = "MainViewModel";

    // Domain state
    private final RoutineRepository routineRepository;
    private final TaskRepository taskRepository;

    // UI state
    private final MutableSubject<List<Integer>> routineOrdering;
    private final MutableSubject<List<Routine>> orderedRoutines;
    private final MutableSubject<List<Integer>> taskOrdering;
    private final MutableSubject<List<Task>> orderedTasks;
    private final MutableSubject<Routine> currentRoutine;
    private final MutableSubject<String> title;
    private final CustomTimer timer;
    private final MutableSubject<String> currentTime;
    private final MutableSubject<String> completedTime;
    private final MutableSubject<Boolean> isTimerRunning;
    private final MutableSubject<Boolean> isShowingMorning;
    // TODO: CITE
    // Handler for updating the current time on the main thread
    private final Handler handler = new Handler(Looper.getMainLooper());
    // Runnable that updates currentTime every second
    private final Runnable updateCurrentTimeRunnable = new Runnable() {
        @Override
        public void run() {
            if (isTimerRunning.getValue() != null && isTimerRunning.getValue()) {
                String formatted = timer.getFormattedTime();
                Log.d("MainViewModel", "Updating currentTime: " + formatted);
                currentTime.setValue(formatted);
                Log.d("MainViewModel", "Updating currentTime: " + currentTime.getValue());
                handler.postDelayed(this, 1000);
            }
        }
    };

    public static final ViewModelInitializer<MainViewModel> initializer =
            new ViewModelInitializer<>(
                    MainViewModel.class,
                    creationExtras -> {
                        var app = (HabitizerApplication) creationExtras.get(APPLICATION_KEY);
                        assert app != null;
                        return new MainViewModel(app.getRoutineRepository(), app.getTaskRepository());
                    }
            );

    public MainViewModel(RoutineRepository routineRepository, TaskRepository taskRepository) {
        this.routineRepository = routineRepository;
        this.taskRepository = taskRepository;
        // Initialize observables
        this.routineOrdering = new PlainMutableSubject<>();
        this.orderedRoutines = new PlainMutableSubject<>();
        this.isShowingMorning = new PlainMutableSubject<>();
        this.taskOrdering = new PlainMutableSubject<>();
        this.orderedTasks = new PlainMutableSubject<>();
        this.currentRoutine = new PlainMutableSubject<>();
        this.title = new PlainMutableSubject<>();
        this.completedTime = new PlainMutableSubject<>();
        this.isTimerRunning = new PlainMutableSubject<>();
        this.currentTime = new PlainMutableSubject<>();
        this.timer = new CustomTimer();
        this.currentTime.setValue("00:00");
        isShowingMorning.setValue(true);
        isTimerRunning.setValue(false);
        completedTime.setValue("");

        routineRepository.findAll().observe(routines -> {
            if (routines == null) return;
            var ordering = new ArrayList<Integer>();
            for (int i = 0; i < routines.size(); i++) {
                ordering.add(routines.get(i).id());
            }
            routineOrdering.setValue(ordering);
        });

        routineOrdering.observe(ordering -> {
            if (ordering == null) return;
            var routines = new ArrayList<Routine>();
            for (var id : ordering) {
                var routine = routineRepository.find(id).getValue();
                if (routine == null) return;
                routines.add(routine);
            }
            orderedRoutines.setValue(routines);
            currentRoutine.setValue(routines.get(0));
        });

        currentRoutine.observe(routine -> {
            if (routine == null) return;
            var ordering = new ArrayList<Integer>();
            for (int i = 0; i < routine.getTasks().size(); i++) {
                ordering.add(routine.getTasks().get(i).id());
            }
            title.setValue(routine.getName());
            taskOrdering.setValue(ordering);
        });

        taskOrdering.observe(ordering -> {
            if (ordering == null) return;
            var tasks = new ArrayList<Task>();
            for (var id : ordering) {
                var task = taskRepository.find(id).getValue();
                if (task == null) return;
                tasks.add(task);
            }
            orderedTasks.setValue(tasks);
        });
    }

    public MutableSubject<List<Task>> getOrderedTasks() {
        return orderedTasks;
    }

    public void nextRoutine() {
        if (routineOrdering.getValue() == null) return;
        var newOrdering = RoutineList.rotateRoutine(routineOrdering.getValue(), 1);
        routineOrdering.setValue(newOrdering);
    }

    private void updateTitle(boolean isShowingMorning) {
        String routineTitle = isShowingMorning ? "Morning Routine" : "Evening Routine";
        String timeDisplay = completedTime.getValue();
        if (timeDisplay != null && !timeDisplay.isEmpty()) {
            routineTitle += " - " + timeDisplay;
        }
        title.setValue(routineTitle);
    }

    public void startTimer() {
        timer.reset();                    // Reset timer to 0 before starting
        completedTime.setValue("00:00");   // Reset display time
        updateTitle(isShowingMorning.getValue());
        timer.start();                    // Start the timer
        isTimerRunning.setValue(true);
        // Start the periodic update of currentTime
        handler.post(updateCurrentTimeRunnable);
    }

    public void stopTimer() {
        if (isTimerRunning.getValue() != null && isTimerRunning.getValue()) {
            timer.stop();
            isTimerRunning.setValue(false);
            // Stop the periodic updates
            handler.removeCallbacks(updateCurrentTimeRunnable);
            String finalTime = timer.getFormattedTime();
            completedTime.setValue(finalTime);
            updateTitle(isShowingMorning.getValue());
        }
    }

    public void forwardTimer() {
        if (!timer.isRunning()) {
            timer.setMockMode(true);
            timer.forward();
            String currentTimeStr = timer.getFormattedTime();
            completedTime.setValue(currentTimeStr);
            updateTitle(isShowingMorning.getValue());
        }
    }

    public MutableSubject<String> getTitle() {
        return title;
    }

    public MutableSubject<Boolean> getIsTimerRunning() {
        return isTimerRunning;
    }

    public MutableSubject<String> getCompletedTime() {
        return completedTime;
    }

    public MutableSubject<String> getCurrentTime() {
        return currentTime;
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        // Remove any pending callbacks to avoid memory leaks
        handler.removeCallbacks(updateCurrentTimeRunnable);
    }
}
