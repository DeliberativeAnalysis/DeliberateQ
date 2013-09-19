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
	private static final double LIPSET_PRECISION = 0.01;

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
		System.out.println(eigenvalues);
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
		Matrix principalLoadings = r.getPrincipalLoadings();
		assertTrue(principalLoadings.columnEquals(1, PRECISION, 0.7797, 0.7675,
				0.8405, 0.7588, 0.6578, 0.5538, 0.8122, 0.7634, 0.8203, 0.7565,
				0.6122, 0.7781));

		assertTrue(principalLoadings.columnEquals(2, PRECISION, 0.1676, 0.1079,
				0.1363, -0.4678, 0.5962, 0.5294, -0.0043, 0.0310, -0.0018,
				-0.3828, -0.4784, -0.1217));

		// check principal loadings labels
		Assert.assertEquals("ADV", principalLoadings.getRowLabel(1));
		Assert.assertEquals("F1", principalLoadings.getColumnLabel(1));

	}

	@Test
	public void testCentroidMethodLipsetAnalysisWithMinEigenvalue()
			throws IOException {
		Data data = new Data(
				AnalysisTest.class.getResourceAsStream("/studies2/Lipset.txt"));
		FactorAnalysisResults r = Analysis.getFactorAnalysisResults(data,
				new DataSelection(data.getFilter(), "Questionnaire"), true,
				FactorExtractionMethod.CENTROID_METHOD,
				EigenvalueThreshold.createWithMinEigenvalue(1.0));

		checkLipsetAnalysisPreReduction(r);

		assertEquals(1, r.getEigenvalueThreshold().getMinEigenvalue(),
				PRECISION);

		// check principal eigenvalues
		assertTrue(r.getPrincipalEigenvaluesVector().columnEquals(1, PRECISION,
				1.6158, 1.4119));

		// check principal eigenvectors
		assertEquals(2, r.getPrincipalEigenvectors().columnCount());
		assertTrue(r.getPrincipalEigenvectors().columnEquals(1, PRECISION,
				-0.2292, -0.3020, 0.2896, 0.2633, -0.4318, 0.3763, 0.4587,
				-0.4057, 0.0042));
		assertTrue(r.getPrincipalEigenvectors().columnEquals(2, PRECISION,
				-0.4626, -0.2174, -0.4485, -0.4109, 0.4637, 0.2174, 0.2330,
				-0.2116, -0.0861));

		// check principal loadings
		assertEquals(2, r.getPrincipalLoadings().columnCount());
		assertTrue(r.getPrincipalLoadings().columnEquals(1, PRECISION, -0.2913,
				-0.3839, 0.3681, 0.3347, -0.5489, 0.4784, 0.5831, -0.5157,
				0.0054));
		assertTrue(r.getPrincipalLoadings().columnEquals(2, PRECISION, -0.5497,
				-0.2583, -0.5329, -0.4882, 0.5510, 0.2583, 0.2768, -0.2515,
				-0.1023));

	}

	@Test
	public void testCentroidMethodUsingMaxFactorsAndEnsurePrincipalEigenvaluesSelection()
			throws IOException {
		Data data = new Data(
				AnalysisTest.class.getResourceAsStream("/studies2/Lipset.txt"));
		FactorAnalysisResults r = Analysis.getFactorAnalysisResults(data,
				new DataSelection(data.getFilter(), "Questionnaire"), true,
				FactorExtractionMethod.CENTROID_METHOD,
				EigenvalueThreshold.createWithMaxFactors(6));

		checkLipsetAnalysisPreReduction(r);

		assertEquals(6, (int) r.getEigenvalueThreshold().getMaxFactors());

		// check principal eigenvalues
		System.out.println(r.getPrincipalEigenvaluesVector());
		assertTrue(r.getPrincipalEigenvaluesVector().columnEquals(1, PRECISION,
				1.6158, 1.4119, 0.8403, 0.5542, 0.3849, 0.2781));

	}

	private void checkLipsetAnalysisPreReduction(FactorAnalysisResults r) {
		// check eigenvalues
		assertTrue(r.getEigenvaluesVector().columnEquals(1, PRECISION, 1.6158,
				1.4119, 0.8403, 0.5542, 0.3849, 0.2781, 0.1874, 0.1839, 0.0840));

		// check eigenvectors
		assertTrue(r.getEigenvectors().columnEquals(1, PRECISION, -0.2292,
				-0.3020, 0.2896, 0.2633, -0.4318, 0.3763, 0.4587, -0.4057,
				0.0042));
		assertTrue(r.getEigenvectors()
				.columnEquals(9, PRECISION, 0.1862, 0.2926, 0.0037, 0.2897,
						0.3807, 0.5688, 0.0107, -0.3388, 0.4610));
		// check loadings
		assertTrue(r.getLoadings().columnEquals(1, PRECISION, -0.2913, -0.3839,
				0.3681, 0.3347, -0.5489, 0.4784, 0.5831, -0.5157, 0.0054));
		assertTrue(r.getLoadings().columnEquals(2, PRECISION, -0.5497, -0.2583,
				-0.5329, -0.4882, 0.5510, 0.2583, 0.2768, -0.2515, -0.1023));
		assertTrue(r.getLoadings().columnEquals(9, PRECISION, 0.0540, 0.0848,
				0.0011, 0.0840, 0.1103, 0.1648, 0.0031, -0.0982, 0.1336));

		// check percent variance
		assertTrue(r.getPercentVariance()
				.columnEquals(1, PRECISION, 17.9536, 15.6878, 9.3365, 6.1583,
						4.2772, 3.0895, 2.0823, 2.0432, 0.9334));
	}
}
