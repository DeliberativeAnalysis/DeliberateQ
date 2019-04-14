package com.github.deliberateq.util.math;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class VectorTest {

    @Test
    public void testRanksAllValuesDifferent() {
        Vector a = new Vector(5.1, 4.2, 2.3, 1.4, 3.5);
        Vector b = a.getRanks();
        Vector c = new Vector(0, 1, 3, 4, 2);
        assertEquals(b, c);
    }
    
    @Test
    public void testRanksSomeRepeats() {
        Vector a = new Vector(1, 3, 3, 3, 2);
        Vector b = a.getRanks();
        Vector c = new Vector(4, 0, 0, 0, 3);
        assertEquals(c, b);
    }

}
