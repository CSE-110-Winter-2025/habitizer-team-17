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
import edu.ucsd.cse110.habitizer.lib.domain.ActiveRoutineRepository;
import edu.ucsd.cse110.habitizer.lib.domain.ActiveTask;
import edu.ucsd.cse110.habitizer.lib.domain.CustomTimerRepository;
import edu.ucsd.cse110.habitizer.lib.domain.MockCustomTimer;
import edu.ucsd.cse110.habitizer.lib.domain.Routine;
import edu.ucsd.cse110.habitizer.lib.domain.RoutineRepository;
import edu.ucsd.cse110.habitizer.lib.domain.Task;
import edu.ucsd.cse110.habitizer.lib.domain.TimerState;
import edu.ucsd.cse110.observables.MutableSubject;
import edu.ucsd.cse110.observables.PlainMutableSubject;
import edu.ucsd.cse110.habitizer.lib.domain.CustomTimer;

public class MainViewModel extends ViewModel {
    // Domain state
    private final RoutineRepository routineRepository;
    private final ActiveRoutineRepository activeRoutineRepository;
    private final CustomTimerRepository customTimerRepository;

    // UI state
    private final MutableSubject<Screen> screen;
    private final MutableSubject<List<Routine>> orderedRoutines;
    private final MutableSubject<Routine> currentRoutine;
    private final MutableSubject<String> title;
    private final MutableSubject<List<Task>> orderedTasks;
    private final MutableSubject<CustomTimer> timer;
    private final MutableSubject<Long> currentTime;
    private final MutableSubject<String> currentTimeDisplay;
    private final MutableSubject<String> completedTimeDisplay;
    private final MutableSubject<TimerState> timerState;
    private final MutableSubject<Integer> goalTime;
    private final MutableSubject<String> goalTimeDisplay;
    private final MutableSubject<Boolean> onFinishedRoutine;

    private final MutableSubject<String> elapsedSinceLastTaskDisplay;
    private boolean isAddingNewRoutine = false;

    private final boolean isMocked = true; //CHANGE THIS IF YOU WANT IT TO BE MOCKED/ NOT MOCKED


    // TODO: CITE
    // Handler for updating the current time on the main thread
    private final Handler handler = new Handler(Looper.getMainLooper());
    // Runnable that updates currentTime every second
    private final Runnable updateCurrentTimeRunnable = new Runnable() {
        @Override
        public void run() {
            if (timer.getValue() == null || timerState.getValue() == null) return;

            if (timerState.getValue() != null && timerState.getValue() == TimerState.RUNNING) {
                currentTime.setValue(timer.getValue().getElapsedTimeInMilliseconds());

                // Update elapsed time since last task
                if (activeRoutine.getValue() != null) {
                    long lastTaskEndTime = activeRoutine.getValue().previousTaskEndTime();
                    long currentElapsedSinceLastTask = timer.getValue().getElapsedTimeInMilliseconds() - lastTaskEndTime;
                    updateElapsedSinceLastTaskDisplay(currentElapsedSinceLastTask);
                }

                handler.postDelayed(this, CustomTimer.MILLISECONDS_PER_SECOND);
            }
        }
    };

    private void updateElapsedSinceLastTaskDisplay(long milliseconds) {
        if (milliseconds < 0) milliseconds = 0;

        long totalSeconds = milliseconds / CustomTimer.MILLISECONDS_PER_SECOND;

        // Format according to requirements
        String formattedTime;
        if (totalSeconds < 60) {
            // Less than a minute, show in 5-second increments
            long roundedSeconds = 5 * Math.round(totalSeconds / 5.0f);
            // If rounding gives us 60 seconds, show as 1m
            if (roundedSeconds >= 60) {
                formattedTime = "1m";
            } else {
                formattedTime = roundedSeconds + "s";
            }
        } else {
            // 60 seconds or more, show in minutes
            long minutes = totalSeconds / 60;
            formattedTime = minutes + "m";
        }

        elapsedSinceLastTaskDisplay.setValue(formattedTime);
    }

    private final MutableSubject<ActiveRoutine> activeRoutine;

    public static final ViewModelInitializer<MainViewModel> initializer =
            new ViewModelInitializer<>(
                    MainViewModel.class,
                    creationExtras -> {
                        var app = (HabitizerApplication) creationExtras.get(APPLICATION_KEY);
                        assert app != null;
                        return new MainViewModel(app.getRoutineRepository(),
                                app.getActiveRoutineRepository(),
                                app.getCustomTimerRepository());
                    }
            );

    public MainViewModel(RoutineRepository routineRepository,
                         ActiveRoutineRepository activeRoutineRepository,
                         CustomTimerRepository customTimerRepository) {
        this.routineRepository = routineRepository;
        this.activeRoutineRepository = activeRoutineRepository;
        this.customTimerRepository = customTimerRepository;
        // Initialize observables
        this.screen = new PlainMutableSubject<>(Screen.PREVIEW_SCREEN);

        this.orderedRoutines = new PlainMutableSubject<>();
        this.orderedTasks = new PlainMutableSubject<>();
        this.currentRoutine = new PlainMutableSubject<>();
        this.title = new PlainMutableSubject<>();
        this.activeRoutine = new PlainMutableSubject<>();

        this.completedTimeDisplay = new PlainMutableSubject<>();
        this.timerState = new PlainMutableSubject<>();
        this.currentTime = new PlainMutableSubject<>();
        this.currentTimeDisplay = new PlainMutableSubject<>();
        this.onFinishedRoutine = new PlainMutableSubject<>(false);
        if (!isMocked) {
            this.timer = new PlainMutableSubject<>(new CustomTimer());
        } else {
            this.timer = new PlainMutableSubject<>(new MockCustomTimer());
        }
        this.goalTime = new PlainMutableSubject<>();
        this.goalTimeDisplay = new PlainMutableSubject<>();
        this.currentTime.setValue((long) 0);
        timerState.setValue(TimerState.INITIAL);
        completedTimeDisplay.setValue("");
        this.elapsedSinceLastTaskDisplay = new PlainMutableSubject<>("");

        // Load stored active routine if one is saved
        activeRoutineRepository.find().observe(storedActiveRoutine -> {
                    if (storedActiveRoutine == null) return;
                    activeRoutine.setValue(storedActiveRoutine);
                    if (timer.getValue() != null)
                        updateElapsedSinceLastTaskDisplay(timer.getValue().getElapsedTimeInMilliseconds() - storedActiveRoutine.previousTaskEndTime());

            screen.setValue(Screen.ACTIVE_ROUTINE_SCREEN);
                }
        );

        customTimerRepository.find().observe(storedCustomTimer -> {
            if (storedCustomTimer == null) return;
            CustomTimer customTimer;
            if (isMocked) {
                customTimer = new MockCustomTimer(storedCustomTimer.getState(), storedCustomTimer.getElapsedTimeInMilliseconds());
            } else {
                customTimer = storedCustomTimer;
            }
            timer.setValue(customTimer);
            timerState.setValue(customTimer.getState());
            currentTime.setValue(customTimer.getElapsedTimeInMilliseconds());
            if (activeRoutine.getValue() != null)
                updateElapsedSinceLastTaskDisplay(customTimer.getElapsedTimeInMilliseconds() - activeRoutine.getValue().previousTaskEndTime());

            if (customTimer.getState() == TimerState.STOPPED) {
                String finalTime =
                        getFormattedTime(customTimer.getElapsedTimeInMilliseconds() + 59 * CustomTimer.MILLISECONDS_PER_SECOND);
                completedTimeDisplay.setValue(finalTime);
                onFinishedRoutine.setValue(true);
            }
        });


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

            // If we're adding a new routine, look for the one with the highest sort order
            if (isAddingNewRoutine) {
                Routine highestSortOrderRoutine = routines.stream()
                        .max(Comparator.comparingInt(Routine::sortOrder))
                        .orElse(routines.get(0));

                currentRoutine.setValue(highestSortOrderRoutine);
                isAddingNewRoutine = false; // Reset the flag
                return;
            }

            // Normal case: Replace current routine with routine with same id if it exists,
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
        if (orderedRoutines.getValue().isEmpty()) return;

        // Find the current routine's position in the ordered list
        List<Routine> routines = orderedRoutines.getValue();
        int currentIndex = -1;

        for (int i = 0; i < routines.size(); i++) {
            if (Objects.equals(routines.get(i).id(), currentRoutine.getValue().id())) {
                currentIndex = i;
                break;
            }
        }

        // If not found for some reason, default to first routine
        if (currentIndex == -1) {
            currentRoutine.setValue(routines.get(0));
            return;
        }

        // Move to next routine
        int nextIndex = (currentIndex + 1) % routines.size();
        currentRoutine.setValue(routines.get(nextIndex));
    }

    public void navigateToNewlyCreatedRoutine(Routine newRoutine) {
        currentRoutine.setValue(newRoutine);
    }

    public void startRoutine() {
        startTimer();

        List<ActiveTask> activeTasks = new ArrayList<>();
        var routine = currentRoutine.getValue();
        if (routine == null) return;
        for (var task : routine.tasks()) {
            ActiveTask newActiveTask = new ActiveTask(task, false, 0);
            activeTasks.add(newActiveTask);
        }

        activeRoutine.setValue(new ActiveRoutine(routine, activeTasks,0L));

        screen.setValue(Screen.ACTIVE_ROUTINE_SCREEN);
    }

    public void checkTask(Integer id) {
        if (timer.getValue() == null) return;
        if (activeRoutine.getValue() == null) return;

        if (timerState.getValue() == TimerState.PAUSED) return;
        var task = activeRoutine.getValue().activeTasks().stream()
                .filter(activeTask -> Objects.equals(activeTask.task().id(), id))
                .findFirst();
        if (task.isEmpty()) return;

        // Get current elapsed time from timer
        long currentTime = timer.getValue().getElapsedTimeInMilliseconds();
        long currentElapsedTime = currentTime - activeRoutine.getValue().previousTaskEndTime();
        var checkedTask = task.get().withChecked(true, currentElapsedTime);

        // Update the active routine with the checked task and new end time
        activeRoutine.setValue(activeRoutine.getValue()
                .withActiveTask(checkedTask)
                .withPreviousTaskEndTime(currentTime));

        // Update elapsed time display immediately to show 0
        updateElapsedSinceLastTaskDisplay(0);
    }

    public boolean checkIfAllCompleted() {
        boolean result = true;
        for (var task : activeRoutine.getValue().activeTasks()) {
            if (!task.checked())
                result = false;
        }
        return result;
    }

    private void updateCurrentTimeDisplay(long currentTime) {
        currentTimeDisplay.setValue(getFormattedTime(currentTime));
    }

    public void startTimer() {

        if (timer.getValue() == null) return;
        timer.getValue().reset();
        timer.getValue().start();
        timerState.setValue(TimerState.RUNNING);

        // When starting a new routine, set the initial previousTaskEndTime to the current time (0)
        if (activeRoutine.getValue() != null) {
            activeRoutine.setValue(activeRoutine.getValue().withPreviousTaskEndTime(0L));
            updateElapsedSinceLastTaskDisplay(0);
        }

        // Start the periodic update of currentTime
        handler.post(updateCurrentTimeRunnable);
    }

    public void pauseTimer() {
        if (timer.getValue() == null) return;
        timer.getValue().pause();
        timerState.setValue(TimerState.PAUSED);
    }

    public void stopTimer() {
        if (timer.getValue() != null && timerState.getValue() != null && timerState.getValue() == TimerState.RUNNING) {
            MockCustomTimer t = (MockCustomTimer) timer.getValue();
            t.stop();
            timerState.setValue(TimerState.STOPPED);
            // Stop the periodic updates
            handler.removeCallbacks(updateCurrentTimeRunnable);
            String finalTime =
                    getFormattedTime(currentTime.getValue() + 59 * CustomTimer.MILLISECONDS_PER_SECOND);
            completedTimeDisplay.setValue(finalTime);
        }
    }

    public void resumeTimer() {
        if (timer.getValue() == null) return;
        timer.getValue().resume();
        timerState.setValue(TimerState.RUNNING);
        handler.post(updateCurrentTimeRunnable);
        onFinishedRoutine.setValue(false);
    }

    public void forwardTimer() {
        if (timer.getValue() == null) return;
        MockCustomTimer mockedTimer = (MockCustomTimer) timer.getValue();
        if (mockedTimer.getState() != TimerState.RUNNING) return;
        mockedTimer.advance();
        currentTimeDisplay.setValue(getFormattedTime(timer.getValue().getElapsedTimeInMilliseconds()));
    }

    public MutableSubject<String> getTitle() {
        return title;
    }

    public MutableSubject<String> getCurrentTimeDisplay() {
        return currentTimeDisplay;
    }

    public MutableSubject<TimerState> getTimerState() {
        return timerState;
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

        // Create a new task with null ID - the repository will assign an ID
        Task task = new Task(null, taskName);

        // Create a new routine with the appended task
        var newRoutine = currentRoutine.getValue().withAppendedTask(task);

        // Save to repository (which will persist to database)
        routineRepository.save(newRoutine);

        // Add logging to track what's happening
        System.out.println("Task added: " + taskName + " to routine: " + newRoutine.name());
    }

    public boolean isMocked(){
        return isMocked;
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        // Remove any pending callbacks to avoid memory leaks
        handler.removeCallbacks(updateCurrentTimeRunnable);
        // save active routine if currently in one
        if (activeRoutine.getValue() != null) {
            activeRoutineRepository.save(activeRoutine.getValue());
            if (timer.getValue() == null) return;
            if (timer.getValue().getState() == TimerState.RUNNING) {
                timer.getValue().pause();
            }
            customTimerRepository.save(timer.getValue());
        }
    }


    public String getFormattedTime(long milliseconds) {

        long totalSeconds = milliseconds / CustomTimer.MILLISECONDS_PER_SECOND;
        long hours = totalSeconds / 3600;
        long minutes = (totalSeconds % 3600) / 60;

        return (hours > 0)
                ? String.format("%dh:%02d/", hours, minutes)
                : String.format("%dm/", minutes);
    }

    public String formatElapsedTime(long milliseconds) {
        if (milliseconds < 0) milliseconds = 0;

        long totalSeconds = milliseconds / CustomTimer.MILLISECONDS_PER_SECOND;

        // If less than a minute, show in seconds
        if (totalSeconds < 60) {
            return totalSeconds + "s";
        } else {
            // Otherwise show in minutes (ceiling to next minute if not exact)
            long minutes = (totalSeconds + 59) / 60;  // Ceiling division
            return minutes + "m";
        }
    }



    public void endRoutine() {
        stopTimer();
        onFinishedRoutine.setValue(true);
    }

    public MutableSubject<Boolean> getOnFinishedRoutine() {
        return onFinishedRoutine;
    }

    public void resetRoutine() {
        if (activeRoutine.getValue() == null) return;
        currentRoutine.setValue(activeRoutine.getValue().routine());
        activeRoutine.setValue(null);
        activeRoutineRepository.delete();
        customTimerRepository.delete();
        onFinishedRoutine.setValue(false);
    }

    public void renameTask(int taskId, String newName) {
        if (currentRoutine.getValue() == null) return;
        if (newName.isBlank()) return;

        var routine = currentRoutine.getValue();
        var updatedRoutine = routine.withRenamedTask(taskId, newName);
        routineRepository.save(updatedRoutine);
    }


    public void removeRoutine(Routine routine) {
        routineRepository.delete(routine);
    }

    public void addRoutineToEnd(String routineName) {
        if (orderedRoutines.getValue() == null) return;

        List<Routine> routines = orderedRoutines.getValue();
        int maxSortOrder = 0;

        // Find the maximum sort order
        for (Routine r : routines) {
            if (r.sortOrder() > maxSortOrder) {
                maxSortOrder = r.sortOrder();
            }
        }

        // Set flag to indicate we're adding a new routine
        isAddingNewRoutine = true;

        // Create with sort order one higher than the max
        Routine newRoutine = new Routine(null, routineName, new ArrayList<>(), 0, maxSortOrder + 1);
        routineRepository.save(newRoutine);
    }


    public void moveTask(int taskId, int direction) { //up is 1, down is 0
        routineRepository.save(currentRoutine.getValue().moveTaskOrdering(taskId, direction));
    }

    public void deleteTask(int id) {
        if (currentRoutine.getValue() == null) return;

        var routine = currentRoutine.getValue();
        var updatedRoutine = routine.withoutTask(id);
        routineRepository.save(updatedRoutine);

    }
    public MutableSubject<String> getElapsedSinceLastTaskDisplay() {
        return elapsedSinceLastTaskDisplay;
    }

    public MutableSubject<CustomTimer> getTimer() {
        return timer;
    }
}
