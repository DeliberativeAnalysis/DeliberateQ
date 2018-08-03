package com.github.deliberateq.qsort;

public class QResult {
    private final int statementNo;
    private final double value;

    public QResult(int statementNo, double value) {
        this.statementNo = statementNo;
        this.value = value;
    }
    
    public int statementNo() {
        return statementNo;
    }
    
    public double value() {
        return value;
    }

    @Override
    public String toString() {
        return "QResult [statementNo=" + statementNo + ", value=" + value + "]";
    }
}
