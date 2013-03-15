package moten.david.util.math;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Test;

public class MatrixTest {

	private static final double PRECISION = 0.0001;

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
