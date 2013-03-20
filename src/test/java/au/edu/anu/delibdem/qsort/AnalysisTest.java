package au.edu.anu.delibdem.qsort;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;

import moten.david.util.math.EigenvalueThreshold;
import moten.david.util.math.FactorAnalysisResults;
import moten.david.util.math.FactorExtractionMethod;
import moten.david.util.math.Matrix;
import moten.david.util.math.Vector;

import org.junit.Assert;
import org.junit.Test;

public class AnalysisTest {

	private static final double PRECISION = 0.0001;

	@Test
	public void testBloomfieldAnalysisFirstStagePrincipalComponentsAnalysis()
			throws IOException {
		Data data = new Data(
				AnalysisTest.class
						.getResourceAsStream("/studies2/Bloomfield Track.txt"));

		FactorAnalysisResults r = Analysis.getFactorAnalysisResults(data,
				new DataSelection(data.getFilter(), "First"), true,
				FactorExtractionMethod.PRINCIPAL_COMPONENTS_ANALYSIS,
				EigenvalueThreshold.createWithMaxFactors(8));

		// check eigenvalues
		Vector eigenvalues = r.getEigenvaluesVector();
		assertTrue(eigenvalues.columnEquals(1, PRECISION, 6.6864, 1.3041,
				0.9285, 0.6598, 0.5559, 0.4329, 0.3922, 0.3302, 0.2578, 0.1903,
				0.1614, 0.1003));

		// check eigenvectors
		Matrix eigenvectors = r.getEigenvectors();
		assertTrue(eigenvectors.columnEquals(1, PRECISION, 0.3015, 0.2968,
				0.3250, 0.2934, 0.2544, 0.2142, 0.3141, 0.2952, 0.3172, 0.2926,
				0.2367, 0.3009));

		assertTrue(eigenvectors.columnEquals(2, PRECISION, 0.1467, 0.0944,
				0.1194, -0.4096, 0.5221, 0.4636, -0.0038, 0.0271, -0.0016,
				-0.3352, -0.4190, -0.1065));

		assertEquals(12, eigenvectors.columnCount());

		// check loadings
		Matrix loadings = r.getLoadings();
		assertTrue(loadings.columnEquals(1, PRECISION, 0.7797, 0.7675, 0.8405,
				0.7588, 0.6578, 0.5538, 0.8122, 0.7634, 0.8203, 0.7565, 0.6122,
				0.7781));

		assertTrue(loadings.columnEquals(2, PRECISION, 0.1676, 0.1079, 0.1363,
				-0.4678, 0.5962, 0.5294, -0.0043, 0.0310, -0.0018, -0.3828,
				-0.4784, -0.1217));

		// check principal eigenvalues
		Vector principalEigenvalues = r.getPrincipalEigenvaluesVector();
		assertTrue(principalEigenvalues.columnEquals(1, PRECISION, 6.6864,
				1.3041, 0.9285, 0.6598, 0.5559, 0.4329, 0.3922, 0.3302));

		// check principal eigenvectors
		Matrix principalEigenvectors = r.getPrincipalEigenvectors();
		assertEquals(8, principalEigenvectors.columnCount());
		assertTrue(principalEigenvectors.columnEquals(1, PRECISION, 0.3015,
				0.2968, 0.3250, 0.2934, 0.2544, 0.2142, 0.3141, 0.2952, 0.3172,
				0.2926, 0.2367, 0.3009));

		assertTrue(principalEigenvectors.columnEquals(2, PRECISION, 0.1467,
				0.0944, 0.1194, -0.4096, 0.5221, 0.4636, -0.0038, 0.0271,
				-0.0016, -0.3352, -0.4190, -0.1065));

		// check principal loadings
		Matrix pincipalLloadings = r.getPrincipalLoadings();
		assertTrue(pincipalLloadings.columnEquals(1, PRECISION, 0.7797, 0.7675,
				0.8405, 0.7588, 0.6578, 0.5538, 0.8122, 0.7634, 0.8203, 0.7565,
				0.6122, 0.7781));

		assertTrue(pincipalLloadings.columnEquals(2, PRECISION, 0.1676, 0.1079,
				0.1363, -0.4678, 0.5962, 0.5294, -0.0043, 0.0310, -0.0018,
				-0.3828, -0.4784, -0.1217));

		Assert.assertEquals("ADV", pincipalLloadings.getRowLabel(1));
		Assert.assertEquals("F1", pincipalLloadings.getColumnLabel(1));

	}

	@Test
	public void testLipsetAnalysis() throws IOException {
		Data data = new Data(
				AnalysisTest.class.getResourceAsStream("/studies2/Lipset.txt"));
	}
}
