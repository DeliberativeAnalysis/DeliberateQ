package com.github.deliberateq.util.math;

import java.util.function.BiFunction;

public enum CorrelationCoefficient {
    PEARSONS("Pearsons", (a, b) -> getPearsonsCorrelationCoefficient(a, b)), //
    CONCORDANCE("Concordance", (a, b) -> getConcordanceCorrelationCoefficient(a, b)), //
    SPEARMANS("Spearmans", (a, b) -> getSpearmansCorrelationCoefficient(a, b));

    private final String abbreviatedName;
    private final BiFunction<Vector, Vector, Double> function;

    private CorrelationCoefficient(String abbreviatedName, BiFunction<Vector, Vector, Double> function) {
        this.abbreviatedName = abbreviatedName;
        this.function = function;
    }

    public String abbreviatedName() {
        return abbreviatedName;
    }
    
    public double apply(Vector a, Vector b) {
        return function.apply(a, b);
    }

    private static double getPearsonsCorrelationCoefficient(Vector a, Vector b) {
        if (a.size() != b.size()) {
            throw new RuntimeException("vectors must be same size");
        }
        Vector d1 = a.getDeviation();
        Vector d2 = b.getDeviation();
        for (int i = a.size(); i >= 1; i--) {
            // go down because we are removing
            if (Vector.isNullEntry(a.getValue(i)) || Vector.isNullEntry(b.getValue(i))) {
                // pairwise deletion
                d1 = d1.removeRow(i).getColumnVector(1);
                d2 = d2.removeRow(i).getColumnVector(1);
            }
        }
        double sigmaXY = 0;
        double sigmaX2 = 0;
        double sigmaY2 = 0;
        for (int i = 1; i <= d1.size(); i++) {
            sigmaXY += d1.getValue(i) * d2.getValue(i);
            sigmaX2 += d1.getValue(i) * d1.getValue(i);
            sigmaY2 += d2.getValue(i) * d2.getValue(i);
        }
        return sigmaXY / Math.sqrt(sigmaX2 * sigmaY2);
    }

    private static double getConcordanceCorrelationCoefficient(Vector a, Vector b) {
        double p = getPearsonsCorrelationCoefficient(a, b);
        double meanX = a.getMean();
        double meanY = b.getMean();
        double varX = a.getVariance();
        double varY = b.getVariance();
        return 2 * p * Math.sqrt(varX * varY) / (sqr(meanX - meanY) + varX + varY);
    }
    
    private static double getSpearmansCorrelationCoefficient(Vector a, Vector b) {
        Vector a2 = a.getRanks();
        Vector b2 = b.getRanks();
        return getPearsonsCorrelationCoefficient(a2, b2);
    }

    private static double sqr(double x) {
        return x * x;
    }

}