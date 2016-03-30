package moten.david.util.math;

import org.apache.commons.math3.distribution.TDistribution;

public class RegressionIntervalFunction implements Function {

	private final Vector v1;
	private final boolean singleResponse;
	private TDistribution tDistribution;
	private final double sumSquares;
	private final double mean;
	private final double sum;

	public RegressionIntervalFunction(Vector v1, boolean singleResponse) {
		this.v1 = v1;
		this.singleResponse = singleResponse;
		if (v1.size() > 0)
			this.tDistribution = new TDistribution(v1.size());
		this.sumSquares = v1.getSumSquares();
		this.mean = v1.getMean();
		this.sum = v1.getSum();
	}

	@Override
	public double f(double x) {
		double s = Math.sqrt(v1.getResiduals().getSumSquares() / (v1.size() - 2));
		double t95 = tDistribution.inverseCumulativeProbability(0.95);
		return t95 * s * Math.sqrt((singleResponse ? 1 : 0) + 1.0 / v1.size()
				+ Math.pow(x - mean, 2) / (sumSquares - Math.pow(sum, 2) / v1.size()));
	}
}
