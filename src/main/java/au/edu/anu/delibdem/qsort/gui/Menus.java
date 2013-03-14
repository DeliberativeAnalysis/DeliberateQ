package au.edu.anu.delibdem.qsort.gui;

import javax.swing.JMenu;

public class Menus {

	public static JMenu getFileMenu() {
		JMenu menu = new JMenu("File");
		menu.add(MenuItems.getOpen());
		menu.addSeparator();
		menu.add(MenuItems.getOpenSamples());
		menu.addSeparator();
		menu.add(MenuItems.getExit());
		return menu;
	}

	public static JMenu getEditMenu() {
		JMenu menu = new JMenu("Edit");
		menu.add(MenuItems.getPreferences());
		return menu;
	}
}
