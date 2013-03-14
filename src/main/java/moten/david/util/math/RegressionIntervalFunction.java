package moten.david.util.math;

import org.apache.commons.math.MathException;
import org.apache.commons.math.distribution.TDistribution;
import org.apache.commons.math.distribution.TDistributionImpl;

public class RegressionIntervalFunction implements Function {

	private Vector v1;
	private boolean singleResponse;
	private TDistribution tDistribution;
	private double sumSquares;
	private double mean;
	private double sum;

	public RegressionIntervalFunction(Vector v1, boolean singleResponse) {
		this.v1 = v1;
		this.singleResponse = singleResponse;
		if (v1.size() > 0)
			this.tDistribution = new TDistributionImpl(v1.size());
		this.sumSquares = v1.getSumSquares();
		this.mean = v1.getMean();
		this.sum = v1.getSum();
	}

	@Override
	public double f(double x) {
		double s = Math.sqrt(v1.getResiduals().getSumSquares()
				/ (v1.size() - 2));
		double t95 = 0;
		try {
			t95 = tDistribution.inverseCumulativeProbability(0.95);
		} catch (MathException e) {
			throw new Error(e);
		}
		return t95
				* s
				* Math.sqrt((singleResponse ? 1 : 0) + 1.0 / v1.size()
						+ Math.pow(x - mean, 2)
						/ (sumSquares - Math.pow(sum, 2) / v1.size()));
	}
}
