package com.github.deliberateq.util.math.gui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

import com.github.deliberateq.util.math.Matrix;
import com.github.deliberateq.util.math.Vector;

public class JMatrix extends JTable {

	private static final long serialVersionUID = 5032757769182987645L;

	private Matrix matrix;

	private boolean[] checked;

	private List<ColumnHeaderCheckboxListener> listeners = new ArrayList<ColumnHeaderCheckboxListener>();

	private boolean range1;

	private double range1start;

	private double range1finish;

	private boolean range2;

	private double range2start;

	private double range2finish;

	private boolean twoDigitsOnly;

	public void addColumnHeaderCheckboxListener(ColumnHeaderCheckboxListener l) {
		listeners.add(l);
	}

	public void removeColumnHeaderCheckboxListener(
			ColumnHeaderCheckboxListener l) {
		listeners.remove(l);
	}

	private void fireCheckBoxChanged(int columnIndex, boolean isSelected) {
		for (ColumnHeaderCheckboxListener l : listeners) {
			l.selectionChanged(columnIndex, isSelected);
		}
	}

	private Vector getColumnVector(int displayedColumnIndex) {
		return matrix.getColumnVector(displayedColumnIndex);
	}

	public List<Vector> getColumnSelections() {
		List<Vector> vectors = new ArrayList<Vector>();
		Enumeration<TableColumn> enumeration = getColumnModel().getColumns();
		while (enumeration.hasMoreElements()) {
			TableColumn tableColumn = enumeration.nextElement();
			if (tableColumn.getHeaderRenderer() instanceof CheckBoxHeader // 
			        && ((CheckBoxHeader) tableColumn.getHeaderRenderer())
						.isSelected()) {
				vectors.add(getColumnVector(tableColumn.getModelIndex()));
			}
		}
		return vectors;
	}

	public JMatrix(Matrix matrix, boolean useColumnHeaderCheckBoxes) {
		super();
		this.matrix = matrix;
		this.checked = new boolean[matrix.rowCount()];
		for (int i = 0; i < checked.length; i++)
			checked[i] = true;
		setModel(getTableModel(matrix));
		if (useColumnHeaderCheckBoxes) {
			Enumeration<TableColumn> enumeration = getColumnModel()
					.getColumns();
			int count = 0;
			while (enumeration.hasMoreElements()) {
				TableColumn aColumn = enumeration.nextElement();
				if (aColumn.getModelIndex() != 0) {
					CheckBoxHeader checkBox = new CheckBoxHeader(
							new ItemListener() {
								@Override
								public void itemStateChanged(ItemEvent e) {
									int column = ((CheckBoxHeader) (e.getItem()))
											.getColumn();
									fireCheckBoxChanged(
											column,
											e.getStateChange() == ItemEvent.SELECTED);
								}
							});
					checkBox.setHorizontalAlignment(SwingConstants.CENTER);
					count++;
					aColumn.setHeaderRenderer(checkBox);
					if (count <= 2)
						checkBox.setSelected(true);
				}
			}
		}
		setDefaultRenderer(Double.class, new DoubleRenderer());
		addMouseListener(createMouseListener());
		setRowSorter(new TableRowSorter(getModel()));
	}

	private class DoubleRenderer extends DefaultTableCellRenderer {

		private DecimalFormat df = new DecimalFormat("0.0000");

		public DoubleRenderer() {
			super();
		}

		@Override
		protected void setValue(Object value) {
			Double d = (Double) value;
			if (!twoDigitsOnly)
				setText(df.format(d));
			else {
				long num = Math.round(d * 100);
				setText(new DecimalFormat("00").format(num));
			}
			setHorizontalAlignment(SwingConstants.RIGHT);
			Color defaultColor = UIManager.getDefaults().getColor(
					"TextArea.background");
			setBackground(defaultColor);
			if (range1 && Math.abs(d) >= range1start
					&& Math.abs(d) <= range1finish)
				setBackground(new Color(152, 251, 152));
			if (range2 && Math.abs(d) >= range2start
					&& Math.abs(d) <= range2finish)
				setBackground(new Color(255, 192, 203));
		}
	}

	private MouseListener createMouseListener() {

		final Component thisComponent = this;
		return new MouseAdapter() {

			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getButton() == MouseEvent.BUTTON3) {
					JPopupMenu menu = new JPopupMenu();
					JMenuItem item = new JMenuItem("Copy matrix tab delimited");
					menu.add(item);
					item.addActionListener(new ActionListener() {
						@Override
						public void actionPerformed(ActionEvent e) {
							boolean includeLabels = true;
							String s = matrix.getDelimited("\t", includeLabels);
							Clipboard clipboard = Toolkit.getDefaultToolkit()
									.getSystemClipboard();
							clipboard.setContents(new StringSelection(s), null);
						}
					});
					menu.show(thisComponent, e.getX(), e.getY());
				}
			}

		};
	}

	public interface ColumnHeaderCheckboxListener {
		void selectionChanged(int columnIndex, boolean isSelected);
	}

	private TableModel getTableModel(final Matrix matrix) {
		TableModel model = new TableModel() {

			private List<TableModelListener> listeners = new ArrayList<TableModelListener>();

			@Override
			public void addTableModelListener(TableModelListener l) {
				listeners.add(l);
			}

			@Override
			public Class<?> getColumnClass(int columnIndex) {
				if (columnIndex == 0)
					return String.class;
				else if (columnIndex == 1)
					return Boolean.class;
				else
					return Double.class;
			}

			@Override
			public int getColumnCount() {
				return matrix.columnCount() + 2;
			}

			@Override
			public String getColumnName(int columnIndex) {
				if (columnIndex == 0)
					return "";
				else if (columnIndex == 1)
					return "";
				else if (matrix.getColumnLabels() != null) {
					return matrix.getColumnLabels()[columnIndex - 2];
				} else
					return columnIndex + "";
			}

			@Override
			public int getRowCount() {
				return matrix.rowCount();
			}

			@Override
			public Object getValueAt(int rowIndex, int columnIndex) {
				if (columnIndex == 0) {
					if (matrix.getRowLabels() != null)
						return matrix.getRowLabels()[rowIndex];
					else
						return (rowIndex + 1) + "";
				} else if (columnIndex == 1)
					return checked[rowIndex];
				else
					return matrix.getValue(rowIndex + 1, columnIndex - 1);
			}

			@Override
			public boolean isCellEditable(int rowIndex, int columnIndex) {
				return columnIndex == 1;
			}

			@Override
			public void removeTableModelListener(TableModelListener l) {
				listeners.remove(l);
			}

			@Override
			public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
				if (columnIndex == 1)
					checked[rowIndex] = (Boolean) aValue;
			}

		};
		return model;
	}

	public Matrix getMatrix() {
		return matrix;
	}

	public void setMatrix(Matrix matrix) {
		this.matrix = matrix;
	}

	public void setChecked(boolean[] checked) {
		this.checked = checked;
	}

	public void removeCheckBoxColumn() {
		TableColumn tc = this.getColumnModel().getColumn(1);
		removeColumn(tc);
	}

	public void setRange1(boolean selected, double start, double finish) {
		range1 = selected;
		range1start = start;
		range1finish = finish;
	}

	public void setRange2(boolean selected, double start, double finish) {
		range2 = selected;
		range2start = start;
		range2finish = finish;
	}

	public void setTwoDigitsOnly(boolean selected) {
		twoDigitsOnly = selected;
	}
}
