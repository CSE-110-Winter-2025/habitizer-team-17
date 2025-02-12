package edu.ucsd.cse110.habitizer.app;

import static androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.viewmodel.ViewModelInitializer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import edu.ucsd.cse110.habitizer.lib.domain.ActiveRoutine;
import edu.ucsd.cse110.habitizer.lib.domain.ActiveTask;
import edu.ucsd.cse110.habitizer.lib.domain.ActiveTaskRepository;
import edu.ucsd.cse110.habitizer.lib.domain.Routine;
import edu.ucsd.cse110.habitizer.lib.domain.RoutineList;
import edu.ucsd.cse110.habitizer.lib.domain.RoutineRepository;
import edu.ucsd.cse110.habitizer.lib.domain.Task;
import edu.ucsd.cse110.habitizer.lib.domain.TaskRepository;
import edu.ucsd.cse110.observables.MutableSubject;
import edu.ucsd.cse110.observables.PlainMutableSubject;

public class MainViewModel extends ViewModel {
    private static final String LOG_TAG = "MainViewModel";

    // Domain state (true "Model" state)
    private final RoutineRepository routineRepository;

    private final TaskRepository taskRepository;

    private final ActiveTaskRepository activeTaskRepository;
    // UI state
    private final MutableSubject<List<Integer>> routineOrdering;
    private final MutableSubject<List<Routine>> orderedRoutines;
    private final MutableSubject<List<Integer>> taskOrdering;
    private final MutableSubject<List<Task>> orderedTasks;
    private final MutableSubject<Routine> currentRoutine;
    private final MutableSubject<String> title;

    private final MutableSubject<ActiveRoutine> activeRoutine;

    private final MutableLiveData<Screen> screen;


    public static final ViewModelInitializer<MainViewModel> initializer =
            new ViewModelInitializer<>(
                    MainViewModel.class,
                    creationExtras -> {
                        var app = (HabitizerApplication) creationExtras.get(APPLICATION_KEY);
                        assert app != null;
                        return new MainViewModel(app.getRoutineRepository(), app.getTaskRepository(), app.getActiveTaskRepository());
                    }
            );

    public MainViewModel(RoutineRepository routineRepository, TaskRepository taskRepository, ActiveTaskRepository activeTaskRepository) {
        this.routineRepository = routineRepository;
        this.taskRepository = taskRepository;
        this.activeTaskRepository = activeTaskRepository;
        // Create the observable objects
        this.routineOrdering = new PlainMutableSubject<>();
        this.orderedRoutines = new PlainMutableSubject<>();
        this.taskOrdering = new PlainMutableSubject<>();
        this.orderedTasks = new PlainMutableSubject<>();
        this.currentRoutine = new PlainMutableSubject<>();
        this.title = new PlainMutableSubject<>();
        this.activeRoutine = new PlainMutableSubject<>();
        this.screen = new MutableLiveData<Screen>(Screen.PREVIEW_SCREEN);

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


        currentRoutine.observe(routine -> {
            if(routine == null) return;
            List<ActiveTask> activeTasks = new ArrayList<>();
            for(var task: routine.getTasks()){
                ActiveTask newActiveTask = new ActiveTask(task, false);
                activeTasks.add(newActiveTask);
                activeTaskRepository.save(newActiveTask);
            };

            activeRoutine.setValue(new ActiveRoutine(routine, activeTasks));

        });
    }

    public MutableSubject<List<Task>> getOrderedTasks() {
        return orderedTasks;
    }

    public MutableSubject<ActiveRoutine> getActiveRoutine(){
        return activeRoutine;
    }

    public void nextRoutine() {
        if(this.routineOrdering.getValue() == null){
            return;
        }
        var newOrdering = RoutineList.rotateRoutine(routineOrdering.getValue(), 1);

        routineOrdering.setValue(newOrdering);
    }

    public void checkTask(Integer id){
        if(this.activeRoutine.getValue() == null){
            return;
        }
        var task = activeTaskRepository.find(id).getValue();
        task = task.setChecked(!task.isChecked());
        activeTaskRepository.save(task);
        activeRoutine.setValue(activeRoutine.getValue().setActiveTask(task));
    }

    public MutableSubject<Routine> getCurrentRoutine() {
        return this.getCurrentRoutine();
    }

    public void setScreen(Screen screen){
        this.screen.setValue(screen);
    }

    public MutableLiveData<Screen> getScreen(){
        return this.screen;
    }

    public MutableSubject<String> getTitle() {
        return this.title;
    }
}
