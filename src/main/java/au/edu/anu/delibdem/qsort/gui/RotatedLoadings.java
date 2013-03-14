package au.edu.anu.delibdem.qsort.gui;

import moten.david.util.math.Matrix;
import moten.david.util.math.MatrixProvider;

public class RotatedLoadings implements MatrixProvider {
	private Rotations rotations;

	public RotatedLoadings(Rotations rotations) {
		super();
		this.rotations = rotations;
	}

	@Override
	public String toString() {
		return "Rotated Loadings";
	}

	@Override
	public Matrix getMatrix() {
		return rotations.getMatrix();
	}
}
