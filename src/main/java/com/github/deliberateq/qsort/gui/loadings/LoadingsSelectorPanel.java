package com.github.deliberateq.qsort.gui.loadings;

import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JPanel;

import com.github.deliberateq.util.math.Matrix;

public class LoadingsSelectorPanel extends JPanel {

	private static final long serialVersionUID = 698235362713111481L;

	public LoadingsSelectorPanel(Matrix loadings) {
		setLayout(new BoxLayout(this, BoxLayout.LINE_AXIS));
		for (int i = 1; i <= loadings.columnCount(); i++) {
			JCheckBox checkBox = new JCheckBox(loadings.getColumnLabel(i));
			if (i <= 3)
				checkBox.setSelected(true);
			add(checkBox);
		}
	}

}
