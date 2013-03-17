package moten.david.util.math;

import java.io.PrintWriter;
import java.util.HashMap;

import moten.david.util.StringOutputStream;
import moten.david.util.math.Varimax.RotationMethod;

public class RotatedLoadings extends HashMap<RotationMethod, Matrix> {

	private static final long serialVersionUID = 4765552026257009699L;

	private static int counter = 0;

	private boolean html;

	private synchronized int nextCounter() {
		counter++;
		return counter;
	}

	@Override
	public String toString() {
		StringOutputStream sos = new StringOutputStream();
		PrintWriter out = new PrintWriter(sos);
		SimpleHeirarchicalFormatter f = FactorAnalysisResults
				.createTextFormatter(out);
		format(null, f);
		out.close();
		return sos.toString();
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
			f.item(get(method).getPearsonCorrelationMatrix());
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
