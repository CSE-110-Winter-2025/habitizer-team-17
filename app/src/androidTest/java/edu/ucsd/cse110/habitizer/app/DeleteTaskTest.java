package edu.ucsd.cse110.habitizer.app;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import edu.ucsd.cse110.habitizer.lib.domain.ActiveRoutine;
import edu.ucsd.cse110.habitizer.lib.domain.ActiveRoutineRepository;
import edu.ucsd.cse110.habitizer.lib.domain.CustomTimer;
import edu.ucsd.cse110.habitizer.lib.domain.CustomTimerRepository;
import edu.ucsd.cse110.habitizer.lib.domain.Routine;
import edu.ucsd.cse110.habitizer.lib.domain.RoutineRepository;
import edu.ucsd.cse110.habitizer.lib.domain.Task;
import edu.ucsd.cse110.observables.PlainMutableSubject;
import edu.ucsd.cse110.observables.MutableSubject;
import kotlin.NotImplementedError;

public class DeleteTaskTest {
    private MainViewModel viewModel;
    private FakeRoutineRepository fakeRepo;

    private static class FakeRoutineRepository implements RoutineRepository {
        private final MutableSubject<List<Routine>> routines = new PlainMutableSubject<>(new ArrayList<>());

        public FakeRoutineRepository() {
            List<Task> tasks1 = new ArrayList<>();
            tasks1.add(new Task(101, "Task A"));
            tasks1.add(new Task(102, "Task B"));

            List<Task> tasks2 = new ArrayList<>();
            tasks2.add(new Task(201, "Task C"));
            tasks2.add(new Task(202, "Task D"));

            List<Routine> newList = new ArrayList<>();
            newList.add(new Routine(1, "Routine 1", tasks1, 0, 0));
            newList.add(new Routine(2, "Routine 2", tasks2, 0, 0));
            routines.setValue(newList);
        }

        @Override
        public MutableSubject<List<Routine>> findAll() {
            return routines;
        }

        @Override
        public MutableSubject<Routine> find(int id) {
            return new PlainMutableSubject<>(null);
        }

        @Override
        public void save(Routine routine) {
            List<Routine> newList = new ArrayList<>(routines.getValue());
            newList.removeIf(r -> r.id() != null && r.id().equals(routine.id())); // Remove old version
            newList.add(routine); // Add updated version
            routines.setValue(newList);
        }

        public void delete(Routine routine){
            List<Routine> newList = new ArrayList<>(routines.getValue());
            newList.remove(routine);
            routines.setValue(newList);
        }
    }

    private static class FakeActiveRoutineRepository implements ActiveRoutineRepository {

        @Override
        public MutableSubject<ActiveRoutine> find() {
            return new PlainMutableSubject<>();
        }

        @Override
        public void save(ActiveRoutine activeRoutine) {

        }

        @Override
        public void delete() {

        }
    }

    private static class FakeCustomTimerRepository implements CustomTimerRepository {

        @Override
        public MutableSubject<CustomTimer> find() {
            return new PlainMutableSubject<>();
        }

        @Override
        public void save(CustomTimer customTimer) {

        }

        @Override
        public void delete() {

        }
    }

    @Before
    public void setUp() {
        fakeRepo = new FakeRoutineRepository();
        var fakeRepo2 = new FakeActiveRoutineRepository();
        var fakeRepo3 = new FakeCustomTimerRepository();

        viewModel = new MainViewModel(fakeRepo, fakeRepo2, fakeRepo3);
    }

    @Test
    public void testDeleteTaskFromRoutine() {
        Routine currentRoutine = fakeRepo.findAll().getValue().get(0);
        assertNotNull(currentRoutine);
        assertEquals(2, currentRoutine.tasks().size());

        int taskIdToDelete = currentRoutine.tasks().get(0).id();
        viewModel.deleteTask(taskIdToDelete);

        Routine updatedRoutine = fakeRepo.findAll().getValue().get(0);
        assertFalse(updatedRoutine.tasks().stream().anyMatch(task -> task.id() == taskIdToDelete));
    }
}
