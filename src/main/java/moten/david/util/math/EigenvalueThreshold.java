package moten.david.util.math;

public class EigenvalueThreshold {

	public static EigenvalueThreshold createWithMaxFactors(int maxFactors) {
		return new EigenvalueThreshold(maxFactors);
	}

	public static EigenvalueThreshold createWithMinEigenvalue(
			double minEigenvalue) {
		return new EigenvalueThreshold(minEigenvalue);
	}

	private final PrincipalFactorCriterion principalFactorCriterion;
	private final Double minEigenvalue;
	private final Integer maxFactors;

	private EigenvalueThreshold(double minEigenvalue) {

		this.principalFactorCriterion = PrincipalFactorCriterion.MIN_EIGENVALUE;
		this.minEigenvalue = minEigenvalue;
		this.maxFactors = null;
	}

	private EigenvalueThreshold(int maxFactors) {

		this.principalFactorCriterion = PrincipalFactorCriterion.MAX_FACTORS;
		this.maxFactors = maxFactors;
		this.minEigenvalue = null;
	}

	public Double getMinEigenvalue() {
		return minEigenvalue;
	}

	public PrincipalFactorCriterion getPrincipalFactorCriterion() {
		return principalFactorCriterion;
	}

	public Integer getMaxFactors() {
		return maxFactors;
	}

	public static enum PrincipalFactorCriterion {
		MIN_EIGENVALUE, MAX_FACTORS
	};
}
