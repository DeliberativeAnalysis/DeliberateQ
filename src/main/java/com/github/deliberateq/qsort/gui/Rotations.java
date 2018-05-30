package com.github.deliberateq.qsort.gui;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import com.github.deliberateq.util.math.Matrix;
import com.github.deliberateq.util.math.MatrixProvider;
import com.github.deliberateq.util.math.MatrixRotation;
import com.github.deliberateq.util.math.Varimax.RotationMethod;

public class Rotations implements MatrixProvider, Serializable {

	private static final long serialVersionUID = 9145288071207927699L;
	private Matrix loadings;
	private Matrix rotatedLoadings;
	private List<MatrixRotation> rotations = new ArrayList<MatrixRotation>();
	private RotationMethod rotationMethod;
	private final boolean[] useWithReference;
	private boolean changed = true;
	private boolean displayLabels = false;

	public Rotations(Matrix loadings) {
		super();
		this.loadings = loadings;
		useWithReference = new boolean[loadings.rowCount()];
		for (int i = 0; i < useWithReference.length; i++) {
			useWithReference[i] = true;
		}
	}

	public Matrix getLoadings() {
		return loadings;
	}

	public void setLoadings(Matrix loadings) {
		this.loadings = loadings;
		changed = true;
	}

	public static Rotations load(InputStream is) throws IOException,
			ClassNotFoundException {
		ObjectInputStream ois = new ObjectInputStream(is);
		Rotations rotations = (Rotations) ois.readObject();
		ois.close();
		return rotations;
	}

	public void save(OutputStream os) throws IOException {
		ObjectOutputStream oos = new ObjectOutputStream(os);
		oos.writeObject(this);
		oos.close();
	}

	// public List<MatrixRotation> getRotations() {
	// return rotations;
	// }

	public void clearRotations() {
		rotations.clear();
		changed = true;
	}

	public void addRotation(MatrixRotation rotation) {
		rotations.add(rotation);
		MatrixRotation.simplify(rotations);
		changed = true;
	}

	public void setRotations(List<MatrixRotation> rotations) {
		this.rotations = rotations;
		changed = true;
	}

	public RotationMethod getRotationMethod() {
		return rotationMethod;
	}

	public void setRotationMethod(RotationMethod rotationMethod) {
		clearAll();
		this.rotationMethod = rotationMethod;
		changed = true;
	}

	@Override
	public String toString() {
		return "Rotations";
	}

	public void setUseWithReference(int row, boolean useRow) {
		useWithReference[row - 1] = useRow;
		changed = true;
	}

	public boolean getUseWithReference(int row) {
		changed = true;
		return useWithReference[row - 1];
	}

	public Matrix getRotatedLoadingsToUseWithReference() {
		Matrix m = getRotatedLoadings().copy();
		for (int row = loadings.rowCount(); row >= 1; row--) {
			if (!getUseWithReference(row))
				m = m.removeRow(row);
		}
		return m;
	}

	public Matrix getLoadingsToUseWithReference() {
		Matrix m = loadings.copy();
		for (int row = loadings.rowCount(); row >= 1; row--) {
			if (!getUseWithReference(row))
				m = m.removeRow(row);
		}
		return m;
	}

	public Matrix getRotatedLoadings() {

		if (changed) {
			Matrix m = loadings;
			if (rotationMethod != null) {
				boolean kaiserNormalize = true;
				Matrix m2 = getLoadingsToUseWithReference();
				List<MatrixRotation> rots = m2.getRotations(rotationMethod,
						kaiserNormalize);
				m = m.rotate(rots, kaiserNormalize);
			}
			m = m.rotate(rotations);

			// now switch columns so eigenvalues always descend in absolute
			// value. Note that the row switching matrix satisfies that
			// multiplied by its transpose equals the identity matrix so this
			// manipulation of the loadings matrix is valid.

			m = m.getColumnsReorderedByEigenvalue(false);

			// now multiply columns by -1 when largest absolute value is
			// negative. Note that the row/column negation matrix satisfies that
			// multiplied by its transpose equals the identity matrix so this
			// manipulation of the loadings matrix is valid.

			m = m.normalizeSignOfColumnsSoMaxAbsoluteValueIsPositive();

			m.setColumnLabels(loadings.getColumnLabels());
			rotatedLoadings = m;
			changed = false;
		}
		return rotatedLoadings;

	}

	public String getSummary() {
		StringBuffer s = new StringBuffer();
		if (rotationMethod != null)
			s.append(rotationMethod.toString() + "\n");
		if (rotations.size() > 0) {
			for (MatrixRotation r : rotations) {
				s.append(new DecimalFormat("00").format(r.getColumn1())
						+ " "
						+ new DecimalFormat("00").format(r.getColumn2())
						+ " "
						+ new DecimalFormat("0.00000").format(r
								.getAngleDegrees()));
				s.append("\n");
			}
		}
		return s.toString();
	}

	private void clearAll() {
		rotationMethod = null;
		rotations.clear();
		changed = true;
	}

	@Override
	public Matrix getMatrix() {
		return getRotatedLoadings();
	}

	public boolean[] getUseWithReference() {
		return useWithReference;
	}

	public void addRotations(List<MatrixRotation> list) {
		rotations.addAll(list);
		changed = true;
	}

	public List<MatrixRotation> getRotations() {
		return rotations;
	}

	public boolean isDisplayLabels() {
		return displayLabels;
	}

	public void setDisplayLabels(boolean displayLabels) {
		this.displayLabels = displayLabels;
	}

}
