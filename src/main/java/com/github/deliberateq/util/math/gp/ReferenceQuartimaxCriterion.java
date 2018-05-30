package com.github.deliberateq.util.math.gp;

import java.awt.Point;
import java.util.Map;

import com.github.deliberateq.util.math.Matrix;
import com.github.deliberateq.util.math.Matrix.MatrixFunction;

public final class ReferenceQuartimaxCriterion implements MatrixFunction {

	private final Matrix reference;
	private final Matrix matrix;

	public ReferenceQuartimaxCriterion(Matrix matrix, Matrix reference) {
		this.reference = reference;
		this.matrix = matrix;
	}

	@Override
	public double function(Matrix m) {
		Map<Point, Double> map = matrix.times(m).getMatchedCorrelations(
				reference);
		// TODO write the quartimax criterion
		throw new RuntimeException("not implemented yet");
	}
}
