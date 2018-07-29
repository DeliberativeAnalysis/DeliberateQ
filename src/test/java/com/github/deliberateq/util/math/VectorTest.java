package com.github.deliberateq.util.math;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class VectorTest {

    private static final double PRECISION = 0.00001;

    @Test
    public void testPearsonCorrelation() {
        Vector a = new Vector(1, 2, 3, 4, 5);
        Vector b = new Vector(-1, 5, 2, 8, 0);
        assertEquals(0.213589, a.getPearsonCorrelation(b), PRECISION);
    }

}
