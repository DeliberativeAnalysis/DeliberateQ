package moten.david.util.math.gp;

import moten.david.util.math.Matrix;
import moten.david.util.math.Matrix.MatrixFunction;

public class QuartimaxCriterion implements MatrixFunction {

	private Matrix matrix;

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
