package com.github.deliberateq.qsort.gui;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JLabel;

public class LinkButton extends JLabel {

	private List<ActionListener> actionListeners = new ArrayList<ActionListener>();

	public LinkButton(String string) {
		super(string);
		setForeground(Color.blue);
		setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		addMouseListener(new MouseAdapter() {

			@Override
			public void mouseClicked(MouseEvent e) {
				fireClicked();
			}
		});
	}

	private static final long serialVersionUID = 4492350799346404635L;

	public void addActionListener(ActionListener l) {
		actionListeners.add(l);
	}

	public void removeActionListener(ActionListener l) {
		actionListeners.remove(l);
	}

	private void fireClicked() {
		for (ActionListener l : actionListeners) {
			l.actionPerformed(new ActionEvent(this, 0, null));
		}
	}

}
