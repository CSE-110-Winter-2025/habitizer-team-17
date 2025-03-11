package edu.ucsd.cse110.habitizer.app;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import edu.ucsd.cse110.habitizer.lib.domain.Routine;
import edu.ucsd.cse110.habitizer.lib.domain.RoutineRepository;
import edu.ucsd.cse110.habitizer.lib.domain.Task;
import edu.ucsd.cse110.observables.PlainMutableSubject;
import edu.ucsd.cse110.observables.MutableSubject;
import kotlin.NotImplementedError;

public class ChangeTaskOrderingTest {
    private MainViewModel viewModel;
    private FakeRoutineRepository fakeRepo;

    private static class FakeRoutineRepository implements RoutineRepository {
        private final MutableSubject<List<Routine>> routines = new PlainMutableSubject<>(new ArrayList<>());
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

        public void delete(Routine routine){
            throw new NotImplementedError();
        }
    }

    @Before
    public void setUp() {
        fakeRepo = new FakeRoutineRepository();
        viewModel = new MainViewModel(fakeRepo);
    }

    @Test
    public void TestChangetaskOrdering() {
        List<Task> tasks = new ArrayList<>();
        tasks.add(new Task(0, "testTask1"));
        tasks.add(new Task(1, "testTask2"));
        tasks.add(new Task(2, "testTask3"));
        fakeRepo.save(new Routine(0, "test", tasks, 0, 0));
        viewModel.moveTask(0, 0);
        viewModel.moveTask(2,1);
        assertEquals(viewModel.getCurrentRoutine().getValue().name(), "test");
        Routine routine = viewModel.getCurrentRoutine().getValue();
        assertEquals(routine.tasks().get(0).name(), "testTask2");
        assertEquals(routine.tasks().get(1).name(), "testTask3");
        assertEquals(routine.tasks().get(2).name(), "testTask1");
    }
}
