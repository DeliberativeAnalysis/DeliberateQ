package moten.david.util.math;

import java.awt.Point;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.Serializable;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.JFrame;

import moten.david.util.StringOutputStream;
import moten.david.util.math.EigenvalueThreshold.PrincipalFactorCriterion;
import moten.david.util.math.Varimax.RotationMethod;
import moten.david.util.math.gui.GraphPanel;
import moten.david.util.permutation.Possibility;
import moten.david.util.permutation.Processor;
import moten.david.util.web.html.Html;

import org.apache.commons.math.MathException;
import org.apache.commons.math.distribution.TDistribution;
import org.apache.commons.math.distribution.TDistributionImpl;

import Jama.EigenvalueDecomposition;
import Jama.SingularValueDecomposition;

import com.google.inject.internal.Lists;

/**
 * @author dave
 * 
 */
public class Matrix implements Html, Serializable, MatrixProvider {

	private static final long serialVersionUID = 9023597611514181325L;

	public static double nullEntry = -99999999.1111;

	private final double[][] m;

	private String[] rowLabels;

	@Override
	public boolean equals(Object obj) {
		if (obj == null)
			return false;
		if (obj instanceof Matrix) {
			return equals((Matrix) obj);
		}
		return false;
	}

	public boolean equals(Matrix matrix) {
		if (matrix == null)
			return false;
		if (matrix.rowCount() != rowCount()
				|| matrix.columnCount() != columnCount())
			return false;
		for (MatrixEntry entry : getEntries()) {
			if (getValue(entry) != matrix.getValue(entry))
				return false;
		}
		return true;
	}

	@Override
	public int hashCode() {
		return ((Double) getValue(1, 1)).hashCode();
	}

	private String[] columnLabels;

	private String decimalFormat = "0.0000";

	private Object userObject;

	private void init() {
		initRowLabels();
		initColumnLabels();
	}

	public Matrix(int rows, int columns) {
		m = new double[rows][columns];
		init();
	}

	public Matrix addRow() {
		Matrix matrix = new Matrix(rowCount() + 1, columnCount());
		for (int i = 1; i <= rowCount(); i++) {
			matrix.setRowLabel(i, getRowLabel(i));
			for (int j = 1; j <= columnCount(); j++) {
				matrix.setValue(i, j, getValue(i, j));
			}
		}
		for (int j = 1; j <= columnCount(); j++) {
			matrix.setColumnLabel(j, getColumnLabel(j));
		}
		return matrix;
	}

	public Matrix setRowLabel(int row, String label) {
		rowLabels[row - 1] = label;
		return this;
	}

	private void initRowLabels() {
		rowLabels = new String[rowCount()];
		for (int i = 0; i < rowLabels.length; i++) {
			rowLabels[i] = "Row " + (i + 1);
		}
	}

	public Matrix setColumnLabel(int column, String label) {
		columnLabels[column - 1] = label;
		return this;
	}

	private void initColumnLabels() {
		columnLabels = new String[columnCount()];
		for (int i = 0; i < columnLabels.length; i++) {
			columnLabels[i] = "Col " + (i + 1);
		}
	}

	public Matrix setRowLabels(String[] labels) {
		rowLabels = new String[rowCount()];
		for (int i = 0; i < labels.length; i++)
			setRowLabel(i + 1, labels[i]);
		return this;
	}

	public String[] getRowLabels() {
		return rowLabels;
	}

	public Matrix setColumnLabels(String[] labels) {
		columnLabels = new String[columnCount()];
		if (labels != null && labels.length > 0) {
			for (int i = 0; i < labels.length; i++)
				setColumnLabel(i + 1, labels[i]);
		}
		return this;
	}

	public String[] getColumnLabels() {
		return columnLabels;
	}

	public static interface Function {
		public double f(int row, int col, double x);
	}

	public Matrix apply(Function function) {
		Matrix result = copy();
		for (int i = 1; i <= rowCount(); i++) {
			for (int j = 1; j <= columnCount(); j++) {
				result.setValue(i, j, function.f(i, j, getValue(i, j)));
			}
		}
		return result;
	}

	public Matrix(double[][] matrix) {
		m = new double[matrix.length][matrix[0].length];
		for (int i = 0; i < matrix.length; i++) {
			for (int j = 0; j < matrix[0].length; j++) {
				m[i][j] = matrix[i][j];
			}
		}
		init();
	}

	public int rowCount() {
		if (m.length == 0)
			return 0;
		else
			return m.length;
	}

	public int columnCount() {
		if (m.length == 0)
			return 0;
		else
			return m[0].length;
	}

	public Matrix setRow(int i, double[] row) {
		for (int j = 0; j < row.length; j++) {
			m[i - 1][j] = row[j];
		}
		return this;
	}

	public double getValue(int i, int j) {
		return m[i - 1][j - 1];
	}

	public double getValue(Point p) {
		return getValue(p.x, p.y);
	}

	public Matrix setValue(int i, int j, double value) {
		m[i - 1][j - 1] = value;
		return this;
	}

	public void plusValue(int i, int j, double value) {
		setValue(i, j, getValue(i, j) + value);
	}

	private String pad(String s, int toLength, boolean prefix) {
		StringBuffer padded = new StringBuffer(s);
		for (int i = 0; i < toLength - s.length(); i++) {
			if (prefix)
				padded.insert(0, " ");
			else
				padded.append(" ");
		}
		return padded.toString();
	}

	private int getRowHeaderWidth() {
		int result = 0;
		if (rowLabels != null) {
			for (int i = 1; i <= rowCount(); i++) {
				if (rowLabels[i - 1].length() > result)
					result = rowLabels[i - 1].length();
			}
			return result + 1;// pad it out by 1 space
		} else
			return 0;
	}

	private int[] getColumnWidths() {
		DecimalFormat df = new DecimalFormat(this.decimalFormat);
		int[] maxSize = new int[columnCount()];
		for (int i = 1; i <= rowCount(); i++) {
			for (int j = 1; j <= columnCount(); j++) {
				if (df.format(getValue(i, j)).length() > maxSize[j - 1])
					maxSize[j - 1] = df.format(getValue(i, j)).length();
			}
		}
		if (columnLabels != null)
			for (int j = 1; j <= columnCount(); j++) {
				if (columnLabels[j - 1].length() > maxSize[j - 1])
					maxSize[j - 1] = columnLabels[j - 1].length();
			}
		// increase all column widths by 1
		for (int j = 1; j <= columnCount(); j++) {
			maxSize[j - 1]++;
		}

		return maxSize;
	}

	@Override
	public String toString() {
		return toString(false);
	}

	public String toString(boolean html) {
		if (html)
			return toHtml();
		StringBuffer s = new StringBuffer();
		int[] maxSize = getColumnWidths();
		int maxRowHeaderWidth = getRowHeaderWidth();
		s.append(pad("", maxRowHeaderWidth, true));
		if (columnLabels != null) {
			for (int j = 1; j <= columnCount(); j++) {
				s.append(pad(columnLabels[j - 1], maxSize[j - 1], true));
			}
			s.append("\n");
		}
		for (int i = 1; i <= rowCount(); i++) {
			StringBuffer row = new StringBuffer();
			for (int j = 1; j <= columnCount(); j++) {
				if (row.length() > 0)
					row.append("");
				if (j == 1 && rowLabels != null)
					row.append(pad(rowLabels[i - 1], maxRowHeaderWidth, true));
				DecimalFormat df = new DecimalFormat(this.decimalFormat);
				String valueString = df.format(getValue(i, j));
				if (getValue(i, j) == nullEntry)
					valueString = "-";
				row.append(pad(valueString, maxSize[j - 1], true));
			}
			if (s.length() > 0)
				s.append("\n");
			s.append(row);

		}
		s.append("\n");
		return s.toString();
	}

	public Matrix copy() {
		Matrix result = new Matrix(m);
		result.setColumnLabels(columnLabels);
		result.setRowLabels(rowLabels);
		return result;
	}

	public Matrix copyStructure() {
		return new Matrix(rowCount(), columnCount());
	}

	public Matrix subMatrix(int startRow, int endRow, int startColumn,
			int endColumn) {
		Matrix result = new Matrix(endRow - startRow + 1, endColumn
				- startColumn + 1);
		for (int i = startRow; i <= endRow; i++) {
			for (int j = startColumn; j <= endColumn; j++) {
				result.setValue(i - startRow + 1, j - startColumn + 1,
						getValue(i, j));
			}
		}
		if (rowLabels != null) {
			String[] a = new String[endRow - startRow + 1];
			for (int i = startRow; i <= endRow; i++) {
				a[i - startRow] = rowLabels[i - 1];
			}
			result.setRowLabels(a);
		}
		if (columnLabels != null) {
			String[] a = new String[endColumn - startColumn + 1];
			for (int i = startColumn; i <= endColumn; i++) {
				a[i - startColumn] = columnLabels[i - 1];
			}
			result.setColumnLabels(a);
		}
		return result;
	}

	public Vector getColumnVector(int column) {
		Vector result = new Vector(rowCount());
		for (int i = 1; i <= rowCount(); i++) {
			result.setValue(i, getValue(i, column));
		}
		result.setRowLabels(rowLabels);
		if (columnLabels != null)
			result.setColumnLabels(new String[] { columnLabels[column - 1] });
		return result;
	}

	public Matrix getPearsonCorrelationMatrix() {
		Matrix result = new Matrix(columnCount(), columnCount());
		for (int i = 1; i <= columnCount(); i++) {
			for (int j = 1; j <= columnCount(); j++) {
				Vector v1 = getColumnVector(i);
				Vector v2 = getColumnVector(j);
				result.setValue(i, j, v1.getPearsonCorrelation(v2));
			}
		}
		result.setRowLabels(columnLabels);
		result.setColumnLabels(columnLabels);
		return result;
	}

	public Jama.Matrix toJamaMatrix() {
		return new Jama.Matrix(m);
	}

	public Matrix(Jama.Matrix jamaMatrix) {
		this(jamaMatrix.getArray());
	}

	public Matrix times(Matrix matrix) {

		if (columnCount() != matrix.rowCount())
			throw new Error("cannot multipy these two matrices, invalid sizes");
		Matrix result = new Matrix(rowCount(), matrix.columnCount());
		for (int i = 1; i <= result.rowCount(); i++) {
			for (int j = 1; j <= result.columnCount(); j++) {
				for (int k = 1; k <= columnCount(); k++) {
					result.plusValue(i, j,
							getValue(i, k) * matrix.getValue(k, j));
				}
			}
		}
		result.setRowLabels(rowLabels);
		result.setColumnLabels(matrix.columnLabels);
		return result;
	}

	public Vector times(Vector vector) {
		Matrix matrix = this.times((Matrix) vector);
		return matrix.getColumnVector(1);
	}

	public Matrix times(double d) {
		Matrix result = copy();
		for (int i = 1; i <= rowCount(); i++) {
			for (int j = 1; j <= columnCount(); j++) {
				result.setValue(i, j, result.getValue(i, j) * d);
			}
		}
		return result;
	}

	public Matrix transpose() {
		Matrix result = new Matrix(columnCount(), rowCount());
		for (int i = 1; i <= rowCount(); i++) {
			for (int j = 1; j <= columnCount(); j++) {
				result.setValue(j, i, getValue(i, j));
			}
		}
		result.setRowLabels(columnLabels);
		result.setColumnLabels(rowLabels);
		return result;
	}

	public Matrix set(Matrix matrix, int i, int j) {
		for (int row = 1; row <= matrix.rowCount(); row++) {
			for (int col = 1; col <= matrix.columnCount(); col++) {
				setValue(i + row - 1, j + col - 1, matrix.getValue(row, col));
			}
		}
		return this;
	}

	public static Matrix createDiagonalMatrix(Vector vector) {
		Matrix matrix = new Matrix(vector.rowCount(), vector.rowCount());
		for (int i = 1; i <= vector.rowCount(); i++)
			matrix.setValue(i, i, vector.getValue(i));
		return matrix;
	}

	// private String[] join(String[] a1, String[] a2) {
	// if (a1 == null)
	// return a2;
	// if (a2 == null)
	// return a1;
	// String[] result = new String[a1.length + a2.length];
	// for (int i = 0; i < a1.length; i++) {
	// result[i] = a1[i];
	// }
	// for (int i = 0; i < a2.length; i++) {
	// result[i + a1.length] = a2[i];
	// }
	// return result;
	// }

	public Matrix appendRight(Matrix matrix) {
		if (rowCount() != matrix.rowCount())
			throw new Error("matrices must have same number of rows");
		Matrix result = new Matrix(rowCount(), this.columnCount()
				+ matrix.columnCount());
		result.set(this, 1, 1);
		result.set(matrix, 1, columnCount() + 1);
		result.setRowLabels(rowLabels);
		// result.setColumnLabels(join(columnLabels,matrix.columnLabels));
		return result;
	}

	public Vector getDiagonal() {
		if (rowCount() != columnCount())
			throw new Error("rowCount must equal columnCount");
		double[] a = new double[rowCount()];
		for (int i = 1; i <= rowCount(); i++) {
			a[i - 1] = getValue(i, i);
		}
		return new Vector(a);
	}

	public Matrix standardizeColumns() {
		Matrix result = getColumnVector(1).standardize();
		for (int i = 2; i <= columnCount(); i++) {
			result = result.appendRight(getColumnVector(i).standardize());
		}
		result.setRowLabels(rowLabels);
		result.setColumnLabels(columnLabels);
		return result;
	}

	public String getDecimalFormat() {
		return decimalFormat;
	}

	public Matrix setDecimalFormat(String decimalFormat) {
		this.decimalFormat = decimalFormat;
		return this;
	}

	public Matrix reverseColumns() {
		Matrix result = copy();
		for (int i = 1; i <= rowCount(); i++) {
			for (int j = 1; j <= columnCount(); j++) {
				result.setValue(i, j, getValue(i, columnCount() - j + 1));
				result.setColumnLabel(j, getColumnLabel(columnCount() - j + 1));
			}
		}
		result.setRowLabels(rowLabels);
		return result;
	}

	public Matrix reverseRows() {
		Matrix result = copy();
		for (int i = 1; i <= rowCount(); i++) {
			for (int j = 1; j <= columnCount(); j++) {
				result.setValue(i, j, getValue(rowCount() - i + 1, j));
				result.setRowLabel(i, getRowLabel(rowCount() - i + 1));
			}
		}
		result.setColumnLabels(columnLabels);
		return result;
	}

	public Matrix add(Matrix matrix) {
		if (rowCount() != matrix.rowCount()
				|| columnCount() != matrix.columnCount())
			throw new Error("matrices must be same size");
		Matrix result = copy();
		for (int i = 1; i <= rowCount(); i++) {
			for (int j = 1; j <= columnCount(); j++) {
				result.setValue(i, j, getValue(i, j) + matrix.getValue(i, j));
			}
		}
		return result;
	}

	public Matrix minus(Matrix matrix) {
		if (rowCount() != matrix.rowCount()
				|| columnCount() != matrix.columnCount())
			throw new Error("matrices must be same size");
		Matrix result = copy();
		for (int i = 1; i <= rowCount(); i++) {
			for (int j = 1; j <= columnCount(); j++) {
				result.setValue(i, j, getValue(i, j) - matrix.getValue(i, j));
			}
		}
		return result;
	}

	public static void main(String[] args) throws FactorAnalysisException,
			MathException {
		Matrix m = new Matrix(14, 10);
		m.setRowLabels(new String[] { "Brazil", "Burma", "China", "Cuba",
				"Egypt", "India", "Indonesia", "Israel", "Jordan",
				"Netherlands", "Poland", "USSR", "UK", "US" });
		m.setColumnLabels(new String[] { "GNP per cap", "Trade", "Power",
				"Stability", "Freedom of oppn", "Foreign conflict",
				"US agreement", "Defence budget", "%GNP Defence",
				"Int law acceptance" });
		m.setRow(1, new double[] { 91, 2729, 7, 0, 2, 0, 69.1, 148, 2.8, 0 });// brazil
		m.setRow(2, new double[] { 51, 407, 4, 0, 1, 0, -9.5, 74, 6.9, 0 });// burma
		m.setRow(3, new double[] { 58, 349, 11, 0, 0, 1, -41.7, 3054, 8.7, 0 });// china
		m.setRow(4, new double[] { 359, 1169, 3, 0, 1, 0, 64.3, 53, 2.4, 0 });// cuba
		m.setRow(5, new double[] { 134, 923, 5, 1, 1, 1, -15.4, 158, 6, 1 });// egypt
		m.setRow(6, new double[] { 70, 2689, 10, 0, 2, 0, -28.6, 410, 1.9, 1 });// india
		m.setRow(7, new double[] { 129, 1601, 8, 0, 1, 0, -21.4, 267, 6.7, 0 });// indonesia
		m.setRow(8, new double[] { 515, 415, 2, 1, 2, 1, 42.9, 33, 2.7, 1 });// israel
		m.setRow(9, new double[] { 70, 83, 1, 0, 1, 1, 8.3, 29, 25.7, 0 });// jordan
		m.setRow(10, new double[] { 707, 5395, 6, 1, 2, 0, 52.3, 468, 6.1, 1 });// netherlands
		m.setRow(11, new double[] { 468, 1852, 9, 0, 0, 1, -41.7, 220, 1.5, 0 });// poland
		m.setRow(12, new double[] { 749, 6530, 13, 1, 0, 1, -41.7, 34000, 20.4,
				0 });// USSR
		m.setRow(13, new double[] { 998, 18677, 12, 1, 2, 1, 69, 3934, 7.8, 0 });// UK
		m.setRow(14, new double[] { 2334, 26836, 14, 1, 2, 1, 100, 40641, 12.2,
				1 });// US

		// c.setValue(1, 1, 0.97);
		// c.setValue(2, 2, 0.97);
		// c.setValue(3, 3, 0.89);
		// c.setValue(4, 4, 0.63);
		// c.setValue(5, 5, 0.91);
		// c.setValue(6, 6, 0.61);
		// c.setValue(7, 7, 0.89);
		// c.setValue(8, 8, 0.9);
		// c.setValue(9, 9, 0.73);
		// c.setValue(10, 10, 0.82);

		m.analyzeFactors(FactorExtractionMethod.PRINCIPAL_COMPONENTS_ANALYSIS,
				EigenvalueThreshold.createWithMinEigenvalue(0.5), null);

		m = new Matrix(new double[][] { { 1, 2, 3 }, { 2, 3, 4 }, { 4, 7, 9 } });
		new Matrix(new double[][] { { 1, 1.5, 4 }, { 3, 4, 5 }, { 8, 4, 2 } });
		Vector v = m.getColumnVector(1);
		Matrix m3 = v.addColumn(m.getColumnVector(2));
		Matrix m4 = m3.rotateDegrees(1, 2, 25);
		System.out.println(m3 + "\n" + m4);
		System.out.println("rot="
				+ m3.getColumnVector(1).getBestCorrelatedRotation(
						m3.getColumnVector(2), m4.getColumnVector(1)) * 180
				/ Math.PI);

	}

	public FactorAnalysisResults analyzeFactors(
			FactorExtractionMethod extractionMethod,
			EigenvalueThreshold eigenvalueThreshold,
			Set<RotationMethod> rotationMethods) throws FactorAnalysisException {
		Matrix c = getPearsonCorrelationMatrix();
		FactorAnalysisResults r = c.analyzeCorrelationMatrixFactors(
				extractionMethod, eigenvalueThreshold, rotationMethods);
		r.setInitial(this);
		return r;
	}

	public FactorAnalysisResults analyzeFactors(
			FactorExtractionMethod extractionMethod,
			Set<RotationMethod> rotationMethods) throws FactorAnalysisException {
		Matrix c = getPearsonCorrelationMatrix();
		FactorAnalysisResults r = c.analyzeCorrelationMatrixFactors(
				extractionMethod, rotationMethods);
		r.setInitial(this);
		return r;
	}

	public Matrix removeRow(int i) {
		Matrix matrix = new Matrix(this.rowCount() - 1, this.columnCount());
		for (int row = 1; row <= matrix.rowCount(); row++) {
			for (int col = 1; col <= matrix.columnCount(); col++) {
				int k = row;
				if (k >= i)
					k = k + 1;
				matrix.setValue(row, col, this.getValue(k, col));
				matrix.setRowLabel(row, this.getRowLabel(k));
			}
		}
		matrix.setColumnLabels(getColumnLabels());
		return matrix;
	}

	public Matrix removeColumn(int i) {
		Matrix matrix = new Matrix(this.rowCount(), this.columnCount() - 1);
		for (int row = 1; row <= matrix.rowCount(); row++) {
			for (int col = 1; col <= matrix.columnCount(); col++) {
				int k = col;
				if (k >= i)
					k = k + 1;
				matrix.setValue(row, col, this.getValue(row, k));
				matrix.setColumnLabel(col, this.getColumnLabel(k));
			}
		}
		matrix.setRowLabels(getRowLabels());
		return matrix;
	}

	public Matrix sortColumns(final VectorFunction vectorFunction) {
		final Matrix m = copy();
		Integer[] indexes = new Integer[columnCount()];
		for (int i = 0; i < indexes.length; i++) {
			indexes[i] = i + 1;
		}
		Arrays.sort(indexes, new Comparator<Integer>() {

			@Override
			public int compare(Integer o1, Integer o2) {
				Double f1 = vectorFunction.f(m.getColumnVector(o1));
				Double f2 = vectorFunction.f(m.getColumnVector(o2));
				return f1.compareTo(f2);
			}
		});
		for (int i = 1; i <= indexes.length; i++) {
			m.setColumnVector(i, getColumnVector(indexes[i - 1]));
		}
		return m;
	}

	public void writeToFile(File file, boolean append) {
		try {
			FileOutputStream fos;
			fos = new FileOutputStream(file, append);
			fos.write(getTabDelimited().getBytes());
			fos.close();
		} catch (Exception e) {
			throw new Error(e);
		}
	}

	public FactorAnalysisResults analyzeCorrelationMatrixFactors(
			FactorExtractionMethod extractionMethod,
			Set<RotationMethod> rotationMethods) throws FactorAnalysisException {
		// return analyzeCorrelationMatrixFactors(extractionMethod, 2.5 * 1 /
		// Math
		// .sqrt(rowCount()));
		return analyzeCorrelationMatrixFactors(extractionMethod,
				EigenvalueThreshold.createWithMinEigenvalue(1.0),
				rotationMethods);
	}

	private static boolean legacy = true;

	public FactorAnalysisResults analyzeCorrelationMatrixFactors(
			FactorExtractionMethod extractionMethod,
			EigenvalueThreshold eigenvalueThreshold,
			Set<RotationMethod> rotationMethods) throws FactorAnalysisException {
		final FactorAnalysisResults r = new FactorAnalysisResults();
		r.extractionMethod = extractionMethod;
		r.setCorrelations(this);
		r.setEigenvalueThreshold(eigenvalueThreshold);
		if (isNaN())
			throw new FactorAnalysisException(
					"correlation matrix is invalid (perhaps the raw data has a column with a std deviation of 0?)");
		if (extractionMethod
				.equals(FactorExtractionMethod.PRINCIPAL_COMPONENTS_ANALYSIS)) {
			performPrincipalComponentsAnalysis(eigenvalueThreshold, r);
		} else if (extractionMethod
				.equals(FactorExtractionMethod.CENTROID_METHOD)) {
			performCentroidMethod(eigenvalueThreshold, r);
		}

		if (legacy) {
			normalizeLoadingSigns(r);

			// TODO ensure eigenvalues are in descending order
			// should be done before principal eigen* are calculated
			makeEigenvaluesDescendInValue(r);
		}

		r.setRotatedLoadings(new RotatedLoadings());
		RotationMethod[] methods;
		if (rotationMethods == null) {
			methods = RotationMethod.values();
		} else {
			methods = rotationMethods.toArray(new RotationMethod[] {});
		}
		long t = System.currentTimeMillis();
		for (RotationMethod rotationMethod : methods) {
			if (!rotationMethod.equals(RotationMethod.OBLIMIN)
					&& !rotationMethod.equals(RotationMethod.NMETHODS)) {
				List<MatrixRotation> rotations = r.getPrincipalLoadings()
						.getRotations(rotationMethod, true);
				Matrix rotatedLoadings = r.getPrincipalLoadings().rotate(
						rotations, true);
				r.getRotatedLoadings().put(rotationMethod, rotatedLoadings);
			}
		}

		r.setRotationTimeMs(System.currentTimeMillis() - t);
		final Vector temp = r.getEigenvalues().getDiagonal();
		r.setPercentVariance(temp.apply(new Function() {
			@Override
			public double f(int row, int col, double x) {
				return 100 * temp.getValue(row) / temp.size();
			}
		}).getColumnVector(1));
		r.getPercentVariance().setColumnLabel(1, "%Variance");
		r.getPercentVariance().setRowLabelPattern("F<index>");
		return r;
	}

	/**
	 * TODO
	 * 
	 * @param r
	 */
	private void makeEigenvaluesDescendInValue(FactorAnalysisResults r) {
		{
			Matrix rowSwitcher = r.getEigenvaluesVector()
					.getRowSwitchingMatrixToOrderByAbsoluteValue(false);
			// switch rows on eigenvalue vector
			r.setEigenvalues(Matrix.createDiagonalMatrix(rowSwitcher.times(r
					.getEigenvaluesVector())));
			// switch columns on eigenvectors matrix
			r.setEigenvectors(r.getEigenvectors()
					.times(rowSwitcher.transpose()));
			// switch columns on loadings
			r.setLoadings(r.getLoadings().times(rowSwitcher.transpose()));
		}
	}

	/**
	 * Updates {@link FactorAnalysisResults} so that the max absolute value
	 * loadings are positive. The sign normalization matrix when multiplied by
	 * its transpose is the identity matrix so this normalization of the
	 * loadings is valid.
	 * 
	 * @param r
	 */
	private void normalizeLoadingSigns(FactorAnalysisResults r) {
		{
			Matrix sn = r
					.getLoadings()
					.getSignNormalizationElementaryMatrixSoMaxAbsoluteValueByColumnIsPositive();
			r.setLoadings(r.getLoadings().times(sn));
			r.setEigenvectors(r.getEigenvectors().times(sn));
		}
		{
			Matrix snPrincipal = r
					.getPrincipalLoadings()
					.getSignNormalizationElementaryMatrixSoMaxAbsoluteValueByColumnIsPositive();
			r.setPrincipalLoadings(r.getPrincipalLoadings().times(snPrincipal));
			r.setPrincipalEigenvectors(r.getPrincipalEigenvectors().times(
					snPrincipal));
		}
	}

	private void performCentroidMethod(EigenvalueThreshold eigenvalueThreshold,
			final FactorAnalysisResults r) {
		long t = System.currentTimeMillis();
		CentroidResults results = analyzeCorrelationMatrixFactorsCentroidMethod();
		r.setExtractionTimeMs(System.currentTimeMillis() - t);
		r.setLoadings(results.loadings);
		r.setEigenvalues(results.eigenvalues);
		r.setEigenvectors(results.eigenvectors);
		r.setPrincipalEigenvalues(results.eigenvalues);
		r.setPrincipalEigenvectors(results.eigenvectors);
		Matrix loadings = results.loadings;
		makeEigenvaluesDescendInValue(r);
		{
			List<Integer> removeThese = Lists.newArrayList();
			for (int i = 1; i <= loadings.columnCount(); i++) {
				if (eigenvalueThreshold.getPrincipalFactorCriterion().equals(
						PrincipalFactorCriterion.MIN_EIGENVALUE)
						&& loadings.getColumnVector(i).getSquare().getSum() < eigenvalueThreshold
								.getMinEigenvalue()) {
					removeThese.add(i);
				}
			}
			Collections.sort(removeThese);
			for (int i = removeThese.size() - 1; i >= 0; i--) {
				Integer column = removeThese.get(i);
				r.setPrincipalEigenvalues(r.getPrincipalEigenvalues()
						.removeRow(column));
				r.setPrincipalEigenvalues(r.getPrincipalEigenvalues()
						.removeColumn(column));
				r.setPrincipalEigenvectors(r.getPrincipalEigenvectors()
						.removeColumn(column));
			}
		}
		{
			// now apply the max factors criterion if set
			if (eigenvalueThreshold.getPrincipalFactorCriterion().equals(
					PrincipalFactorCriterion.MAX_FACTORS)
					&& r.getPrincipalEigenvalues().rowCount() > eigenvalueThreshold
							.getMaxFactors()) {
				// for each extraneous row
				int extraRows = r.getPrincipalEigenvalues().rowCount()
						- eigenvalueThreshold.getMaxFactors();
				List<Integer> removeThese = Lists.newArrayList();
				for (int j = 1; j <= extraRows; j++) {
					// remove the row and col from loadings,
					// principalEigenvalues and principalEigenvectors if
					// it contains the smallest eigenvalue
					Point pos = r.getPrincipalEigenvalues().getDiagonal()
							.getPositionOfMinValue();
					removeThese.add(pos.x);
				}
				Collections.sort(removeThese);
				for (int j = removeThese.size() - 1; j >= 0; j--) {
					Integer row = removeThese.get(j);
					r.setPrincipalEigenvalues(r.getPrincipalEigenvalues()
							.removeRow(row));
					r.setPrincipalEigenvalues(r.getPrincipalEigenvalues()
							.removeColumn(row));
					r.setPrincipalEigenvectors(r.getPrincipalEigenvectors()
							.removeColumn(row));
				}
			}
		}
		r.setLoadings(r.getEigenvectors().times(
				r.getEigenvalues().apply(SQUARE_ROOT)));
		r.setPrincipalLoadings(r.getPrincipalEigenvectors().times(
				r.getPrincipalEigenvalues().apply(SQUARE_ROOT)));
	}

	public static Function SQUARE_ROOT = new Function() {
		@Override
		public double f(int i, int j, double x) {
			return Math.sqrt(x);
		}
	};

	private void performPrincipalComponentsAnalysis(
			EigenvalueThreshold eigenvalueThreshold,
			final FactorAnalysisResults r) {
		if (legacy) {
			long t = System.currentTimeMillis();
			EigenvalueDecomposition e = toJamaMatrix().eig();
			r.setExtractionTimeMs(System.currentTimeMillis() - t);
			r.setEigenvalues(new Matrix(e.getD()));
			r.getEigenvalues().setRowLabels(this.columnLabels);
			r.getEigenvalues().setColumnLabelPattern("F<reverse-index>");
			r.getEigenvalues().setRowLabelPattern("F<reverse-index>");
			r.setEigenvectors(new Matrix(e.getV()));
			r.getEigenvectors().setRowLabels(this.columnLabels);
			r.getEigenvectors().setColumnLabelPattern("F<reverse-index>");
			r.setPrincipalEigenvalues(r.getEigenvalues().copy());
			r.setPrincipalEigenvectors(r.getEigenvectors().copy());

			// min eigenvalue checks
			Vector eigenvalues = r.getEigenvaluesVector().copyVector();
			Matrix principalEigenvalues = removeEigenvalueRowsLessThanMinEigenvalue(
					eigenvalueThreshold, eigenvalues, r.getEigenvalues());
			r.setPrincipalEigenvalues(principalEigenvalues);

			Matrix principalEigenvectors = removeEigenvectorColumnsLessThanMinEigenvalue(
					eigenvalueThreshold, eigenvalues, r.getEigenvectors());
			r.setPrincipalEigenvectors(principalEigenvectors);

			// now apply the max factors criterion if set
			if (eigenvalueThreshold.getPrincipalFactorCriterion().equals(
					PrincipalFactorCriterion.MAX_FACTORS)
					&& r.getPrincipalEigenvalues().rowCount() > eigenvalueThreshold
							.getMaxFactors()) {
				// for each extraneous row
				int extraRows = r.getPrincipalEigenvalues().rowCount()
						- eigenvalueThreshold.getMaxFactors();
				List<Integer> removeThese = Lists.newArrayList();
				for (int j = 1; j <= extraRows; j++) {
					// remove the row and col from loadings,
					// principalEigenvalues and principalEigenvectors if
					// it contains the smallest eigenvalue
					Point pos = r.getPrincipalEigenvalues().getDiagonal()
							.getPositionOfMinValue();
					removeThese.add(pos.x);
				}
				Collections.sort(removeThese);
				for (int j = removeThese.size() - 1; j >= 0; j--) {
					Integer row = removeThese.get(j);
					r.setPrincipalEigenvalues(r.getPrincipalEigenvalues()
							.removeRow(row));
					r.setPrincipalEigenvalues(r.getPrincipalEigenvalues()
							.removeColumn(row));
					r.setPrincipalEigenvectors(r.getPrincipalEigenvectors()
							.removeColumn(row));
				}
			}

			r.setLoadings(r.getEigenvectors().times(
					r.getEigenvalues().apply(SQUARE_ROOT)));
			r.setPrincipalLoadings(r.getPrincipalEigenvectors().times(
					r.getPrincipalEigenvalues().apply(SQUARE_ROOT)));
			r.setLoadings(r.getLoadings().reverseColumns());
			r.setPrincipalLoadings(r.getPrincipalLoadings().reverseColumns());
			// clean up the presentation of the eigenvalues and vectors
			r.setEigenvalues(r.getEigenvalues().reverseColumns().reverseRows());
			r.setEigenvectors(r.getEigenvectors().reverseColumns());
			r.setPrincipalEigenvalues(r.getPrincipalEigenvalues()
					.reverseColumns().reverseRows());
			r.setPrincipalEigenvectors(r.getPrincipalEigenvectors()
					.reverseColumns());
		} else {
			long t = System.currentTimeMillis();
			EigenvalueDecomposition e = toJamaMatrix().eig();
			Matrix eigenvalues = new Matrix(e.getD());
			eigenvalues.setRowLabels(this.columnLabels);
			eigenvalues.setColumnLabelPattern("F<index>");
			Matrix eigenvectors = new Matrix(e.getV());
			eigenvectors.setRowLabels(this.columnLabels);
			eigenvectors.setColumnLabelPattern("F<index>");

			Components c = new Components(eigenvectors, eigenvalues);
			Components pc = c.getPrincipalComponents(eigenvalueThreshold);
			pc.getEigenvectors().setColumnLabelPattern("F<index>");

			r.setExtractionTimeMs(System.currentTimeMillis() - t);

			r.setEigenvalues(c.getEigenvalues());
			r.setEigenvectors(c.getEigenvectors());
			r.setLoadings(c.getLoadings());
			r.setPrincipalEigenvalues(pc.getEigenvalues());
			r.setPrincipalEigenvectors(pc.getEigenvectors());
			r.setPrincipalLoadings(pc.getLoadings());
		}
	}

	public Matrix removeEigenvectorColumnsLessThanMinEigenvalue(
			EigenvalueThreshold eigenvalueThreshold, Vector eigenvalues,
			Matrix principalEigenvectors) {
		if (eigenvalueThreshold.getPrincipalFactorCriterion().equals(
				PrincipalFactorCriterion.MIN_EIGENVALUE))
			for (int i = eigenvalues.rowCount(); i >= 1; i--) {
				if (eigenvalues.getValue(i, i) < eigenvalueThreshold
						.getMinEigenvalue()) {
					principalEigenvectors = principalEigenvectors
							.removeColumn(i);
				}
			}
		return principalEigenvectors;
	}

	private Matrix removeEigenvalueRowsLessThanMinEigenvalue(
			EigenvalueThreshold eigenvalueThreshold, Vector eigenvalues,
			Matrix principalEigenvalues) {
		for (int i = eigenvalues.rowCount(); i >= 1; i--) {
			if (eigenvalueThreshold.getPrincipalFactorCriterion().equals(
					PrincipalFactorCriterion.MIN_EIGENVALUE)
					&& eigenvalues.getValue(i) < eigenvalueThreshold
							.getMinEigenvalue()) {
				principalEigenvalues = principalEigenvalues.removeRow(i)
						.removeColumn(i);
			}
		}
		return principalEigenvalues;
	}

	public Matrix getKaiserNormalization(boolean inverse) {
		Matrix transposed = transpose();
		Vector kaiser = new Vector(rowCount());
		for (int i = 1; i <= rowCount(); i++) {
			double d = Math.sqrt(transposed.getColumnVector(i).getSumSquares());
			if (d == 0)
				d = 1;
			if (inverse)
				d = 1 / d;
			kaiser.setValue(i, d);
		}
		Matrix m = new Matrix(rowCount(), rowCount());
		m.setRowLabels(getRowLabels());
		m.setColumnLabels(getRowLabels());
		m.setDiagonal(kaiser);
		return m;
	}

	public List<MatrixRotation> getRotations(RotationMethod rotationMethod,
			boolean kaiserNormalized) {
		Matrix loadingsRotated = transpose();
		Varimax rotator = new Varimax(rotationMethod,
				loadingsRotated.getArray(), 0);
		rotator.setKaiserNormalisation(kaiserNormalized);
		Varimax.setVerbose(false);
		List<MatrixRotation> rotations = rotator.rotate();
		return rotations;
	}

	private String getTabDelimited() {
		StringOutputStream sos = new StringOutputStream();
		PrintWriter out = new PrintWriter(sos);

		for (int i = 1; i <= rowCount(); i++) {
			if (i == 1) {
				for (int j = 1; j <= columnCount(); j++) {
					out.print("\t" + getColumnLabel(j));
				}
				out.println();
			}
			for (int j = 1; j <= columnCount(); j++) {
				if (j == 1)
					out.print(getRowLabel(i));
				out.print("\t" + getValue(i, j));
			}
			out.println();
		}
		out.close();
		return sos.toString();
	}

	/**
	 * @param pattern
	 *            - the value <column> is substituted with the column number
	 */
	public Matrix setColumnLabelPattern(String pattern) {
		for (int i = 1; i <= columnCount(); i++) {
			String s = pattern.replace("<index>", i + "");
			s = s.replace("<reverse-index>", (columnCount() - i + 1) + "");
			setColumnLabel(i, s);
		}
		return this;
	}

	/**
	 * @param pattern
	 *            - the value <column> is substituted with the column number
	 */
	public Matrix setRowLabelPattern(String pattern) {
		for (int i = 1; i <= rowCount(); i++) {
			String s = pattern.replace("<index>", i + "");
			s = s.replace("<reverse-index>", (rowCount() - i + 1) + "");
			setRowLabel(i, s);
		}
		return this;
	}

	public boolean hasSameDimensions(Matrix m) {
		return m.rowCount() == rowCount() && m.columnCount() == columnCount();
	}

	public Matrix getPercentDifference(final Matrix m) {
		if (!this.hasSameDimensions(m))
			throw new Error("matrices must be same size");
		Matrix m2 = copy();
		m2 = m2.apply(new Function() {

			@Override
			public double f(int row, int col, double x) {
				return (m.getValue(row, col) - x) / x * 100.0;
			}
		});
		return m2;
	}

	private static int graphNumber = 0;

	public JFrame graphColumns(int col1, int col2) {
		graphNumber++;
		GraphPanel panel = new GraphPanel(this.getColumnVector(col1),
				getColumnVector(col2));
		panel.setLabelsVisible(false);
		JFrame frame = new JFrame();
		frame.add(panel);
		frame.setSize(500, 500);
		frame.setLocation(graphNumber * 20, graphNumber * 20);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		return frame;
	}

	private double[][] getArray() {
		return m;
	}

	public Matrix setColumnVector(int column, Vector vector) {
		if (rowCount() != vector.rowCount())
			throw new Error("vector must have same size as rows in matrix!");
		for (int i = 1; i <= rowCount(); i++) {
			this.setValue(i, column, vector.getValue(i));
		}
		this.setColumnLabel(column, vector.getColumnLabel(1));
		return this;
	}

	public Vector getColumnSumSquares() {
		Vector vector = new Vector(columnCount());
		for (int i = 1; i <= vector.rowCount(); i++) {
			vector.setValue(i, getColumnVector(i).getSumSquares());
		}
		vector.setLabels(getColumnLabels());
		return vector;
	}

	public String getRowLabel(int row) {
		if (rowLabels == null)
			return null;
		else
			return rowLabels[row - 1];
	}

	public String getColumnLabel(int col) {
		if (columnLabels == null)
			return null;
		else
			return columnLabels[col - 1];
	}

	public Vector getSumColumnVectors() {
		Vector v = new Vector(rowCount());
		for (int col = 1; col <= columnCount(); col++) {
			v = v.add(getColumnVector(col));
		}
		return v;
	}

	public String getDelimited(String delimiter, boolean includeLabels) {
		StringBuffer s = new StringBuffer();
		if (includeLabels) {
			StringBuffer line = new StringBuffer();
			// add one empty column for the column for the row label
			for (int j = 1; j <= columnCount(); j++) {
				line.append(delimiter);
				line.append(getColumnLabel(j));
			}
			s.append(line.toString());
			s.append("\n");
		}
		for (int i = 1; i <= rowCount(); i++) {
			StringBuffer line = new StringBuffer();
			if (includeLabels)
				line.append(getRowLabel(i));
			for (int j = 1; j <= columnCount(); j++) {
				if (line.length() > 0)
					line.append(delimiter);
				double val = getValue(i, j);
				if (Math.floor(val) == val) {
					DecimalFormat df = new DecimalFormat("0");
					line.append(df.format(val));
				} else
					line.append(getValue(i, j));
			}
			line.append("\n");
			s.append(line);
		}
		return s.toString();
	}

	public int columnOfLeastTotal() {
		double total = getColumnVector(1).getSum();
		int resultColumn = 1;
		for (int col = 2; col <= columnCount(); col++) {
			double sum = getColumnVector(col).getSum();
			if (sum < total) {
				total = sum;
				resultColumn = col;
			}
		}
		return resultColumn;
	}

	public Matrix multiplyColumn(int col, double x) {
		Matrix m = copy();
		for (int row = 1; row <= rowCount(); row++) {
			m.setValue(row, col, x * m.getValue(row, col));
		}
		return m;
	}

	public Matrix multiplyRow(int row, double x) {
		Matrix m = copy();
		for (int col = 1; col <= columnCount(); col++) {
			m.setValue(row, col, x * m.getValue(row, col));
		}
		return m;
	}

	public Matrix reflect(int row, int col) {
		Matrix m = multiplyColumn(col, -1);
		m = m.multiplyRow(row, -1);
		m.setValue(row, col, -m.getValue(row, col));
		return m;
	}

	public double getSum() {
		double result = 0;
		for (int row = 1; row <= rowCount(); row++) {
			for (int col = 1; col <= columnCount(); col++) {
				result += getValue(row, col);
			}
		}
		return result;
	}

	public Matrix setDiagonal(Vector v) {
		if (rowCount() != v.size() || columnCount() != v.size()) {
			throw new Error("Matrix must be square and of same size as vector!");
		}
		for (int row = 1; row <= rowCount(); row++) {
			setValue(row, row, v.getValue(row));
		}
		return this;
	}

	public static Matrix getIdentity(int size) {
		Matrix m = new Matrix(size, size);
		Vector v = new Vector(size, 1);
		m.setDiagonal(v);
		return m;
	}

	/**
	 * Given a matrix m, to rotate columns col1 and col2 by angle, do this: m =
	 * m.times(Matrix.getRotationMatrix(m.columnCount(),col1,col2,angle));
	 * 
	 * @param size
	 * @param col1
	 * @param col2
	 * @param angle
	 * @return
	 */
	public static Matrix getRotationMatrix(int size, int col1, int col2,
			double angle) {
		Matrix m = getIdentity(size);
		double c = Math.cos(angle);
		double s = Math.sin(angle);
		m.setValue(col1, col1, c);
		m.setValue(col1, col2, s);
		m.setValue(col2, col1, -s);
		m.setValue(col2, col2, c);
		return m;
	}

	public boolean isSquare() {
		return rowCount() == columnCount();
	}

	public Matrix rotate(int col1, int col2, double angle) {
		Matrix m = getRotationMatrix(columnCount(), col1, col2, angle);
		m = this.times(m);
		m.setRowLabels(m.getRowLabels());
		m.setColumnLabels(m.getColumnLabels());
		return m;
	}

	public Matrix rotateDegrees(int col1, int col2, double angleDegrees) {
		return rotate(col1, col2, angleDegrees / 180.0 * Math.PI);
	}

	public List<Integer> getPositiveManifoldReflections() {
		List<Integer> reflections = new ArrayList<Integer>();
		Matrix m = copy();
		for (int row = 1; row <= rowCount(); row++) {
			m.setValue(row, row, 0);
		}
		boolean keepGoing = true;
		while (keepGoing) {
			int col = m.columnOfLeastTotal();
			double leastTotal = m.getColumnVector(m.columnOfLeastTotal())
					.getSum();
			if (leastTotal < 0) {
				reflections.add(col);
				m = m.reflect(col, col);
			} else
				keepGoing = false;
		}
		return reflections;
	}

	public Matrix reflect(List<Integer> reflections) {
		Matrix m = this;
		for (Integer i : reflections)
			m = m.reflect(i, i);
		return m;
	}

	public Matrix getSignificant(double threshold) {
		Matrix m = copy();
		for (int row = 1; row <= m.rowCount(); row++) {
			for (int col = 1; col <= m.columnCount(); col++) {
				double value = m.getValue(row, col);
				if (Math.abs(value) < threshold || value == nullEntry)
					m.setValue(row, col, 0);
			}
		}
		return m;
	}

	public Matrix setRow(int row, double x) {
		for (int col = 1; col <= columnCount(); col++) {
			setValue(row, col, x);
		}
		return this;
	}

	public Matrix setColumn(int col, double x) {
		for (int row = 1; row <= rowCount(); row++) {
			setValue(row, col, x);
		}
		return this;
	}

	public Matrix zeroRowsWithMoreThanNEntries(int n) {
		Matrix m = copy();
		for (int row = 1; row <= m.rowCount(); row++) {
			int count = 0;
			for (int col = 1; col <= m.columnCount(); col++) {
				if (m.getValue(row, col) != 0) {
					count++;
				}
			}
			if (count > n) {
				m.setRow(row, 0);
			}
		}
		return m;
	}

	public Matrix getAbsoluteValue() {
		Matrix m = copy();
		for (int row = 1; row <= m.rowCount(); row++) {
			for (int col = 1; col <= m.columnCount(); col++) {
				m.setValue(row, col, Math.abs(m.getValue(row, col)));
			}
		}
		return m;
	}

	public Point getPositionOfMaxValue() {
		Point p = new Point(1, 1);
		double max = getValue(1, 1);
		for (int i = 1; i <= rowCount(); i++) {
			for (int j = 1; j <= columnCount(); j++) {
				if (getValue(i, j) > max) {
					p = new Point(i, j);
					max = getValue(i, j);
				}
			}
		}
		return p;
	}

	public Point getPositionOfMinValue() {
		Point p = null;
		Double min = null;
		for (int i = 1; i <= rowCount(); i++) {
			for (int j = 1; j <= columnCount(); j++) {
				if (min == null || getValue(i, j) < min) {
					p = new Point(i, j);
					min = getValue(i, j);
				}
			}
		}
		return p;
	}

	public boolean isNaN() {
		for (int i = 1; i <= rowCount(); i++) {
			for (int j = 1; j <= columnCount(); j++) {
				if (Double.isNaN(getValue(i, j)))
					return true;
			}
		}
		return false;
	}

	public enum FactorScoreStrategy {
		ZERO_ROWS_WITH_MORE_THAN_ONE_SIGNIFICANT_FACTOR_LOADING(
				"Exclude Confounding Sorts"), ZERO_ROWS_WITH_MORE_THAN_TWO_SIGNIFICANT_FACTOR_LOADINGS(
				"Ignore participants with more than two significant factor loadings"), USE_ALL_SIGNIFICANT_FACTOR_LOADINGS(
				"Use all participants with significant factor loadings");
		private String name;

		private FactorScoreStrategy(String name) {
			this.name = name;
		}

		@Override
		public String toString() {
			return name;
		}
	}

	public Matrix getFactorScores(Matrix loadings, double threshold,
			List<Double> distribution) {
		return getFactorScores(
				loadings,
				threshold,
				distribution,
				FactorScoreStrategy.ZERO_ROWS_WITH_MORE_THAN_ONE_SIGNIFICANT_FACTOR_LOADING);
	}

	public List<MatrixEntry> getEntries() {
		List<MatrixEntry> list = new ArrayList<MatrixEntry>();
		for (int i = 1; i <= rowCount(); i++) {
			for (int j = 1; j <= columnCount(); j++) {
				list.add(new MatrixEntry(i, j));
			}
		}
		return list;
	}

	public double getValue(MatrixEntry entry) {
		return getValue(entry.getRow(), entry.getColumn());
	}

	public void setValue(MatrixEntry entry, double d) {
		setValue(entry.getRow(), entry.getColumn(), d);
	}

	public Matrix getFactorScores(Matrix loadings, double threshold,
			FactorScoreStrategy strategy) {
		Vector firstRow = getColumnVector(1);
		double[] values = firstRow.getValues();
		Arrays.sort(values);
		List<Double> distribution = new ArrayList<Double>();
		for (Double d : values)
			distribution.add(d);
		return getFactorScores(loadings, threshold, distribution, strategy);
	}

	public Matrix getFactorScoresZ(Matrix loadings, double threshold,
			FactorScoreStrategy strategy) {
		Matrix m = loadings.getSignificant(threshold);
		if (strategy
				.equals(FactorScoreStrategy.ZERO_ROWS_WITH_MORE_THAN_ONE_SIGNIFICANT_FACTOR_LOADING)) {
			m = m.zeroRowsWithMoreThanNEntries(1);
		} else if (strategy
				.equals(FactorScoreStrategy.ZERO_ROWS_WITH_MORE_THAN_TWO_SIGNIFICANT_FACTOR_LOADINGS)) {
			m = m.zeroRowsWithMoreThanNEntries(2);
		}
		// calculate factor weights
		m = m.apply(new Function() {

			@Override
			public double f(int row, int col, double x) {
				return x / (1 - x * x);
			}
		});
		final Matrix f_m = m;
		m = m.apply(new Function() {

			@Override
			public double f(int row, int col, double x) {
				return x / f_m.getColumnVector(col).getMaxAbsoluteValue();
			}
		});
		Matrix baseData = this.copy().setNullsToZero();
		m = baseData.times(m);
		m = m.getStandardizedColumns(0, 1);
		return m;
	}

	public static class FactorScoreCombination {
		private double confidence = 0; // percent
		private FactorScoreStrategy strategy;
		private double threshold;

		public FactorScoreCombination(double confidence, double threshold,
				FactorScoreStrategy strategy) {
			super();
			this.confidence = confidence;
			this.strategy = strategy;
			this.threshold = threshold;
		}

		public double getConfidence() {
			return confidence;
		}

		public void setConfidence(double confidence) {
			this.confidence = confidence;
		}

		public FactorScoreStrategy getStrategy() {
			return strategy;
		}

		public void setStrategy(FactorScoreStrategy strategy) {
			this.strategy = strategy;
		}

		@Override
		public String toString() {
			return "Confidence " + new DecimalFormat("0.0").format(confidence)
					+ "% Threshold "
					+ new DecimalFormat("0.00").format(threshold) + " "
					+ strategy;
		}

		public double getThreshold() {
			return threshold;
		}

		public void setThreshold(double threshold) {
			this.threshold = threshold;
		}

	}

	public FactorScoreCombination getFactorScoresBest(Matrix loadings,
			int numValidFactors) {
		try {
			TDistribution tDistribution = new TDistributionImpl(
					loadings.rowCount());

			double bestConfidence = 0;
			double bestThreshold = 0;
			FactorScoreStrategy bestStrategy = null;

			outer: for (double confidenceMax = 99; confidenceMax >= 50; confidenceMax -= 5) {
				for (double confidence = confidenceMax; confidence >= confidenceMax - 5; confidence--) {
					double threshold = tDistribution
							.inverseCumulativeProbability(confidence / 100)
							/ Math.sqrt(loadings.rowCount());
					for (FactorScoreStrategy strategy : FactorScoreStrategy
							.values()) {
						Matrix scores = getFactorScoresZ(loadings, threshold,
								strategy);
						boolean isNaN = false;
						for (int i = 1; i <= Math.min(numValidFactors,
								loadings.columnCount()); i++) {
							if (scores.getColumnVector(i).isNaN())
								isNaN = true;
						}
						if (!isNaN) {
							bestConfidence = confidence;
							bestStrategy = strategy;
							bestThreshold = threshold;
							break outer;
						}
					}
				}
			}
			return new FactorScoreCombination(bestConfidence, bestThreshold,
					bestStrategy);
		} catch (Exception e) {
			throw new Error(e);
		}
	}

	private Matrix getFactorScores(Matrix loadings, double threshold,
			List<Double> distribution, FactorScoreStrategy strategy) {

		Matrix m = getFactorScoresZ(loadings, threshold, strategy);
		m = m.getDistributedColumns(distribution);
		return m;
	}

	public interface MatrixFunction {
		public double function(Matrix m);
	}

	public Matrix getGradient(MatrixFunction f, double epsilon) {
		Matrix t = copy();
		Matrix result = new Matrix(rowCount(), columnCount());
		for (MatrixEntry entry : getEntries()) {
			double v = getValue(entry);
			t.setValue(entry, v - epsilon);
			double f1 = f.function(t);
			t.setValue(entry, v + epsilon);
			double f2 = f.function(t);
			double derivative = (f2 - f1) / 2 / epsilon;
			result.setValue(entry, derivative);
			t.setValue(entry, v);
		}
		return result;
	}

	public Matrix getGradientProjectionOrthogonal(MatrixFunction mf) {

		double a1 = 1;
		Matrix t = getIdentity(columnCount());
		for (int iter = 0; iter <= 100; iter++) {
			double f = -mf.function(t);
			Matrix g = t.getGradient(mf, 0.0001).times(-1);
			Matrix m = t.transpose().times(g);
			Matrix s = m.add(m.transpose()).times(0.5);
			Matrix gp = g.minus(t.times(s));
			double norm = gp.getFrobeniusNorm();
			if (norm < 0.00001)
				break;
			a1 = 2 * a1;
			Matrix tt = null;
			for (int i = 0; i <= 10; i++) {
				Matrix x = t.minus(gp.times(a1));
				SingularValueDecomposition svd = new Jama.Matrix(x.getArray())
						.svd();
				Matrix svdU = new Matrix(svd.getU());
				Matrix svdV = new Matrix(svd.getV());
				tt = svdU.times(svdV.transpose());
				double ft = -mf.function(tt);
				if (ft < f - 0.5 * norm * norm * a1)
					break;
				a1 = a1 / 2;
			}
			t = tt;
		}
		return t;

	}

	private double getFrobeniusNorm() {
		double d = 0;
		for (MatrixEntry entry : getEntries()) {
			d += Math.pow(getValue(entry), 2);
		}
		d = Math.sqrt(d);
		return d;
	}

	public Matrix restrictRows(Matrix m) {
		if (m == null)
			return null;
		Set<String> set = new HashSet<String>();
		for (String s : m.getRowLabels())
			set.add(s);
		Matrix result = copy();
		for (int i = rowCount(); i >= 1; i--) {
			if (!set.contains(getRowLabel(i))) {
				if (result.rowCount() == 1)
					result = null;
				else
					result = result.removeRow(i);
			}
		}
		return result;
	}

	private Matrix setNullsToZero() {
		for (int i = 1; i <= rowCount(); i++) {
			for (int j = 1; j <= columnCount(); j++) {
				if (getValue(i, j) == nullEntry)
					setValue(i, j, 0);
			}
		}
		return this;
	}

	public Matrix subMatrixColumns(int start, int finish) {
		return subMatrix(1, rowCount(), start, finish);
	}

	public Matrix subMatrixRows(int start, int finish) {
		return subMatrix(start, finish, 1, columnCount());
	}

	public Matrix getColumnCorrelations(Matrix m) {
		Matrix result = new Matrix(columnCount(), m.columnCount());
		if (m.rowCount() != rowCount())
			throw new Error("matrices must have same number of rows");
		result.setRowLabels(getColumnLabels());
		result.setColumnLabels(m.getColumnLabels());
		for (int i = 1; i <= columnCount(); i++) {
			for (int j = 1; j <= m.columnCount(); j++) {
				result.setValue(
						i,
						j,
						getColumnVector(i).getPearsonCorrelation(
								m.getColumnVector(j)));
			}
		}
		return result;
	}

	public class CentroidResults {
		public Matrix correlationMatrix;
		public Matrix loadings;
		public Matrix eigenvalues;
		public Matrix eigenvectors;

		@Override
		public String toString() {
			StringOutputStream sos = new StringOutputStream();
			PrintWriter out = new PrintWriter(sos);
			out.println("eigenvalues:\n" + eigenvalues);
			out.println("eigenvectors:\n" + eigenvectors);
			out.println("loadings:\n" + loadings);
			out.close();
			return sos.toString();
		}
	}

	private CentroidResults analyzeCorrelationMatrixFactorsCentroidMethod() {
		CentroidResults result = new CentroidResults();
		result.correlationMatrix = this;
		Matrix correlationMatrix = this;
		Matrix allLoadings = new Matrix(rowCount(), rowCount());
		allLoadings.setRowLabels(correlationMatrix.getRowLabels());
		final Vector eigenvalues = new Vector(rowCount());
		for (int count = 1; count <= rowCount(); count++) {
			List<Integer> reflections = correlationMatrix
					.getPositiveManifoldReflections();
			Matrix positiveManifold = correlationMatrix.reflect(reflections);
			Matrix m = positiveManifold.copy();
			// zero the diagonal
			m.setDiagonal(new Vector(m.rowCount()));
			// find the loadings (do 10 iterations)
			int numIterations = 10;
			Vector loadings = null;
			for (int i = 1; i <= numIterations; i++) {
				double total = m.getSum();
				loadings = m.transpose().getSumColumnVectors()
						.times(1 / Math.sqrt(total));
				m.setDiagonal(loadings.getSquare());
			}
			eigenvalues.setValue(count, loadings.getSquare().getSum());
			// calculate the residual
			Matrix residual = positiveManifold.minus(loadings.times(loadings
					.transpose()));
			// zero the diagonal
			residual.setDiagonal(new Vector(residual.rowCount()));
			// remove the reflections performed to get positive manifold
			for (Integer col : reflections) {
				residual = residual.reflect(col, col);
				loadings.setValue(col, -loadings.getValue(col));
			}
			loadings.setColumnLabel(1, "F" + count);
			allLoadings.setColumnVector(count, loadings);
			correlationMatrix = residual;
		}

		result.loadings = allLoadings;

		result.eigenvectors = result.loadings.apply(new Function() {

			@Override
			public double f(int row, int col, double x) {
				return x / Math.sqrt(eigenvalues.getValue(col));
			}
		});

		// now convert eigenvalues from a vector to a matrix
		result.eigenvalues = Matrix.getIdentity(eigenvalues.size())
				.setDiagonal(eigenvalues);
		result.eigenvalues.setRowLabelPattern("F<index>");
		result.eigenvalues.setColumnLabelPattern("F<index>");

		return result;
	}

	public Matrix getNormalizedColumns() {
		Matrix m = copy();
		for (int i = 1; i <= columnCount(); i++) {
			m.setColumnVector(i, m.getColumnVector(i).standardize());
		}
		return m;
	}

	public Matrix getStandardizedColumns(double mean, double sd) {
		Matrix m = copy();
		for (int i = 1; i <= columnCount(); i++) {
			m.setColumnVector(i, m.getColumnVector(i).standardize(mean, sd));
		}
		return m;
	}

	public Matrix getDistributedColumns(List<Double> distribution) {
		Matrix m = copy();
		String[] labels = this.getColumnLabels();
		for (int i = 1; i <= columnCount(); i++) {
			m.setColumnVector(i,
					m.getColumnVector(i).getDistributed(distribution));
		}
		m.setColumnLabels(labels);
		return m;
	}

	public Map<Point, Double> getMatchedCorrelations(Matrix ref) {
		Matrix c = getColumnCorrelations(ref);
		// map from ref factor to main factor and correlation value
		Map<Point, Double> map = new LinkedHashMap<Point, Double>();
		for (int i = 1; i <= c.columnCount(); i++) {
			Point p = c.getAbsoluteValue().getPositionOfMaxValue();
			if (c.getValue(p.x, p.y) != 0) {
				map.put(p, c.getValue(p.x, p.y));
				c = c.setRow(p.x, 0).setColumn(p.y, 0);
			}
		}
		return map;
	}

	@Override
	public String toHtml() {
		StringBuffer s = new StringBuffer();
		s.append("<html>");
		s.append("<table class=\"matrix\">");
		int[] maxSize = getColumnWidths();
		int maxRowHeaderWidth = getRowHeaderWidth();
		s.append("<tr class=\"matrix\">");
		s.append("<td class=\"matrix\">");
		s.append(pad("", maxRowHeaderWidth, true));
		s.append("</td>");
		if (columnLabels != null) {
			for (int j = 1; j <= columnCount(); j++) {
				s.append("<td class=\"matrix\">");
				s.append(pad(columnLabels[j - 1], maxSize[j - 1], true));
				s.append("</td>");
			}
			s.append("\n");
		}
		s.append("</tr>");
		for (int i = 1; i <= rowCount(); i++) {
			s.append("<tr class=\"matrix\">");
			StringBuffer row = new StringBuffer();
			for (int j = 1; j <= columnCount(); j++) {
				if (row.length() > 0)
					row.append("");
				if (j == 1 && rowLabels != null) {
					row.append("<td class=\"matrix\">");
					row.append(pad(rowLabels[i - 1], maxRowHeaderWidth, true));
					row.append("</td>");
				}
				DecimalFormat df = new DecimalFormat(this.decimalFormat);
				row.append("<td class=\"matrix\">");
				row.append(pad(df.format(getValue(i, j)), maxSize[j - 1], true));
				row.append("</td>");
			}
			if (s.length() > 0)
				s.append("\n");
			s.append(row);
			s.append("</tr>");
		}
		s.append("\n");
		s.append("</table>");
		s.append("</html>");
		return s.toString();
	}

	public enum NullStrategy {
		SET_TO_ZERO, THROW_ERROR, SET_TO_COLUMN_MEAN, SET_TO_NULL_ENTRY_CODE;
	}

	public Matrix(InputStream is, boolean hasRowLabels,
			boolean hasColumnLabels, NullStrategy nullStrategy)
			throws IOException {

		InputStreamReader isr = new InputStreamReader(is);
		BufferedReader br = new BufferedReader(isr);
		Matrix m = null;
		String line;
		if (hasColumnLabels) {
			int start = 0;
			if (hasRowLabels)
				start = 1;
			line = br.readLine();
			String[] item = line.split("\t");
			m = new Matrix(1, item.length - start);
			for (int i = start; i < item.length; i++) {
				m.setColumnLabel(i + 1 - start, item[i]);
			}
		}
		int count = 1;
		while ((line = br.readLine()) != null) {
			String[] item = line.split("\t");
			if (m == null) {
				if (hasRowLabels) {
					m = new Matrix(1, item.length - 1);
				} else
					m = new Matrix(1, item.length);
			}
			if (count > m.rowCount())
				m = m.addRow();
			if (hasRowLabels)
				m.setRowLabel(m.rowCount(), item[0]);
			int start = 0;
			if (hasRowLabels)
				start = 1;
			for (int i = start; i < item.length; i++) {
				String valueString = item[i];
				int col = i + 1 - start;
				boolean isNull = isNull(valueString);
				if (isNull) {
					m.setValue(m.rowCount(), col, nullEntry);
				} else {
					m.setValue(m.rowCount(), col, Double.parseDouble((item[i])));
				}
			}
			count++;
		}

		for (int col = 1; col <= m.columnCount(); col++) {
			double sum = 0;
			int n = 0;
			for (int row = 1; row <= m.rowCount(); row++) {
				if (m.getValue(row, col) != nullEntry) {
					sum += m.getValue(row, col);
					n++;
				}
			}
			double mean = sum / n;

			for (int row = 1; row <= m.rowCount(); row++) {
				if (m.getValue(row, col) == nullEntry) {
					if (nullStrategy.equals(NullStrategy.SET_TO_COLUMN_MEAN)) {
						m.setValue(row, col, mean);
					} else if (nullStrategy.equals(NullStrategy.SET_TO_ZERO)) {
						m.setValue(row, col, 0);
					} else if (nullStrategy.equals(NullStrategy.THROW_ERROR)) {
						throw new Error("null entry at " + row + "," + col);
					} else if (nullStrategy
							.equals(NullStrategy.SET_TO_NULL_ENTRY_CODE)) {
						// do nothing
					} else
						throw new Error("null entry at " + row + "," + col);
				}
			}
		}
		this.m = m.m;
		this.rowLabels = m.rowLabels;
		this.columnLabels = m.columnLabels;
	}

	public Matrix(Matrix matrix) {
		this(matrix.getArray());
		setRowLabels(matrix.getRowLabels());
		setColumnLabels(matrix.getColumnLabels());
	}

	private boolean isNull(String s) {
		if (s == null)
			return true;
		if (s.trim().length() == 0)
			return true;
		if (s.trim().equalsIgnoreCase("null"))
			return true;
		if (s.trim().equalsIgnoreCase("#NULL!"))
			return true;
		return false;
	}

	/**
	 * transposes around the other axis
	 * 
	 * @return
	 */
	public Matrix transposeOther() {
		return reverseColumns().reverseRows();
	}

	public Matrix removeColumnsWithNoStandardDeviation() {
		Matrix m = copy();
		for (int i = columnCount(); i >= 1; i--) {
			if (getColumnVector(i).getStandardDeviation() == 0) {
				m = m.removeColumn(i);
			}
		}
		return m;
	}

	public Matrix addColumn() {
		return transpose().addRow().transpose();
	}

	public Matrix insertColumn() {
		return reverseColumns().addColumn().reverseColumns();
	}

	public Matrix zeroEntriesLessThan(double d) {
		Matrix m = copy();
		for (int i = 1; i <= rowCount(); i++) {
			for (int j = 1; j <= columnCount(); j++) {
				if (m.getValue(i, j) < d)
					m.setValue(i, j, 0);
			}
		}
		return m;
	}

	public int getColumnOfBestCorrelation(Vector v) {
		double d = 0;
		int col = 1;
		for (int i = 1; i <= columnCount(); i++) {
			double corr = Math.abs(getColumnVector(i).getPearsonCorrelation(v));
			if (corr > d) {
				d = corr;
				col = i;
			}
		}
		return col;
	}

	public List<MatrixRotation> rotateTo2(final Vector v, final int startCol,
			int maxCols, final double startDegrees, final double finishDegrees,
			final int numSectors,
			final List<Integer> excludeTheseColumnsFromRotation) {
		// brute force method not tested succesfully yet
		final int[] counter = new int[1];
		final List<MatrixRotation> rotations = new ArrayList<MatrixRotation>();
		final double[] bestCorrelation = new double[1];
		final int endCol = Math.min(startCol + maxCols, columnCount());
		System.out.println("processing possibilities, cols " + startCol + "-"
				+ endCol);
		Possibility.doPossibility(endCol - startCol, numSectors,
				new Processor() {

					@Override
					public void processValues(int[] values) {
						counter[0]++;
						if (counter[0] % 10000 == 0)
							System.out.println(counter[0]
									+ " possibilities tested");
						double change = (finishDegrees - startDegrees)
								/ numSectors;
						Matrix m = copy();
						for (int i = 0; i < values.length; i++) {
							if (excludeTheseColumnsFromRotation == null
									|| !excludeTheseColumnsFromRotation
											.contains(startCol + i + 1)) {
								m = m.rotateDegrees(startCol, startCol + i + 1,
										startDegrees + (values[i] - 1) * change);
							}
						}
						double corr = Math.abs(m.getColumnVector(startCol)
								.getPearsonCorrelation(v));
						if (corr > bestCorrelation[0]) {
							bestCorrelation[0] = corr;
							rotations.clear();
							for (int i = 0; i < values.length; i++) {
								if (excludeTheseColumnsFromRotation == null
										|| !excludeTheseColumnsFromRotation
												.contains(startCol + i + 1)) {
									rotations
											.add(new MatrixRotation(
													startCol,
													startCol + i + 1,
													Math.PI
															/ 180
															* (startDegrees + (values[i] - 1)
																	* change)));
								}
							}
						}
					}
				});
		return rotations;
	}

	public Matrix rotate(MatrixRotation rotation) {
		return rotate(rotation.getColumn1(), rotation.getColumn2(),
				rotation.getAngle());
	}

	public Matrix rotate(List<MatrixRotation> rotations) {
		Matrix m = this;
		for (MatrixRotation rotation : rotations) {
			m = m.rotate(rotation);
		}
		return m;
	}

	public Matrix rotate(List<MatrixRotation> rotations, boolean kaiserNormalize) {
		Matrix m = this;
		if (kaiserNormalize) {
			m = getKaiserNormalization(true).times(m);
		}
		m = m.rotate(rotations);
		if (kaiserNormalize) {
			m = getKaiserNormalization(false).times(m);
		}
		return m;
	}

	public static interface MatrixComparison {
		public double compare(Matrix m, Matrix reference);
	}

	public List<MatrixRotation> getRotationsTo(Matrix reference) {
		MatrixComparison comparison = new MatrixComparison() {

			@Override
			public double compare(Matrix m, Matrix reference) {
				Map<Point, Double> map = m.getMatchedCorrelations(reference);
				double sum = 0;
				for (double d : map.values())
					sum += d * d;
				return sum;
			}
		};
		return getRotationsTo(reference, comparison, 0.005);
	}

	public List<MatrixRotation> getRotationsTo(Matrix reference,
			MatrixComparison comparison, double epsilon) {

		if (rowCount() != reference.rowCount())
			throw new Error("matrices must have same row count");

		boolean keepGoing = true;
		double startCorrelation = -0;
		Matrix m = copy();

		List<MatrixRotation> rotations = new ArrayList<MatrixRotation>();
		// keep going while the correlation improves by at least epsilon each
		// time
		while (keepGoing) {
			double bestCorrelation = comparison.compare(m, reference);
			for (int i = 1; i <= m.columnCount() - 1; i++) {
				for (int j = i + 1; j <= m.columnCount(); j++) {
					for (int k = 1; k <= reference.columnCount(); k++) {
						Vector v1 = m.getColumnVector(i);
						Vector v2 = m.getColumnVector(j);
						Vector v = reference.getColumnVector(k);
						double angle = v1.getBestCorrelatedRotation(v2, v);
						MatrixRotation rotation = new MatrixRotation(i, j,
								angle);
						double test = comparison.compare(m.rotate(rotation),
								reference);
						if (test > bestCorrelation) {
							bestCorrelation = test;
							m = m.rotate(rotation);
							rotations.add(rotation);
						}
					}
				}
			}
			keepGoing = (bestCorrelation - startCorrelation) > epsilon;
			startCorrelation = bestCorrelation;
		}
		MatrixRotation.simplify(rotations);
		return rotations;
	}

	public double getMaximum() {
		double d = Double.NaN;
		for (int i = 1; i <= rowCount(); i++) {
			for (int j = 1; j <= columnCount(); j++) {
				if (Double.isNaN(d))
					d = getValue(i, j);
				d = Math.max(d, getValue(i, j));
			}
		}
		return d;
	}

	public Object getUserObject() {
		return userObject;
	}

	public void setUserObject(Object userObject) {
		this.userObject = userObject;
	}

	@Override
	public Matrix getMatrix() {
		return this;
	}

	public Matrix restrictRows(boolean[] useRow) {
		if (useRow == null)
			return this;
		Matrix m = copy();
		for (int i = useRow.length; i >= 1; i--) {
			if (!useRow[i - 1])
				m = m.removeRow(i);
		}
		return m;
	}

	public Matrix removeRowsByLabel(StringFilter filter) {
		Matrix m = this;
		if (filter != null)
			for (int k = m.rowCount(); k >= 1; k--)
				if (!filter.accept(m.getRowLabel(k)))
					m = m.removeRow(k);
		return m;
	}

	public double[] getRow(Integer rowNum) {
		double[] row = new double[columnCount()];
		for (int col = 1; col <= columnCount(); col++) {
			row[col - 1] = this.getValue(rowNum, col);
		}
		return row;
	}

	public Matrix getColumnsReorderedByEigenvalue(boolean ascending) {
		Vector v = transpose().times(this).getDiagonal();
		return v.getRowSwitchingMatrixToOrderByAbsoluteValue(false)
				.times(transpose()).transpose();
	}

	public Matrix getSignNormalizationElementaryMatrixSoMaxAbsoluteValueByColumnIsPositive() {
		Matrix m = Matrix.getIdentity(columnCount());
		String[] labels = this.getColumnLabels();
		m.setColumnLabels(Arrays.copyOf(labels, labels.length));
		for (int col = 1; col <= columnCount(); col++) {
			Vector cv = getColumnVector(col);
			int maxAbsoluteValueIndex = cv.getOrderedIndicesByAbsoluteValue(
					false).get(0);
			if (cv.getValue(maxAbsoluteValueIndex) < 0)
				m = m.multiplyColumn(col, -1);
		}
		return m;
	}

	public Matrix normalizeSignOfColumnsSoMaxAbsoluteValueIsPositive() {
		return this
				.times(getSignNormalizationElementaryMatrixSoMaxAbsoluteValueByColumnIsPositive());
	}

	public boolean columnEquals(int col, double precision, double... values) {
		if (values.length != rowCount())
			throw new RuntimeException(
					"number of values does not match number of rows");
		for (int row = 1; row <= rowCount(); row++) {
			if (Math.abs(getValue(row, col) - values[row - 1]) > precision)
				return false;
		}
		return true;
	}
}
