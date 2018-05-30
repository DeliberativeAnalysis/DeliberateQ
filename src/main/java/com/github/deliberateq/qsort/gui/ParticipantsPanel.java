package com.github.deliberateq.qsort.gui;

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

import com.github.deliberateq.qsort.Data;
import com.github.deliberateq.util.event.Event;
import com.github.deliberateq.util.event.EventManager;

public class ParticipantsPanel extends JPanel {

	private static final long serialVersionUID = -5357822385951586019L;
	private Object[] participantCheckBoxes;
	private boolean updatingMultipleCheckBoxes;
	private final Data data;
	private JCheckBoxList participantList;

	public ParticipantsPanel(final Data data, JFrame frame) {
		this.data = data;
		SpringLayout layout = new SpringLayout();
		setLayout(layout);
		LinkButton selectAll = new LinkButton("Select all");
		LinkButton selectNone = new LinkButton("Select none");
		add(selectAll);
		add(selectNone);

		Component list = createParticipantList(data, selectAll, selectNone);
		JScrollPane scroll = new JScrollPane(list);
		scroll.setBorder(BorderFactory.createEmptyBorder());
		add(scroll);

		Component typeList = createParticipantTypesList(data);
		JScrollPane typeScroll = new JScrollPane(typeList);
		typeScroll.setBorder(BorderFactory.createEmptyBorder());
		add(typeScroll);

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
		// layout.putConstraint(SpringLayout.EAST, scroll, 0,
		// SpringLayout.HORIZONTAL_CENTER, this);
		layout.putConstraint(SpringLayout.NORTH, typeScroll, 0,
				SpringLayout.NORTH, scroll);
		layout.putConstraint(SpringLayout.SOUTH, typeScroll, 0,
				SpringLayout.SOUTH, scroll);
		layout.putConstraint(SpringLayout.WEST, typeScroll, 20,
				SpringLayout.EAST, scroll);
		layout.putConstraint(SpringLayout.EAST, typeScroll, 0,
				SpringLayout.EAST, this);

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

	private Component createParticipantList(final Data data,
			LinkButton selectAll, LinkButton selectNone) {
		final String[] participantIds = data.getParticipantIds().toArray(
				new String[0]);
		participantCheckBoxes = new Object[participantIds.length];
		participantList = new JCheckBoxList();
		for (int i = 0; i < participantIds.length; i++) {
			final JCheckBox checkBox = new JCheckBox("" + participantIds[i]);
			checkBox.setSelected(data.getFilter().contains(participantIds[i]));
			participantCheckBoxes[i] = checkBox;
			checkBox.addChangeListener(createChangeListener(checkBox, data,
					participantIds[i]));
		}
		participantList.setListData(participantCheckBoxes);
		// event listeners
		selectAll
				.addActionListener(createSelectAllActionListener(participantCheckBoxes));
		selectNone
				.addActionListener(createSelectNoneActionListener(participantCheckBoxes));
		return participantList;
	}

	private ChangeListener createChangeListener(final JCheckBox checkBox,
			final Data data, final String participantId) {
		return new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				if (checkBox.isSelected())
					data.getFilter().add(participantId);
				else
					data.getFilter().remove(participantId);
				if (!updatingMultipleCheckBoxes)
					EventManager.getInstance().notify(
							new Event(data, Events.DATA_CHANGED));
			}
		};
	}

	private Component createParticipantTypesList(final Data data) {
		final String[] participantTypes = data.getParticipantTypes().toArray(
				new String[0]);
		final Object[] checkBoxes = new Object[participantTypes.length];
		JCheckBoxList list = new JCheckBoxList();
		for (int i = 0; i < participantTypes.length; i++) {
			final JCheckBox checkBox = new JCheckBox("" + participantTypes[i]);
			checkBox.setSelected(false);

			checkBoxes[i] = checkBox;
			final Integer finalI = i;
			checkBox.addChangeListener(new ChangeListener() {
				@Override
				public void stateChanged(ChangeEvent e) {
					if (checkBox.isSelected()) {
						for (String participantId : data
								.getParticipantIds(participantTypes[finalI])) {
							data.getFilter().add(participantId);
						}
					} else
						for (String participantId : data
								.getParticipantIds(participantTypes[finalI])) {
							data.getFilter().remove(participantId);
						}
					refreshParticipantList();
					EventManager.getInstance().notify(
							new Event(data, Events.DATA_CHANGED));
				}

			});
		}
		list.setListData(checkBoxes);
		return list;
	}

	private void refreshParticipantList() {

		updatingMultipleCheckBoxes = true;
		for (Object o : participantCheckBoxes) {
			JCheckBox checkBox = (JCheckBox) o;
			checkBox.setSelected(data.getFilter().contains(checkBox.getText()));
		}
		updatingMultipleCheckBoxes = false;
		participantList.setListData(participantCheckBoxes);
		EventManager.getInstance().notify(new Event(data, Events.DATA_CHANGED));
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
