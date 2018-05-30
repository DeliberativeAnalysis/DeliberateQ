package com.github.deliberateq.qsort.gui;

import java.awt.Component;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;

import com.github.deliberateq.qsort.DataSelection;
import com.github.deliberateq.util.math.FactorAnalysisResults;
import com.github.deliberateq.util.math.MatrixProvider;

class MyRenderer extends DefaultTreeCellRenderer {

	private static final long serialVersionUID = 6295584082120301853L;
	private ImageIcon matrixIcon;
	private ImageIcon graphIcon;
	private ImageIcon rotateIcon;
	private ImageIcon factorizeIcon;
	private ImageIcon vennIcon;
	private Icon referenceIcon;

	public MyRenderer() {
		matrixIcon = LookAndFeel.getMatrixIcon();
		graphIcon = LookAndFeel.getGraphIcon();
		rotateIcon = LookAndFeel.getRotateIcon();
		factorizeIcon = LookAndFeel.getFactorizeIcon();
		vennIcon = LookAndFeel.getVennIcon();
		referenceIcon = LookAndFeel.getReferenceIcon();
	}

	@Override
	public Component getTreeCellRendererComponent(JTree tree, Object value,
			boolean sel, boolean expanded, boolean leaf, int row,
			boolean hasFocus) {

		super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf,
				row, hasFocus);
		setIcon(null);
		Object userObject = ((DefaultMutableTreeNode) value).getUserObject();
		if (userObject instanceof DataSelection) {
			setIcon(graphIcon);
		} else if (userObject instanceof Rotations) {
			setIcon(rotateIcon);
		} else if (userObject instanceof VennInfo) {
			setIcon(vennIcon);
		} else if (userObject instanceof FactorAnalysisResults) {
			// setIcon(factorizeIcon);
		} else if ("Principal Components Analysis".equals(value.toString())
				|| "Centroid Method".equals(value.toString())) {
			setIcon(factorizeIcon);
		} else if (userObject instanceof RotatedLoadings) {
			setIcon(matrixIcon);
			if (userObject.equals(Model.getInstance().getReference())) {
				setIcon(referenceIcon);
			}
		} else if (userObject instanceof MatrixProvider) {
			setIcon(matrixIcon);
			if (userObject.equals(Model.getInstance().getReference())) {
				setIcon(referenceIcon);
			}
		}
		return this;
	}

}