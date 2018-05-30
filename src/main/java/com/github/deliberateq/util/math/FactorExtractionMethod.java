package com.github.deliberateq.util.math;

public enum FactorExtractionMethod {
	PRINCIPAL_COMPONENTS_ANALYSIS("Principal Components Analysis"), CENTROID_METHOD(
			"Centroid Method");
	private String name;

	private FactorExtractionMethod(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return name;
	}
}