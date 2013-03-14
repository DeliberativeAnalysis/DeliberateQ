package au.edu.anu.delibdem.qsort.gui;

import java.awt.Component;
import java.awt.GridLayout;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.prefs.Preferences;

import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JMenu;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import moten.david.util.event.Event;
import moten.david.util.event.EventManager;
import moten.david.util.event.EventManagerListener;
import au.edu.anu.delibdem.qsort.Data;

import com.google.inject.Inject;

public class MainPanel extends JPanel {

	private static final String PREF_OPEN_STUDY_CURRENT_DIRECTORY = "open.study.current.directory";
	private static final long serialVersionUID = -4881720973156188291L;

	@Inject
	public MainPanel(EventManager eventManager) {

		setLayout(new GridLayout(1, 1));

		final JTabbedPane tabs = new JTabbedPane();
		setBackground(new JMenu().getBackground());
		this.add(tabs);
		try {

			final Component owner = this;
			eventManager.addListener(Events.OPEN, new EventManagerListener() {
				@Override
				public void notify(Event arg0) {
					try {
						Preferences prefs = java.util.prefs.Preferences
								.userNodeForPackage(this.getClass());

						// Create a file chooser
						final JFileChooser fc = new JFileChooser();
						String directoryName = prefs.get(
								PREF_OPEN_STUDY_CURRENT_DIRECTORY, null);
						if (directoryName != null)
							fc.setCurrentDirectory(new File(directoryName));
						fc.setMultiSelectionEnabled(true);
						// In response to a button click:
						if (fc.showOpenDialog(owner) == JFileChooser.APPROVE_OPTION) {
							File[] files = fc.getSelectedFiles();
							for (File file : files) {
								tabs.addTab(file.getName(), new DataPanel(
										new Data(new FileInputStream(file))));
							}
							prefs.put(PREF_OPEN_STUDY_CURRENT_DIRECTORY, fc
									.getCurrentDirectory().getAbsolutePath());
						}
					} catch (Exception e) {
						throw new Error(e);
					}
				}
			});

			eventManager.addListener(Events.OPEN_SAMPLES,
					new EventManagerListener() {
						@Override
						public void notify(Event event) {
							try {
								addSampleTabs(tabs);
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
					});

		} catch (Exception e) {
			throw new Error(e);
		}
	}

	private void addSampleTabs(JTabbedPane tabs) throws IOException {

		// addTab(tabs, new Data(getClass().getResourceAsStream(
		// "/studies/New Mexico.txt")));
		// addTab(tabs, new Data(getClass().getResourceAsStream(
		// "/studies/UBC Biobank.txt")));
		// addTab(tabs, new Data(getClass().getResourceAsStream(
		// "/studies/Fremantle.txt")));
		// addTab(tabs, new Data(getClass().getResourceAsStream(
		// "/studies/Lipset.txt")));
		addTab(tabs,
				new Data(getClass().getResourceAsStream(
						"/studies2/Bloomfield Track.txt")));
		addTab(tabs,
				new Data(getClass().getResourceAsStream(
						"/studies2/Bloomfield Track - No Prefs.txt")));
	}

	private void addTab(JTabbedPane tabs, Data data) {
		final ImageIcon icon = createImageIcon("images/graph.gif", "graphs");
		tabs.addTab(data.getTitle(), icon, new DataPanel(data));
		CloseableTabComponent buttonTab = new CloseableTabComponent(tabs, icon,
				true);
		tabs.setTabComponentAt(tabs.getTabCount() - 1, buttonTab);
	}

	/** Returns an ImageIcon, or null if the path was invalid. */
	protected ImageIcon createImageIcon(String path, String description) {
		java.net.URL imgURL = getClass().getResource(path);
		if (imgURL != null) {
			return new ImageIcon(imgURL, description);
		} else {
			System.err.println("Couldn't find file: " + path);
			return null;
		}
	}

}
