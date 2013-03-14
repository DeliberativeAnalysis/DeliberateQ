package au.edu.anu.delibdem.qsort.gui;

import java.awt.Component;
import java.awt.GridLayout;

import javax.swing.JPanel;

public class DataViewerPanel extends JPanel {

	private static final long serialVersionUID = -5315235119397534284L;
	private Component parent;
	private Component content;

	public DataViewerPanel(Component parent) {
		this.parent = parent;
	}

	public void setContent(Component panel) {
		removeAll();
		content = panel;
		setLayout(new GridLayout(1, 1));
		add(panel);
		invalidate();
		parent.validate();
	}

	public Component getContent() {
		return content;
	}

}
