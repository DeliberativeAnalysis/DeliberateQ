package com.github.deliberateq.qsort.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JToolBar;

import com.github.deliberateq.qsort.gui.images.ResourceLocator;
import com.github.deliberateq.util.event.Event;
import com.github.deliberateq.util.event.EventManager;

public class Toolbars {

	public static JToolBar getMainToolbar() {
		JToolBar toolBar = new JToolBar();
		JButton button = new JButton();
		button.setToolTipText("Add rotation");
		toolBar.add(button);
		ImageIcon icon = new ImageIcon(ResourceLocator.getInstance().getClass()
				.getResource("rotate.gif"));
		button.setIcon(icon);
		button.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				EventManager.getInstance().notify(
						new Event(null, Events.ADD_ROTATION));
			}
		});

		return toolBar;
	}

}
