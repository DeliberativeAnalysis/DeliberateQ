package com.github.deliberateq.qsort.gui;

import java.awt.Dimension;
import java.awt.GridLayout;

import javax.swing.JDialog;

import com.github.deliberateq.util.event.Event;
import com.github.deliberateq.util.event.EventManagerListener;

public class PreferencesDialog extends JDialog {

	private static PreferencesDialog instance;

	public synchronized static PreferencesDialog getInstance() {
		if (instance == null)
			instance = new PreferencesDialog();
		return instance;
	}

	private static final long serialVersionUID = -7263342124145871688L;

	public PreferencesDialog() {
		setTitle("Preferences");
		getContentPane().setLayout(new GridLayout(1, 1));
		PreferencesPanel preferencesPanel = new PreferencesPanel(
				Preferences.getInstance());
		getContentPane().add(preferencesPanel);
		Dimension d = preferencesPanel.getPreferredSize();
		d.setSize(d.getWidth(), d.getHeight() + 35);
		setSize(d);
		EventManagerListener eml = new EventManagerListener() {
			@Override
			public void notify(Event event) {
				if (event.getType().equals(PreferencesPanel.closeEvent))
					setVisible(false);
			}
		};
		preferencesPanel.addEventManagerListener(eml);
		setAlwaysOnTop(true);
	}

}
