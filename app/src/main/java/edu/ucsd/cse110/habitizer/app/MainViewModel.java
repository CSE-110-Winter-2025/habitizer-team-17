package edu.ucsd.cse110.habitizer.app;

import static androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;
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
import edu.ucsd.cse110.observables.Subject;

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
    private final MutableSubject<String> currentTimeDisplay;
    private final MutableSubject<String> completedTime;
    private final MutableSubject<Boolean> isTimerRunning;
    private final MutableSubject<Boolean> isShowingMorning;
    private final MutableSubject<Integer> goalTime;
    private final MutableSubject<String> goalTimeDisplay;
    private long lastCompletionSeconds;

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
        this.currentTimeDisplay = new PlainMutableSubject<>();
        this.timer = new CustomTimer();
        this.goalTime = new PlainMutableSubject<>();
        this.goalTimeDisplay = new PlainMutableSubject<>();
        this.currentTime.setValue("0m");
        isShowingMorning.setValue(true);
        isTimerRunning.setValue(false);
        completedTime.setValue("");
        this.lastCompletionSeconds = 0;
        loadTasks();

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
            goalTime.setValue(routine.getGoalTime());
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

        currentTime.observe(time -> {
            if (time == null) return;
            updateCurrentTimeDisplay(currentTime.getValue());
        });

        goalTime.observe(time -> {
            if (time == null) return;
            goalTimeDisplay.setValue(time.toString());
            updateGoalTimeDisplay(time);
        });
    }
    private void loadTasks() {
        var tasks = taskRepository.findAll().getValue();
        if (tasks != null) {
            orderedTasks.setValue(tasks);
        }
    }

    public MutableSubject<List<Task>> getOrderedTasks() {
        return orderedTasks;
    }

    public void toggleTaskCompletion(int taskId) {
        Task task = taskRepository.find(taskId).getValue();
        if (task != null) {
            boolean newCompletionState = !task.isCompleted();
            task.setCompleted(newCompletionState);

            if (newCompletionState && isTimerRunning.getValue()) {
                // Get current elapsed seconds
                long currentSeconds = timer.elapsedTime / timer.Final_Seconds;

                // Calculate difference and round up
                long diffSeconds = currentSeconds - lastCompletionSeconds;
                long roundedSeconds = ((diffSeconds + 59) / 60) * 60;  // Round up to nearest minute

                // Create temporary timer to format the difference
                CustomTimer tempTimer = new CustomTimer();
                tempTimer.elapsedTime = roundedSeconds * timer.Final_Seconds;
                String relativeTime = tempTimer.getFormattedTime();

                task.setCompletionTime(relativeTime);
                lastCompletionSeconds = currentSeconds;
            }
            taskRepository.save(task);
            loadTasks();
        }
    }


    public void nextRoutine() {
        if (routineOrdering.getValue() == null) return;
        var newOrdering = RoutineList.rotateRoutine(routineOrdering.getValue(), 1);
        isShowingMorning.setValue(!isShowingMorning.getValue());
        routineOrdering.setValue(newOrdering);
    }

    private void updateTitle(boolean isShowingMorning) {
        String routineTitle = isShowingMorning ? "Morning Routine" : "Evening Routine";
        title.setValue(routineTitle);
    }

    public MutableSubject<Boolean> getIsShowingMorning() {
        return this.isShowingMorning;
    }

    public MutableSubject<Integer> getGoalTime() {
        return goalTime;
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

    public void setRoutineGoalTime(int id, Integer time) {
        routineRepository.setRoutineGoalTime(id, time);
        goalTime.setValue(getRoutineGoalTime(id));
    }

    public int getRoutineGoalTime(int id) {
        return routineRepository.getRoutineTime(id);
    }

    public void updateGoalTimeDisplay(int time) {
        String newGoalTimeDisplay = time + "m";
        goalTimeDisplay.setValue(newGoalTimeDisplay + " ");
        goalTimeDisplay.setValue(newGoalTimeDisplay);
    }


    public MutableSubject<String> getGoalTimeDisplay() {
        return goalTimeDisplay;
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        // Remove any pending callbacks to avoid memory leaks
        handler.removeCallbacks(updateCurrentTimeRunnable);
    }
}
