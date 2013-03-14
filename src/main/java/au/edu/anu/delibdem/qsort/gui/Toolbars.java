package au.edu.anu.delibdem.qsort.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JToolBar;

import moten.david.util.event.Event;
import moten.david.util.event.EventManager;
import au.edu.anu.delibdem.qsort.gui.images.ResourceLocator;

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
