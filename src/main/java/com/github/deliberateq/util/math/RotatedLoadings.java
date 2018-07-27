package com.github.deliberateq.util.math;

import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;

import com.github.deliberateq.util.math.Varimax.RotationMethod;

public final class RotatedLoadings extends HashMap<RotationMethod, Matrix> {

	private static final long serialVersionUID = 4765552026257009699L;

	private static int counter = 0;

	private synchronized int nextCounter() {
		counter++;
		return counter;
	}

	@Override
	public String toString() {
		ByteArrayOutputStream bytes = new ByteArrayOutputStream();
		PrintWriter out = new PrintWriter(bytes);
		SimpleHeirarchicalFormatter f = FactorAnalysisResults
				.createTextFormatter(out);
		format(null, f);
		out.close();
		return new String(bytes.toByteArray(), StandardCharsets.UTF_8);
	}

	public void format(Matrix data, SimpleHeirarchicalFormatter f) {
		for (RotationMethod method : keySet()) {
			f.header(method.toString(), false);
			f.blockStart();

			f.header("Loadings", true);
			f.blockStart();
			f.item(get(method));
			f.blockFinish();

			f.header("Correlation", true);
			f.blockStart();
			f.item(get(method).getCorrelationCoefficientMatrix());
			f.blockFinish();

			Object[] objects = new Object[] {
					get(method).setColumnLabelPattern("F<index>"), data };
			f.link("Manual Rotations", "manual-rotation-" + nextCounter(),
					objects, "rotate");

			f.link("Factor Scores", "scores-" + nextCounter(), objects,
					"scores");
			f.blockFinish();
		}
	}
}
