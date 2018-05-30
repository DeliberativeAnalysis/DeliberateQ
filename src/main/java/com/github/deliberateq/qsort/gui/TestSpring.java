package com.github.deliberateq.qsort.gui;

import java.awt.Color;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.Spring;
import javax.swing.SpringLayout;
import javax.swing.SpringLayout.Constraints;

public class TestSpring extends JPanel {

	public TestSpring() {
		SpringLayout layout = new SpringLayout();
		setLayout(layout);
		JLabel label = new JLabel("Hello");
		add(label);
		final Constraints cPanel = layout.getConstraints(this);
		final Constraints c = layout.getConstraints(label);

		Spring spring = new Spring() {

			@Override
			public int getMaximumValue() {
				return 0;
			}

			@Override
			public int getMinimumValue() {
				return 0;
			}

			@Override
			public int getPreferredValue() {
				return 0;
			}

			@Override
			public int getValue() {
				return cPanel.getConstraint(SpringLayout.SOUTH).getValue() / 2;
			}

			@Override
			public void setValue(int value) {

			}
		};
		c.setConstraint(SpringLayout.HORIZONTAL_CENTER, spring);
		setBorder(BorderFactory.createLineBorder(Color.black));
		label.setBorder(BorderFactory.createLineBorder(Color.black));
	}

	public static void main(String[] args) {
		new QuickFrame(new TestSpring());
	}

}
