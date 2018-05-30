package com.github.deliberateq.qsort.gui;

import com.github.deliberateq.util.math.Matrix;
import com.github.deliberateq.util.math.MatrixProvider;

public class Model {

	private static Model instance;

	private MatrixProvider reference;

	public MatrixProvider getReference() {
		return reference;
	}

	public Matrix getReferenceMatrix() {
		if (reference == null)
			return null;
		else if (reference instanceof MatrixProvider) {
			return reference.getMatrix();
		} else
			throw new RuntimeException("reference not recognized: " + reference);
	}

	public void setReference(MatrixProvider reference) {
		this.reference = reference;
	}

	public synchronized static Model getInstance() {
		if (instance == null)
			instance = new Model();
		return instance;
	}

	private Model() {

	}

}
