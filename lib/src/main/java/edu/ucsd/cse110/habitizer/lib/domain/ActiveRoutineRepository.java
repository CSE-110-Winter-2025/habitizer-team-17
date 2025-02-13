package edu.ucsd.cse110.habitizer.lib.domain;

import java.util.List;

import edu.ucsd.cse110.habitizer.lib.data.InMemoryDataSource;
import edu.ucsd.cse110.observables.MutableSubject;

public class ActiveRoutineRepository {

    private final InMemoryDataSource dataSource;

    public ActiveRoutineRepository(InMemoryDataSource dataSource) {
        this.dataSource = dataSource;
    }


    public MutableSubject<ActiveRoutine> get() {

        return dataSource.getActiveRoutineSubject();
    }

    public void save(ActiveRoutine activeRoutine) {

        dataSource.putActiveRoutine(activeRoutine);
    }
}
