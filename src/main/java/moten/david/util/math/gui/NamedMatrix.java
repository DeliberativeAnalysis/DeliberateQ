package moten.david.util.math.gui;

import moten.david.util.math.Matrix;

public class NamedMatrix {
	private String name;

	private Matrix matrix;

	public NamedMatrix(String name, Matrix matrix) {
		this.name = name;
		this.matrix = matrix;
	}

	@Override
	public String toString() {
		return name;
	}

	public Matrix getMatrix() {
		return matrix;
	}
}
