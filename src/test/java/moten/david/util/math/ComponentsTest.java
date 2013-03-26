package moten.david.util.math;

import org.junit.Test;

public class ComponentsTest {

	public Components createComponents() {
		Matrix eVectors = new Matrix(new double[][] { { 1, 1 }, { 1, -1 } });
		Vector d = new Vector(3, 4);
		Matrix eValues = new Matrix(2, 2).setDiagonal(d);
		return new Components(eVectors, eValues);
	}

	@Test
	public void testGetEigenvectors() {
		// TODO
	}

	@Test
	public void testGetEigenvalues() {
		// TODO
	}

	@Test
	public void testGetLoadings() {
		// TODO
	}

	@Test
	public void testMakeEigenvaluesDescendDoesNotChangeIfAlreadyDescending() {
		// TODO
	}

	@Test
	public void testMakeEigenvaluesDescendDoesNotChangeIfAscending() {
		// TODO
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
