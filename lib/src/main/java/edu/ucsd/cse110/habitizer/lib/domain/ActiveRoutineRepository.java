package edu.ucsd.cse110.habitizer.lib.domain;

import edu.ucsd.cse110.observables.MutableSubject;

public interface ActiveRoutineRepository {
    MutableSubject<ActiveRoutine> find();

    void save(ActiveRoutine activeRoutine);

    void delete();
}
