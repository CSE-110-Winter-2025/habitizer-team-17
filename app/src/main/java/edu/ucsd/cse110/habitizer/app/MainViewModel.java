package edu.ucsd.cse110.habitizer.app;

import static androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.lifecycle.ViewModel;
import androidx.lifecycle.viewmodel.ViewModelInitializer;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import edu.ucsd.cse110.habitizer.lib.domain.ActiveRoutine;
import edu.ucsd.cse110.habitizer.lib.domain.ActiveTask;
import edu.ucsd.cse110.habitizer.lib.domain.MockTimer;
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
    private final MutableSubject<Integer> goalTime;
    private final MutableSubject<String> goalTimeDisplay;

    private final boolean isMocked = true; //CHANGE THIS IF YOU WANT IT TO BE MOCKED/ NOT MOCKED

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
                        return new MainViewModel(app.getRoutineRepository(), app.getTaskRepository());
                    }
            );

    public MainViewModel(RoutineRepository routineRepository, TaskRepository taskRepository) {
        this.routineRepository = routineRepository;
        this.taskRepository = taskRepository;
        // Initialize observables
        this.routineOrdering = new PlainMutableSubject<>();
        this.orderedRoutines = new PlainMutableSubject<>();
        this.taskOrdering = new PlainMutableSubject<>();
        this.orderedTasks = new PlainMutableSubject<>();
        this.currentRoutine = new PlainMutableSubject<>();
        this.title = new PlainMutableSubject<>();
        this.activeRoutine = new PlainMutableSubject<>();

        this.completedTime = new PlainMutableSubject<>();
        this.isTimerRunning = new PlainMutableSubject<>();
        this.currentTime = new PlainMutableSubject<>();
        this.currentTimeDisplay = new PlainMutableSubject<>();
        if(!isMocked) {
            this.timer = new CustomTimer();
        } else {
            this.timer = new MockTimer();
        }
        this.goalTime = new PlainMutableSubject<>();
        this.goalTimeDisplay = new PlainMutableSubject<>();
        this.currentTime.setValue("0m");
        isTimerRunning.setValue(false);
        completedTime.setValue("");


        routineRepository.findAll().observe(
                routines -> {
                    if (routines == null) return;

                    var ordering = new ArrayList<Integer>();
                    for (int i = 0; i < routines.size(); i++) {
                        ordering.add(routines.get(i).id());
                    }
                    routineOrdering.setValue(ordering);
                }
        );

        routineOrdering.observe(ordering -> {
            if (ordering == null) return;
            var routines = new ArrayList<Routine>();
            for (var id : ordering) {
                var routine = routineRepository.find(id).getValue();
                if (routine == null) return;
                routines.add(routine);
            }
            this.orderedRoutines.setValue(routines);
        });

        orderedRoutines.observe(routines -> {
            if (routines == null) return;
            currentRoutine.setValue(routines.get(0));
        });


        // Initialize task ordering for current routine
        currentRoutine.observe(routine -> {
            if (routine == null) return;
            var ordering = new ArrayList<Integer>();
            for (int i = 0; i < routine.tasks().size(); i++) {
                ordering.add(routine.tasks().get(i).id());
            }
            taskOrdering.setValue(ordering);
        });

        // Change title when current routine changes
        currentRoutine.observe(routine -> {
            if (routine == null) return;
            title.setValue(routine.name());
        });


        // Update ordered tasks when the ordering changes
        currentRoutine.observe(routine -> {
            if (routine == null) return;
            goalTime.setValue(routine.goalTime());
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
            goalTimeDisplay.setValue(time.toString());
            updateGoalTimeDisplay(time);
        });
    }

    public MutableSubject<List<Task>> getOrderedTasks() {
        return orderedTasks;
    }

    public MutableSubject<ActiveRoutine> getActiveRoutine() {
        return activeRoutine;
    }

    public void nextRoutine() {
        if (this.routineOrdering.getValue() == null) {
            return;
        }
        var newOrdering = RoutineList.rotateOrdering(routineOrdering.getValue(), 1);

        routineOrdering.setValue(newOrdering);
    }

    public void checkTask(Integer id) {
        if (this.activeRoutine.getValue() == null) {
            return;
        }
        var task = activeRoutine.getValue().activeTasks().stream().filter(activeTask -> Objects.equals(activeTask.task().id(), id)).findFirst();
        if (task.isEmpty()) return;
        var checkedTask = task.get().withChecked(true);
        activeRoutine.setValue(activeRoutine.getValue().withActiveTask(checkedTask));
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
        MockTimer mockedTimer = (MockTimer)timer;
        mockedTimer.forward();
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
        var routine = routineRepository.find(id).getValue();
        if (routine == null) return;
        var newRoutine = routine.withGoalTime(time);
        routineRepository.save(newRoutine);
    }

    public int getRoutineGoalTime(int id) {
        Routine routine = Objects.requireNonNull(routineRepository.find(id).getValue());
        return routine.goalTime();
    }

    public void updateGoalTimeDisplay(int time) {
        String newGoalTimeDisplay = time + "m";
        goalTimeDisplay.setValue(newGoalTimeDisplay + " ");
        goalTimeDisplay.setValue(newGoalTimeDisplay);
    }


    public MutableSubject<String> getGoalTimeDisplay() {
        return goalTimeDisplay;
    }

    public MutableSubject<Routine> getCurrentRoutine() {
        return currentRoutine;
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
}
