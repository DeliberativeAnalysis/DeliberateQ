package au.edu.anu.delibdem.qsort.gui;

import java.awt.Font;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class FactorSelector extends JPanel {

	private static final long serialVersionUID = 8560906513148870193L;
	private final JCheckBox[] checkBoxes;
	private int lastSelected = -1;

	private final List<ChangeListener> listeners = new ArrayList<ChangeListener>();

	public FactorSelector(int numFactors, final int maxSelectable) {
		setLayout(new BoxLayout(this, BoxLayout.LINE_AXIS));
		checkBoxes = new JCheckBox[numFactors];
		for (int i = 0; i < numFactors; i++) {
			checkBoxes[i] = new JCheckBox("F" + (i + 1) + "");
			add(checkBoxes[i]);
			final int index = i;
			checkBoxes[i].addChangeListener(new ChangeListener() {
				@Override
				public void stateChanged(ChangeEvent arg0) {
					if (lastSelected != -1) {
						int count = 0;
						for (JCheckBox check : checkBoxes)
							if (check.isSelected())
								count++;
						if (count > maxSelectable) {
							checkBoxes[lastSelected].setSelected(false);
						}
					}
					if (checkBoxes[index].isSelected())
						lastSelected = index;
					fireChanged();
				}
			});
		}
		setSignificant(1, false);
	}

	public void setSignificant(int factor, boolean significant) {
		JCheckBox checkBox = checkBoxes[factor - 1];
		Font font = checkBox.getFont();
		if (significant)
			checkBox.setFont(font.deriveFont(Font.BOLD));
		else
			checkBox.setFont(font.deriveFont(Font.PLAIN));
	}

	public void setSelected(int factor, boolean selected) {
		checkBoxes[factor - 1].setSelected(selected);
	}

	public boolean getSelected(int factor) {
		return checkBoxes[factor - 1].isSelected();
	}

	public static void main(String[] args) {
		new QuickFrame(new FactorSelector(4, 5));
	}

	public void addChangeListener(ChangeListener l) {
		listeners.add(l);
	}

	public void removeChangeListener(ChangeListener l) {
		listeners.remove(l);
	}

	private void fireChanged() {
		for (ChangeListener listener : listeners)
			listener.stateChanged(new ChangeEvent(this));
	}

	public boolean[] getSelected() {
		boolean[] selected = new boolean[checkBoxes.length];
		for (int i = 0; i < selected.length; i++) {
			selected[i] = checkBoxes[i].isSelected();
		}
		return selected;
	}
}
