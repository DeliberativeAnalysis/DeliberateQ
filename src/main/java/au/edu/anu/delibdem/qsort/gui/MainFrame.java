package au.edu.anu.delibdem.qsort.gui;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.util.Stack;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.UnsupportedLookAndFeelException;

import moten.david.util.event.Event;
import moten.david.util.event.EventManager;
import moten.david.util.event.EventManagerListener;
import moten.david.util.gui.swing.v1.SwingUtil;
import moten.david.util.math.Matrix;
import moten.david.util.math.MatrixProvider;
import moten.david.util.math.gui.JMatrix;
import au.edu.anu.delibdem.qsort.Data;
import au.edu.anu.delibdem.qsort.gui.injection.ApplicationInjector;

import com.google.inject.Inject;

public class MainFrame extends JFrame {

	private static final long serialVersionUID = 7824719192824923010L;
	private final EventManager eventManager;

	@Inject
	public MainFrame(MainPanel mainPanel, EventManager eventManager) {
		this.eventManager = eventManager;
		setSize(800, 600);

		getContentPane().setLayout(new BorderLayout());

		createMenuBar();

		getContentPane().add(mainPanel, BorderLayout.CENTER);

		createStatusBar();

		createExitListener();

		createMoreListeners();

		createOpenObjectListener();

		createSetReferenceListener();

		createEditPreferencesListener();

		createLookAndFeelListener();

		setIcon();
	}

	private void createLookAndFeelListener() {
		eventManager.addListener(Events.UPDATE_LOOK_AND_FEEL,
				new EventManagerListener() {
					@Override
					public void notify(Event event) {
						LookAndFeel.setLookAndFeel();
					}
				});
	}

	private void createEditPreferencesListener() {
		eventManager.addListener(Events.PREFERENCES,
				new EventManagerListener() {
					@Override
					public void notify(Event event) {
						JDialog dialog = PreferencesDialog.getInstance();
						SwingUtil.centre(dialog);
						dialog.setVisible(true);
					}
				});
	}

	private void createSetReferenceListener() {
		eventManager.addListener(Events.SET_REFERENCE,
				new EventManagerListener() {
					@Override
					public void notify(Event event) {
						Model.getInstance().setReference(
								(MatrixProvider) event.getObject());
						eventManager.notify(new Event(event.getObject(),
								Events.REFERENCE_SET));
					}
				});
	}

	private void createOpenObjectListener() {
		final JFrame frame = this;
		eventManager.addListener(Events.OPEN_OBJECT,
				new EventManagerListener() {
					@Override
					public void notify(Event event) {
						JDialog dialog = new JDialog(frame);
						dialog.setIconImage(LookAndFeel.getPrimaryIcon()
								.getImage());
						dialog.setTitle("Viewer");
						dialog.setSize(500, 500);
						JPanel panel = new JPanel();
						panel.setLayout(new GridLayout(1, 1));
						if (event.getObject() instanceof Matrix)
							panel.add(new JMatrix((Matrix) event.getObject(),
									true));
						dialog.add(panel);
						dialog.setModal(false);
						SwingUtil.centre(dialog);
						dialog.setVisible(true);
					}
				});
	}

	private void setIcon() {
		setIconImage(LookAndFeel.getPrimaryIcon().getImage());
	}

	private void createExitListener() {
		eventManager.addListener(Events.APPLICATION_EXIT,
				new EventManagerListener() {

					@Override
					public void notify(Event arg0) {
						System.exit(0);
					}
				});
	}

	private void createMenuBar() {
		setJMenuBar(MenuBars.getMain());
	}

	private void createStatusBar() {
		final JLabel status = new JLabel(" ");
		final Stack<String> messages = new Stack<String>();
		getContentPane().add(status, BorderLayout.PAGE_END);
		eventManager.addListener(Events.STATUS, new EventManagerListener() {
			@Override
			public void notify(Event event) {
				String message = (String) event.getObject();
				messages.push(message);
				status.setText(message);
			}
		});
		eventManager.addListener(Events.STATUS_FINISHED,
				new EventManagerListener() {
					@Override
					public void notify(Event event) {
						messages.pop();
						if (messages.size() > 0)
							status.setText(messages.lastElement());
						else
							status.setText(" ");
					}
				});

	}

	private void createMoreListeners() {
		final JFrame frame = MainFrame.this;
		eventManager.addListener(Events.FILTER, new EventManagerListener() {
			@Override
			public void notify(Event event) {

				JDialog dialog = new JDialog(frame);
				dialog.setIconImage(LookAndFeel.getPersonIcon().getImage());
				dialog.setTitle("Participants");

				dialog.getContentPane().setLayout(new GridLayout(1, 1));
				JPanel panel = new ParticipantsPanel((Data) event.getObject(),
						frame);
				dialog.getContentPane().add(panel);
				dialog.setSize(panel.getPreferredSize());
				dialog.setModal(false);
				int x = frame.getLocation().x + frame.getWidth() - 2
						* dialog.getWidth() - 50;
				dialog.setLocation(x, frame.getLocation().y + 150);
				dialog.setVisible(true);
			}
		});

		eventManager.addListener(Events.NEW_DATA_COMBINATION,
				new EventManagerListener() {
					@Override
					public void notify(Event event) {
						Data data = (Data) event.getObject();
						final JDialog dialog = new JDialog(frame);
						dialog.setIconImage(LookAndFeel.getPrimaryIcon()
								.getImage());
						dialog.setTitle("New Data Selection");

						DataSelectionPanel panel = new DataSelectionPanel(data,
								frame);
						panel.addDataSelectionPanelListener(new DataSelectionPanel.Listener() {
							@Override
							public void closed() {
								dialog.setVisible(false);
							}
						});
						dialog.getContentPane().setLayout(new GridLayout(1, 1));
						dialog.getContentPane().add(panel);
						dialog.setSize(400, frame.getHeight() * 2 / 3);
						SwingUtil.centre(dialog);
						dialog.setModal(true);
						dialog.setVisible(true);
						dialog.dispose();
					}
				});

	}

	public static void main(String[] args) throws ClassNotFoundException,
			InstantiationException, IllegalAccessException,
			UnsupportedLookAndFeelException {
		LookAndFeel.setLookAndFeel();
		final MainFrame frame = ApplicationInjector.getInjector().getInstance(
				MainFrame.class);
		final EventManager eventManager = ApplicationInjector.getInjector()
				.getInstance(EventManager.class);
		frame.setTitle("AdvanceQ");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				SwingUtil.centre(frame);
				frame.setVisible(true);
				if ("true".equals(System.getProperty("openSamples")))
					eventManager.notify(new Event(null, Events.OPEN_SAMPLES));
			}
		});

	}
}
