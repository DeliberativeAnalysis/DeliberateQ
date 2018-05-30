package com.github.deliberateq.util.math.gp;

import com.github.deliberateq.util.math.Matrix;
import com.github.deliberateq.util.math.Matrix.MatrixFunction;

public final class QuartimaxCriterion implements MatrixFunction {

	private final Matrix matrix;

	public QuartimaxCriterion(Matrix matrix) {
		this.matrix = matrix;
	}

	@Override
	public double function(Matrix m) {
		Matrix k = matrix.times(m);
		k = k.apply(new Matrix.Function() {

			@Override
			public double f(int row, int col, double x) {
				return Math.pow(x, 4);
			}
		});
		return k.getSum();
	}
}
