package moten.david.util.math;

public class RegressionLine implements Function {
	private double a0;
	private double a1;
	private double r2;

	/**
	 * Line is y=a0+a1*x
	 * 
	 * @param a0
	 * @param a1
	 */
	public RegressionLine(double a0, double a1, double r2) {
		super();
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

	public void setA0(double a0) {
		this.a0 = a0;
	}

	public double getA1() {
		return a1;
	}

	public void setA1(double a1) {
		this.a1 = a1;
	}

	public double getR2() {
		return r2;
	}

	public void setR2(double r2) {
		this.r2 = r2;
	}

	@Override
	public double f(double x) {
		return valueAt(x);
	}

}