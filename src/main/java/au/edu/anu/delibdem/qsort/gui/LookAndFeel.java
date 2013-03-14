package au.edu.anu.delibdem.qsort.gui;

import java.awt.Color;
import java.awt.Font;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.UIManager;
import javax.swing.plaf.FontUIResource;

import moten.david.util.gui.swing.v1.SwingUtil;
import au.edu.anu.delibdem.qsort.gui.images.ResourceLocator;

public class LookAndFeel {

	public static void setLookAndFeel() {

		if ("true".equalsIgnoreCase(System.getProperty("systemLookAndFeel"))
				|| (System.getProperty("systemLookAndFeel") == null && Preferences
						.getInstance().isSystemLookAndFeel())) {
			try {
				UIManager.setLookAndFeel(UIManager
						.getSystemLookAndFeelClassName());
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			SwingUtil.setUIFont(new FontUIResource("Arial", Font.PLAIN, 11));
			UIManager.put("Panel.background", Color.white);
			Object panelBackground = UIManager.get("Panel.background");
			UIManager.put("CheckBox.background", panelBackground);
			UIManager.put("RadioButton.background", panelBackground);
			UIManager.put("RadioButton.background", panelBackground);
			UIManager.put("Slider.background", panelBackground);
			UIManager.put("List.background", panelBackground);
			ImageIcon plus = ResourceLocator.getInstance().getImageIcon(
					"plus.gif");
			ImageIcon minus = ResourceLocator.getInstance().getImageIcon(
					"minus.gif");
			UIManager.put("Tree.expandedIcon", minus);
			UIManager.put("Tree.collapsedIcon", plus);
		}
	}

	public static ImageIcon getPrimaryIcon() {
		ImageIcon icon = new ImageIcon(ResourceLocator.getInstance().getClass()
				.getResource("venn.gif"));
		return icon;
	}

	public static ImageIcon getMatrixIcon() {
		ImageIcon icon = ResourceLocator.getInstance()
				.getImageIcon("table.gif");
		return icon;
	}

	public static ImageIcon getGraphIcon() {
		ImageIcon icon = ResourceLocator.getInstance().getImageIcon(
				"graph-dots.gif");
		return icon;
	}

	public static ImageIcon getRotateIcon() {
		ImageIcon icon = ResourceLocator.getInstance().getImageIcon("rot.gif");
		return icon;
	}

	public static ImageIcon getFactorizeIcon() {
		ImageIcon icon = ResourceLocator.getInstance().getImageIcon(
				"factorize.gif");
		return icon;
	}

	public static ImageIcon getVennIcon() {
		ImageIcon icon = ResourceLocator.getInstance().getImageIcon("venn.gif");
		return icon;
	}

	public static ImageIcon getPersonIcon() {
		ImageIcon icon = ResourceLocator.getInstance().getImageIcon(
				"person.gif");
		return icon;
	}

	public static Icon getCopyIcon() {
		ImageIcon icon = ResourceLocator.getInstance().getImageIcon("copy.gif");
		return icon;
	}

	public static Icon getReferenceIcon() {
		ImageIcon icon = ResourceLocator.getInstance().getImageIcon(
				"set-reference.gif");
		return icon;
	}

	public static Icon getStandardErrorIcon1() {
		ImageIcon icon = ResourceLocator.getInstance().getImageIcon(
				"std-error-1.gif");
		return icon;
	}

	public static Icon getStandardErrorIcon2() {
		ImageIcon icon = ResourceLocator.getInstance().getImageIcon(
				"std-error-2.gif");
		return icon;
	}

	public static Icon getDigitsIcon() {
		ImageIcon icon = ResourceLocator.getInstance().getImageIcon("00.gif");
		return icon;
	}

	public static Icon getStopIcon(String string) {
		ImageIcon icon = ResourceLocator.getInstance().getImageIcon("00.gif");
		return icon;
	}

	public static Icon getRotationMethodIcon() {
		ImageIcon icon = ResourceLocator.getInstance().getImageIcon(
				"rotation-method.gif");
		return icon;

	}

}
