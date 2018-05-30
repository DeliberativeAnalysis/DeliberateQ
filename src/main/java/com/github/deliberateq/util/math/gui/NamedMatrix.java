package com.github.deliberateq.util.math.gui;

import com.github.deliberateq.util.math.Matrix;

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
