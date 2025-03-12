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
import edu.ucsd.cse110.habitizer.lib.domain.TimerState;
import edu.ucsd.cse110.observables.PlainMutableSubject;
import edu.ucsd.cse110.observables.MutableSubject;
import kotlin.NotImplementedError;

public class TimerTest {
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
    public void testTimerState() {
        viewModel.startTimer();
        assertEquals(TimerState.RUNNING, viewModel.getTimerState().getValue());
        viewModel.pauseTimer();
        assertEquals(TimerState.PAUSED, viewModel.getTimerState().getValue());
        viewModel.resumeTimer();
        assertEquals(TimerState.RUNNING, viewModel.getTimerState().getValue());
        viewModel.stopTimer();
        assertEquals(TimerState.STOPPED, viewModel.getTimerState().getValue());
    }
}
