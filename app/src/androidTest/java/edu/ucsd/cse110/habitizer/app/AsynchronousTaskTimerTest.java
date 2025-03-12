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
import edu.ucsd.cse110.observables.MutableSubject;
import edu.ucsd.cse110.observables.PlainMutableSubject;

public class AsynchronousTaskTimerTest {

    private MainViewModel viewModel;
    private FakeRoutineRepository fakeRepo;


    private static class FakeRoutineRepository implements RoutineRepository {
        private MutableSubject<List<Routine>> routines = new PlainMutableSubject<>(new ArrayList<>());

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
            newList.add(routine);
            routines.setValue(newList);
        }

        public void delete(Routine routine) {
            routines.getValue().remove(routine);
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
        var fakeRepo1 = new FakeActiveRoutineRepository();
        var fakeRepo2 = new FakeCustomTimerRepository();
        viewModel = new MainViewModel(fakeRepo, fakeRepo1, fakeRepo2);
    }

    @Test
    public void asyncTaskTimerTest() {
        List<Task> oneTask = new ArrayList<Task>();
        oneTask.add(new Task(0, "test task"));
        Routine routine = new Routine(0, "test routine", oneTask, 50, 0);
        fakeRepo.save(routine);
        viewModel.startRoutine();
        assertEquals("0s", viewModel.getElapsedSinceLastTaskDisplay().getValue());
        viewModel.forwardTimer();
        viewModel.forwardTimer();
        assertEquals("30s", viewModel.getElapsedSinceLastTaskDisplay().getValue());
        viewModel.forwardTimer();
        viewModel.forwardTimer();
        assertEquals("1m", viewModel.getElapsedSinceLastTaskDisplay().getValue());
        viewModel.checkTask(0);
        assertEquals("0s", viewModel.getElapsedSinceLastTaskDisplay().getValue());

    }
}