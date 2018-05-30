package com.github.deliberateq.qsort.gui.loadings;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.github.deliberateq.qsort.gui.Rotations;
import com.github.deliberateq.util.math.Matrix;
import com.github.deliberateq.util.math.MatrixRotation;
import com.github.deliberateq.util.math.StringFilter;
import com.github.deliberateq.util.math.gui.GraphPanel;

public class LoadingsGraphsPanel extends JPanel {

	private static final Logger log = Logger
			.getLogger(LoadingsGraphsPanel.class.getName());

	private static final long serialVersionUID = -5440895661166009687L;

	private final Component parent;
	private GraphPanel currentGp;
	private final List<GraphPanel> graphPanels = new ArrayList<GraphPanel>();
	private final Map<GraphPanel, Point> map = new HashMap<GraphPanel, Point>();

	private final List<ChangeListener> changeListeners = new ArrayList<ChangeListener>();

	private StringFilter rowLabelFilter = null;

	private Rotations rotations;

	public LoadingsGraphsPanel(Component parent) {
		this.parent = parent;
	}

	public void setDisplayLabels(boolean display) {
		for (GraphPanel gp : graphPanels) {
			gp.setLabelsVisible(display);
		}
	}

	public void setRowLabelFilter(StringFilter rowLabelFilter) {
		this.rowLabelFilter = rowLabelFilter;
	}

	public void setRotations(final Rotations rotations) {
		this.rotations = rotations;
		if (graphPanels.size() == 0) {
			final Matrix loadings = rotations.getRotatedLoadings();
			int numGraphs = loadings.columnCount()
					* (loadings.columnCount() - 1) / 2;
			int numRows = (numGraphs + 2) / 3;
			setLayout(new GridLayout(numRows, 3));
			removeAll();
			for (int i = 1; i < loadings.columnCount(); i++) {
				for (int j = i + 1; j <= loadings.columnCount(); j++) {
					Matrix m = new Matrix(loadings.rowCount(), 2);
					m.setRowLabels(loadings.getRowLabels());
					m.setColumnVector(1, loadings.getColumnVector(i));
					m.setColumnVector(2, loadings.getColumnVector(j));
					final GraphPanel gp = new GraphPanel(m);
					graphPanels.add(gp);
					map.put(gp, new Point(i, j));
					gp.setPreferredSize(new Dimension(300, 300));
					gp.setXLabel(m.getColumnLabel(1));
					gp.setYLabel(m.getColumnLabel(2));
					add(gp);
					gp.setLabelsVisible(false);
					gp.setFocusable(true);
					gp.addMouseListener(new MouseAdapter() {
						@Override
						public void mouseClicked(MouseEvent e) {
							gp.requestFocusInWindow();
						}
					});
					gp.addKeyListener(new KeyAdapter() {
						@Override
						public void keyPressed(KeyEvent e) {
							if (e.getKeyCode() == 37 || e.getKeyCode() == 39) {
								if (gp != null) {
									int direction = -1;
									if (e.getKeyCode() == 37)
										direction = 1;
									rotations.addRotation(new MatrixRotation(
											map.get(gp).x, map.get(gp).y,
											direction));
									updateGraphs(rotations);
								}
							}
						}
					});
					gp.addFocusListener(new FocusListener() {
						@Override
						public void focusGained(FocusEvent e) {
							if (currentGp != null)
								currentGp.setBorder(BorderFactory
										.createEmptyBorder());
							currentGp = gp;
							updateSelection();
						}

						@Override
						public void focusLost(FocusEvent e) {
						}
					});
				}
			}
		} else {
			updateGraphs(rotations);
		}
		invalidate();
		parent.validate();
	}

	private void updateGraphs(Rotations rotations) {
		int count = 0;
		Matrix rotatedLoadings = rotations.getRotatedLoadings();
		for (int i = 1; i < rotatedLoadings.columnCount(); i++) {
			for (int j = i + 1; j <= rotatedLoadings.columnCount(); j++) {
				Matrix m = new Matrix(rotatedLoadings.rowCount(), 2);
				m.setRowLabels(rotatedLoadings.getRowLabels());
				m.setColumnVector(1, rotatedLoadings.getColumnVector(i));
				m.setColumnVector(2, rotatedLoadings.getColumnVector(j));
				if (rowLabelFilter != null)
					m = m.removeRowsByLabel(rowLabelFilter);
				graphPanels.get(count).setMatrix(m);
				graphPanels.get(count).repaint();
				count++;
			}
		}
		fireChanged();
	}

	private void fireChanged() {
		for (ChangeListener c : changeListeners) {
			c.stateChanged(new ChangeEvent(this));
		}
	}

	private void updateSelection() {
		if (currentGp != null)
			currentGp.setBorder(BorderFactory.createEtchedBorder(Color.black,
					Color.lightGray));
		repaint();
	}

	public void addChangeListener(ChangeListener c) {
		changeListeners.add(c);
	}

	public void removeChangeListener(ChangeListener c) {
		changeListeners.remove(c);
	}

	public void resize(int i) {
		for (GraphPanel gp : graphPanels) {
			gp.setPreferredSize(new Dimension(i, i));
		}
	}

	public void redraw() {
		setRotations(rotations);
	}
}
