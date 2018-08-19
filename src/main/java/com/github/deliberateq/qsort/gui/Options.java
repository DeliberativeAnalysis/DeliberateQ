package com.github.deliberateq.qsort.gui;

import com.github.deliberateq.util.math.CorrelationCoefficient;

public final class Options {

    private final CorrelationCoefficient cc;

    public Options(CorrelationCoefficient cc) {
        this.cc = cc;
    }

    public CorrelationCoefficient cc() {
        return cc;
    }

}
