package au.edu.anu.delibdem.qsort.gui;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SpringLayout;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import moten.david.util.event.Event;
import moten.david.util.event.EventManager;
import au.edu.anu.delibdem.qsort.Data;

public class StagesPanel extends JPanel {

	private static final long serialVersionUID = -5357822385951586019L;

	public StagesPanel(final Data data, JFrame frame) {
		SpringLayout layout = new SpringLayout();
		setLayout(layout);
		LinkButton selectAll = new LinkButton("Select all");
		LinkButton selectNone = new LinkButton("Select none");
		add(selectAll);
		add(selectNone);

		Component list = createStageList(data, selectAll, selectNone);
		JScrollPane scroll = new JScrollPane(list);
		scroll.setBorder(BorderFactory.createEmptyBorder());
		add(scroll);

		// layout
		setPreferredSize(new Dimension(200, frame.getHeight() * 2 / 3));

		layout.putConstraint(SpringLayout.NORTH, selectAll, 5,
				SpringLayout.NORTH, this);
		layout.putConstraint(SpringLayout.WEST, selectAll, 5,
				SpringLayout.WEST, this);
		layout.putConstraint(SpringLayout.NORTH, selectNone, 5,
				SpringLayout.SOUTH, selectAll);
		layout.putConstraint(SpringLayout.WEST, selectNone, 0,
				SpringLayout.WEST, selectAll);
		layout.putConstraint(SpringLayout.NORTH, scroll, 5, SpringLayout.SOUTH,
				selectNone);
		layout.putConstraint(SpringLayout.SOUTH, scroll, 0, SpringLayout.SOUTH,
				this);
		layout.putConstraint(SpringLayout.WEST, scroll, 5, SpringLayout.WEST,
				this);
		layout.putConstraint(SpringLayout.EAST, scroll, 0,
				SpringLayout.HORIZONTAL_CENTER, this);
	}

	private ActionListener createSelectNoneActionListener(
			final Object[] checkBoxes) {
		return new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				for (int i = 0; i < checkBoxes.length; i++) {
					JCheckBox checkBox = (JCheckBox) checkBoxes[i];
					checkBox.setSelected(false);
					repaint();
				}
			}
		};
	}

	private Component createStageList(final Data data, LinkButton selectAll,
			LinkButton selectNone) {
		final String[] stages = data.getStageTypes().toArray(new String[0]);
		final Object[] checkBoxes = new Object[stages.length];
		JCheckBoxList list = new JCheckBoxList();
		for (int i = 0; i < stages.length; i++) {
			final JCheckBox checkBox = new JCheckBox("" + stages[i]);
			checkBox.setSelected(data.getStageFilter().contains(stages[i]));

			checkBoxes[i] = checkBox;
			checkBox.addChangeListener(createChangeListener(checkBox, data,
					stages[i]));
		}
		list.setListData(checkBoxes);
		// event listeners
		selectAll.addActionListener(createSelectAllActionListener(checkBoxes));
		selectNone
				.addActionListener(createSelectNoneActionListener(checkBoxes));

		return list;
	}

	private ChangeListener createChangeListener(final JCheckBox checkBox,
			final Data data, final String stage) {
		return new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				if (checkBox.isSelected())
					data.getStageFilter().add(stage);
				else
					data.getStageFilter().remove(stage);
				EventManager.getInstance().notify(
						new Event(data, Events.DATA_CHANGED));
			}
		};
	}

	private ActionListener createSelectAllActionListener(
			final Object[] checkBoxes) {

		return new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				for (int i = 0; i < checkBoxes.length; i++) {
					JCheckBox checkBox = (JCheckBox) checkBoxes[i];
					checkBox.setSelected(true);
					repaint();
				}
			}
		};
	}
}
