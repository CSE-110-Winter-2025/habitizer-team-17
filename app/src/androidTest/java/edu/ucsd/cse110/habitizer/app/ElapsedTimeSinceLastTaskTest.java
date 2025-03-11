package edu.ucsd.cse110.habitizer.app;

import static org.junit.Assert.assertEquals;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;

import edu.ucsd.cse110.habitizer.lib.domain.ActiveRoutine;
import edu.ucsd.cse110.habitizer.lib.domain.ActiveTask;
import edu.ucsd.cse110.habitizer.lib.domain.CustomTimer;
import edu.ucsd.cse110.habitizer.lib.domain.Routine;
import edu.ucsd.cse110.habitizer.lib.domain.RoutineRepository;
import edu.ucsd.cse110.habitizer.lib.domain.Task;
import edu.ucsd.cse110.observables.PlainMutableSubject;
import edu.ucsd.cse110.observables.MutableSubject;

public class ElapsedTimeSinceLastTaskTest {

    private MainViewModel viewModel;
    private FakeRoutineRepository fakeRepo;

    private static class FakeRoutineRepository implements RoutineRepository {
        private MutableSubject<java.util.List<Routine>> routines = new PlainMutableSubject<>(new ArrayList<>());

        @Override
        public MutableSubject<java.util.List<Routine>> findAll() {
            return routines;
        }

        @Override
        public MutableSubject<Routine> find(int id) {
            return new PlainMutableSubject<>(null);
        }

        @Override
        public void save(Routine routine) {
            java.util.List<Routine> newList = new ArrayList<>(routines.getValue());
            newList.add(routine);
            routines.setValue(newList);
        }

        public void delete(Routine routine) {
            routines.getValue().remove(routine);
        }
    }

    @Before
    public void setUp() {
        fakeRepo = new FakeRoutineRepository();
        viewModel = new MainViewModel(fakeRepo);
    }

    @Test
    public void testElapsedTimeSinceLastCheckedTask() throws Exception {
        Task brushTeeth = new Task(101, "Brush teeth");
        Task takeShower = new Task(102, "Take a Shower");

        Routine routine = new Routine(1, "Morning Routine", Arrays.asList(brushTeeth, takeShower), 46, 0);
        viewModel.getCurrentRoutine().setValue(routine); // Sets routine and goalTime

        ActiveTask activeBrush = new ActiveTask(brushTeeth, false, 0L);
        ActiveTask activeShower = new ActiveTask(takeShower, false, 0L);
        ActiveRoutine activeRoutine = new ActiveRoutine(routine, new ArrayList<>(Arrays.asList(activeBrush, activeShower)), 0L);
        viewModel.getActiveRoutine().setValue(activeRoutine);

        viewModel.checkTask(101);


        for (int i = 0; i < 10; i++) {
            viewModel.forwardTimer();
        }

        Field timerField = MainViewModel.class.getDeclaredField("timer");
        timerField.setAccessible(true);
        CustomTimer timer = (CustomTimer) timerField.get(viewModel);
        long elapsedTime = timer.getElapsedTimeInMilliSeconds();

        Method updateMethod = MainViewModel.class.getDeclaredMethod("updateElapsedSinceLastTaskDisplay", long.class);
        updateMethod.setAccessible(true);
        updateMethod.invoke(viewModel, elapsedTime);

        assertEquals("5m", viewModel.getElapsedSinceLastTaskDisplay().getValue());

        assertEquals("46m", viewModel.getGoalTimeDisplay().getValue());
    }
}
