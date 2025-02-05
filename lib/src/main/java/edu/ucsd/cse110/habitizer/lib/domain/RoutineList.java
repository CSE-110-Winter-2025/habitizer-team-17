package edu.ucsd.cse110.habitizer.lib.domain;

import java.util.ArrayList;
import java.util.List;

public class RoutineList {
    List<Routine> routines;
    private Integer id;

    public RoutineList(List<Routine> routines, Integer id) {
        this.routines = routines;
        this.id = id;
    }

    public List<Routine> getRoutines() {
        return this.routines;
    }

    public Integer id() {
        return  this.id;
    }
}
