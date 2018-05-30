package com.github.deliberateq.util.math;

public final class MatrixEntry {

	private final int row;
	private final int column;

	public MatrixEntry(int row, int column) {
		this.row = row;
		this.column = column;
	}

	public int getRow() {
		return row;
	}

	public int getColumn() {
		return column;
	}

}
