package edu.ucsd.cse110.habitizer.lib.domain;

import java.util.ArrayList;
import java.util.List;

public class RoutineList {
    List<Routine> routines;

    public RoutineList(List<Routine> routines) {
        this.routines = routines;
    }

    public List<Routine> getRoutines() {
        return this.routines;
    }
}
