package edu.ucsd.cse110.habitizer.app;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import edu.ucsd.cse110.habitizer.lib.data.InMemoryDataSource;
import edu.ucsd.cse110.habitizer.lib.domain.ActiveRoutine;
import edu.ucsd.cse110.habitizer.lib.domain.ActiveRoutineRepository;
import edu.ucsd.cse110.habitizer.lib.domain.CustomTimer;
import edu.ucsd.cse110.habitizer.lib.domain.CustomTimerRepository;
import edu.ucsd.cse110.habitizer.lib.domain.Routine;
import edu.ucsd.cse110.habitizer.lib.domain.RoutineRepository;
import edu.ucsd.cse110.habitizer.lib.domain.TimerState;
import edu.ucsd.cse110.observables.MutableSubject;
import edu.ucsd.cse110.observables.PlainMutableSubject;
import kotlin.NotImplementedError;

public class PauseRoutineTest {
    private FakeRoutineRepository routineRepository;
    private FakeActiveRoutineRepository activeRoutineRepository;
    private FakeCustomTimerRepository customTimerRepository;
    private MainViewModel viewModel;

    private static class FakeRoutineRepository implements RoutineRepository {
        private final MutableSubject<List<Routine>> routines =
                new PlainMutableSubject<>(List.of(InMemoryDataSource.MORNING_ROUTINE,
                        InMemoryDataSource.EVENING_ROUTINE));

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
            List<Routine> newList = new ArrayList<>(Objects.requireNonNull(routines.getValue()));
            newList.add(routine);
            routines.setValue(newList);
        }

        public void delete(Routine routine) {
            throw new NotImplementedError();
        }
    }

    private static class FakeActiveRoutineRepository implements ActiveRoutineRepository {
        MutableSubject<ActiveRoutine> activeRoutine = new PlainMutableSubject<>();

        @Override
        public MutableSubject<ActiveRoutine> find() {
            return activeRoutine;
        }

        @Override
        public void save(ActiveRoutine activeRoutine) {
            this.activeRoutine.setValue(activeRoutine);
        }

        @Override
        public void delete() {
            activeRoutine.setValue(null);
        }
    }

    private static class FakeCustomTimerRepository implements CustomTimerRepository {

        MutableSubject<CustomTimer> customTimer = new PlainMutableSubject<>();

        @Override
        public MutableSubject<CustomTimer> find() {
            return customTimer;
        }

        @Override
        public void save(CustomTimer customTimer) {
            this.customTimer.setValue(customTimer);
        }

        @Override
        public void delete() {
            customTimer.setValue(null);
        }
    }


    @Before
    public void init() {
        routineRepository = new FakeRoutineRepository();
        activeRoutineRepository = new FakeActiveRoutineRepository();
        customTimerRepository = new FakeCustomTimerRepository();
        viewModel = new MainViewModel(routineRepository, activeRoutineRepository,
                customTimerRepository);
    }

    @Test
    public void testPause() throws InterruptedException {
        // check off task and pause
        viewModel.startRoutine();
        viewModel.checkTask(0);
        viewModel.pauseTimer();

        var currentTime = viewModel.getCurrentTime().getValue();
        Thread.sleep(1050);
        var newTime = viewModel.getCurrentTime().getValue();

        // check paused state
        assertEquals(currentTime, newTime);
        assertEquals(TimerState.PAUSED, viewModel.getTimerState().getValue());

        // save to db
        viewModel.onCleared();

        // check active routine saved to db
        assertNotNull(activeRoutineRepository.activeRoutine.getValue());
        var task = activeRoutineRepository.activeRoutine.getValue().activeTasks().stream()
                .filter(activeTask -> Objects.requireNonNull(activeTask.task().id()) == 0)
                .findFirst();
        assertTrue(task.isPresent());
        assertTrue(task.get().checked());

        // check timer also saved
        assertNotNull(customTimerRepository.customTimer.getValue());
        assertEquals(TimerState.PAUSED, customTimerRepository.customTimer.getValue().getState());
        assertEquals(currentTime,
                Long.valueOf(customTimerRepository.customTimer.getValue().getElapsedTimeInMilliseconds()));

        // start app again and check populated with db
        var viewModel2 = new MainViewModel(routineRepository, activeRoutineRepository,
                customTimerRepository);
        assertEquals(activeRoutineRepository.activeRoutine.getValue(),
                viewModel2.getActiveRoutine().getValue());
        assertEquals(Screen.ACTIVE_ROUTINE_SCREEN, viewModel2.getScreen().getValue());
        assertEquals(customTimerRepository.customTimer.getValue().getState(),
                viewModel2.getTimerState().getValue());
    }
}
