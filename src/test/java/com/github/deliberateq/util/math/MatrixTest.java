package com.github.deliberateq.util.math;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import com.github.deliberateq.util.math.Matrix;
import com.github.deliberateq.util.math.MatrixRotation;
import com.github.deliberateq.util.math.StringFilter;
import com.github.deliberateq.util.math.Vector;
import com.github.deliberateq.util.math.Matrix.MatrixFunction;
import com.github.deliberateq.util.math.Matrix.NullStrategy;
import com.github.deliberateq.util.math.Varimax.RotationMethod;
import com.github.deliberateq.util.math.gp.QuartimaxCriterion;

public class MatrixTest {

	private static final double PRECISION = 0.0001;

	@org.junit.Test
	public void testRotateTo() {
		Matrix m = new Matrix(new double[][] { { 1, 2, 3 }, { 2, 7, 4 },
				{ 6, 1, 8 } });
		Matrix reference = m.rotateDegrees(1, 2, 75);
		List<MatrixRotation> rotations = m.getRotationsTo(reference, CorrelationCoefficient.PEARSONS);
		Assert.assertTrue(rotations.size() > 0);
		assertMagnitudeLessThan(
				Math.abs(rotations.get(0).getAngleDegrees() - 75.0), 0.01);

	}

	private void assertMagnitudeLessThan(double d, double limit) {
		if (Math.abs(d) > limit)
			throw new AssertionError("not less than");
	}

	@org.junit.Test
	public void testVectorMeanSumTimesMinusAndMaxAbsoluteValue() {
		Vector v = new Vector(new double[] { 5, 6, 8, 9 });
		Assert.assertTrue(v.getMean() == 7);
		assertMagnitudeLessThan(v.getStandardDeviation() - 1.5811, 0.001);
		Assert.assertTrue(v.getSum() == 28);

		v = v.times(2.0);
		Vector v2 = new Vector(new double[] { 10, 12, 16, 19 });
		assertMagnitudeLessThan(v.minus(v2).getMaxAbsoluteValue(), 0.0001);
	}

	@org.junit.Test
	public void testRestrictRows() {
		Matrix m = new Matrix(new double[][] { { 1, 2 }, { 2, 3 }, { 3, 4 } });
		m.setRowLabels(new String[] { "hello", "what", "hey" });
		Matrix m2 = m.copy();
		m2.setRowLabel(2, "what-not");
		Matrix m3 = m.restrictRows(m2);
		Assert.assertTrue(m3.rowCount() == 2);
		Assert.assertTrue(m3.getRowLabel(1).equals("hello"));
		Assert.assertTrue(m3.getRowLabel(2).equals("hey"));
	}

	@org.junit.Test
	public void testRemoveRowsByLabel() {
		Matrix m = new Matrix(new double[][] { { 1, 2 }, { 2, 3 }, { 3, 4 } });
		m.setRowLabels(new String[] { "hello", "what", "hey" });
		Matrix m2 = m.removeRowsByLabel(new StringFilter() {

			@Override
			public boolean accept(String s) {
				return s.equals("hello");
			}
		});
		Assert.assertEquals(3, m.rowCount());
		Assert.assertEquals(1, m2.rowCount());
	}

	@org.junit.Test
	public void testGradientProjectionOrthogonal() throws IOException {
		InputStream is = getClass().getResourceAsStream(
				"/math/test-thurstones-box.txt");
		final Matrix matrix = new Matrix(is, false, false,
				NullStrategy.SET_TO_ZERO);
		MatrixFunction mf = new QuartimaxCriterion(matrix);
		Matrix r = matrix.getGradientProjectionOrthogonal(mf);
		Matrix m = matrix.times(r);
		assertMagnitudeLessThan(0.0105 - m.getValue(1, 1), 0.0001);
		assertMagnitudeLessThan(-0.9934 - m.getValue(1, 2), 0.0001);
		assertMagnitudeLessThan(-0.0899 - m.getValue(1, 3), 0.0001);
		assertMagnitudeLessThan(0.9744 - m.getValue(20, 1), 0.0001);
		assertMagnitudeLessThan(-0.0926 - m.getValue(20, 2), 0.0001);
		assertMagnitudeLessThan(-0.0908 - m.getValue(20, 3), 0.0001);
	}

	@org.junit.Test
	public void testMatrixEquals() {
		Matrix m = new Matrix(new double[][] { { 1, 2 }, { 2, 3 }, { 3, 4 } });
		Assert.assertTrue(m.equals((Object) m));
		Assert.assertTrue(m.equals(m));
		Assert.assertFalse(m.equals(m.add(m)));
		Assert.assertFalse(m.equals(m.removeRow(3)));
		Assert.assertFalse(m.equals(null));
		Matrix m2 = m.copy();
		Assert.assertTrue(m.equals(m2));
		m2.setValue(3, 2, 5);
		Assert.assertFalse(m.equals(m2));
	}

	@org.junit.Test
	public void testVarimax() {
		// test taken from http://htsnp.stanford.edu/PCA/pcaVSvarimax.pdf
		Matrix m = new Matrix(new double[][] { { 0.0978322, 0.5690011 },
				{ 0.647965, -0.40432 }, { 0.1198195, 0.6968811 },
				{ 0.745972, 0.164681 } });
		boolean kaiserNormalized = true;
		Matrix after = m.getKaiserNormalization(true).times(m);
		List<MatrixRotation> rotations = m.getRotations(RotationMethod.VARIMAX,
				kaiserNormalized);
		Matrix result = m.rotate(rotations, kaiserNormalized);
		Matrix desiredResult = new Matrix(new double[][] {
				{ 0.00012428, 0.5773503 }, { 0.70744623, -0.288827 },
				{ 0.00015222, 0.7071068 }, { 0.70716891, 0.2885229 } });
		double highestDifference = result.minus(desiredResult)
				.getAbsoluteValue().getMaximum();
		System.out.println(highestDifference);
		Assert.assertTrue(highestDifference < 0.001);
	}

	@Test
	public void testMatrixSetRow() {
		Matrix m = new Matrix(2, 2);
		m.setValue(1, 1, 1);
		m.setValue(1, 2, 2);
		m.setValue(2, 1, 3);
		m.setValue(2, 2, 4);
		m.setRow(1, new double[] { 5, 6 });
		assertEquals(5.0, m.getValue(1, 1), PRECISION);
		assertEquals(6.0, m.getValue(1, 2), PRECISION);
		assertEquals(3.0, m.getValue(2, 1), PRECISION);
		assertEquals(4.0, m.getValue(2, 2), PRECISION);
	}

	@Test
	public void testCanGetOrdering() {
		Vector v = new Vector(new double[] { 3, 1, 2 });
		List<Integer> indices = v.getOrderedIndicesByAbsoluteValue(true);
		assertEquals(3, indices.size());
		assertEquals(2, indices.get(0).intValue());
		assertEquals(3, indices.get(1).intValue());
		assertEquals(1, indices.get(2).intValue());
	}

	@Test
	public void testCanGenerateOrderingMatrixFromVector() {
		Vector v = new Vector(new double[] { 3, 1, 2 });
		Matrix m = v.getRowSwitchingMatrixToOrderByAbsoluteValue(true);
		assertEquals(0, m.getValue(1, 1), PRECISION);
		assertEquals(1, m.getValue(1, 2), PRECISION);
		assertEquals(0, m.getValue(1, 3), PRECISION);
		assertEquals(0, m.getValue(2, 1), PRECISION);
		assertEquals(0, m.getValue(2, 2), PRECISION);
		assertEquals(1, m.getValue(2, 3), PRECISION);
		assertEquals(1, m.getValue(3, 1), PRECISION);
		assertEquals(0, m.getValue(3, 2), PRECISION);
		assertEquals(0, m.getValue(3, 3), PRECISION);
	}

	@Test
	public void testGetSignNormalizationElementaryMatrixSoMaxAbsoluteValueByColumnIsPositive() {
		Matrix m = new Matrix(2, 2);
		m.setValue(1, 1, 1);
		m.setValue(1, 2, 2);
		m.setValue(2, 1, 3);
		m.setValue(2, 2, -4);
		m = m.getSignNormalizationElementaryMatrixSoMaxAbsoluteValueByColumnIsPositive();
		assertEquals(1, m.getValue(1, 1), PRECISION);
		assertEquals(0, m.getValue(1, 2), PRECISION);
		assertEquals(0, m.getValue(2, 1), PRECISION);
		assertEquals(-1, m.getValue(2, 2), PRECISION);
	}

	@Test
	public void testColumnNormalization() {
		Matrix m = new Matrix(2, 2);
		m.setValue(1, 1, 1);
		m.setValue(1, 2, 2);
		m.setValue(2, 1, 3);
		m.setValue(2, 2, -4);
		m = m.normalizeSignOfColumnsSoMaxAbsoluteValueIsPositive();
		assertEquals(1, m.getValue(1, 1), PRECISION);
		assertEquals(-2, m.getValue(1, 2), PRECISION);
		assertEquals(3.0, m.getValue(2, 1), PRECISION);
		assertEquals(4.0, m.getValue(2, 2), PRECISION);
	}
	
}