package com.github.deliberateq.util.math.gui;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.RenderingHints;

import javax.swing.JFrame;
import javax.swing.JPanel;

import com.github.deliberateq.util.gui.swing.v1.SwingUtil;

public class VennDiagramPanel extends JPanel {

    private static final long serialVersionUID = -5420193567677216537L;
    
    private int numCircles;

	public VennDiagramPanel(int numCircles) {
		this.numCircles = numCircles;
	}

	@Override
	public void paint(Graphics g) {
		Graphics2D g2d = (Graphics2D) g;
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);
		int centreX = getWidth() / 2;
		int centreY = getHeight() / 2;
		int radius = Math.min(getWidth(), getHeight()) * 2 / 7;
		int overlap = radius / 3;
		double angle = 2 * Math.PI / numCircles;
		for (int i = 0; i < numCircles; i++) {
			int x = (int) Math.round(centreX + Math.cos(i * angle)
					* (radius - overlap));
			int y = (int) Math.round(centreY + Math.sin(i * angle)
					* (radius - overlap));

			g2d.drawOval(x - radius, y - radius, 2 * radius, 2 * radius);
		}
	}

	public static void main(String[] args) {
		JFrame frame = new JFrame();
		frame.setLayout(new GridLayout(1, 1));
		frame.add(new VennDiagramPanel(5));
		frame.setSize(600, 600);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		SwingUtil.centre(frame);
		frame.setVisible(true);
	}

}
