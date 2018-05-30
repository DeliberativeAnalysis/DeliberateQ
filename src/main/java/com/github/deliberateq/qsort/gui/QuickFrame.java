package com.github.deliberateq.qsort.gui;

import java.awt.GridLayout;

import javax.swing.JFrame;
import javax.swing.JPanel;

import com.github.deliberateq.util.gui.swing.v1.SwingUtil;

public class QuickFrame extends JFrame {

	private static final long serialVersionUID = -2482700386414560817L;

	public QuickFrame(JPanel panel) {
		this(panel, 1000, 600);
	}

	public QuickFrame(JPanel panel, int width, int height) {
		setLayout(new GridLayout(1, 1));
		add(panel);
		setSize(width, height);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		SwingUtil.centre(this);
		setVisible(true);
	}

}
