package com.github.deliberateq.qsort.gui;

import java.awt.Color;
import java.awt.Font;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.UIManager;
import javax.swing.plaf.FontUIResource;

import com.github.deliberateq.qsort.gui.images.ResourceLocator;
import com.github.deliberateq.util.gui.swing.v1.SwingUtil;

public class LookAndFeel {

    public static void setLookAndFeel() {

        if ("true".equalsIgnoreCase(System.getProperty("systemLookAndFeel"))
                || System.getProperty("systemLookAndFeel") == null
                        && Preferences.getInstance().isSystemLookAndFeel()) {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
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
            ImageIcon plus = ResourceLocator.getInstance().getImageIcon("plus.gif");
            ImageIcon minus = ResourceLocator.getInstance().getImageIcon("minus.gif");
            UIManager.put("Tree.expandedIcon", minus);
            UIManager.put("Tree.collapsedIcon", plus);
        }
    }

    public static ImageIcon getPrimaryIcon() {
        return ResourceLocator.getInstance().getImageIcon("venn.gif");
    }

    public static ImageIcon getMatrixIcon() {
        return ResourceLocator.getInstance().getImageIcon("table.gif");
    }

    public static ImageIcon getGraphIcon() {
        return ResourceLocator.getInstance().getImageIcon("graph-dots.gif");
    }

    public static ImageIcon getRotateIcon() {
        return ResourceLocator.getInstance().getImageIcon("rot.gif");
    }

    public static ImageIcon getFactorizeIcon() {
        return ResourceLocator.getInstance().getImageIcon("factorize.gif");
    }

    public static ImageIcon getVennIcon() {
        return ResourceLocator.getInstance().getImageIcon("venn.gif");
    }

    public static ImageIcon getPersonIcon() {
        return ResourceLocator.getInstance().getImageIcon("person.gif");
    }

    public static Icon getCopyIcon() {
        return ResourceLocator.getInstance().getImageIcon("copy.gif");
    }

    public static Icon getReferenceIcon() {
        return ResourceLocator.getInstance().getImageIcon("set-reference.gif");
    }

    public static Icon getStandardErrorIcon1() {
        return ResourceLocator.getInstance().getImageIcon("std-error-1.gif");
    }

    public static Icon getStandardErrorIcon2() {
        return ResourceLocator.getInstance().getImageIcon("std-error-2.gif");
    }

    public static Icon getDigitsIcon() {
        return ResourceLocator.getInstance().getImageIcon("00.gif");
    }

    public static Icon getStopIcon(String string) {
        return ResourceLocator.getInstance().getImageIcon("00.gif");
    }

    public static Icon getRotationMethodIcon() {
        return ResourceLocator.getInstance().getImageIcon("rotation-method.gif");
    }

}
