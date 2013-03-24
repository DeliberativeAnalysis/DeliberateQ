package moten.david.util.math;

import java.awt.Point;
import java.util.Collections;
import java.util.List;

import moten.david.util.math.EigenvalueThreshold.PrincipalFactorCriterion;

import com.google.inject.internal.Lists;

public class Components {

	private final Matrix eigenvectors;
	private final Matrix eigenvalues;

	public Components(Matrix eigenvectors, Matrix eigenvalues) {
		this.eigenvectors = eigenvectors;
		this.eigenvalues = eigenvalues;
	}

	public Matrix getEigenvalues() {
		return eigenvalues;
	}

	public Vector getEigenvaluesVector() {
		return eigenvalues.getDiagonal();
	}

	public Matrix getEigenvectors() {
		return eigenvectors;
	}

	public Matrix getLoadings() {
		return eigenvectors.times(eigenvalues.apply(Matrix.SQUARE_ROOT));
	}

	public Components getPrincipalComponents(
			EigenvalueThreshold eigenvalueThreshold) {

		// apply min eigenvalue criterion if set
		Components c = removeEntriesLessThanMinEigenvalue(eigenvalueThreshold);

		// apply max factors criterion if set
		c = removeEntriesAccordingToMaxFactors(c, eigenvalueThreshold);

		// get positive manifold
		c = c.normalizeLoadingSigns();

		// return result
		return c;
	}

	public Components removeEntriesAccordingToMaxFactors(Components c,
			EigenvalueThreshold eigenvalueThreshold) {

		Matrix eValues = c.getEigenvalues();
		Matrix eVectors = c.getEigenvectors();
		// now apply the max factors criterion if set
		if (eigenvalueThreshold.getPrincipalFactorCriterion().equals(
				PrincipalFactorCriterion.MAX_FACTORS)
				&& c.getEigenvalues().rowCount() > eigenvalueThreshold
						.getMaxFactors()) {
			Vector eigenvaluesVector = c.getEigenvaluesVector();
			// for each extraneous row
			int extraRows = eigenvaluesVector.rowCount()
					- eigenvalueThreshold.getMaxFactors();
			List<Integer> removeThese = Lists.newArrayList();
			for (int j = 1; j <= extraRows; j++) {
				// remove the row and col from loadings,
				// principalEigenvalues and principalEigenvectors if
				// it contains the smallest eigenvalue
				Point pos = eigenvaluesVector.getPositionOfMinValue();
				removeThese.add(pos.x);
			}
			Collections.sort(removeThese);
			for (int j = removeThese.size() - 1; j >= 0; j--) {
				Integer row = removeThese.get(j);
				eValues = eValues.removeRow(row).removeColumn(row);
				eVectors = eVectors.removeColumn(row);
			}
		}
		return new Components(eVectors, eValues);
	}

	public Components makeEigenvaluesDescendInValue(
			EigenvalueThreshold eigenvalueThreshold) {
		Matrix rowSwitcher = getEigenvaluesVector()
				.getRowSwitchingMatrixToOrderByAbsoluteValue(false);
		// switch rows on eigenvalue vector
		Matrix eigenvalues = Matrix.createDiagonalMatrix(rowSwitcher
				.times(getEigenvaluesVector()));

		// switch columns on eigenvectors matrix
		Matrix eigenvectors = getEigenvectors().times(rowSwitcher.transpose());
		return new Components(eigenvectors, eigenvalues);
	}

	// TODO rename to makePositiveManifold?
	public Components normalizeLoadingSigns() {
		Matrix sn = getLoadings()
				.getSignNormalizationElementaryMatrixSoMaxAbsoluteValueByColumnIsPositive();
		Matrix modifiedEigenvectors = eigenvectors.times(sn);
		return new Components(modifiedEigenvectors, eigenvalues);
	}

	private Components removeEntriesLessThanMinEigenvalue(
			EigenvalueThreshold eigenvalueThreshold) {

		Matrix eValues = getEigenvalues();
		Matrix eVectors = getEigenvectors();
		Vector eigenvaluesVector = getEigenvaluesVector();
		for (int i = eigenvaluesVector.rowCount(); i >= 1; i--) {
			if (eigenvalueThreshold.getPrincipalFactorCriterion().equals(
					PrincipalFactorCriterion.MIN_EIGENVALUE)
					&& eigenvaluesVector.getValue(i) < eigenvalueThreshold
							.getMinEigenvalue()) {
				eValues = eValues.removeRow(i).removeColumn(i);
				eVectors = eVectors.removeColumn(i);
			}
		}
		return new Components(eVectors, eValues);
	}

}
