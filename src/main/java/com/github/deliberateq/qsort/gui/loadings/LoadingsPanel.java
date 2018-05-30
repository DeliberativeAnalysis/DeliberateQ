package com.github.deliberateq.qsort.gui.loadings;

import java.awt.Dimension;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.prefs.Preferences;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JTextPane;
import javax.swing.SpringLayout;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.filechooser.FileFilter;

import com.github.deliberateq.qsort.gui.Events;
import com.github.deliberateq.qsort.gui.LookAndFeel;
import com.github.deliberateq.qsort.gui.Model;
import com.github.deliberateq.qsort.gui.ReferencePanel;
import com.github.deliberateq.qsort.gui.Rotations;
import com.github.deliberateq.util.event.Event;
import com.github.deliberateq.util.event.EventManager;
import com.github.deliberateq.util.event.EventManagerListener;
import com.github.deliberateq.util.math.Matrix;
import com.github.deliberateq.util.math.MatrixRotation;
import com.github.deliberateq.util.math.StringFilter;
import com.github.deliberateq.util.math.Varimax.RotationMethod;

public class LoadingsPanel extends JPanel {

	private static final String ROTATIONS_EXTENSION = ".rotations";

	private static final String OPEN_ROTATIONS_DIRECTORY = "open.rotations.directory";

	private static final long serialVersionUID = -3365081719446429950L;

	private final Rotations rotations;

	public LoadingsPanel(Rotations rots, StringFilter rowLabelsFilter) {
		this.rotations = rots;
		SpringLayout layout = new SpringLayout();
		setLayout(layout);
		// LoadingsSelectorPanel selectorPanel = new LoadingsSelectorPanel(
		// loadings);
		// add(selectorPanel);
		final LoadingsGraphsPanel graphs = new LoadingsGraphsPanel(this);

		graphs.setRotations(rotations);

		EventManager.getInstance().addListener(Events.DATA_CHANGED,
				new EventManagerListener() {
					@Override
					public void notify(Event arg0) {
						graphs.setRotations(rotations);
					}
				});

		JScrollPane scroll = new JScrollPane(graphs);
		scroll.setBorder(BorderFactory.createEmptyBorder());
		add(scroll);

		JPanel controls = new JPanel();
		SpringLayout layoutControls = new SpringLayout();
		controls.setLayout(layoutControls);
		controls.setPreferredSize(new Dimension(0, 50));
		add(controls);
		final JButton rotate = new JButton();
		rotate.setToolTipText("Rotate with method");
		rotate.setIcon(LookAndFeel.getRotationMethodIcon());
		rotate.setMargin(new Insets(0, 0, 0, 0));
		controls.add(rotate);
		final JCheckBox showLabels = new JCheckBox("Show labels");
		controls.add(showLabels);
		final JTextPane rotationSummary = new JTextPane();
		final JScrollPane rotationSummaryScroll = new JScrollPane(
				rotationSummary);
		rotationSummaryScroll.setBorder(BorderFactory.createEmptyBorder());
		rotationSummaryScroll.setPreferredSize(new Dimension(130, 0));
		rotationSummary.setText(rotations.getSummary());
		controls.add(rotationSummaryScroll);
		final ReferencePanel referencePanel = new ReferencePanel(this);
		controls.add(referencePanel);
		referencePanel.update(rotations);
		JCheckBox applyFilter = new JCheckBox("Filter");
		controls.add(applyFilter);
		applyFilter.setSelected(false);
		graphs.setRowLabelFilter(null);
		applyFilter.addActionListener(createApplyFilterActionListener(
				applyFilter, graphs, rowLabelsFilter));

		final JSlider slider = new JSlider(0, 100);
		controls.add(slider);
		slider.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				graphs.resize((slider.getValue()) * (slider.getValue()) / 8);
				graphs.invalidate();
				validate();
			}
		});
		slider.setValue(50);

		graphs.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				rotationSummary.setText(rotations.getSummary());
				referencePanel.update(rotations);
			}
		});

		final JPopupMenu popup = new JPopupMenu();
		for (RotationMethod r : RotationMethod.values()) {
			JMenuItem item = new JMenuItem(r.toString());
			final RotationMethod rotationMethod = r;
			item.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					rotations.setRotationMethod(rotationMethod);
					graphs.setRotations(rotations);
				}

			});
			popup.add(item);
		}
		JMenuItem item = new JMenuItem("Clear rotations");
		item.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				rotations.setRotationMethod(null);
				graphs.setRotations(rotations);
			}

		});
		popup.add(item);

		item = new JMenuItem("Rotate to Reference (Pairwise)");
		item.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				Thread t = new Thread(new Runnable() {
					@Override
					public void run() {
						if (Model.getInstance().getReferenceMatrix() == null)
							return;
						EventManager
								.getInstance()
								.notify(new Event(
										"Rotating to reference (Dave's Pairwise)...",
										Events.STATUS));
						rotations.clearRotations();
						Matrix m = rotations.getLoadingsToUseWithReference();
						Matrix r = Model.getInstance().getReferenceMatrix()
								.restrictRows(rotations.getUseWithReference());
						m = m.restrictRows(r);
						r = r.restrictRows(m);
						List<MatrixRotation> list = m.getRotationsTo(r);
						rotations.setRotationMethod(null);
						rotations.addRotations(list);
						graphs.setRotations(rotations);
						EventManager.getInstance().notify(
								new Event(null, Events.STATUS_FINISHED));
					}
				});
				t.start();
			}

		});
		popup.add(item);

		rotate.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				popup.show(rotate, 0, rotate.getHeight());
			}
		});

		showLabels.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent arg0) {
				rotations.setDisplayLabels(showLabels.isSelected());
				graphs.setDisplayLabels(showLabels.isSelected());
				graphs.repaint();

			}
		});
		showLabels.setSelected(rotations.isDisplayLabels());

		JButton saveRotations = new JButton("Save...");
		Insets inset = new Insets(2, 0, 2, 0);
		saveRotations.setMargin(inset);
		saveRotations.setToolTipText("Save rotations...");
		JButton loadRotations = new JButton("Load...");
		loadRotations.setMargin(inset);
		loadRotations.setToolTipText("Load rotations...");
		final JPanel owner = this;
		saveRotations.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				Preferences prefs = java.util.prefs.Preferences
						.userNodeForPackage(this.getClass());
				// Create a file chooser
				final JFileChooser fc = new JFileChooser();
				String directoryName = prefs
						.get(OPEN_ROTATIONS_DIRECTORY, null);
				if (directoryName != null)
					fc.setCurrentDirectory(new File(directoryName));
				fc.setMultiSelectionEnabled(false);
				fc.addChoosableFileFilter(new FileFilter() {
					@Override
					public boolean accept(File file) {
						return file.getName().endsWith(ROTATIONS_EXTENSION);
					}

					@Override
					public String getDescription() {
						return "AdvanceQ Rotations (*" + ROTATIONS_EXTENSION + ")";
					}
				});
				// In response to a button click:
				if (fc.showSaveDialog(owner) == JFileChooser.APPROVE_OPTION) {
					File file = fc.getSelectedFile();
					if (!file.getName().contains("."))
						file = new File(file.getPath() + ROTATIONS_EXTENSION);
					// load the object
					FileOutputStream fos;
					try {
						fos = new FileOutputStream(file);
						rotations.save(fos);
						fos.close();
					} catch (FileNotFoundException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					}
					prefs.put(OPEN_ROTATIONS_DIRECTORY, fc
							.getCurrentDirectory().getAbsolutePath());
				}
			}
		});

		loadRotations.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				Preferences prefs = java.util.prefs.Preferences
						.userNodeForPackage(this.getClass());
				// Create a file chooser
				final JFileChooser fc = new JFileChooser();
				String directoryName = prefs
						.get(OPEN_ROTATIONS_DIRECTORY, null);
				if (directoryName != null)
					fc.setCurrentDirectory(new File(directoryName));
				fc.setMultiSelectionEnabled(false);
				fc.addChoosableFileFilter(new FileFilter() {
					@Override
					public boolean accept(File file) {
						return file.getName().endsWith(ROTATIONS_EXTENSION);
					}

					@Override
					public String getDescription() {
						return "AdvanceQ Rotations (*" + ROTATIONS_EXTENSION + ")";
					}
				});

				// In response to a button click:
				if (fc.showOpenDialog(owner) == JFileChooser.APPROVE_OPTION) {
					File file = fc.getSelectedFile();
					// load the object
					FileInputStream fis;
					try {
						fis = new FileInputStream(file);
						Rotations r = Rotations.load(fis);
						rotations.clearRotations();
						rotations.setRotationMethod(r.getRotationMethod());
						rotations.addRotations(r.getRotations());
						graphs.setRotations(rotations);
						fis.close();
					} catch (IOException e) {
						e.printStackTrace();
					} catch (ClassNotFoundException e) {
						e.printStackTrace();
					}
					prefs.put(OPEN_ROTATIONS_DIRECTORY, fc
							.getCurrentDirectory().getAbsolutePath());
				}
			}
		});

		controls.add(saveRotations);
		controls.add(loadRotations);

		// layout
		layoutControls.putConstraint(SpringLayout.NORTH, rotate, 0,
				SpringLayout.NORTH, controls);
		layoutControls.putConstraint(SpringLayout.WEST, rotate, 0,
				SpringLayout.WEST, controls);
		layoutControls.putConstraint(SpringLayout.VERTICAL_CENTER, showLabels,
				0, SpringLayout.VERTICAL_CENTER, rotate);
		layoutControls.putConstraint(SpringLayout.WEST, showLabels, 20,
				SpringLayout.EAST, rotate);
		layoutControls.putConstraint(SpringLayout.WEST, rotationSummaryScroll,
				20, SpringLayout.EAST, referencePanel);
		layoutControls.putConstraint(SpringLayout.NORTH, rotationSummaryScroll,
				0, SpringLayout.NORTH, controls);
		layoutControls.putConstraint(SpringLayout.SOUTH, rotationSummaryScroll,
				0, SpringLayout.SOUTH, controls);
		layoutControls.putConstraint(SpringLayout.NORTH, referencePanel, 0,
				SpringLayout.NORTH, controls);
		layoutControls.putConstraint(SpringLayout.SOUTH, referencePanel, 0,
				SpringLayout.SOUTH, controls);
		layoutControls.putConstraint(SpringLayout.WEST, referencePanel, 20,
				SpringLayout.EAST, showLabels);
		layoutControls.putConstraint(SpringLayout.WEST, slider, 0,
				SpringLayout.WEST, controls);
		layoutControls.putConstraint(SpringLayout.NORTH, slider, 4,
				SpringLayout.SOUTH, showLabels);
		layoutControls.putConstraint(SpringLayout.EAST, slider, 0,
				SpringLayout.EAST, showLabels);
		layoutControls.putConstraint(SpringLayout.NORTH, saveRotations, 0,
				SpringLayout.NORTH, rotationSummaryScroll);
		layoutControls.putConstraint(SpringLayout.WEST, saveRotations, 20,
				SpringLayout.EAST, rotationSummaryScroll);
		layoutControls.putConstraint(SpringLayout.NORTH, loadRotations, 0,
				SpringLayout.NORTH, rotationSummaryScroll);
		layoutControls.putConstraint(SpringLayout.WEST, loadRotations, 20,
				SpringLayout.EAST, saveRotations);
		layoutControls.putConstraint(SpringLayout.NORTH, applyFilter, 0,
				SpringLayout.NORTH, rotationSummaryScroll);
		layoutControls.putConstraint(SpringLayout.WEST, applyFilter, 10,
				SpringLayout.EAST, loadRotations);

		layout.putConstraint(SpringLayout.NORTH, controls, 5,
				SpringLayout.NORTH, this);
		layout.putConstraint(SpringLayout.WEST, controls, 10,
				SpringLayout.WEST, this);
		layout.putConstraint(SpringLayout.EAST, controls, 0, SpringLayout.EAST,
				this);
		layout.putConstraint(SpringLayout.NORTH, scroll, 10,
				SpringLayout.SOUTH, controls);
		layout.putConstraint(SpringLayout.SOUTH, scroll, 0, SpringLayout.SOUTH,
				this);
		layout.putConstraint(SpringLayout.WEST, scroll, 0, SpringLayout.WEST,
				this);
		layout.putConstraint(SpringLayout.EAST, scroll, 0, SpringLayout.EAST,
				this);

	}

	private ActionListener createApplyFilterActionListener(
			final JCheckBox applyFilter, final LoadingsGraphsPanel graphs,
			final StringFilter rowLabelsFilter) {
		return new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if (applyFilter.isSelected())
					graphs.setRowLabelFilter(rowLabelsFilter);
				else
					graphs.setRowLabelFilter(null);
				graphs.redraw();
			}
		};
	}
}
