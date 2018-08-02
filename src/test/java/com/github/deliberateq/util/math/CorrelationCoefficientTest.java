package com.github.deliberateq.util.math;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class CorrelationCoefficientTest {

    private static final double PRECISION = 0.00001;
    
    @Test
    public void testConcordanceCalculation() {
        Vector a = new Vector(new double[] { 1, 2, 3, 4, 5 });
        Vector b = new Vector(new double[] { -1, 5, 2, 8, 0 });
        assertEquals( 0.1538462, CorrelationCoefficient.CONCORDANCE.apply(a, b), PRECISION);
    }


    @Test
    public void testPearsonCorrelation() {
        Vector a = new Vector(1, 2, 3, 4, 5);
        Vector b = new Vector(-1, 5, 2, 8, 0);
        assertEquals(0.213589, CorrelationCoefficient.PEARSONS.apply(a, b), PRECISION);
    }

}
