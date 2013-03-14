package au.edu.anu.delibdem.qsort.gui.images;

import javax.swing.ImageIcon;

public class ResourceLocator {
	private static ResourceLocator instance;

	public synchronized static ResourceLocator getInstance() {
		if (instance == null)
			instance = new ResourceLocator();
		return instance;
	}

	public ImageIcon getImageIcon(String name) {
		return new ImageIcon(getClass().getResource(name));
	}
}
