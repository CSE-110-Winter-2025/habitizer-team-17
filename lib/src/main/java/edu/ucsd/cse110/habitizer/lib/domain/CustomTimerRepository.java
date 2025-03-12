package edu.ucsd.cse110.habitizer.lib.domain;

import edu.ucsd.cse110.observables.MutableSubject;

public interface CustomTimerRepository {
    MutableSubject<CustomTimer> find();

    void save(CustomTimer customTimer);

    void delete();
}
