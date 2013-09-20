package moten.david.util.math;

import java.io.PrintWriter;
import java.text.DecimalFormat;

public class FactorAnalysisResults {

	private final FactorAnalysisInput input;

	private Matrix initial;

	private Matrix eigenvalues;

	private Matrix eigenvectors;

	private Vector percentVariance;

	private Matrix principalEigenvalues;

	private Matrix principalEigenvectors;

	private Matrix loadings;

	private Matrix principalLoadings;

	private RotatedLoadings rotatedLoadings;

	private long extractionTimeMs;

	private long rotationTimeMs;

	private String title;

	public FactorAnalysisResults(FactorAnalysisInput input) {
		this.input = input;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public static SimpleHeirarchicalFormatter createTextFormatter(
			final PrintWriter out) {
		return new SimpleHeirarchicalFormatter() {

			@Override
			public void blockFinish() {
				out.println();
			}

			@Override
			public void blockStart() {
				out.println();
			}

			@Override
			public void header(String s, boolean collapsed) {
				out.println(s);
			}

			@Override
			public void item(Object object) {
				out.println(object);
			}

			@Override
			public void link(String s, String id, Object object, String action) {
				out.println(s);
				out.println(object);
			}

			@Override
			public void image(String s, String id, Object object, String action) {
				out.println("image: " + s);
				out.println(object);
			}
		};

	}

	@Override
	public String toString() {
		return title;
	}

	public String toStringVerbose() {
		return "FactorAnalysisResults [\nextractionMethod="
				+ input.getExtractionMethod()
				+ "\n, initial=\n"
				+ initial
				+ "\n, correlations=\n"
				+ input.getCorrelations()
				+ "\n, eigenvalues=\n"
				+ eigenvalues.getDiagonal()
				+ "\n, eigenvectors=\n"
				+ eigenvectors
				+ ",\n percentVariance=\n"
				+ percentVariance
				+ ",\n eigenvalueThreshold="
				+ input.getEigenvalueThreshold()
				+ ",\n principalEigenvalues=\n"
				+ (principalEigenvalues != null ? principalEigenvalues
						.getDiagonal() : "null")
				+ ",\n principalEigenvectors=\n" + principalEigenvectors
				+ ",\n loadings=\n" + loadings + ",\n principalLoadings=\n"
				+ principalLoadings + ",\n rotatedLoadings=\n"
				+ rotatedLoadings + ",\n extractionTimeMs=" + extractionTimeMs
				+ ",\n rotationTimeMs=" + rotationTimeMs + ",\n title=" + title
				+ "]";

	}

	private void print(Matrix data, SimpleHeirarchicalFormatter f) {

		f.header(input.getExtractionMethod().toString(), false);
		f.blockStart();

		// f.header("Raw Data", true);
		// f.blockStart();
		// f.item(initial);
		// f.blockFinish();

		f.header("Correlations", true);
		f.blockStart();
		f.item(input.getCorrelations());
		f.blockFinish();

		f.header("Eigenvalues", true);
		f.blockStart();
		f.item("Extraction time = "
				+ new DecimalFormat("0.000").format(extractionTimeMs / 1000.0)
				+ "s");
		f.item(eigenvalues.getDiagonal()
				.setRowLabels(eigenvalues.getRowLabels())
				.setColumnLabel(1, "Eigenvalue"));
		f.blockFinish();

		f.header("Eigenvectors", true);
		f.blockStart();
		f.item(eigenvectors);
		f.blockFinish();

		f.header("Percent Variance", true);
		f.blockStart();
		f.item(percentVariance);
		f.blockFinish();

		f.header("Loadings", true);
		f.blockStart();
		f.item(loadings);
		f.blockFinish();

		f.header("Eigenvalue Threshold", true);
		f.blockStart();
		f.item("eigenvalue threshold to extract principal loadings = "
				+ input.getEigenvalueThreshold());
		f.blockFinish();

		f.header("Principal Eigenvalues", true);
		f.blockStart();
		f.item(principalEigenvalues.getDiagonal()
				.setRowLabels(principalEigenvalues.getRowLabels())
				.setColumnLabel(1, "Eigenvalue"));
		f.blockFinish();

		f.header("Principal Eigenvectors", true);
		f.blockStart();
		f.item(principalEigenvectors);
		f.blockFinish();

		f.header("Principal Loadings", true);
		f.blockStart();
		f.item(principalLoadings);
		f.blockFinish();

		f.header("Principal Rotated Loadings", false);
		f.blockStart();
		rotatedLoadings.format(data, f);
		f.blockFinish();

		f.blockFinish();
	}

	public FactorExtractionMethod getExtractionMethod() {
		return input.getExtractionMethod();
	}

	public Matrix getInitial() {
		return initial;
	}

	public Matrix getCorrelations() {
		return input.getCorrelations();
	}

	public Matrix getEigenvalues() {
		return eigenvalues;
	}

	public Vector getEigenvaluesVector() {
		Vector matrix = eigenvalues.getDiagonal();
		for (int i = 1; i <= matrix.rowCount(); i++) {
			matrix.setRowLabel(i, eigenvalues.getRowLabel(i));
		}
		matrix.setColumnLabel(1, "Eigenvalue");
		return matrix;
	}

	public Matrix getEigenvectors() {
		return eigenvectors;
	}

	public Vector getPercentVariance() {
		return percentVariance;
	}

	public Matrix getPrincipalEigenvalues() {
		return principalEigenvalues;
	}

	public Vector getPrincipalEigenvaluesVector() {
		Vector matrix = principalEigenvalues.getDiagonal();
		for (int i = 1; i <= matrix.rowCount(); i++) {
			matrix.setRowLabel(i, principalEigenvalues.getRowLabel(i));
		}
		matrix.setColumnLabel(1, "Eigenvalue");
		return matrix;
	}

	public Matrix getPrincipalEigenvectors() {
		return principalEigenvectors;
	}

	public Matrix getLoadings() {
		return loadings;
	}

	public Matrix getPrincipalLoadings() {
		return principalLoadings;
	}

	public RotatedLoadings getRotatedLoadings() {
		return rotatedLoadings;
	}

	public void setInitial(Matrix initial) {
		this.initial = initial;
	}

	public void setEigenvalues(Matrix eigenvalues) {
		this.eigenvalues = eigenvalues;
	}

	public void setEigenvectors(Matrix eigenvectors) {
		this.eigenvectors = eigenvectors;
	}

	public void setPercentVariance(Vector percentVariance) {
		this.percentVariance = percentVariance;
	}

	public void setPrincipalEigenvalues(Matrix principalEigenvalues) {
		this.principalEigenvalues = principalEigenvalues;
	}

	public void setPrincipalEigenvectors(Matrix principalEigenvectors) {
		this.principalEigenvectors = principalEigenvectors;
	}

	public void setLoadings(Matrix loadings) {
		this.loadings = loadings;
	}

	public void setPrincipalLoadings(Matrix principalLoadings) {
		this.principalLoadings = principalLoadings;
	}

	public void setRotatedLoadings(RotatedLoadings rotatedLoadings) {
		this.rotatedLoadings = rotatedLoadings;
	}

	public long getExtractionTimeMs() {
		return extractionTimeMs;
	}

	public void setExtractionTimeMs(long extractionTimeMs) {
		this.extractionTimeMs = extractionTimeMs;
	}

	public long getRotationTimeMs() {
		return rotationTimeMs;
	}

	public void setRotationTimeMs(long rotationTimeMs) {
		this.rotationTimeMs = rotationTimeMs;
	}

	public EigenvalueThreshold getEigenvalueThreshold() {
		return input.getEigenvalueThreshold();
	}

}
