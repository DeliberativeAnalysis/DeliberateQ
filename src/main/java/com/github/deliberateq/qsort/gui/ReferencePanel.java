package com.github.deliberateq.qsort.gui;

import java.awt.Component;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Point;
import java.text.DecimalFormat;
import java.util.Map;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import com.github.deliberateq.util.math.CorrelationCoefficient;
import com.github.deliberateq.util.math.Matrix;

public class ReferencePanel extends JPanel {

	private static final long serialVersionUID = 5929980881633539622L;

	private Component parent;

	public ReferencePanel(Component parent) {
		this.parent = parent;
	}

	public void update(Rotations rotations, CorrelationCoefficient cc) {
		removeAll();
		if (Model.getInstance().getReferenceMatrix() != null) {
			Matrix m = rotations.getRotatedLoadingsToUseWithReference();
			Matrix r = Model.getInstance().getReferenceMatrix()
					.restrictRows(rotations.getUseWithReference());
			m = m.restrictRows(r);
			r = r.restrictRows(m);
			Map<Point, Double> map = m.getMatchedCorrelations(r, cc);
			setLayout(new GridLayout(3, map.keySet().size() + 1));
			addBoldItem("Reference");
			for (Point p : map.keySet())
				addItem(p.y + "");
			addBoldItem("This");
			for (Point p : map.keySet())
				addItem(p.x + "");
			addBoldItem("Correlation");
			for (Point p : map.keySet())
				addItem(new DecimalFormat("0.0000").format(map.get(p)));
		}
		invalidate();
		parent.validate();
	}

	public void addItem(String s) {
		JLabel label = new JLabel(s);
		label.setHorizontalAlignment(SwingConstants.RIGHT);
		add(label);
	}

	public void addBoldItem(String s) {
		JLabel label = new JLabel(s);
		label.setFont(label.getFont().deriveFont(Font.BOLD));
		label.setHorizontalAlignment(SwingConstants.RIGHT);
		add(label);
	}

}
