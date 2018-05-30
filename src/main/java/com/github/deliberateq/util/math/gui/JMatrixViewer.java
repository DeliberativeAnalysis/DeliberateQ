package com.github.deliberateq.util.math.gui;

import java.awt.BorderLayout;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JToggleButton;
import javax.swing.SpringLayout;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.table.TableModel;

import com.github.deliberateq.qsort.gui.Events;
import com.github.deliberateq.qsort.gui.LookAndFeel;
import com.github.deliberateq.util.event.Event;
import com.github.deliberateq.util.event.EventManager;
import com.github.deliberateq.util.math.Matrix;
import com.github.deliberateq.util.math.gui.JMatrix.ColumnHeaderCheckboxListener;
import com.github.deliberateq.util.math.gui.SelectionManager.SelectionManagerEvent;

public class JMatrixViewer extends JPanel {

	private static final long serialVersionUID = -1121186696388154732L;

	private NamedMatrix namedMatrix;

	private JScrollPane scrollPane;

	private JMatrix jMatrix;

	private boolean useCheckBoxes = false;

	private final JButton copy;

	private final SpringLayout layout;

	private final JButton rotate;

	private final JButton reference;

	private final List<EventManager> eventManagers = new ArrayList<EventManager>();

	private final JToggleButton standardError1;

	private final JToggleButton standardError2;

	private final JToggleButton digits;

	public boolean getUseCheckBoxes() {
		return useCheckBoxes;
	}

	public void setUseCheckBoxes(boolean useCheckBoxes) {
		this.useCheckBoxes = useCheckBoxes;
	}

	public JMatrixViewer() {
		layout = new SpringLayout();
		setLayout(layout);
		copy = new JButton();
		copy.setToolTipText("Copy");
		copy.setMargin(new Insets(0, 0, 0, 0));
		copy.setIcon(LookAndFeel.getCopyIcon());

		add(copy);
		copy.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				boolean includeLabels = true;
				String s = jMatrix.getMatrix()
						.getDelimited("\t", includeLabels);
				Clipboard clipboard = Toolkit.getDefaultToolkit()
						.getSystemClipboard();
				clipboard.setContents(new StringSelection(s), null);
			}
		});

		rotate = new JButton();
		rotate.setMargin(new Insets(0, 0, 0, 0));
		rotate.setIcon(LookAndFeel.getRotateIcon());
		rotate.setToolTipText("Rotations");
		add(rotate);
		rotate.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				EventManager.getInstance().notify(
						new Event(jMatrix.getMatrix(), Events.ADD_ROTATION));
			}
		});

		reference = new JButton();
		reference.setToolTipText("Set as Reference");
		reference.setMargin(new Insets(0, 0, 0, 0));
		reference.setIcon(LookAndFeel.getReferenceIcon());
		add(reference);
		reference.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				fireSetReference();
			}
		});

		standardError1 = new JToggleButton();
		standardError1.setToolTipText("Paint between 1 and 2 std error");
		standardError1.setMargin(new Insets(0, 0, 0, 0));
		standardError1.setIcon(LookAndFeel.getStandardErrorIcon1());
		add(standardError1);
		standardError1.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				if (jMatrix != null) {
					jMatrix.setRange1(standardError1.isSelected(),
							getStandardError(), getStandardError() * 2);
					jMatrix.repaint();
				}
			}
		});

		standardError2 = new JToggleButton();
		standardError2.setToolTipText("Paint greater than 2 std error");
		standardError2.setMargin(new Insets(0, 0, 0, 0));
		standardError2.setIcon(LookAndFeel.getStandardErrorIcon2());
		add(standardError2);
		standardError2.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				if (jMatrix != null) {
					jMatrix.setRange2(standardError2.isSelected(),
							getStandardError() * 2, 1.1);
					jMatrix.repaint();
				}
			}
		});

		digits = new JToggleButton();
		digits.setToolTipText("Show two digits only");
		digits.setMargin(new Insets(0, 0, 0, 0));
		digits.setIcon(LookAndFeel.getDigitsIcon());
		add(digits);
		digits.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				jMatrix.setTwoDigitsOnly(digits.isSelected());
				jMatrix.repaint();
			}
		});

		SelectionManager.getInstance().addSelectionListener(
				new SelectionListener() {

					@Override
					public void selectionChanged(SelectionManagerEvent event) {
						if (event.getObject() != null
								&& event.getObject() instanceof NamedMatrix) {
							removeAll();
							namedMatrix = (NamedMatrix) event.getObject();
							setNamedMatrix(namedMatrix);
						}
					}
				});
		layout.putConstraint(SpringLayout.NORTH, rotate, 5, SpringLayout.NORTH,
				this);
		layout.putConstraint(SpringLayout.WEST, rotate, 5, SpringLayout.WEST,
				this);
		layout.putConstraint(SpringLayout.NORTH, copy, 0, SpringLayout.NORTH,
				rotate);
		layout.putConstraint(SpringLayout.WEST, copy, 20, SpringLayout.EAST,
				rotate);
		layout.putConstraint(SpringLayout.NORTH, reference, 0,
				SpringLayout.NORTH, rotate);
		layout.putConstraint(SpringLayout.WEST, reference, 20,
				SpringLayout.EAST, copy);
		layout.putConstraint(SpringLayout.NORTH, standardError1, 0,
				SpringLayout.NORTH, rotate);
		layout.putConstraint(SpringLayout.WEST, standardError1, 20,
				SpringLayout.EAST, reference);
		layout.putConstraint(SpringLayout.NORTH, standardError2, 0,
				SpringLayout.NORTH, rotate);
		layout.putConstraint(SpringLayout.WEST, standardError2, 20,
				SpringLayout.EAST, standardError1);
		layout.putConstraint(SpringLayout.NORTH, digits, 0, SpringLayout.NORTH,
				rotate);
		layout.putConstraint(SpringLayout.WEST, digits, 20, SpringLayout.EAST,
				standardError2);
	}

	protected double getStandardError() {
		if (jMatrix == null)
			return -1;
		else
			return 1 / Math.sqrt(jMatrix.getMatrix().rowCount());
	}

	public JMatrixViewer(NamedMatrix namedMatrix) {
		this();
		setNamedMatrix(namedMatrix);
	}

	public void setChecked(boolean[] checked) {
		jMatrix.setChecked(checked);
	}

	public void removeCheckBoxColumn() {
		jMatrix.removeCheckBoxColumn();
	}

	public void setNamedMatrix(NamedMatrix namedMatrix) {
		if (scrollPane != null) {
			layout.removeLayoutComponent(scrollPane);
			remove(scrollPane);
		}
		this.namedMatrix = namedMatrix;
		if (namedMatrix.getMatrix() == null) {
			jMatrix = null;
		} else {
			jMatrix = new JMatrix(namedMatrix.getMatrix(), this.useCheckBoxes);
			jMatrix.addColumnHeaderCheckboxListener(new ColumnHeaderCheckboxListener() {

				@Override
				public void selectionChanged(int columnIndex, boolean isSelected) {
					fireSelectionChanged();
				}
			});
			jMatrix.addFocusListener(new FocusAdapter() {

				@Override
				public void focusLost(FocusEvent e) {
					fireChanged(jMatrix.getModel());
				}
			});
			scrollPane = new JScrollPane(jMatrix);
			add(scrollPane, BorderLayout.CENTER);
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					ColumnResizer.adjustColumnPreferredWidths(jMatrix);
					jMatrix.revalidate();
				}
			});
			layout.putConstraint(SpringLayout.NORTH, scrollPane, 5,
					SpringLayout.SOUTH, copy);
			layout.putConstraint(SpringLayout.WEST, scrollPane, 0,
					SpringLayout.WEST, this);
			layout.putConstraint(SpringLayout.EAST, scrollPane, 0,
					SpringLayout.EAST, this);
			layout.putConstraint(SpringLayout.SOUTH, scrollPane, 0,
					SpringLayout.SOUTH, this);
		}

		if (namedMatrix.getMatrix() != null
				&& namedMatrix.getMatrix().getAbsoluteValue().getMaximum() < 1.1) {
			standardError1.setSelected(true);
			standardError2.setSelected(true);
		}
		digits.setSelected(false);
		validate();
		fireSelectionChanged();
	}

	protected void fireChanged(TableModel model) {
		Event event = new Event(model, Events.TABLE_CHANGED);
		for (EventManager em : eventManagers) {
			em.notify(event);
		}
	}

	private void fireSelectionChanged() {
		SelectionManager.getInstance().selectionChanged(
				new SelectionManagerEvent() {

					@Override
					public String getDescription() {
						return "Checkbox changed";
					}

					@Override
					public Object getObject() {
						return jMatrix;
					}
				});

	}

	public Matrix getMatrix() {
		return namedMatrix.getMatrix();
	}

	public void addEventManager(EventManager em) {
		eventManagers.add(em);
	}

	public void removeEventManager(EventManager em) {
		eventManagers.remove(em);
	}

	public void fireSetReference() {
		Event event = new Event(null, Events.SET_REFERENCE_REQUESTED);
		for (EventManager em : eventManagers) {
			em.notify(event);
		}
	}

}
