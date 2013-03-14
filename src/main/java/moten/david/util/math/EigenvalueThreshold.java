package moten.david.util.math;

public class EigenvalueThreshold {

	private PrincipalFactorCriterion principalFactorCriterion;
	private Double minEigenvalue;

	public EigenvalueThreshold() {
		// no argument constructor
	}

	public EigenvalueThreshold(double minEigenvalue) {

		this.principalFactorCriterion = PrincipalFactorCriterion.MIN_EIGENVALUE;
		this.minEigenvalue = minEigenvalue;
	}

	public Double getMinEigenvalue() {
		return minEigenvalue;
	}

	public void setMinEigenvalue(Double minEigenvalue) {
		this.minEigenvalue = minEigenvalue;
	}

	private Integer maxFactors;

	public PrincipalFactorCriterion getPrincipalFactorCriterion() {
		return principalFactorCriterion;
	}

	public void setPrincipalFactorCriterion(
			PrincipalFactorCriterion principalFactorCriterion) {
		this.principalFactorCriterion = principalFactorCriterion;
	}

	public Integer getMaxFactors() {
		return maxFactors;
	}

	public void setMaxFactors(Integer maxFactors) {
		this.maxFactors = maxFactors;
	}

	public static enum PrincipalFactorCriterion {
		MIN_EIGENVALUE, MAX_FACTORS
	};
}
