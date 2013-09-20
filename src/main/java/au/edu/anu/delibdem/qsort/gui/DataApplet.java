package au.edu.anu.delibdem.qsort.gui;

import java.awt.Font;
import java.awt.GridLayout;
import java.io.InputStream;
import java.net.URL;
import java.util.logging.Logger;

import javax.swing.JApplet;
import javax.swing.plaf.FontUIResource;

import moten.david.util.gui.swing.v1.SwingUtil;
import au.edu.anu.delibdem.qsort.Data;

public class DataApplet extends JApplet {

	private static final Logger log = Logger.getLogger(DataApplet.class
			.getName());

	private static final long serialVersionUID = -5571179331867074679L;
	private Data data;

	@Override
	public void init() {
		super.init();
		try {
			LookAndFeel.setLookAndFeel();
			SwingUtil.setUIFont(new FontUIResource("Arial", Font.PLAIN, 11));
			URL url = new URL(getDocumentBase(), this.getParameter("data-url"));
			log.info("loading from " + url);
			InputStream is = url.openStream();
			data = new Data(is);
			is.close();
			DataPanel dp = new DataPanel(data);
			setLayout(new GridLayout(1, 1));
			add(dp);
			// add(new MainPanel());

		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

}
