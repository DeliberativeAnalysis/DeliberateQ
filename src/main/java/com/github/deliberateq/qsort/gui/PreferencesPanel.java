package com.github.deliberateq.qsort.gui;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SpringLayout;
import javax.swing.SwingConstants;

import com.github.deliberateq.util.event.Event;
import com.github.deliberateq.util.event.EventManager;
import com.github.deliberateq.util.event.EventManagerListener;
import com.github.deliberateq.util.event.EventType;

public class PreferencesPanel extends JPanel {

	private static final long serialVersionUID = -8693306014917500875L;

	List<EventManagerListener> eventManagerListeners = new ArrayList<EventManagerListener>();

	private final JButton close;

	public void addEventManagerListener(EventManagerListener em) {
		eventManagerListeners.add(em);
	}

	public void removeEventManagerListener(EventManagerListener em) {
		eventManagerListeners.remove(em);
	}

	public static EventType closeEvent = new EventType() {
	};

	public PreferencesPanel(final Preferences preferences) {
		SpringLayout layout = new SpringLayout();
		setLayout(layout);

		int rows = 0;
		final String[][] keys = {
				{ Preferences.EIGENVALUE_THRESHOLD,
						Preferences.EIGENVALUE_THRESHOLD_DEFAULT },
				{ Preferences.VENN_MAX_STANDARD_ERRORS, "3.0" },
				{ Preferences.MAX_PRINCIPAL_FACTORS,
						Preferences.MAX_PRINCIPAL_FACTORS_DEFAULT } };
		for (String[] o : keys) {
			final String key = o[0];
			final String defaultValue = o[1];
			JLabel label = new JLabel(key);
			final JTextField field = new JTextField(preferences.getProperty(
					key, defaultValue));
			field.setHorizontalAlignment(SwingConstants.RIGHT);
			field.addKeyListener(new KeyAdapter() {
				@Override
				public void keyTyped(KeyEvent e) {
					if (e.getKeyChar() == KeyEvent.VK_ENTER) {
						close.doClick();
					}
				}

			});
			field.addFocusListener(new FocusAdapter() {

				@Override
				public void focusGained(FocusEvent arg0) {
					field.setText(preferences.getProperty(key, defaultValue));
					field.setSelectionStart(0);
					field.setSelectionEnd(field.getText().length());
				}

				@Override
				public void focusLost(FocusEvent e) {
					preferences.setProperty(key, field.getText());
				}
			});
			add(label);
			add(field);
			rows++;
		}

		final JCheckBox systemLookAndFeel = new JCheckBox(
				"System Look and Feel");
		systemLookAndFeel.setSelected(Preferences.getInstance()
				.isSystemLookAndFeel());
		systemLookAndFeel.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				JOptionPane
						.showMessageDialog(PreferencesPanel.this,
								"You will need to restart DeliberateQ for this to take effect");
				Preferences.getInstance().setSystemLookAndFeel(
						systemLookAndFeel.isSelected());
				EventManager.getInstance().notify(
						new Event(null, Events.UPDATE_LOOK_AND_FEEL));
			}
		});
		add(systemLookAndFeel);
		add(new JLabel(" "));
		rows++;

		add(new JLabel(" "));
		close = new JButton("Close");
		close.setDefaultCapable(true);
		add(close);

		close.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				fireClosed();
			}
		});
		rows++;

		int cols = 2;
		SpringUtilities.makeCompactGrid(this, // parent
				rows, cols, 3, 3, // initX, initY
				3, 3); // xPad, yPad
		setPreferredSize(new Dimension(220, 25 * rows
				+ close.getPreferredSize().height));
	}

	private void fireClosed() {
		for (EventManagerListener eml : eventManagerListeners) {
			eml.notify(new Event(this, closeEvent));
		}
	}

}
