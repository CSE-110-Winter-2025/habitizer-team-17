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
import edu.ucsd.cse110.observables.PlainMutableSubject;
import edu.ucsd.cse110.observables.MutableSubject;

public class DeleteRoutineTest {
    private MainViewModel viewModel;
    private FakeRoutineRepository fakeRepo;

    private static class FakeRoutineRepository implements RoutineRepository {
        private final MutableSubject<List<Routine>> routines = new PlainMutableSubject<>(new ArrayList<>());

        public FakeRoutineRepository() {
            List<Routine> newList = new ArrayList<>();
            newList.add(new Routine(1, "test 1", new ArrayList<>(), 0, 0));
            newList.add(new Routine(2, "test 2", new ArrayList<>(), 0, 0));
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
            newList.add(routine);
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
    public void testAddRoutineToEnd() {
        viewModel.removeRoutine(viewModel.getCurrentRoutine().getValue());
        List<Routine> routines = fakeRepo.findAll().getValue();

        assertEquals(1, routines.size());
        assertFalse(routines.get(0).name().equals("test 1"));
        assertEquals("test 2", routines.get(0).name());

        viewModel.removeRoutine(viewModel.getCurrentRoutine().getValue());
        viewModel.addRoutineToEnd("test 3");
        viewModel.addRoutineToEnd("test 4");
        viewModel.removeRoutine(viewModel.getCurrentRoutine().getValue());
        routines = fakeRepo.findAll().getValue();
        assertEquals(1, routines.size());
        assertFalse(routines.get(0).name().equals("test 3"));
        assertEquals("test 4", routines.get(0).name());
    }
}