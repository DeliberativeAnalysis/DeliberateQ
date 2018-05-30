package com.github.deliberateq.util.math;

import java.util.Collections;
import java.util.List;

import com.github.deliberateq.util.math.EigenvalueThreshold.PrincipalFactorCriterion;

public final class Components {

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

	public Components getPrincipalComponents(EigenvalueThreshold threshold) {

		System.out.println("getting pc, initial = ");
		System.out.println(this);

		Components c = makeEigenvaluesDescendInValue();
		System.out.println("order eigenvalues desc=");
		System.out.println(this);

		// apply min eigenvalue criterion if set
		c = c.removeEntriesLessThanMinEigenvalue(threshold);
		System.out.println("removed les than min eigenvalue=");
		System.out.println(this);

		// apply max factors criterion if set
		c = removeEntriesMoreThanMaxFactors(c, threshold);
		System.out.println("removed more than max factors=");
		System.out.println(this);

		// get positive manifold
		c = c.normalizeLoadingSigns();
		System.out.println("normalized manifold=");
		System.out.println(this);

		// return result
		return c;
	}

	Components removeEntriesMoreThanMaxFactors(Components c,
			EigenvalueThreshold threshold) {

		if (threshold.getPrincipalFactorCriterion().equals(
				PrincipalFactorCriterion.MAX_FACTORS)
				&& c.getEigenvalues().rowCount() > threshold.getMaxFactors()) {

			final Vector eigenvaluesVector = c.getEigenvaluesVector();
			Matrix eValues = c.getEigenvalues();
			Matrix eVectors = c.getEigenvectors();

			// count the values to delete
			int numToDelete = eigenvaluesVector.rowCount()
					- threshold.getMaxFactors();

			// get the indices of the eigenvalues in ascending order
			List<Integer> ordered = eigenvaluesVector
					.getOrderedIndicesByAbsoluteValue(true);

			// get the indices that we are going to delete
			List<Integer> removeThese = ordered.subList(0, numToDelete);

			// now sort the indices to remove because we must remove
			// rows/columns from the bottom/right first
			Collections.sort(removeThese);

			// remove the rows/columns from the bottom/right first
			for (int j = removeThese.size() - 1; j >= 0; j--) {
				Integer row = removeThese.get(j);
				// remove the factor marked for deletion from the diagonal
				// matrix of eigenvalues
				eValues = eValues.removeRow(row).removeColumn(row);
				// remove the factor marked for deletion from the eigenvectors
				eVectors = eVectors.removeColumn(row);
			}
			return new Components(eVectors, eValues);
		} else
			return this;
	}

	Components makeEigenvaluesDescendInValue() {

		// calculate row switcher for ordering of eigenvalues vector
		Vector eigenvaluesVector = getEigenvaluesVector();
		Matrix rowSwitcher = eigenvaluesVector
				.getRowSwitchingMatrixToOrderByAbsoluteValue(false);

		// switch rows on eigenvalue vector
		Matrix eValues = Matrix.createDiagonalMatrix(rowSwitcher
				.times(eigenvaluesVector));

		// switch columns on eigenvectors matrix
		Matrix eVectors = getEigenvectors().times(rowSwitcher.transpose());
		return new Components(eVectors, eValues);
	}

	// TODO rename to makePositiveManifold?
	Components normalizeLoadingSigns() {
		Matrix sn = getLoadings()
				.getSignNormalizationElementaryMatrixSoMaxAbsoluteValueByColumnIsPositive();
		Matrix modifiedEigenvectors = eigenvectors.times(sn);
		return new Components(modifiedEigenvectors, eigenvalues);
	}

	Components removeEntriesLessThanMinEigenvalue(
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

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Components [eigenvectors=\n");
		builder.append(eigenvectors);
		builder.append(", eigenvalues=\n");
		builder.append(eigenvalues);
		builder.append("]");
		return builder.toString();
	}

}
