package com.github.deliberateq.qsort.gui;

import java.awt.GridLayout;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.swing.JFrame;

import com.github.deliberateq.qsort.Data;
import com.github.deliberateq.util.gui.swing.v1.SwingUtil;
import com.github.deliberateq.util.math.CorrelationCoefficient;

public class DataFrame extends JFrame {

	private static final long serialVersionUID = -2032992163241329984L;

	public DataFrame(Data data, CorrelationCoefficient cc) {
		setLayout(new GridLayout(1, 1));
		DataPanel dp = new DataPanel(data, cc);
		setTitle("Intersubjective Correlation");
		add(dp);
	}

	public static void main(String[] args) throws FileNotFoundException,
			IOException {

		LookAndFeel.setLookAndFeel();
		String filename = "src/New Mexico.txt";
		if (args.length > 0)
			filename = args[0];
		Data data = new Data(new FileInputStream(filename));
		DataFrame frame = new DataFrame(data, CorrelationCoefficient.PEARSONS);
		frame.setSize(1000, 600);
		SwingUtil.centre(frame);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
	}

}
