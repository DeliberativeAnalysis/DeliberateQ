package moten.david.util.math.gp;

import java.awt.Point;
import java.util.Map;

import moten.david.util.math.Matrix;
import moten.david.util.math.Matrix.MatrixFunction;

public class ReferenceCorrelationCriterion implements MatrixFunction {

	private Matrix reference;
	private Matrix matrix;

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
