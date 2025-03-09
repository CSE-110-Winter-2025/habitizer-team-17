package edu.ucsd.cse110.habitizer.lib.domain;

import java.util.List;

import edu.ucsd.cse110.habitizer.lib.data.InMemoryDataSource;
import edu.ucsd.cse110.observables.MutableSubject;
import kotlin.NotImplementedError;

public class SimpleRoutineRepository implements RoutineRepository {
    private final InMemoryDataSource dataSource;

    public SimpleRoutineRepository(InMemoryDataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public MutableSubject<Routine> find(int id) {
        return dataSource.getRoutineSubject(id);
    }

    @Override
    public MutableSubject<List<Routine>> findAll() {

        return dataSource.getAllRoutinesSubject();
    }

    @Override
    public void save(Routine routine) {
        dataSource.putRoutine(routine);
    }


    public void delete(Routine routine) {
        throw new NotImplementedError();
    }
}
