package com.github.deliberateq.qsort.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.io.IOException;
import java.util.Properties;
import java.util.Stack;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.UnsupportedLookAndFeelException;

import com.github.deliberateq.qsort.Data;
import com.github.deliberateq.qsort.gui.injection.ApplicationInjector;
import com.github.deliberateq.util.event.Event;
import com.github.deliberateq.util.event.EventManager;
import com.github.deliberateq.util.event.EventManagerListener;
import com.github.deliberateq.util.gui.swing.v1.SwingUtil;
import com.github.deliberateq.util.math.Matrix;
import com.github.deliberateq.util.math.MatrixProvider;
import com.github.deliberateq.util.math.gui.JMatrix;
import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
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
        eventManager.addListener(Events.UPDATE_LOOK_AND_FEEL, new EventManagerListener() {
            @Override
            public void notify(Event event) {
                SwingUtilities.invokeLater(() -> 
                LookAndFeel.setLookAndFeel()
                );
            }
        });
    }

    private void createEditPreferencesListener() {
        eventManager.addListener(Events.PREFERENCES, new EventManagerListener() {
            @Override
            public void notify(Event event) {
                SwingUtilities.invokeLater(() -> {
                    JDialog dialog = PreferencesDialog.getInstance();
                    SwingUtil.centre(dialog);
                    dialog.setVisible(true);
                });
            }
        });
    }

    private void createSetReferenceListener() {
        eventManager.addListener(Events.SET_REFERENCE, new EventManagerListener() {
            @Override
            public void notify(Event event) {
                Model.getInstance().setReference((MatrixProvider) event.getObject());
                eventManager.notify(new Event(event.getObject(), Events.REFERENCE_SET));
            }
        });
    }

    private void createOpenObjectListener() {
        final JFrame frame = this;
        eventManager.addListener(Events.OPEN_OBJECT, new EventManagerListener() {
            @Override
            public void notify(Event event) {
                JDialog dialog = new JDialog(frame);
                dialog.setIconImage(LookAndFeel.getPrimaryIcon().getImage());
                dialog.setTitle("Viewer");
                dialog.setSize(500, 500);
                JPanel panel = new JPanel();
                panel.setLayout(new GridLayout(1, 1));
                if (event.getObject() instanceof Matrix)
                    panel.add(new JMatrix((Matrix) event.getObject(), true));
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
        eventManager.addListener(Events.APPLICATION_EXIT, new EventManagerListener() {

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
        eventManager.addListener(Events.STATUS_FINISHED, new EventManagerListener() {
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
        final JFrame frame = this;
        eventManager.addListener(Events.PARTICIPANT_FILTER, new EventManagerListener() {
            @Override
            public void notify(Event event) {

                JDialog dialog = new JDialog(frame);
                dialog.setIconImage(LookAndFeel.getPersonIcon().getImage());
                dialog.setTitle("Participants");

                dialog.getContentPane().setLayout(new GridLayout(1, 1));
                JPanel panel = new ParticipantsPanel((Data) event.getObject(), frame);
                dialog.getContentPane().add(panel);
                dialog.setSize(panel.getPreferredSize());
                dialog.setModal(false);
                int x = frame.getLocation().x + frame.getWidth() - 2 * dialog.getWidth() - 50;
                dialog.setLocation(x, frame.getLocation().y + 150);
                dialog.setVisible(true);
            }
        });
        
        eventManager.addListener(Events.STATEMENT_FILTER, new EventManagerListener() {
            @Override
            public void notify(Event event) {

                JDialog dialog = new JDialog(frame);
                dialog.setIconImage(LookAndFeel.getPersonIcon().getImage());
                dialog.setTitle("Q Statements");

                dialog.getContentPane().setLayout(new GridLayout(1, 1));
                JPanel panel = new StatementsPanel((Data) event.getObject(), frame);
//                panel.setPreferredSize(new Dimension(frame.getWidth() -10, frame.getHeight() - 50));
                dialog.getContentPane().add(panel);
                dialog.setSize(panel.getPreferredSize());
                dialog.setModal(false);
                int x = frame.getLocation().x + frame.getWidth() - 2 * dialog.getWidth() - 50;
                dialog.setLocation(x, frame.getLocation().y + 150);
                dialog.setVisible(true);
            }
        });

        eventManager.addListener(Events.NEW_DATA_COMBINATION, new EventManagerListener() {
            @Override
            public void notify(Event event) {
                Data data = (Data) event.getObject();
                final JDialog dialog = new JDialog(frame);
                dialog.setIconImage(LookAndFeel.getPrimaryIcon().getImage());
                dialog.setTitle("New Data Selection");

                DataSelectionPanel panel = new DataSelectionPanel(data, frame);
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

    public static void main(String[] args) throws ClassNotFoundException, InstantiationException,
            IllegalAccessException, UnsupportedLookAndFeelException, IOException {
        LookAndFeel.setLookAndFeel();
        final MainFrame frame = ApplicationInjector.getInjector().getInstance(MainFrame.class);
        final EventManager eventManager = ApplicationInjector.getInjector().getInstance(EventManager.class);
        Properties p = new Properties();
        p.load(MainFrame.class.getResourceAsStream("/version.properties"));
        String version = p.getProperty("version", "?");
        frame.setTitle("DeliberateQ " + version);
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
