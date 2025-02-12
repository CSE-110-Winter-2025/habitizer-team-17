package edu.ucsd.cse110.habitizer.app;

import static androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.viewmodel.ViewModelInitializer;

import java.util.ArrayList;
import java.util.Collections;
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

    // Domain state (true "Model" state)
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
    private final MutableSubject<String> completedTime;
    private final MutableSubject<Boolean> isTimerRunning;


    public static final ViewModelInitializer<MainViewModel> initializer =
            new ViewModelInitializer<>(
                    MainViewModel.class,
                    creationExtras -> {
                        var app = (HabitizerApplication) creationExtras.get(APPLICATION_KEY);
                        assert app != null;
                        return new MainViewModel(app.getRoutineRepository(), app.getTaskRepository());
                    }
            );
    private final PlainMutableSubject<Boolean> isShowingMorning;

    public MainViewModel(RoutineRepository routineRepository, TaskRepository taskRepository) {
        this.routineRepository = routineRepository;
        this.taskRepository = taskRepository;
        // Create the observable objects
        this.routineOrdering = new PlainMutableSubject<>();
        this.orderedRoutines = new PlainMutableSubject<>();
        this.isShowingMorning = new PlainMutableSubject<>();
        this.taskOrdering = new PlainMutableSubject<>();
        this.orderedTasks = new PlainMutableSubject<>();
        this.currentRoutine = new PlainMutableSubject<>();
        this.title = new PlainMutableSubject<>();
        this.completedTime = new PlainMutableSubject<>();
        this.isTimerRunning = new PlainMutableSubject<>();
        this.timer = new CustomTimer();
        isShowingMorning.setValue(true);
        this.isTimerRunning.setValue(false);
        this.completedTime.setValue("");
        
        routineRepository.findAll().observe(
                routines -> {
                    if(routines == null) return;

                    var ordering = new ArrayList<Integer>();
                    for(int i = 0; i < routines.size(); i++){
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
            currentRoutine.setValue(routines.get(0));
        });



        // Initialize ordering when tasks are loaded
        currentRoutine.observe(routine -> {
            if (routine == null) return;

            var ordering = new ArrayList<Integer>();
            for (int i = 0; i < routine.getTasks().size(); i++) {
                ordering.add(routine.getTasks().get(i).id());
            }
            title.setValue(routine.getName());
            taskOrdering.setValue(ordering);
        });


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

    }

    public MutableSubject<List<Task>> getOrderedTasks() {
        return orderedTasks;
    }

    public void nextRoutine() {
        if(this.routineOrdering.getValue() == null){
            return;
        }
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
        timer.reset(); // Reset timer to 0 before starting
        completedTime.setValue("00:00"); // Reset display time
        updateTitle(isShowingMorning.getValue()); // Update title
        timer.start(); // Start timer
        isTimerRunning.setValue(true);
    }


    public void stopTimer() {
        if (isTimerRunning.getValue()) {
            timer.stop();
            isTimerRunning.setValue(false);
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
        public MutableSubject<Routine> getCurrentRoutine() {
        return this.getCurrentRoutine();
    }

    public MutableSubject<String> getTitle() {
        return this.title;
    }

    public MutableSubject<Boolean> getIsTimerRunning() {
        return isTimerRunning;
    }

    public MutableSubject<String> getCompletedTime() {
        return completedTime;
    }
}
