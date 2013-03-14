package moten.david.util.math.gui;

import java.awt.Component;
import java.awt.event.ItemListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JCheckBox;
import javax.swing.JTable;
import javax.swing.UIManager;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumnModel;

/**
 * Add a JCheckBox to the Renderer
 * 
 * @author Jan-Friedrich Mutter (jmutter@bigfoot.de)
 */
public class CheckBoxHeader extends JCheckBox implements TableCellRenderer,
		MouseListener {

	private static final long serialVersionUID = 3876916573038744433L;

	/**
	 * the Renderer Component.
	 * 
	 * @see #getTableCellRendererComponent(JTable table, Object value, boolean
	 *      isSelected, boolean hasFocus, int row, int column)
	 */
	protected CheckBoxHeader rendererComponent;

	/** To which (JTable-) columns does this Checkbox belong ? */
	protected int column;

	/**
	 * remembers, if mousePressed() was called before. Workaround, because
	 * dozens of mouseevents occurs after one mouseclick.
	 */
	protected boolean mousePressed = false;

	/**
	 * @param itemListener
	 *            will be notified when Checkbox will be checked/unchecked
	 */
	public CheckBoxHeader(ItemListener itemListener) {
		rendererComponent = this;
		rendererComponent.addItemListener(itemListener);
	}

	/** @return this */
	// pasted from javax.swing.table.TableColumn.createDefaultHeaderRenderer()
	// with some slight modifications.
	// implements TableCellRenderer
	@Override
	public Component getTableCellRendererComponent(JTable table, Object value,
			boolean isSelected, boolean hasFocus, int row, int column) {

		if (table != null) {
			JTableHeader header = table.getTableHeader();
			if (header != null) {
				rendererComponent.setForeground(header.getForeground());
				rendererComponent.setBackground(header.getBackground());
				rendererComponent.setFont(header.getFont());

				header.addMouseListener(rendererComponent);
			}
		}

		setColumn(column);
		rendererComponent.setText((value == null) ? "" : value.toString());
		setBorder(UIManager.getBorder("TableHeader.cellBorder"));

		return rendererComponent;
	}

	/**
	 * @param column
	 *            to which the CheckBox belongs to
	 */
	protected void setColumn(int column) {
		this.column = column;
	}

	/** @return the column to which the CheckBox belongs to */
	public int getColumn() {
		return column;
	}

	/** ************** Implementation of MouseListener ***************** */

	/**
	 * Calls doClick(), because the CheckBox doesn't receive any mouseevents
	 * itself. (because it is in a CellRendererPane).
	 */
	protected void handleClickEvent(MouseEvent e) {
		// Workaround: dozens of mouseevents occur for only one mouse click.
		// First MousePressedEvents, then MouseReleasedEvents, (then
		// MouseClickedEvents).
		// The boolean flag 'mousePressed' is set to make sure
		// that the action is performed only once.
		if (mousePressed) {
			mousePressed = false;

			JTableHeader header = (JTableHeader) (e.getSource());
			JTable tableView = header.getTable();
			TableColumnModel columnModel = tableView.getColumnModel();
			int viewColumn = columnModel.getColumnIndexAtX(e.getX());
			int column = tableView.convertColumnIndexToModel(viewColumn);

			if (viewColumn == this.column && e.getClickCount() == 1
					&& column != -1) {
				doClick();
			}
		}
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		handleClickEvent(e);
		// Header doesn't repaint itself properly
		((JTableHeader) e.getSource()).repaint();
	}

	@Override
	public void mousePressed(MouseEvent e) {
		mousePressed = true;
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		// works - problem: works even if column is dragged or resized ...
		// handleClickEvent(e);
		// properly repainting by the Header
	}

	@Override
	public void mouseEntered(MouseEvent e) {
	}

	@Override
	public void mouseExited(MouseEvent e) {
	}
}
