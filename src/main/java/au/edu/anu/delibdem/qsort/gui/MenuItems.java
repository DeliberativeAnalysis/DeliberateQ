package au.edu.anu.delibdem.qsort.gui;

import javax.swing.JMenuItem;

public class MenuItems {

	public static JMenuItem getExit() {
		JMenuItem m = new JMenuItem("Exit");
		m.addActionListener(ActionListeners
				.getNotifyActionListener(Events.APPLICATION_EXIT));
		return m;
	}

	public static JMenuItem getOpen() {
		JMenuItem m = new JMenuItem("Open Study...");
		m.addActionListener(ActionListeners
				.getNotifyActionListener(Events.OPEN));
		return m;
	}

	public static JMenuItem getOpenSamples() {
		JMenuItem m = new JMenuItem("Open Sample Studies");
		m.addActionListener(ActionListeners
				.getNotifyActionListener(Events.OPEN_SAMPLES));
		return m;
	}

	public static JMenuItem getPreferences() {
		JMenuItem m = new JMenuItem("Preferences...");
		m.addActionListener(ActionListeners
				.getNotifyActionListener(Events.PREFERENCES));
		return m;
	}

}