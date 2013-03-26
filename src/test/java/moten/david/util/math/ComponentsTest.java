package moten.david.util.math;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class ComponentsTest {

	private static final double PRECISION = 0.0001;

	public Components createComponents() {
		Matrix eVectors = new Matrix(new double[][] { { 1, 1 }, { 1, -1 } });
		Matrix eValues = new Matrix(new double[][] { { 9, 0 }, { 0, 16 } });
		return new Components(eVectors, eValues);
	}

	@Test
	public void testGetEigenvalues() {
		Components c = createComponents();
		assertTrue(c.getEigenvalues().columnEquals(1, PRECISION, 9, 0));
		assertTrue(c.getEigenvalues().columnEquals(2, PRECISION, 0, 16));
	}

	@Test
	public void testGetEigenvectors() {
		Components c = createComponents();
		assertTrue(c.getEigenvectors().columnEquals(1, PRECISION, 1, 1));
		assertTrue(c.getEigenvectors().columnEquals(2, PRECISION, 1, -1));
	}

	@Test
	public void testCalculateLoadings() {
		Components c = createComponents();
		assertTrue(c.getLoadings().columnEquals(1, PRECISION, 3, 3));
		assertTrue(c.getLoadings().columnEquals(2, PRECISION, 4, -4));
	}

	@Test
	public void testMakeEigenvaluesDescendDoesNotChangeIfAlreadyDescending() {
		// TODO
	}

	@Test
	public void testMakeEigenvaluesDescendDoesNotChangeIfAscending() {
		Components c = createComponents().makeEigenvaluesDescendInValue();
		assertTrue(c.getEigenvalues().columnEquals(1, PRECISION, 16, 0));
		assertTrue(c.getEigenvalues().columnEquals(2, PRECISION, 0, 9));
		assertTrue(c.getEigenvectors().columnEquals(1, PRECISION, 1, -1));
		assertTrue(c.getEigenvectors().columnEquals(2, PRECISION, 1, 1));
	}

	@Test
	public void testNormalizeLoadingSigns() {
		// TODO
	}

	@Test
	public void testRemoveEntriesMoreThanMaxFactors() {
		// TODO
	}

	@Test
	public void testRemoveEntriesLessThanMinEigenvalue() {
		// TODO
	}
}
