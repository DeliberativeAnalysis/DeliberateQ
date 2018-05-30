package com.github.deliberateq.qsort.gui;

import java.awt.Component;
import java.util.logging.Logger;

import javax.swing.DefaultCellEditor;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;

public class MyCellEditor extends DefaultCellEditor {

	private static final long serialVersionUID = 258266914034071752L;
	private static final Logger log = Logger.getLogger(MyCellEditor.class
			.getName());
	private final JTextField box;
	private Object userObject;

	public MyCellEditor() {
		super(new JTextField());
		box = ((JTextField) editorComponent);
	}

	@Override
	public Component getTreeCellEditorComponent(final JTree tree,
			final Object value, final boolean isSelected,
			final boolean expanded, final boolean leaf, final int row) {
		userObject = ((DefaultMutableTreeNode) value).getUserObject();
		log.info("userObject class=" + userObject.getClass().getName());
		if (userObject instanceof ObjectDecorator) {
			ObjectDecorator r = (ObjectDecorator) userObject;
			box.setText(r.getName());
			box.setEditable(true);
		} else {
			box.setText(userObject.toString());
			box.setEditable(false);
		}
		return box;
	}

	@Override
	public Object getCellEditorValue() {
		if (userObject instanceof ObjectDecorator) {
			ObjectDecorator r = (ObjectDecorator) userObject;
			r.setName(box.getText());
		}
		return userObject;
	}

}
