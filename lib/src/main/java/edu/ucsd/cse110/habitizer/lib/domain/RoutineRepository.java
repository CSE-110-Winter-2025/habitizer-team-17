package edu.ucsd.cse110.habitizer.lib.domain;

import java.util.List;

import edu.ucsd.cse110.observables.MutableSubject;

public interface RoutineRepository {
    MutableSubject<Routine> find(int id);

    MutableSubject<List<Routine>> findAll();

    void save(Routine routine);


    void delete(Routine routine);
}
