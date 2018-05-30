package com.github.deliberateq.util.math;

public final class RegressionLine implements Function {
	private final double a0;
	private final double a1;
	private final double r2;

	/**
	 * Line is y=a0+a1*x
	 * 
	 * @param a0
	 * @param a1
	 */
	public RegressionLine(double a0, double a1, double r2) {
		this.a0 = a0;
		this.a1 = a1;
		this.r2 = r2;
	}

	public double valueAt(double x) {
		return a0 + a1 * x;
	}

	public double getA0() {
		return a0;
	}

	public double getA1() {
		return a1;
	}

	public double getR2() {
		return r2;
	}

	@Override
	public double f(double x) {
		return valueAt(x);
	}

}