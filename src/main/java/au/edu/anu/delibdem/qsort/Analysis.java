package au.edu.anu.delibdem.qsort;

import java.util.HashSet;
import java.util.Set;

import moten.david.util.math.EigenvalueThreshold;
import moten.david.util.math.FactorAnalysisException;
import moten.david.util.math.FactorAnalysisResults;
import moten.david.util.math.FactorExtractionMethod;
import moten.david.util.math.Matrix;
import moten.david.util.math.Varimax.RotationMethod;

public class Analysis {

	public static FactorAnalysisResults getFactorAnalysisResults(Data data,
			DataSelection dataSelection, boolean isIntersubjective,
			FactorExtractionMethod method,
			EigenvalueThreshold eigenvalueThreshold) {
		Set<RotationMethod> rotationMethods = new HashSet<RotationMethod>();
		rotationMethods.add(RotationMethod.VARIMAX);
		try {
			Matrix m = getMatrix(data, dataSelection, isIntersubjective);
			if (m == null)
				return null;

			return m.analyzeFactors(method, eigenvalueThreshold,
					rotationMethods);
		} catch (FactorAnalysisException e) {
			throw new RuntimeException(e);
		}
	}

	private static Matrix getMatrix(Data data, DataSelection combination,
			boolean isIntersubjective) {
		Matrix m = data.getRawData(combination, null, (isIntersubjective ? 1
				: 2));
		return m;
	}

}
