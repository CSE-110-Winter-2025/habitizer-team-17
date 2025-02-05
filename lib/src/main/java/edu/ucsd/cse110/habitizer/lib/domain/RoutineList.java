package edu.ucsd.cse110.habitizer.lib.domain;

import java.util.ArrayList;

public class RoutineList {
    ArrayList<Routine> routines;

    public RoutineList(ArrayList<Routine> routines) {
        this.routines = routines;
    }

    public ArrayList<Routine> getRoutines() {
        return this.routines;
    }
}
