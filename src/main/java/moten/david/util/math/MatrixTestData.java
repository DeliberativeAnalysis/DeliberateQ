package moten.david.util.math;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import moten.david.util.math.Matrix.NullStrategy;

public class MatrixTestData {

	public static Matrix getMatrix1() {
		Matrix m = new Matrix(10, 10);
		m.setColumnLabels(new String[] { "Foley", "Mrtek", "Kendall",
				"Hofmann", "Stephenson", "Burt-Cattell", "Kerlinger",
				"textbook", "quantum", "Brown" });
		int i = 1;
		m.setRow(i++, new double[] { 1.00, 0.17, 0.79, 0.76, -0.70, 0.86, 0.48,
				0.85, -0.71, -0.67 });
		m.setRow(i++, new double[] { 0.17, 1.00, 0.14, -0.05, 0.06, 0.12, 0.74,
				0.20, -0.08, 0.24 });
		m.setRow(i++, new double[] { 0.79, 0.14, 1.00, 0.73, -0.70, 0.70, 0.27,
				0.82, -0.53, -0.57 });
		m.setRow(i++, new double[] { 0.76, -0.05, 0.73, 1.00, -0.85, 0.80,
				0.23, 0.82, -0.77, -0.81 });
		m.setRow(i++, new double[] { -0.70, 0.06, -0.70, -0.85, 1.00, -0.82,
				-0.17, -0.76, 0.73, 0.76 });
		m.setRow(i++, new double[] { 0.86, 0.12, 0.70, 0.80, -0.82, 1.00, 0.39,
				0.82, -0.65, -0.66 });
		m.setRow(i++, new double[] { 0.48, 0.74, 0.27, 0.23, -0.17, 0.39, 1.00,
				0.44, -0.48, -0.28 });
		m.setRow(i++, new double[] { 0.85, 0.20, 0.82, 0.82, -0.76, 0.82, 0.44,
				1.00, -0.74, -0.67 });
		m.setRow(i++, new double[] { -0.71, -0.08, -0.53, -0.77, 0.73, -0.65,
				-0.48, -0.74, 1.00, 0.85 });
		m.setRow(i++, new double[] { -0.67, 0.24, -0.56, -0.82, 0.76, -0.65,
				-0.27, -0.67, 0.85, 1.00 });
		return m;
	}

	public static Matrix getMatrix2() {
		int i = 1;
		Matrix m = new Matrix(47, 14);
		m.setColumnLabels(new String[] { "R", "Age", "S", "Ed", "Ex0", "Ex1",
				"LF", "M", "N", "NW", "U1", "U2", "W", "X" });
		m.setRow(i++, new double[] { 79.1, 151, 1, 91, 58, 56, 510, 950, 33,
				301, 108, 41, 394, 261 });
		m.setRow(i++, new double[] { 163.5, 143, 0, 113, 103, 95, 583, 1012,
				13, 102, 96, 36, 557, 194 });
		m.setRow(i++, new double[] { 57.8, 142, 1, 89, 45, 44, 533, 969, 18,
				219, 94, 33, 318, 250 });
		m.setRow(i++, new double[] { 196.9, 136, 0, 121, 149, 141, 577, 994,
				157, 80, 102, 39, 673, 167 });
		m.setRow(i++, new double[] { 123.4, 141, 0, 121, 109, 101, 591, 985,
				18, 30, 91, 20, 578, 174 });
		m.setRow(i++, new double[] { 68.2, 121, 0, 110, 118, 115, 547, 964, 25,
				44, 84, 29, 689, 126 });
		m.setRow(i++, new double[] { 96.3, 127, 1, 111, 82, 79, 519, 982, 4,
				139, 97, 38, 620, 168 });
		m.setRow(i++, new double[] { 155.5, 131, 1, 109, 115, 109, 542, 969,
				50, 179, 79, 35, 472, 206 });
		m.setRow(i++, new double[] { 85.6, 157, 1, 90, 65, 62, 553, 955, 39,
				286, 81, 28, 421, 239 });
		m.setRow(i++, new double[] { 70.5, 140, 0, 118, 71, 68, 632, 1029, 7,
				15, 100, 24, 526, 174 });
		m.setRow(i++, new double[] { 167.4, 124, 0, 105, 121, 116, 580, 966,
				101, 106, 77, 35, 657, 170 });
		m.setRow(i++, new double[] { 84.9, 134, 0, 108, 75, 71, 595, 972, 47,
				59, 83, 31, 580, 172 });
		m.setRow(i++, new double[] { 51.1, 128, 0, 113, 67, 60, 624, 972, 28,
				10, 77, 25, 507, 206 });
		m.setRow(i++, new double[] { 66.4, 135, 0, 117, 62, 61, 595, 986, 22,
				46, 77, 27, 529, 190 });
		m.setRow(i++, new double[] { 79.8, 152, 1, 87, 57, 53, 530, 986, 30,
				72, 92, 43, 405, 264 });
		m.setRow(i++, new double[] { 94.6, 142, 1, 88, 81, 77, 497, 956, 33,
				321, 116, 47, 427, 247 });
		m.setRow(i++, new double[] { 53.9, 143, 0, 110, 66, 63, 537, 977, 10,
				6, 114, 35, 487, 166 });
		m.setRow(i++, new double[] { 92.9, 135, 1, 104, 123, 115, 537, 978, 31,
				170, 89, 34, 631, 165 });
		m.setRow(i++, new double[] { 75.0, 130, 0, 116, 128, 128, 536, 934, 51,
				24, 78, 34, 627, 135 });
		m.setRow(i++, new double[] { 122.5, 125, 0, 108, 113, 105, 567, 985,
				78, 94, 130, 58, 626, 166 });
		m.setRow(i++, new double[] { 74.2, 126, 0, 108, 74, 67, 602, 984, 34,
				12, 102, 33, 557, 195 });
		m.setRow(i++, new double[] { 43.9, 157, 1, 89, 47, 44, 512, 962, 22,
				423, 97, 34, 288, 276 });
		m.setRow(i++, new double[] { 121.6, 132, 0, 96, 87, 83, 564, 953, 43,
				92, 83, 32, 513, 227 });
		m.setRow(i++, new double[] { 96.8, 131, 0, 116, 78, 73, 574, 1038, 7,
				36, 142, 42, 540, 176 });
		m.setRow(i++, new double[] { 52.3, 130, 0, 116, 63, 57, 641, 984, 14,
				26, 70, 21, 486, 196 });
		m.setRow(i++, new double[] { 199.3, 131, 0, 121, 160, 143, 631, 1071,
				3, 77, 102, 41, 674, 152 });
		m.setRow(i++, new double[] { 34.2, 135, 0, 109, 69, 71, 540, 965, 6, 4,
				80, 22, 564, 139 });
		m.setRow(i++, new double[] { 121.6, 152, 0, 112, 82, 76, 571, 1018, 10,
				79, 103, 28, 537, 215 });
		m.setRow(i++, new double[] { 104.3, 119, 0, 107, 166, 157, 521, 938,
				168, 89, 92, 36, 637, 154 });
		m.setRow(i++, new double[] { 69.6, 166, 1, 89, 58, 54, 521, 973, 46,
				254, 72, 26, 396, 237 });
		m.setRow(i++, new double[] { 37.3, 140, 0, 93, 55, 54, 535, 1045, 6,
				20, 135, 40, 453, 200 });
		m.setRow(i++, new double[] { 75.4, 125, 0, 109, 90, 81, 586, 964, 97,
				82, 105, 43, 617, 163 });
		m.setRow(i++, new double[] { 107.2, 147, 1, 104, 63, 64, 560, 972, 23,
				95, 76, 24, 462, 233 });
		m.setRow(i++, new double[] { 92.3, 126, 0, 118, 97, 97, 542, 990, 18,
				21, 102, 35, 589, 166 });
		m.setRow(i++, new double[] { 65.3, 123, 0, 102, 97, 87, 526, 948, 113,
				76, 124, 50, 572, 158 });
		m.setRow(i++, new double[] { 127.2, 150, 0, 100, 109, 98, 531, 964, 9,
				24, 87, 38, 559, 153 });
		m.setRow(i++, new double[] { 83.1, 177, 1, 87, 58, 56, 638, 974, 24,
				349, 76, 28, 382, 254 });
		m.setRow(i++, new double[] { 56.6, 133, 0, 104, 51, 47, 599, 1024, 7,
				40, 99, 27, 425, 225 });
		m.setRow(i++, new double[] { 82.6, 149, 1, 88, 61, 54, 515, 953, 36,
				165, 86, 35, 395, 251 });
		m.setRow(i++, new double[] { 115.1, 145, 1, 104, 82, 74, 560, 981, 96,
				126, 88, 31, 488, 228 });
		m.setRow(i++, new double[] { 88.0, 148, 0, 122, 72, 66, 601, 998, 9,
				19, 84, 20, 590, 144 });
		m.setRow(i++, new double[] { 54.2, 141, 0, 109, 56, 54, 523, 968, 4, 2,
				107, 37, 489, 170 });
		m.setRow(i++, new double[] { 82.3, 162, 1, 99, 75, 70, 522, 996, 40,
				208, 73, 27, 496, 224 });
		m.setRow(i++, new double[] { 103.0, 136, 0, 121, 95, 96, 574, 1012, 29,
				36, 111, 37, 622, 162 });
		m.setRow(i++, new double[] { 45.5, 139, 1, 88, 46, 41, 480, 968, 19,
				49, 135, 53, 457, 249 });
		m.setRow(i++, new double[] { 50.8, 126, 0, 104, 106, 97, 599, 989, 40,
				24, 78, 25, 593, 171 });
		m.setRow(i++, new double[] { 84.9, 130, 0, 121, 90, 91, 623, 1049, 3,
				22, 113, 40, 588, 160 });
		return m;
	}

	public static Matrix getBrownTable26Loadings()
			throws FactorAnalysisException, IOException {
		// Matrix m;
		// m = getMatrix2();
		// AnalysisResults results = m.analyzeFactors(0.0);
		// System.out.println(m.getPearsonCorrelationMatrix());
		// System.out.println(results.loadings);
		// Matrix approximation = results.loadings.times(results.loadings
		// .transpose());
		// System.out
		// .println(m.getPearsonCorrelationMatrix().minus(approximation));
		// approximation = results.rotatedLoadings.get(RotationMethod.IVARIMAX)
		// .times(
		// results.rotatedLoadings.get(RotationMethod.IVARIMAX)
		// .transpose());
		// System.out
		// .println(m.getPearsonCorrelationMatrix().minus(approximation));

		Matrix m = getBrownTable26();
		FactorAnalysisResults results = m.analyzeFactors(
				FactorExtractionMethod.CENTROID_METHOD,
				new EigenvalueThreshold(1.0), null);
		Matrix loadings = results.getLoadings().subMatrixColumns(1, 7);
		String[] labels = loadings.getColumnLabels();
		loadings = loadings.rotateDegrees(1, 2, 31);
		loadings = loadings.rotateDegrees(1, 3, -35);
		loadings = loadings.rotateDegrees(1, 5, -19);
		loadings = loadings.rotateDegrees(2, 4, 13);
		loadings = loadings.rotateDegrees(4, 6, 9);
		loadings = loadings.rotateDegrees(3, 6, -12);
		loadings = loadings.rotateDegrees(2, 5, -10);
		loadings = loadings.rotateDegrees(2, 6, -10);
		loadings = loadings.rotateDegrees(4, 5, 34);
		loadings = loadings.rotateDegrees(5, 6, -25);
		loadings = loadings.rotateDegrees(5, 7, 29);
		loadings = loadings.rotateDegrees(3, 7, -15);
		loadings.setColumnLabels(labels);
		// System.out.println("manually rotated loadings:\n" + loadings);

		// Graph3dPanel graph = new Graph3dPanel();
		// graph.setMatrix(loadings.subMatrixColumns(1, 3));
		//
		// int numFactors = 3;
		// Matrix factorScores = m.getFactorScores(loadings.subMatrixColumns(1,
		// numFactors), 0.3, getSampleDistributionSpssCarSave());
		// System.out.println("manually rotated factor scores:\n" +
		// factorScores);

		return loadings;
	}

	private static void testPca() throws FactorAnalysisException {
		InputStream is = Double.class.getResourceAsStream("/car.txt");
		Matrix m;
		try {
			m = new Matrix(is, true, true, NullStrategy.SET_TO_NULL_ENTRY_CODE);
			is.close();
		} catch (IOException e) {
			throw new Error(e);
		}
		FactorAnalysisResults results = m.analyzeFactors(
				FactorExtractionMethod.PRINCIPAL_COMPONENTS_ANALYSIS,
				new EigenvalueThreshold(1.0), null);
		System.out.println(results);
		results = m.analyzeFactors(FactorExtractionMethod.CENTROID_METHOD,
				new EigenvalueThreshold(1.0), null);
		System.out.println(results);
	}

	public static List<Double> getSampleDistributionSpssCarSave() {
		List<Double> distribution = new ArrayList<Double>();
		distribution.add(-4.0);
		distribution.add(-4.0);
		distribution.add(-3.0);
		distribution.add(-3.0);
		distribution.add(-3.0);
		distribution.add(-2.0);
		distribution.add(-2.0);
		distribution.add(-2.0);
		distribution.add(-2.0);
		distribution.add(-1.0);
		distribution.add(-1.0);
		distribution.add(-1.0);
		distribution.add(-1.0);
		distribution.add(-1.0);
		distribution.add(0.0);
		distribution.add(0.0);
		distribution.add(0.0);
		distribution.add(0.0);
		distribution.add(0.0);
		distribution.add(1.0);
		distribution.add(1.0);
		distribution.add(1.0);
		distribution.add(1.0);
		distribution.add(1.0);
		distribution.add(2.0);
		distribution.add(2.0);
		distribution.add(2.0);
		distribution.add(2.0);
		distribution.add(3.0);
		distribution.add(3.0);
		distribution.add(3.0);
		distribution.add(4.0);
		distribution.add(4.0);
		return distribution;
	}

	public static void main(String[] args) throws FactorAnalysisException,
			IOException {
		// getBrownTable26Loadings();
		// testPca();
		Matrix m = getBrownTable26();

		FactorAnalysisResults results = m.analyzeFactors(
				FactorExtractionMethod.PRINCIPAL_COMPONENTS_ANALYSIS,
				new EigenvalueThreshold(0.2), null);
		System.out.println(results);
		// results = m.analyzeFactors(FactorExtractionMethod.CENTROID_METHOD,
		// 1.0);
		// System.out.println(results);

		// m = m.transpose();
		// for (int i=1;i<=m.columnCount();i++) {
		// m.setColumnLabel(i, Data.COLUMN_PREFIX_Q_RESULTS_FORCED + (i-2));
		// }
		// m = m.insertColumn().setColumnLabel(1, Data.COLUMN_PARTICIPANT_TYPE);
		// for (int i = 1; i <= m.rowCount(); i++) {
		// m.setValue(i, 1, 1);
		// }
		// m = m.insertColumn().setColumnLabel(1, Data.COLUMN_PARTICIPANT_NO);
		// for (int i = 1; i <= m.rowCount(); i++) {
		// m.setValue(i, 1, i);
		// }
		// m = m.insertColumn().setColumnLabel(1, Data.COLUMN_STAGE);
		// for (int i = 1; i <= m.rowCount(); i++) {
		// m.setValue(i, 1, 1);
		// }
		// m.writeToFile(new File("/temp.txt"), false);
	}

	public static Vector getVector(double x, double y) {
		Vector v = new Vector(2);
		v.setValue(1, x);
		v.setValue(2, y);
		return v;
	}

	public static Matrix getBrownTable26() {
		Matrix m = new Matrix(33, 9);
		m.setColumnLabel(1, "1.US");
		m.setColumnLabel(2, "2.US");
		m.setColumnLabel(3, "3.US");
		m.setColumnLabel(4, "4.US");
		m.setColumnLabel(5, "5.Japan");
		m.setColumnLabel(6, "6.Canada");
		m.setColumnLabel(7, "7.Britain");
		m.setColumnLabel(8, "8.US");
		m.setColumnLabel(9, "9.France");
		int i = 1;
		m.setRow(i++, new double[] { 4, 4, 7, 8, 1, 6, 7, 3, 8 });
		m.setRow(i++, new double[] { 5, 5, 3, 6, 4, 2, 5, 7, 6 });
		m.setRow(i++, new double[] { 3, 4, 3, 2, 8, 5, 3, 5, 5 });
		m.setRow(i++, new double[] { 5, 2, 9, 4, 4, 8, 6, 2, 6 });
		m.setRow(i++, new double[] { 3, 7, 4, 4, 6, 8, 5, 1, 1 });
		m.setRow(i++, new double[] { 6, 8, 5, 8, 6, 9, 6, 9, 2 });
		m.setRow(i++, new double[] { 5, 6, 1, 2, 9, 3, 4, 5, 7 });
		m.setRow(i++, new double[] { 4, 6, 2, 3, 7, 5, 2, 4, 7 });
		m.setRow(i++, new double[] { 5, 1, 6, 5, 1, 3, 5, 4, 3 });
		m.setRow(i++, new double[] { 4, 5, 1, 1, 9, 3, 4, 4, 5 });
		m.setRow(i++, new double[] { 6, 7, 2, 4, 7, 6, 6, 6, 5 });
		m.setRow(i++, new double[] { 6, 4, 8, 5, 5, 4, 4, 4, 3 });
		m.setRow(i++, new double[] { 7, 9, 8, 8, 4, 6, 6, 6, 6 });
		m.setRow(i++, new double[] { 8, 4, 7, 3, 8, 5, 7, 4, 9 });
		m.setRow(i++, new double[] { 4, 6, 5, 2, 5, 1, 1, 9, 4 });
		m.setRow(i++, new double[] { 1, 2, 2, 1, 7, 8, 9, 5, 3 });
		m.setRow(i++, new double[] { 2, 5, 7, 4, 5, 4, 8, 5, 7 });
		m.setRow(i++, new double[] { 2, 3, 4, 7, 3, 5, 7, 6, 4 });
		m.setRow(i++, new double[] { 4, 3, 6, 9, 3, 6, 5, 2, 7 });
		m.setRow(i++, new double[] { 1, 5, 5, 3, 5, 4, 7, 6, 6 });
		m.setRow(i++, new double[] { 8, 8, 6, 9, 4, 3, 3, 7, 8 });
		m.setRow(i++, new double[] { 7, 3, 3, 6, 7, 2, 4, 7, 2 });
		m.setRow(i++, new double[] { 8, 6, 5, 6, 6, 4, 2, 8, 2 });
		m.setRow(i++, new double[] { 6, 5, 7, 3, 2, 1, 1, 8, 3 });
		m.setRow(i++, new double[] { 6, 7, 3, 5, 2, 7, 8, 2, 6 });
		m.setRow(i++, new double[] { 5, 6, 8, 6, 2, 7, 3, 6, 5 });
		m.setRow(i++, new double[] { 9, 7, 6, 4, 8, 5, 5, 1, 4 });
		m.setRow(i++, new double[] { 7, 8, 4, 7, 5, 9, 9, 8, 1 });
		m.setRow(i++, new double[] { 7, 4, 5, 5, 6, 4, 2, 5, 5 });
		m.setRow(i++, new double[] { 2, 1, 4, 7, 3, 7, 6, 3, 4 });
		m.setRow(i++, new double[] { 3, 3, 4, 6, 6, 6, 4, 3, 8 });
		m.setRow(i++, new double[] { 3, 2, 9, 7, 3, 7, 8, 3, 4 });
		m.setRow(i++, new double[] { 9, 9, 6, 5, 4, 2, 3, 7, 9 });
		m.setRowLabelPattern("Q<index>");
		return m;
	}

}