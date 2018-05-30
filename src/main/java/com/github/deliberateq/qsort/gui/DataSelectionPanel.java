package com.github.deliberateq.qsort.gui;

import java.awt.Button;
import java.awt.Dimension;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SpringLayout;

import com.github.deliberateq.qsort.Data;

public class DataSelectionPanel extends JPanel {

	private boolean cancelled = false;

	private static final long serialVersionUID = -5718086890567472132L;

	private final List<Listener> listeners = new ArrayList<Listener>();

	public interface Listener {
		void closed();
	}

	public void addDataSelectionPanelListener(Listener l) {
		listeners.add(l);
	}

	private void fireClosed() {
		for (Listener l : listeners)
			l.closed();
	}

	public DataSelectionPanel(Data data, JFrame frame) {
		SpringLayout layout = new SpringLayout();
		setLayout(layout);
		final StagesPanel stagesPanel = new StagesPanel(data, frame);
		final ParticipantsPanel participantsPanel = new ParticipantsPanel(data,
				frame);
		// stagesPanel.setPreferredSize(new Dimension(150, 300));
		// participantsPanel.setPreferredSize(new Dimension(150, 300));
		final Button ok = new Button("OK");
		ok.addActionListener(createOkActionListener());
		Button cancel = new Button("Cancel");
		cancel.addActionListener(createCancelActionListener());
		final TextField name = new TextField(50);
		name.setText("Filtered");
		JLabel nameLabel = new JLabel("Name:");
		add(stagesPanel);
		add(participantsPanel);
		add(ok);

		add(cancel);
		add(nameLabel);
		add(name);

		ok.setPreferredSize(new Dimension(60, 25));
		cancel.setPreferredSize(new Dimension(60, 25));
		layout.putConstraint(SpringLayout.WEST, nameLabel, 5,
				SpringLayout.WEST, this);
		layout.putConstraint(SpringLayout.NORTH, nameLabel, 10,
				SpringLayout.NORTH, this);
		layout.putConstraint(SpringLayout.VERTICAL_CENTER, name, 0,
				SpringLayout.VERTICAL_CENTER, nameLabel);
		layout.putConstraint(SpringLayout.WEST, name, 5, SpringLayout.EAST,
				nameLabel);
		layout.putConstraint(SpringLayout.NORTH, stagesPanel, 5,
				SpringLayout.SOUTH, name);
		layout.putConstraint(SpringLayout.WEST, stagesPanel, 5,
				SpringLayout.WEST, this);
		layout.putConstraint(SpringLayout.NORTH, participantsPanel, 0,
				SpringLayout.NORTH, stagesPanel);
		layout.putConstraint(SpringLayout.WEST, participantsPanel, 5,
				SpringLayout.EAST, stagesPanel);
		layout.putConstraint(SpringLayout.EAST, participantsPanel, 5,
				SpringLayout.EAST, this);
		layout.putConstraint(SpringLayout.SOUTH, stagesPanel, 5,
				SpringLayout.NORTH, ok);
		layout.putConstraint(SpringLayout.SOUTH, participantsPanel, 0,
				SpringLayout.SOUTH, stagesPanel);

		layout.putConstraint(SpringLayout.EAST, ok, -5, SpringLayout.EAST, this);
		layout.putConstraint(SpringLayout.SOUTH, ok, -5, SpringLayout.SOUTH,
				this);
		layout.putConstraint(SpringLayout.EAST, cancel, -5, SpringLayout.WEST,
				ok);
		layout.putConstraint(SpringLayout.NORTH, cancel, 0, SpringLayout.NORTH,
				ok);
		layout.putConstraint(SpringLayout.SOUTH, cancel, 0, SpringLayout.SOUTH,
				ok);
	}

	private ActionListener createCancelActionListener() {
		return new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				cancelled = true;
				fireClosed();
			}
		};

	}

	private ActionListener createOkActionListener() {
		return new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				cancelled = false;
				fireClosed();
			}
		};
	}

	public boolean isCancelled() {
		return cancelled;
	}
}
