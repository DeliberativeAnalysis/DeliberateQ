package com.github.deliberateq.qsort.gui;

import com.github.deliberateq.util.math.Matrix;
import com.github.deliberateq.util.math.MatrixProvider;

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
