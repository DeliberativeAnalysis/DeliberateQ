package com.github.deliberateq.util.math.gp;

import java.awt.Point;
import java.util.Map;

import com.github.deliberateq.util.math.Matrix;
import com.github.deliberateq.util.math.Matrix.MatrixFunction;

public final class ReferenceCorrelationCriterion implements MatrixFunction {

	private final Matrix reference;
	private final Matrix matrix;

	public ReferenceCorrelationCriterion(Matrix matrix, Matrix reference) {
		this.reference = reference;
		this.matrix = matrix;
	}

	@Override
	public double function(Matrix m) {
		Map<Point, Double> map = matrix.times(m).getMatchedCorrelations(
				reference);
		double sum = 0;
		for (double d : map.values())
			sum += d * d;
		return sum;
	}
}
