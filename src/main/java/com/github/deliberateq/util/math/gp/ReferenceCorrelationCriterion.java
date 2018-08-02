package com.github.deliberateq.util.math.gp;

import java.awt.Point;
import java.util.Map;

import com.github.deliberateq.util.math.CorrelationCoefficient;
import com.github.deliberateq.util.math.Matrix;
import com.github.deliberateq.util.math.Matrix.MatrixFunction;

public final class ReferenceCorrelationCriterion implements MatrixFunction {

	private final Matrix reference;
	private final Matrix matrix;
    private final CorrelationCoefficient cc;

	public ReferenceCorrelationCriterion(Matrix matrix, Matrix reference, CorrelationCoefficient cc) {
		this.reference = reference;
		this.matrix = matrix;
		this.cc = cc;
	}

	@Override
	public double function(Matrix m) {
		Map<Point, Double> map = matrix.times(m).getMatchedCorrelations(
				reference, cc);
		double sum = 0;
		for (double d : map.values())
			sum += d * d;
		return sum;
	}
}
