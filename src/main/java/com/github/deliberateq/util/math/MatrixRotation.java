package com.github.deliberateq.util.math;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.util.List;

public class MatrixRotation implements Serializable {

	private static final long serialVersionUID = 2930912548858495735L;
	private int column1;
	private int column2;
	private double angle;

	public int getColumn1() {
		return column1;
	}

	public MatrixRotation(int column1, int column2, double angle) {
		super();
		this.column1 = column1;
		this.column2 = column2;
		this.angle = angle;
	}

	public MatrixRotation(int column1, int column2, int angleDegrees) {
		super();
		this.column1 = column1;
		this.column2 = column2;
		this.angle = Math.PI / 180 * angleDegrees;
	}

	public void setColumn1(int column1) {
		this.column1 = column1;
	}

	public int getColumn2() {
		return column2;
	}

	public void setColumn2(int column2) {
		this.column2 = column2;
	}

	public double getAngle() {
		return angle;
	}

	public void setAngle(double angle) {
		this.angle = angle;
	}

	public double getAngleDegrees() {
		return angle * 180 / Math.PI;
	}

	@Override
	public String toString() {
		return "(" + column1 + "," + column2 + ","
				+ new DecimalFormat("0.00000").format(getAngleDegrees()) + ")";
	}

	public static void simplify(List<MatrixRotation> list) {
		int i = 0;
		while (list.size() > i + 1) {
			if (list.get(i).column1 == list.get(i + 1).column1
					&& list.get(i).column2 == list.get(i + 1).column2) {
				list.get(i).setAngle(
						list.get(i).getAngle() + list.get(i + 1).getAngle());
				list.remove(i + 1);
			} else {
				i++;
			}
		}
		for (i = list.size() - 1; i >= 0; i--) {
			if (Math.abs(list.get(i).getAngle()) < 0.000001)
				list.get(i).setAngle(0);
			if (list.get(i).getAngle() == 0)
				list.remove(i);
		}
	}
}
