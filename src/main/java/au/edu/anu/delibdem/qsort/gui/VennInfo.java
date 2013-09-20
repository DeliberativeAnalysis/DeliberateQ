package au.edu.anu.delibdem.qsort.gui;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import moten.david.util.math.FactorScoreAnalyzer;
import moten.david.util.math.Matrix;
import moten.david.util.math.Matrix.FactorScoreStrategy;

import org.apache.commons.math.MathException;
import org.apache.commons.math.distribution.TDistribution;
import org.apache.commons.math.distribution.TDistributionImpl;

public class VennInfo {

	private Matrix initialData;
	private Rotations rotations;
	private int selectedIndex;
	private float confidence = 95;
	private float thresholdSE = 1.0f;
	private FactorScoreStrategy strategy = FactorScoreStrategy.ZERO_ROWS_WITH_MORE_THAN_ONE_SIGNIFICANT_FACTOR_LOADING;
	private final Map<Integer, String> statements;
	private boolean[] selected;

	public Rotations getRotations() {
		return rotations;
	}

	public void setRotations(Rotations rotations) {
		this.rotations = rotations;
	}

	public VennInfo(Matrix data, Rotations rotations,
			Map<Integer, String> statements) {
		super();
		this.initialData = data;
		this.rotations = rotations;
		this.statements = statements;
		this.selected = new boolean[rotations.getLoadings().columnCount()];
		for (int i = 0; i < Math.min(5, selected.length); i++) {
			selected[i] = true;
		}
	}

	public Matrix getInitialData() {
		return initialData;
	}

	public void setInitialData(Matrix data) {
		this.initialData = data;
	}

	@Override
	public String toString() {
		return "Venn Diagrams";
	}

	public List<Double> getThresholds() {
		FactorScoreAnalyzer fsa = getFactorScoreAnalyzer();
		if (fsa == null)
			return new ArrayList<Double>();
		else
			return new ArrayList<Double>(fsa.getGrouped().keySet());
	}

	public FactorScoreAnalyzer getFactorScoreAnalyzer() {
		Matrix.FactorScoreCombination best = initialData.getFactorScoresBest(
				rotations.getRotatedLoadings(), 5);
		if (best.getStrategy() == null)
			return null;
		Matrix mScores = initialData.getFactorScoresZ(
				rotations.getRotatedLoadings(), best.getThreshold(),
				best.getStrategy());
		FactorScoreAnalyzer fsa = new FactorScoreAnalyzer(mScores);
		fsa.setFactorScoreCombination(best);
		return fsa;
	}

	public FactorScoreAnalyzer getFactorScoreAnalyzer(double threshold,
			FactorScoreStrategy strategy) {
		Matrix mScores = initialData.getFactorScoresZ(
				rotations.getRotatedLoadings(), threshold, strategy);
		FactorScoreAnalyzer fsa = new FactorScoreAnalyzer(mScores);
		return fsa;
	}

	public int getSelectedIndex() {
		return selectedIndex;
	}

	public void setSelectedIndex(int selectedIndex) {
		this.selectedIndex = selectedIndex;
	}

	public Map<Integer, String> getStatements() {
		return statements;
	}

	public float getConfidence() {
		return confidence;
	}

	public void setConfidence(float confidence) {
		this.confidence = confidence;
	}

	public float getThresholdSE() {
		return thresholdSE;
	}

	public void setThresholdSE(float thresholdSE) {
		this.thresholdSE = thresholdSE;
	}

	public FactorScoreStrategy getStrategy() {
		return strategy;
	}

	public void setStrategy(FactorScoreStrategy strategy) {
		this.strategy = strategy;
	}

	public double getThreshold() {

		int numParticipants = initialData.rowCount();
		TDistribution tDistribution = new TDistributionImpl(numParticipants);
		double t;
		try {
			t = tDistribution.inverseCumulativeProbability(confidence / 100.0);
		} catch (MathException e) {
			throw new RuntimeException(e);
		}
		double standardError = 1 / Math.sqrt(numParticipants);
		float thresholdSE = getThresholdSE();
		return thresholdSE * standardError * t;

	}

	public void setSelected(boolean[] selected) {
		this.selected = selected;
	}

	public boolean[] getSelected() {
		return selected;
	}

}
