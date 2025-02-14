package edu.ucsd.cse110.habitizer.app;

import static androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.viewmodel.ViewModelInitializer;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import edu.ucsd.cse110.habitizer.lib.domain.ActiveRoutine;
import edu.ucsd.cse110.habitizer.lib.domain.ActiveTask;
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

    // UI state
    private final MutableSubject<List<Integer>> routineOrdering;
    private final MutableSubject<List<Routine>> orderedRoutines;
    private final MutableSubject<List<Integer>> taskOrdering;
    private final MutableSubject<List<Task>> orderedTasks;
    private final MutableSubject<Routine> currentRoutine;
    private final MutableSubject<String> title;

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
        // Create the observable objects
        this.routineOrdering = new PlainMutableSubject<>();
        this.orderedRoutines = new PlainMutableSubject<>();
        this.taskOrdering = new PlainMutableSubject<>();
        this.orderedTasks = new PlainMutableSubject<>();
        this.currentRoutine = new PlainMutableSubject<>();
        this.title = new PlainMutableSubject<>();
        this.activeRoutine = new PlainMutableSubject<>();


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

    public MutableSubject<String> getTitle() {
        return this.title;
    }
}
