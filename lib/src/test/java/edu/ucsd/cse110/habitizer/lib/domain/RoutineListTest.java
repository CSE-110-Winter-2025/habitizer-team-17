package edu.ucsd.cse110.habitizer.lib.domain;

import static org.junit.Assert.*;

import org.junit.Test;

import java.util.List;

public class RoutineListTest {
    @Test
    public void testRotateOrdering() {
        List<Integer> ordering = List.of(0, 1, 2, 3);
        List<Integer> newOrdering = RoutineList.rotateOrdering(ordering, 2);
        assertEquals(List.of(2, 3, 0, 1), newOrdering);
    }
}
