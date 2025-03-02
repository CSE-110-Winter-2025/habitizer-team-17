package edu.ucsd.cse110.habitizer.app;

import static androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY;

import android.os.Handler;
import android.os.Looper;

import androidx.lifecycle.ViewModel;
import androidx.lifecycle.viewmodel.ViewModelInitializer;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

import edu.ucsd.cse110.habitizer.lib.domain.ActiveRoutine;
import edu.ucsd.cse110.habitizer.lib.domain.ActiveTask;
import edu.ucsd.cse110.habitizer.lib.domain.MockTimer;
import edu.ucsd.cse110.habitizer.lib.domain.Routine;
import edu.ucsd.cse110.habitizer.lib.domain.RoutineRepository;
import edu.ucsd.cse110.habitizer.lib.domain.Task;
import edu.ucsd.cse110.observables.MutableSubject;
import edu.ucsd.cse110.observables.PlainMutableSubject;
import edu.ucsd.cse110.habitizer.lib.domain.CustomTimer;

public class MainViewModel extends ViewModel {
    // Domain state
    private final RoutineRepository routineRepository;

    // UI state
    private final MutableSubject<Screen> screen;
    private final MutableSubject<List<Routine>> orderedRoutines;
    private final MutableSubject<Routine> currentRoutine;
    private final MutableSubject<String> title;
    private final MutableSubject<List<Task>> orderedTasks;
    private final CustomTimer timer;
    private final MutableSubject<Long> currentTime;
    private final MutableSubject<String> currentTimeDisplay;
    private final MutableSubject<String> completedTimeDisplay;
    private final MutableSubject<Boolean> isTimerRunning;
    private final MutableSubject<Integer> goalTime;
    private final MutableSubject<String> goalTimeDisplay;

    private final MutableSubject<Boolean> onFinishedRoutine;



    private final boolean isMocked = true; //CHANGE THIS IF YOU WANT IT TO BE MOCKED/ NOT MOCKED

    // TODO: CITE
    // Handler for updating the current time on the main thread
    private final Handler handler = new Handler(Looper.getMainLooper());
    // Runnable that updates currentTime every second
    private final Runnable updateCurrentTimeRunnable = new Runnable() {
        @Override
        public void run() {
            if (isTimerRunning.getValue() != null && isTimerRunning.getValue()) {
                currentTime.setValue(timer.getElapsedTimeInMilliSeconds());
                handler.postDelayed(this, CustomTimer.MILLISECONDS_PER_SECOND);
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
        this.screen = new PlainMutableSubject<>(Screen.PREVIEW_SCREEN);

        this.orderedRoutines = new PlainMutableSubject<>();
        this.orderedTasks = new PlainMutableSubject<>();
        this.currentRoutine = new PlainMutableSubject<>();
        this.title = new PlainMutableSubject<>();
        this.activeRoutine = new PlainMutableSubject<>();

        this.completedTimeDisplay = new PlainMutableSubject<>();
        this.isTimerRunning = new PlainMutableSubject<>();
        this.currentTime = new PlainMutableSubject<>();
        this.currentTimeDisplay = new PlainMutableSubject<>();
        this.onFinishedRoutine = new PlainMutableSubject<>(false);
        if(!isMocked) {
            this.timer = new CustomTimer();
        } else {
            this.timer = new MockTimer();
        }
        this.goalTime = new PlainMutableSubject<>();
        this.goalTimeDisplay = new PlainMutableSubject<>();
        this.currentTime.setValue((long)0);
        isTimerRunning.setValue(false);
        completedTimeDisplay.setValue("");

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
            if (routines == null || routines.isEmpty()) {
                // Optionally: clear currentRoutine or set to null
                currentRoutine.setValue(null);
                return;
            }
            if (currentRoutine.getValue() == null) {
                currentRoutine.setValue(routines.get(0));
                return;
            }
            // Replace current routine with routine with same id if it exists,
            // else default to the first routine.
            var routineWithSameId = routines.stream()
                    .filter(routine -> Objects.equals(routine.id(), currentRoutine.getValue().id()))
                    .findFirst();
            if (routineWithSameId.isPresent()) {
                currentRoutine.setValue(routineWithSameId.get());
            } else {
                currentRoutine.setValue(routines.get(0));
            }
        });


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

    public MutableSubject<Screen> getScreen() {
        return screen;
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

        // Get current elapsed time from timer
        long currentTime = timer.getElapsedTimeInMilliSeconds();
        long currentElapsedTime = currentTime - activeRoutine.getValue().previousTaskEndTime();
        var checkedTask = task.get().withChecked(true, currentElapsedTime);
        activeRoutine.setValue(activeRoutine.getValue().withActiveTask(checkedTask).withPreviousTaskEndTime(currentTime));
    }

    public boolean checkIfAllCompleted(){
        boolean result = true;
        for(var task: activeRoutine.getValue().activeTasks()){
            if(!task.checked())
                result = false;
        }
        return result;
    }

    private void updateCurrentTimeDisplay(long currentTime) {
        currentTimeDisplay.setValue(getFormattedTime(currentTime));
    }

    public void startTimer() {
        timer.reset();
        timer.start();
        isTimerRunning.setValue(true);
        // Start the periodic update of currentTime
        handler.post(updateCurrentTimeRunnable);
    }

    public void stopTimer() {
        if (isTimerRunning.getValue() != null && isTimerRunning.getValue()) {
            MockTimer t = (MockTimer)timer;
            t.stop();
            isTimerRunning.setValue(false);
            // Stop the periodic updates
            handler.removeCallbacks(updateCurrentTimeRunnable);
            String finalTime = getFormattedTime(currentTime.getValue() + 59*CustomTimer.MILLISECONDS_PER_SECOND);
            completedTimeDisplay.setValue(finalTime);
        }
    }

    public void forwardTimer() {
        MockTimer mockedTimer = (MockTimer)timer;
        mockedTimer.forward();
        currentTimeDisplay.setValue(getFormattedTime(timer.getElapsedTimeInMilliSeconds()));
    }

    public MutableSubject<String> getTitle() {
        return title;
    }

    public MutableSubject<String> getCurrentTimeDisplay() {
        return currentTimeDisplay;
    }

    public MutableSubject<Boolean> getIsTimerRunning() {
        return isTimerRunning;
    }

    public MutableSubject<String> getCompletedTimeDisplay() {
        return completedTimeDisplay;
    }

    public MutableSubject<Long> getCurrentTime() {
        return currentTime;
    }

    public void setCurrentRoutineGoalTime(int time) {
        if (currentRoutine.getValue() == null) return;
        if (time < 0) return;
        var newRoutine = currentRoutine.getValue().withGoalTime(time);
        routineRepository.save(newRoutine);
    }


    public MutableSubject<String> getGoalTimeDisplay() {
        return goalTimeDisplay;
    }

    public MutableSubject<Routine> getCurrentRoutine() {
        return currentRoutine;
    }

    public void appendTaskToCurrentRoutine(String taskName) {
        if (currentRoutine.getValue() == null) return;
        if (taskName.isBlank()) return;
        Task task = new Task(null, taskName);
        var newRoutine = currentRoutine.getValue().withAppendedTask(task);
        System.out.println(newRoutine.tasks().get(newRoutine.tasks().size()-1));
        routineRepository.save(newRoutine);
    }

    public boolean isMocked(){
        return isMocked;
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        // Remove any pending callbacks to avoid memory leaks
        handler.removeCallbacks(updateCurrentTimeRunnable);
    }


    public String getFormattedTime(long milliseconds) {

        long totalSeconds = milliseconds / CustomTimer.MILLISECONDS_PER_SECOND;
        long hours = totalSeconds / 3600;
        long minutes = (totalSeconds % 3600) / 60;

        return (hours > 0)
                ? String.format("%dh:%02d/", hours, minutes)
                : String.format("%dm/", minutes);
    }


    public void endRoutine(){

        stopTimer();
        onFinishedRoutine.setValue(true);
    }

    public MutableSubject<Boolean> getOnFinishedRoutine(){
        return onFinishedRoutine;
    }

    public void resetRoutine(){
        onFinishedRoutine.setValue(false);

    }

    public void renameTask(int taskId, String newName) {
        if (currentRoutine.getValue() == null) return;
        if (newName.isBlank()) return;

        var routine = currentRoutine.getValue();
        var updatedRoutine = routine.withRenamedTask(taskId, newName);
        routineRepository.save(updatedRoutine);
    }
}
