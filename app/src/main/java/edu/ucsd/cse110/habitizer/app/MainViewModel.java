package edu.ucsd.cse110.habitizer.app;

import static androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.lifecycle.ViewModel;
import androidx.lifecycle.viewmodel.ViewModelInitializer;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

import edu.ucsd.cse110.habitizer.lib.domain.ActiveRoutine;
import edu.ucsd.cse110.habitizer.lib.domain.ActiveTask;
import edu.ucsd.cse110.habitizer.lib.domain.Routine;
import edu.ucsd.cse110.habitizer.lib.domain.RoutineRepository;
import edu.ucsd.cse110.habitizer.lib.domain.Task;
import edu.ucsd.cse110.observables.MutableSubject;
import edu.ucsd.cse110.observables.PlainMutableSubject;
import edu.ucsd.cse110.habitizer.lib.domain.CustomTimer;

public class MainViewModel extends ViewModel {
    private static final String LOG_TAG = "MainViewModel";

    // Domain state
    private final RoutineRepository routineRepository;

    // UI state
    private final MutableSubject<List<Routine>> orderedRoutines;
    private final MutableSubject<Routine> currentRoutine;
    private final MutableSubject<String> title;
    private final MutableSubject<List<Task>> orderedTasks;
    private final CustomTimer timer;
    private final MutableSubject<String> currentTime;
    private final MutableSubject<String> currentTimeDisplay;
    private final MutableSubject<String> completedTime;
    private final MutableSubject<Boolean> isTimerRunning;
    private final MutableSubject<Integer> goalTime;
    private final MutableSubject<String> goalTimeDisplay;

    // TODO: CITE
    // Handler for updating the current time on the main thread
    private final Handler handler = new Handler(Looper.getMainLooper());
    // Runnable that updates currentTime every second
    private final Runnable updateCurrentTimeRunnable = new Runnable() {
        @Override
        public void run() {
            if (isTimerRunning.getValue() != null && isTimerRunning.getValue()) {
                String formatted = timer.getFormattedTime();
                currentTime.setValue(formatted);
                handler.postDelayed(this, 1000);
            }
        }
    };

    private final MutableSubject<ActiveRoutine> activeRoutine;

    public static final ViewModelInitializer<MainViewModel> initializer =
            new ViewModelInitializer<>(
                    MainViewModel.class,
                    creationExtras -> {
                        var app = (HabitizerApplication) creationExtras.get(APPLICATION_KEY);
                        assert app != null;
                        return new MainViewModel(app.getRoutineRepository());
                    }
            );

    public MainViewModel(RoutineRepository routineRepository) {
        this.routineRepository = routineRepository;
        // Initialize observables
        this.orderedRoutines = new PlainMutableSubject<>();
        this.orderedTasks = new PlainMutableSubject<>();
        this.currentRoutine = new PlainMutableSubject<>();
        this.title = new PlainMutableSubject<>();
        this.activeRoutine = new PlainMutableSubject<>();

        this.completedTime = new PlainMutableSubject<>();
        this.isTimerRunning = new PlainMutableSubject<>();
        this.currentTime = new PlainMutableSubject<>();
        this.currentTimeDisplay = new PlainMutableSubject<>();
        this.timer = new CustomTimer();
        this.goalTime = new PlainMutableSubject<>();
        this.goalTimeDisplay = new PlainMutableSubject<>();
        this.currentTime.setValue("0m");
        isTimerRunning.setValue(false);
        completedTime.setValue("");

        // Load routines when changed and order them
        routineRepository.findAll().observe(routines -> {
            if (routines == null) return;

            var newOrderedRoutines = routines.stream()
                    .sorted(Comparator.comparingInt(Routine::sortOrder))
                    .toList();

            orderedRoutines.setValue(newOrderedRoutines);
        });

        // Set currently displayed routine
        orderedRoutines.observe(routines -> {
            if (routines == null) return;
            if (currentRoutine.getValue() == null) {
                currentRoutine.setValue(routines.get(0));
                return;
            }

            // replace current routine with routine with same id if it exists
            // else default to first routine
            var routineWithSameId = routines.stream()
                    .filter(routine -> Objects.equals(routine.id(), currentRoutine.getValue().id()))
                    .findFirst();
            if (routineWithSameId.isPresent()) {
                currentRoutine.setValue(routineWithSameId.get());
            } else {
                currentRoutine.setValue(routines.get(0));
            }
        });


        // Update title when current routine changes
        currentRoutine.observe(routine -> {
            if (routine == null) return;
            title.setValue(routine.name());
        });


        // Update ordered tasks when current routine changes
        currentRoutine.observe(routine -> {
            if (routine == null) return;
            orderedTasks.setValue(routine.tasks());
        });

        // Update goal time when current routine changes
        currentRoutine.observe(routine -> {
            if (routine == null) return;
            goalTime.setValue(routine.goalTime());
        });

        currentRoutine.observe(routine -> {
            if (routine == null) return;
            List<ActiveTask> activeTasks = new ArrayList<>();
            for (var task : routine.tasks()) {
                ActiveTask newActiveTask = new ActiveTask(task, false);
                activeTasks.add(newActiveTask);
            }

            activeRoutine.setValue(new ActiveRoutine(routine, activeTasks));
        });

        activeRoutine.observe(routine -> {
            if (routine == null) return;
            System.out.println(routine.routine().name());
            System.out.println(routine.activeTasks().get(0).task().name());
        });

        currentTime.observe(time -> {
            if (time == null) return;
            updateCurrentTimeDisplay(currentTime.getValue());
        });

        goalTime.observe(time -> {
            if (time == null) return;
            // TODO: make transforming times into displays its own method
            var display = time + "m";
            goalTimeDisplay.setValue(display);
        });
    }

    public MutableSubject<List<Task>> getOrderedTasks() {
        return orderedTasks;
    }

    public MutableSubject<ActiveRoutine> getActiveRoutine() {
        return activeRoutine;
    }

    public void nextRoutine() {
        if (orderedRoutines.getValue() == null) return;
        if (currentRoutine.getValue() == null) return;

        var currentSortOrder = currentRoutine.getValue().sortOrder();
        var nextSortOrder = (currentSortOrder + 1) % orderedRoutines.getValue().size();

        currentRoutine.setValue(orderedRoutines.getValue().get(nextSortOrder));
    }

    public void checkTask(Integer id) {
        if (this.activeRoutine.getValue() == null) {
            return;
        }
        var task = activeRoutine.getValue().activeTasks().stream()
                .filter(activeTask -> Objects.equals(activeTask.task().id(), id))
                .findFirst();
        if (task.isEmpty()) return;
        var checkedTask = task.get().withChecked(true);
        activeRoutine.setValue(activeRoutine.getValue().withActiveTask(checkedTask));
    }

    private void updateCurrentTimeDisplay(String currentTime) {
        currentTimeDisplay.setValue(currentTime);
    }

    public void startTimer() {
        timer.reset();
        completedTime.setValue("00:00");
        timer.start();
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
            String finalTime = currentTimeDisplay.getValue();
            completedTime.setValue(finalTime);
        }
    }

    public void forwardTimer() {
        timer.forward();
        currentTimeDisplay.setValue(timer.getFormattedTime());
    }

    public MutableSubject<String> getTitle() {
        Log.d("MainViewModel", "This is from getTitle");
        return title;
    }

    public MutableSubject<String> getCurrentTimeDisplay() {
        return currentTimeDisplay;
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

    public void setCurrentRoutineGoalTime(int time) {
        if (currentRoutine.getValue() == null) return;
        var newRoutine = currentRoutine.getValue().withGoalTime(time);
        routineRepository.save(newRoutine);
    }


    public MutableSubject<String> getGoalTimeDisplay() {
        return goalTimeDisplay;
    }

    public MutableSubject<Routine> getCurrentRoutine() {
        return currentRoutine;
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        // Remove any pending callbacks to avoid memory leaks
        handler.removeCallbacks(updateCurrentTimeRunnable);
    }
}
