package moten.david.util.math;

public class Components {

	private final Matrix eigenvectors;
	private final Matrix eigenvalues;
	private final EigenvalueThreshold eigenvalueThreshold;

	public Components(Matrix eigenvectors, Matrix eigenvalues,
			EigenvalueThreshold eigenvalueThreshold) {
		this.eigenvectors = eigenvectors;
		this.eigenvalues = eigenvalues;
		this.eigenvalueThreshold = eigenvalueThreshold;
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

	public Matrix getPrincipalEigenvalues() {
		// TODO
		return null;
	}

	public Vector getPrincipalEigenvaluesVector() {
		// TODO
		return null;
	}

	public Matrix getPrincipalLoadings() {
		// TODO
		return null;
	}

	public Components makeEigenvaluesDescendInValue() {
		{
			Matrix rowSwitcher = getEigenvaluesVector()
					.getRowSwitchingMatrixToOrderByAbsoluteValue(false);
			// switch rows on eigenvalue vector
			Matrix eigenvalues = Matrix.createDiagonalMatrix(rowSwitcher
					.times(getEigenvaluesVector()));

			// switch columsn on eigenvectors matrix
			Matrix eigenvectors = getEigenvectors().times(
					rowSwitcher.transpose());
			return new Components(eigenvectors, eigenvalues,
					eigenvalueThreshold);
		}
	}
}
