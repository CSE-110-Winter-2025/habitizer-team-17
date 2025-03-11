package edu.ucsd.cse110.habitizer.app;

import static org.junit.Assert.assertEquals;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import edu.ucsd.cse110.habitizer.lib.domain.Routine;
import edu.ucsd.cse110.habitizer.lib.domain.RoutineRepository;
import edu.ucsd.cse110.observables.MutableSubject;
import edu.ucsd.cse110.observables.PlainMutableSubject;

public class CompletedTaskTimeTest {

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

    @Before
    public void setUp() {
        fakeRepo = new FakeRoutineRepository();
        viewModel = new MainViewModel(fakeRepo);
    }

    private String getFormattedElapsedTime(long milliseconds) throws Exception {
        Method updateMethod = MainViewModel.class.getDeclaredMethod("updateElapsedSinceLastTaskDisplay", long.class);
        updateMethod.setAccessible(true);
        updateMethod.invoke(viewModel, milliseconds);
        return viewModel.getElapsedSinceLastTaskDisplay().getValue();
    }

    @Test
    public void testCompletedTaskTimeFormatting() throws Exception {
        assertEquals("0s", getFormattedElapsedTime(1000));

        assertEquals("5s", getFormattedElapsedTime(3000));

        assertEquals("20s", getFormattedElapsedTime(22000));

        assertEquals("25s", getFormattedElapsedTime(23000));

        assertEquals("55s", getFormattedElapsedTime(57000));

        assertEquals("1m", getFormattedElapsedTime(58000));
    }
}
