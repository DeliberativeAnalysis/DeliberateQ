package com.github.deliberateq.util.math.gui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Polygon;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.Spring;
import javax.swing.SpringLayout;
import javax.swing.SpringLayout.Constraints;

import com.github.deliberateq.util.event.EventManager;
import com.github.deliberateq.util.gui.swing.v1.SwingUtil;

import javax.swing.ToolTipManager;

public class VennDiagramEdwardsPanel extends JPanel {

	private static final long serialVersionUID = -7797951962718256766L;

	private int n;

	private final String[] factorLabel = new String[] { "F1", "F2", "F3", "F4",
			"F5" };

	private Object userObject;

	private final Map<String, String> labels = new HashMap<String, String>();

	private final Map<String, String> labelInfos = new HashMap<String, String>();

	List<EventManager> eventManagers = new ArrayList<EventManager>();

	private Map<String, JLabel> labelComponents;

	private JPanel backgroundPanel;

	private final SpringLayout layout;

	public void addEventManager(EventManager em) {
		eventManagers.add(em);
	}

	public void removeEventManager(EventManager em) {
		eventManagers.remove(em);
	}

	public void setLabel(String key, String value) {
		labels.put(key, value);
	}

	public void clearLabels() {
		labels.clear();
		update();
	}

	public VennDiagramEdwardsPanel(final int n) {
		this.n = n;
		layout = new SpringLayout();
		setLayout(layout);
		createBackgroundPanel();
		createPositions();
	}

	public void setNumberSets(int n) {
		this.n = n;
	}

	private void createPositions() {
		HashMap<String, LabelPosition> labelPositions = new HashMap<String, LabelPosition>();
		labelPositions.put("1", new LabelPosition(1f / 3, 0, 1, 3f / 2));
		labelPositions.put("2", new LabelPosition(4f / 3, 0, 1, -3f / 2));
		labelPositions.put("3", new LabelPosition(1, -1f / 2, 1, -3f / 4));
		labelPositions.put("4", new LabelPosition(1, -3f / 2, 1, -2f / 3));
		labelPositions.put("5", new LabelPosition(1, -1f / 9, 1, -8f / 7));
		labelPositions.put("12", new LabelPosition(4f / 3, 0, 1, 3f / 2));
		labelPositions.put("13", new LabelPosition(1, -1f / 2, 1, 3f / 4));
		labelPositions.put("14", new LabelPosition(1, -3f / 2, 1, 2f / 3));
		labelPositions.put("15", new LabelPosition(1, -1f / 9, 1, 8f / 7));
		labelPositions.put("23", new LabelPosition(1, 1f / 2, 1, -3f / 4));
		labelPositions.put("24", new LabelPosition(1, 3f / 2, 1, -2f / 3));
		labelPositions.put("25", new LabelPosition(1, 1f / 9, 1, -8f / 7));
		labelPositions.put("34", new LabelPosition(1, -2f / 3, 1, -1f / 2));
		labelPositions.put("35", new LabelPosition(1, -1f / 6, 1, -3f / 4));
		labelPositions.put("45", new LabelPosition(1, -5f / 4, 1, -1f / 20));
		labelPositions.put("123", new LabelPosition(1, 1f / 2, 1, 3f / 4));
		labelPositions.put("124", new LabelPosition(1, 3f / 2, 1, 2f / 3));
		labelPositions.put("125", new LabelPosition(1, 1f / 9, 1, 8f / 7));
		labelPositions.put("134", new LabelPosition(1, -2f / 3, 1, 1f / 2));
		labelPositions.put("135", new LabelPosition(1, -1f / 6, 1, 3f / 4));
		labelPositions.put("145", new LabelPosition(1, -5f / 4, 1, 1f / 20));
		labelPositions.put("234", new LabelPosition(1, 2f / 3, 1, -1f / 2));
		labelPositions.put("235", new LabelPosition(1, 1f / 6, 1, -3f / 4));
		labelPositions.put("245", new LabelPosition(1, 5f / 4, 1, -1f / 20));
		labelPositions.put("345", new LabelPosition(1, -1f / 2, 1, -1f / 5));
		labelPositions.put("1234", new LabelPosition(1, 2f / 3, 1, 1f / 2));
		labelPositions.put("1235", new LabelPosition(1, 1f / 6, 1, 3f / 4));
		labelPositions.put("1245", new LabelPosition(1, 5f / 4, 1, 1f / 20));
		labelPositions.put("1345", new LabelPosition(1, -1f / 2, 1, 1f / 5));
		labelPositions.put("2345", new LabelPosition(1, 1f / 2, 1, -1f / 5));
		labelPositions.put("12345", new LabelPosition(1, 1f / 2, 1, 1f / 5));
		labelComponents = new HashMap<String, JLabel>();
		for (String key : labelPositions.keySet()) {
			JLabel label = new JLabel("boo");
			labelComponents.put(key, label);
			add(label);
			Constraints panelConstraints = layout.getConstraints(this);
			Constraints c = layout.getConstraints(label);
			LabelPosition lp = labelPositions.get(key);
			c.setConstraint(
					SpringLayout.HORIZONTAL_CENTER,
					getSpring(panelConstraints, lp.getCentreXFactor(),
							lp.getRadiusXFactor(), true));
			c.setConstraint(
					SpringLayout.VERTICAL_CENTER,
					getSpring(panelConstraints, lp.getCentreYFactor(),
							lp.getRadiusYFactor(), false));
		}
	}

	private Spring getSpring(final Constraints panelConstraints, final float a,
			final float b, final boolean xAxis) {
		return new Spring() {

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
				int width = panelConstraints.getConstraint(SpringLayout.WIDTH)
						.getValue();
				int height = panelConstraints
						.getConstraint(SpringLayout.HEIGHT).getValue();
				int min = Math.min(width, height);
				float radius = min * 9 / 32;
				int centreX = width / 2;
				int centreY = height / 2;
				int pointX = Math.round(a * centreX + b * radius);
				int pointY = Math.round(a * centreY + b * radius);
				if (xAxis)
					return pointX;
				else
					return pointY;
			}

			@Override
			public void setValue(int value) {

			}
		};

	}

	private void createBackgroundPanel() {
		backgroundPanel = new JPanel() {
			private static final long serialVersionUID = 4991079420775849437L;

			@Override
			public void paint(Graphics g) {
				int centreX = getCentreX();
				int centreY = getCentreY();
				int radius = getRadius();
				Graphics2D g2d = (Graphics2D) g;
				g2d.setBackground(Color.white);
				g2d.clearRect(0, 0, getWidth(), getHeight());
				g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
						RenderingHints.VALUE_ANTIALIAS_ON);
				g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
						RenderingHints.VALUE_TEXT_ANTIALIAS_ON);// VALUE_TEXT_ANTIALIAS_LCD_HRGB
				// in jdk6
				g2d.setRenderingHint(RenderingHints.KEY_RENDERING,
						RenderingHints.VALUE_RENDER_QUALITY);
				g2d.setStroke(new BasicStroke(2));
				g.setFont(g.getFont().deriveFont(10.0f));
				Font nFont = g.getFont().deriveFont(18.0f);
				Font defaultFont = g.getFont();
				int labelMargin = 10;
				if (n >= 1) {
					g.setColor(Color.red);
					g.drawRect(0, centreY, getWidth() - 1, getHeight()
							- centreY - 1);
					g.setFont(nFont);
					g.drawString(factorLabel[0], 10, getHeight() - labelMargin);
					g.setFont(defaultFont);
				}
				if (n >= 2) {
					g.setColor(Color.black);
					g.drawRect(centreX, 1, getWidth() / 2 - 1, getHeight() - 1);
					g.setFont(nFont);
					g.drawString(factorLabel[1], getWidth()
							- g.getFontMetrics().stringWidth("F1")
							- labelMargin, g.getFontMetrics().getAscent()
							+ labelMargin);
					g.setFont(defaultFont);
				}
				if (n >= 3) {
					g.setColor(Color.CYAN);
					g.drawOval(centreX - radius, centreY - radius, 2 * radius,
							2 * radius);
					g.setFont(nFont);
					g.drawString(
							factorLabel[2],
							Math.round((float) (centreX - radius
									* Math.cos(Math.PI / 3)))
									- labelMargin,
							Math.round((float) (centreY - radius
									* Math.sin(Math.PI / 3)))
									- labelMargin);
					g.setFont(defaultFont);
				}
				if (n >= 4) {
					g.setColor(Color.green);
					Polygon p = new Polygon();
					p.addPoint(centreX - 7 * radius / 4, centreY - radius);
					p.addPoint(centreX, centreY - 1 * radius / 2);
					p.addPoint(centreX + 7 * radius / 4, centreY - radius);
					p.addPoint(centreX + 7 * radius / 4, centreY + radius);
					p.addPoint(centreX, centreY + 1 * radius / 2);
					p.addPoint(centreX - 7 * radius / 4, centreY + radius);
					g.drawPolygon(p);
					g.setFont(nFont);
					g.drawString(factorLabel[3], centreX - 7 * radius / 4
							+ labelMargin, centreY - radius + labelMargin
							+ g.getFontMetrics().getAscent());
					g.setFont(defaultFont);
				}
				if (n >= 5) {
					g.setColor(Color.blue);
					Polygon p = new Polygon();
					p.addPoint(centreX, centreY - 3 * radius / 2);
					int z = Math.round((float) (2 * radius / 3 / Math.sqrt(2)));
					p.addPoint(centreX - z, centreY - z);
					p.addPoint(centreX - 3 * radius / 2, centreY);
					p.addPoint(centreX - z, centreY + z);
					p.addPoint(centreX, centreY + 3 * radius / 2);
					p.addPoint(centreX + z, centreY + z);
					p.addPoint(centreX + 3 * radius / 2, centreY);
					p.addPoint(centreX + z, centreY - z);
					g.drawPolygon(p);
					g.setFont(nFont);
					g.drawString(factorLabel[4], centreX - labelMargin
							- g.getFontMetrics().stringWidth("F5"), centreY - 3
							* radius / 2 + g.getFontMetrics().getAscent());
					g.setFont(defaultFont);
				}
				g.setFont(defaultFont);
				g.setColor(Color.black);
			}
		};
		add(backgroundPanel);
		backgroundPanel.setLocation(0, 0);

	}

	private static class LabelPosition {
		private float centreXFactor;
		private float radiusXFactor;
		private float centreYFactor;
		private float radiusYFactor;

		public float getCentreXFactor() {
			return centreXFactor;
		}

		public void setCentreXFactor(float centreXFactor) {
			this.centreXFactor = centreXFactor;
		}

		public float getRadiusXFactor() {
			return radiusXFactor;
		}

		public void setRadiusXFactor(float radiusXFactor) {
			this.radiusXFactor = radiusXFactor;
		}

		public float getCentreYFactor() {
			return centreYFactor;
		}

		public void setCentreYFactor(float centreYFactor) {
			this.centreYFactor = centreYFactor;
		}

		public float getRadiusYFactor() {
			return radiusYFactor;
		}

		public void setRadiusYFactor(float radiusYFactor) {
			this.radiusYFactor = radiusYFactor;
		}

		public LabelPosition(float centreXFactor, float radiusXFactor,
				float centreYFactor, float radiusYFactor) {
			this.centreXFactor = centreXFactor;
			this.radiusXFactor = radiusXFactor;
			this.centreYFactor = centreYFactor;
			this.radiusYFactor = radiusYFactor;
		}
	}

	public void update() {
		int order = 0;
		ToolTipManager.sharedInstance().setEnabled(true);
		ToolTipManager.sharedInstance().setDismissDelay(1200000);
		for (String s : labelComponents.keySet()) {
			JLabel lab = labelComponents.get(s);
			lab.setText(labels.get(s));
			lab.setVisible(labels.get(s) != null
					&& labels.get(s).trim().length() > 0);
			lab.setToolTipText(labelInfos.get(s));
			setComponentZOrder(lab, order);
			order++;
			ToolTipManager.sharedInstance().registerComponent(lab);
		}
		setComponentZOrder(backgroundPanel, order);
		// invalidate();
	}

	private int getCentreX() {
		return getWidth() / 2;
	}

	private int getCentreY() {
		return getHeight() / 2;
	}

	private int getRadius() {
		return Math.min(getHeight(), getWidth()) * 9 / 32;
	}

	@Override
	public void paint(Graphics g) {
		backgroundPanel.setSize(getSize());
		super.paint(g);
	}

	public BufferedImage getImage() {
		int w = getWidth(), h = getHeight();
		BufferedImage image = new BufferedImage(w, h,
				BufferedImage.TYPE_INT_RGB);
		Graphics2D g2 = image.createGraphics();
		paint(g2);
		g2.dispose();
		return image;
	}

	public static void main(String[] args) {
		JFrame frame = new JFrame();
		frame.setLayout(new GridLayout(1, 1));
		VennDiagramEdwardsPanel venn = new VennDiagramEdwardsPanel(5);
		frame.add(venn);
		frame.setSize(600, 400);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		SwingUtil.centre(frame);
		frame.setVisible(true);
	}

	public Object getUserObject() {
		return userObject;
	}

	public void setUserObject(Object userObject) {
		this.userObject = userObject;
	}

	public Map<String, String> getLabelInfos() {
		return labelInfos;
	}

	public void setFactorLabel(int factorNum, String label) {
		factorLabel[factorNum - 1] = label;
	}

}
