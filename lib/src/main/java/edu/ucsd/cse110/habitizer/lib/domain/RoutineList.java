package edu.ucsd.cse110.habitizer.lib.domain;

import java.sql.Array;
import java.util.ArrayList;
import java.util.List;

public class RoutineList {
    public static List<Integer> rotateRoutine(List<Integer> ordering, int k) {
        var newOrdering = new ArrayList<Integer>();
        for(int i = 0; i < ordering.size(); i++){
            int thatI = ordering.get(Math.floorMod(i+k, ordering.size()));
            newOrdering.add(thatI);
        }
        return newOrdering;
    }
}
