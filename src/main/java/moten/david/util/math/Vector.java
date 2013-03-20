package moten.david.util.math;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.google.inject.internal.Lists;

public class Vector extends Matrix {

	private static final long serialVersionUID = -4275438891677241883L;

	public Vector(int size) {
		// a vector is a matrix with one or many rows and only one column
		super(size, 1);
	}

	public Vector(int size, double startValue) {
		this(size);
		for (int i = 1; i <= size(); i++) {
			setValue(i, startValue);
		}
	}

	public Vector(double[] values) {
		this(values.length);
		for (int i = 0; i < values.length; i++) {
			setValue(i + 1, values[i]);
		}
	}

	public Vector(List<Double> values) {
		this(values.size());
		for (int i = 0; i < values.size(); i++) {
			setValue(i + 1, values.get(i));
		}
	}

	public double getValue(int i) {
		return this.getValue(i, 1);
	}

	public void setValue(int i, double d) {
		setValue(i, 1, d);
	}

	public int size() {
		return this.rowCount();
	}

	public double getMean() {
		double total = 0;
		int count = 0;
		for (int i = 1; i <= size(); i++) {
			if (!isNullEntry(getValue(i))) {
				total += getValue(i);
				count++;
			}
		}
		return total / count;
	}

	public boolean isNullEntry(double d) {
		return d == nullEntry;
	}

	public Vector getDeviation() {
		Vector result = copy().getColumnVector(1);
		double mean = getMean();
		for (int i = 1; i <= size(); i++) {
			result.setValue(i, getValue(i) - mean);
		}
		return result;
	}

	@Override
	public double getSum() {
		double result = 0;
		for (double d : getValues()) {
			result += d;
		}
		return result;
	}

	public double getPearsonCorrelation(Vector v) {
		if (size() != v.size()) {
			throw new Error("vectors must be same size");
		}
		Vector d1 = getDeviation();
		Vector d2 = v.getDeviation();
		for (int i = size(); i >= 1; i--) {
			// go down because we are removing
			if (isNullEntry(getValue(i)) || isNullEntry(v.getValue(i))) {
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
		double result = sigmaXY / Math.sqrt(sigmaX2 * sigmaY2);
		return result;
	}

	public double[] getValues() {
		double[] values = new double[rowCount()];
		for (int i = 1; i <= rowCount(); i++) {
			values[i - 1] = getValue(i);
		}
		return values;
	}

	public double getStandardDeviation() {
		double mean = getMean();
		double sigmaX2 = 0;
		for (double val : getValues()) {
			sigmaX2 += Math.pow(val - mean, 2);
		}
		double sd = Math.sqrt(sigmaX2 / size());
		return sd;
	}

	public Vector standardize() {
		double sd = getStandardDeviation();
		Vector result = getDeviation();
		for (int i = 1; i <= size(); i++) {
			result.setValue(i, result.getValue(i) / sd);
		}
		return result;
	}

	public Vector standardize(double mean, double sd) {
		double thisSd = getStandardDeviation();
		Vector result = getDeviation();
		for (int i = 1; i <= size(); i++) {
			result.setValue(i, result.getValue(i) / thisSd * sd + mean);
		}
		return result;
	}

	public Vector normalize() {
		return standardize();
	}

	public String getColumnLabel() {
		return getColumnLabel(1);
	}

	public Vector getVariance() {
		Vector result = getDeviation();
		for (int i = 1; i <= result.size(); i++) {
			result.setValue(i, Math.pow(result.getValue(i), 2));
		}
		return result;
	}

	public double getSumSquares() {
		return dotProduct(this);
	}

	public Vector getResiduals() {
		Vector result = new Vector(size());
		double mean = getMean();
		for (int i = 1; i <= size(); i++) {
			result.setValue(i, getValue(i) - mean);
		}
		return result;
	}

	public double getSumSquares(Vector v) {
		double result = 0;
		if (v == null)
			throw new Error("cannot measure distance to a null vector");
		if (v.size() != this.size())
			throw new Error("Vectors must be same size!");
		for (int i = 1; i <= this.size(); i++) {
			result += Math.pow(this.getValue(i) - v.getValue(i), 2);
		}
		return result;
	}

	public String[] getLabels() {
		return super.getRowLabels();
	}

	public void setLabels(String[] labels) {
		super.setRowLabels(labels);
	}

	public double getAngle() {
		if (size() != 2)
			throw new Error("This method applies only for vectors of length 2!");
		double angle = Math.atan(getValue(1) / getValue(2));
		if (getValue(1) < 0 && getValue(2) < 0)
			angle = Math.PI + angle;
		else if (getValue(1) < 0 && getValue(2) > 0)
			angle = 2 * Math.PI + angle;
		else if (getValue(1) > 0 && getValue(2) < 0)
			angle = Math.PI + angle;
		return angle;
	}

	public Vector rotate(double theta) {
		if (size() != 2)
			throw new Error("This method only applies for vectors of length 2!");
		Matrix rotation = new Matrix(new double[][] {
				{ Math.cos(-theta), -Math.sin(-theta) },
				{ Math.sin(-theta), Math.cos(-theta) } });
		return rotation.times(this);
	}

	public Vector plus(Vector vector) {
		if (size() != vector.size())
			throw new Error("Cannot add a vector of a different size");
		Vector result = new Vector(size());
		for (int i = 1; i <= size(); i++) {
			result.setValue(i, this.getValue(i) + vector.getValue(i));
		}
		return result;
	}

	@Override
	public Vector times(double d) {
		Vector result = new Vector(this);
		for (int i = 1; i <= size(); i++) {
			result.setValue(i, getValue(i) * d);
		}
		return result;
	}

	public Vector add(Vector v) {
		return super.add(v).getColumnVector(1);
	}

	public double dotProduct(Vector v) {
		double result = 0;
		if (size() != v.size())
			throw new Error(
					"can only do dotProduct with a vector of the same size");
		for (int i = 1; i <= size(); i++) {
			result += getValue(i) * v.getValue(i);
		}
		return result;
	}

	public double getMaxAbsoluteValue() {
		double result = 0;
		for (int i = 1; i <= size(); i++) {
			result = Math.max(getValue(i), result);
		}
		return result;
	}

	public RegressionLine getLinearRegression(Vector v) {

		double sumX = getSum();
		double sumY = v.getSum();
		double sumXY = dotProduct(v);
		double sumXX = getSumSquares();
		double sumYY = v.getSumSquares();
		double a0 = 0;
		double a1 = 0;
		int n = size();
		if (n >= 2) {
			float xBar = (float) sumX / n;
			float yBar = (float) sumY / n;
			a1 = (float) ((n * sumXY - sumX * sumY) / (n * sumXX - sumX * sumX));
			a0 = (float) (yBar - a1 * xBar);
		} else {
			throw new Error("need at least 2 rows to perform linear regression");
		}
		// calculate r2
		double numerator = n * sumXY - sumX * sumY;
		numerator = numerator * numerator;
		double denom = (n * sumXX - sumX * sumX) * (n * sumYY - sumY * sumY);
		double r2 = numerator / denom;
		return new RegressionLine(a0, a1, r2);
	}

	public Vector(Vector vector) {
		this(vector.rowCount(), vector.columnCount());
		for (int i = 1; i <= rowCount(); i++) {
			setValue(i, vector.getValue(i));
		}
		setRowLabels(vector.getRowLabels());
		setColumnLabels(vector.getColumnLabels());
	}

	public Vector add(double d) {
		Vector v = new Vector(this);
		for (int i = 1; i <= v.rowCount(); i++) {
			v.setValue(i, v.getValue(i) + d);
		}
		return v;
	}

	public Vector minus(Vector v) {

		return this.add(v.times(-1));
	}

	public Vector getSquare() {
		Vector v = new Vector(this);
		for (int i = 1; i <= v.rowCount(); i++) {
			v.setValue(i, v.getValue(i) * v.getValue(i));
		}
		return v;
	}

	/**
	 * @param distribution
	 *            ordered from low to high
	 * @return
	 */
	public Vector getDistributed(List<Double> distribution) {
		if (isNaN())
			return new Vector(size(), Double.NaN);
		Vector result = new Vector(this);
		Vector m = new Vector(this);
		double biggerThanMax = getMaximum() + 1;
		for (Double value : distribution) {
			int i = m.indexOfMinimum();
			result.setValue(i, value);
			m.setValue(i, biggerThanMax);
		}
		return result;
	}

	@Override
	public double getMaximum() {
		double result = 0;
		for (int i = 1; i <= size(); i++) {
			if (i == 1)
				result = getValue(i);
			result = Math.max(result, getValue(i));
		}
		return result;
	}

	public int indexOfMaximum() {
		int index = 1;
		for (int i = 1; i <= size(); i++) {
			if (getValue(i) > getValue(index))
				index = i;
		}
		return index;
	}

	public int indexOfMinimum() {
		int index = 1;
		for (int i = 1; i <= size(); i++) {
			if (getValue(i) < getValue(index))
				index = i;
		}
		return index;
	}

	public double getMinimum() {
		double result = 0;
		for (int i = 1; i <= size(); i++) {
			if (i == 1)
				result = getValue(i);
			result = Math.min(result, getValue(i));
		}
		return result;
	}

	public Matrix addColumn(Vector v2) {
		Matrix m = addColumn();
		return m.setColumnVector(m.columnCount(), v2);
	}

	/**
	 * get best angle that rotates this with v2 to get v
	 * 
	 * @param v2
	 * @param v
	 * @return
	 */
	public double getBestCorrelatedRotation(Vector v2, final Vector v) {

		if (size() != v2.size() || size() != v.size()) {
			throw new Error("vectors must all be of the same length");
		}
		double corr = getPearsonCorrelation(v);
		Matrix m = addColumn(v2);
		double initialRotation = 0;

		int numSteps = 17;
		double stepSize = 10;
		for (int i = 1; i <= numSteps; i++) {
			for (int sign : new int[] { -1, 1 }) {
				double angle = sign * i * stepSize;
				double c = m.rotateDegrees(1, 2, angle).getColumnVector(1)
						.getPearsonCorrelation(v);
				if (c > corr) {
					corr = c;
					initialRotation = angle;
				}
			}
		}
		m = m.rotateDegrees(1, 2, initialRotation);
		final Matrix finalM = m;

		final double delta = 0.0001;

		final moten.david.util.math.Function f = new moten.david.util.math.Function() {
			@Override
			public double f(double x) {
				return finalM.rotateDegrees(1, 2, x).getColumnVector(1)
						.getPearsonCorrelation(v);
			}
		};

		final moten.david.util.math.Function fd = new moten.david.util.math.Function() {

			@Override
			public double f(double x) {
				return (f.f(x + delta) - f.f(x)) / delta;
			}
		};

		// bisection method
		double startX = -stepSize / 2;
		double endX = stepSize / 2;
		while (endX - startX > 0.0001) {
			double x = (startX + endX) / 2;
			double derivative = fd.f(x);
			if (derivative > 0)
				startX = x;
			else
				endX = x;
		}
		double x = (startX + endX) / 2;
		return Math.PI / 180 * (initialRotation + x);
	}

	private static class ValueAndIndex {
		int index;
		double value;

		ValueAndIndex(int index, double value) {
			super();
			this.index = index;
			this.value = value;
		}

		@Override
		public String toString() {
			return "[index=" + index + ", value=" + value + "]";
		}
	}

	/**
	 * Returns a list of the row numbers of the elements in ordered absolute
	 * value. For example the vector (0.3,0.1,-0.2) returns (2,3,1) from this
	 * method with ascending=true.
	 * 
	 * @param ascending
	 * @return
	 */
	public List<Integer> getOrderedIndicesByAbsoluteValue(
			final boolean ascending) {
		List<ValueAndIndex> list = Lists.newArrayList();
		for (int i = 1; i <= size(); i++) {
			list.add(new ValueAndIndex(i, getValue(i)));
		}
		Collections.sort(list, new Comparator<ValueAndIndex>() {
			@Override
			public int compare(ValueAndIndex o1, ValueAndIndex o2) {
				int multiplier = ascending ? 1 : -1;
				return multiplier
						* ((Double) Math.abs(o1.value)).compareTo((Math
								.abs(o2.value)));
			}
		});
		List<Integer> orderedRowNumbers = Lists.newArrayList();
		for (ValueAndIndex v : list) {
			orderedRowNumbers.add(v.index);
		}
		return orderedRowNumbers;
	}

	/**
	 * Returns the row switching elementary matrix (identity matrix with rows
	 * switched) that orders <code>this</code> into ascending or descending
	 * absolute value when the matrix is multiplied by <code>this</code>.
	 * 
	 * @param ascending
	 * @return
	 */
	public Matrix getRowSwitchingMatrixToOrderByAbsoluteValue(boolean ascending) {
		Matrix identity = Matrix.getIdentity(size());
		Matrix m = Matrix.getIdentity(size());
		List<Integer> rowNumbers = getOrderedIndicesByAbsoluteValue(ascending);
		for (int i = 1; i <= size(); i++) {
			m.setRow(i, identity.getRow(rowNumbers.get(i - 1)));
		}
		return m;
	}
}
