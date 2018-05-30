package com.github.deliberateq.util.math;

public class FactorAnalysisInput {
	private final Matrix correlations;
	private final FactorExtractionMethod extractionMethod;
	private final EigenvalueThreshold eigenvalueThreshold;

	public FactorAnalysisInput(Matrix correlations,
			FactorExtractionMethod extractionMethod,
			EigenvalueThreshold eigenvalueThreshold) {
		super();
		this.correlations = correlations;
		this.extractionMethod = extractionMethod;
		this.eigenvalueThreshold = eigenvalueThreshold;
	}

	public Matrix getCorrelations() {
		return correlations;
	}

	public FactorExtractionMethod getExtractionMethod() {
		return extractionMethod;
	}

	public EigenvalueThreshold getEigenvalueThreshold() {
		return eigenvalueThreshold;
	}

}