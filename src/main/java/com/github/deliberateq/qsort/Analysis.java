package com.github.deliberateq.qsort;

import java.util.HashSet;
import java.util.Set;

import com.github.deliberateq.util.math.CorrelationCoefficient;
import com.github.deliberateq.util.math.EigenvalueThreshold;
import com.github.deliberateq.util.math.FactorAnalysisException;
import com.github.deliberateq.util.math.FactorAnalysisResults;
import com.github.deliberateq.util.math.FactorExtractionMethod;
import com.github.deliberateq.util.math.Matrix;
import com.github.deliberateq.util.math.Varimax.RotationMethod;

public class Analysis {

	public static FactorAnalysisResults getFactorAnalysisResults(Data data,
			DataSelection dataSelection, boolean isIntersubjective,
			FactorExtractionMethod method,
			EigenvalueThreshold eigenvalueThreshold, String title, CorrelationCoefficient cc) {
		Set<RotationMethod> rotationMethods = new HashSet<RotationMethod>();
		rotationMethods.add(RotationMethod.VARIMAX);
		try {
			Matrix m = getMatrix(data, dataSelection, isIntersubjective);
			if (m == null)
				return null;

			FactorAnalysisResults result = m.analyzeFactors(method,
					eigenvalueThreshold, rotationMethods, cc);
			result.setTitle(title);
			return result;
		} catch (FactorAnalysisException e) {
			throw new RuntimeException(e);
		}
	}

	private static Matrix getMatrix(Data data, DataSelection combination,
			boolean isIntersubjective) {
		Matrix m = data.getRawData(combination, null, isIntersubjective ? 1
				: 2);
		return m;
	}

}
