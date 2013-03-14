package au.edu.anu.delibdem.qsort.gui;

public enum RotateToType {

	ROTATE_TO_REFERENCE_PAIRWISE("Rotate to Reference (Pairwise)"), ROTATE_TO_REFERENCE_GPA(
			"Rotate to Reference (GPA)");

	private String name;

	private RotateToType(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return name;
	}
}
