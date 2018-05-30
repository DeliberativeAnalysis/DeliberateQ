package com.github.deliberateq.qsort.gui;

import javax.swing.JMenuBar;

public class MenuBars extends JMenuBar {

	private static final long serialVersionUID = -3662933753453491647L;

	public static JMenuBar getMain() {
		JMenuBar menuBar = new JMenuBar();
		menuBar.add(Menus.getFileMenu());
		menuBar.add(Menus.getEditMenu());
		return menuBar;
	}

}
