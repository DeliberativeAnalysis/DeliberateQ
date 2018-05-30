package com.github.deliberateq.qsort.gui;

import java.awt.GridLayout;

import javax.swing.JPanel;
import javax.swing.JSplitPane;

import com.github.deliberateq.util.event.Event;
import com.github.deliberateq.util.event.EventManager;
import com.github.deliberateq.util.event.EventManagerListener;
import com.github.deliberateq.util.math.FactorAnalysisResults;
import com.github.deliberateq.util.math.Matrix;
import com.github.deliberateq.util.math.gui.JMatrixViewer;
import com.github.deliberateq.util.math.gui.NamedMatrix;

public class FactorPanel extends JPanel {

	private static final long serialVersionUID = 6292520943354194017L;

	public FactorPanel(FactorAnalysisResults r) {
		EventManager eventManager = new EventManager();
		JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
		FactorTreePanel tree = new FactorTreePanel(r);
		tree.addEventManager(eventManager);
		split.setLeftComponent(tree);
		final JMatrixViewer viewer = new JMatrixViewer();
		viewer.setUseCheckBoxes(false);
		split.setRightComponent(viewer);
		split.setDividerLocation(0.25);
		setLayout(new GridLayout(1, 1));
		add(split);
		eventManager.addListener(Events.MATRIX, new EventManagerListener() {
			@Override
			public void notify(Event event) {
				Matrix m = (Matrix) event.getObject();
				viewer.setNamedMatrix(new NamedMatrix("", m));
			}
		});
	}

}
