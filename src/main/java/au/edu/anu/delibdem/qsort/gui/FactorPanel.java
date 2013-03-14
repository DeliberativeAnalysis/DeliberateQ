package au.edu.anu.delibdem.qsort.gui;

import java.awt.GridLayout;

import javax.swing.JPanel;
import javax.swing.JSplitPane;

import moten.david.util.event.Event;
import moten.david.util.event.EventManager;
import moten.david.util.event.EventManagerListener;
import moten.david.util.math.FactorAnalysisResults;
import moten.david.util.math.Matrix;
import moten.david.util.math.gui.JMatrixViewer;
import moten.david.util.math.gui.NamedMatrix;

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
