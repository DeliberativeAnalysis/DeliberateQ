package com.github.deliberateq.util.math.gui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.SpringLayout;
import javax.swing.UIDefaults;
import javax.swing.UIManager;

import org.apache.commons.math3.stat.regression.SimpleRegression;

import com.github.deliberateq.util.gui.swing.v1.SwingUtil;
import com.github.deliberateq.util.math.Function;
import com.github.deliberateq.util.math.Matrix;
import com.github.deliberateq.util.math.RegressionIntervalFunction;
import com.github.deliberateq.util.math.Vector;

public class GraphPanel extends JPanel {

	private static final long serialVersionUID = -432231924155696764L;

	private Vector[] vector1;

	private Vector[] vector2;

	private boolean labelsVisible = true;

	private String xLabel = "";

	private String yLabel = "";

	private List<String> pointLabels = null;

	private final List<GraphFunction> graphFunctions = new ArrayList<GraphFunction>();

	private final Map<Vector, String> comments = new HashMap<Vector, String>();

	private double proportionDrawn;

	private boolean displayMeans = false;

	private boolean displayRegression = false;

	private boolean displayArrowHeads = true;

	private boolean useScaling = false;

	private final Color[] colors = new Color[] { Color.RED, Color.BLUE,
			Color.GREEN, Color.MAGENTA, Color.CYAN };

	private static class GraphFunction {
		public Function function;
		public Color color;

		public GraphFunction(Function function, Color color) {
			super();
			this.function = function;
			this.color = color;
		}
	}

	public float getFontScaling() {
		return Toolkit.getDefaultToolkit().getScreenResolution() / 96.0f;
	}

	public float getSizeScaling() {
		if (useScaling)
			return Math.min(getHeight(), getWidth()) / 500.0f;
		else
			return 1.0f;
	}

	public int scale(int i) {
		return Math.round(i * getSizeScaling());
	}

	public void addFunction(Function f, Color c) {
		GraphFunction gf = new GraphFunction(f, c);
		graphFunctions.add(gf);
	}

	public GraphPanel(Vector v1, Vector v2) {
		this(new Vector[] { v1 }, new Vector[] { v2 });
	}

	public GraphPanel(Matrix m) {
		this(m.getColumnVector(1), m.getColumnVector(2));
		if (m.columnCount() != 2)
			throw new RuntimeException("matrix must have exactly two columns");
	}

	public void setMatrix(Matrix m) {
		if (m.columnCount() != 2)
			throw new RuntimeException("matrix must have exactly two columns");
		setVector1(new Vector[] { m.getColumnVector(1) });
		setVector2(new Vector[] { m.getColumnVector(2) });
	}

	public GraphPanel(Vector[] vector1, Vector[] vector2, double proportionDrawn) {
		this.vector1 = vector1;
		this.vector2 = vector2;
		this.proportionDrawn = proportionDrawn;
		UIDefaults uiDefaults = UIManager.getDefaults();
		this.setBackground(uiDefaults.getColor("TextBox.background"));
		setLayout(new GridLayout(1, 1));
		SpringLayout layout = new SpringLayout();
		setLayout(layout);
		createMouseListener();
	}

	private void createMouseListener() {
		final GraphPanel gp = this;
		addMouseListener(new MouseAdapter() {

			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getButton() == MouseEvent.BUTTON3) {
					JPopupMenu popup = new JPopupMenu();
					JMenuItem item = new JMenuItem("Save As High Res JPEG...");
					popup.add(item);
					item.addActionListener(new ActionListener() {

						@Override
						public void actionPerformed(ActionEvent event) {
							Dimension d = getSize();
							try {
								JFileChooser chooser = new JFileChooser();
								chooser.setCurrentDirectory(new File(System
										.getProperty("user.home")));
								if (chooser.showSaveDialog(gp) != JFileChooser.APPROVE_OPTION)
									return;
								File file = chooser.getSelectedFile();

								setSize(3000, 3000);
								FileOutputStream os;
								os = new FileOutputStream(file);
								boolean saveUseScaling = useScaling;
								useScaling = true;
								ImageIO.write(getImage(), "jpeg", os);
								useScaling = saveUseScaling;
								repaint();
								os.close();
							} catch (FileNotFoundException e1) {
								e1.printStackTrace();
							} catch (IOException e) {
								e.printStackTrace();
							} finally {
								setSize(d);
							}
						}
					});
					popup.show(gp, e.getX(), e.getY());
				}
			}
		});

	}

	public GraphPanel(Vector[] vector1, Vector[] vector2) {
		this(vector1, vector2, 1.0);
	}

	@Override
	public Dimension getMinimumSize() {
		return new Dimension(100, 100);
	}

	@Override
	public Dimension getMaximumSize() {
		return new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE);
	}

	static double radiansToDegrees(double x) {
		return 180 / Math.PI * x;
	}

	private Point getOrigin() {
		Point origin = new Point(this.getSize().width / 2,
				this.getSize().height / 2);
		return origin;
	}

	private double getAxisLength() {
		return Math.max(Math.floor(Math.max(vector1[0].getMaxAbsoluteValue(),
				vector2[0].getMaxAbsoluteValue())), 1);
	}

	private int getAxisScreenLength() {
		return (int) Math.floor(3 * getMinSize() / 8.0);
	}

	private Point getPoint(Vector v) {
		Point origin = getOrigin();
		Point p = new Point();
		p.x = (int) Math.round(origin.x + v.getValue(1) / getAxisLength()
				* getAxisScreenLength());
		p.y = (int) Math.round(origin.y - v.getValue(2) / getAxisLength()
				* getAxisScreenLength());
		return p;
	}

	private int getMinSize() {
		return (this.getSize().width > this.getSize().height ? this.getSize().height
				: this.getSize().width);
	}

	private Vector getVector(Point p) {
		Vector vector = new Vector(2);
		Point origin = getOrigin();
		vector.setValue(1, (p.x - origin.x) / (double) getAxisScreenLength()
				* getAxisLength());
		vector.setValue(2, -(p.y - origin.y) / (double) getAxisScreenLength()
				* getAxisLength());
		return vector;
	}

	private void drawLine(Graphics g, Vector v1, Vector v2) {
		drawLine(g, v1, v2, 1.0);
	}

	public Vector between(Vector v1, Vector v2, double proportion) {
		Vector diff = v2.minus(v1).times(proportion);
		Vector endVector = v1.add(diff);
		return endVector;
	}

	private void drawLine(Graphics g, Vector v1, Vector v2, double proportion) {
		Vector endVector = between(v1, v2, proportion);
		Point p1 = getPoint(v1);
		Point p2 = getPoint(endVector);
		Graphics2D g2d = (Graphics2D) g;
		g2d.drawLine(p1.x, p1.y, p2.x, p2.y);
	}

	private Stroke getDottedStroke1() {
		return new BasicStroke(1f, BasicStroke.CAP_ROUND,
				BasicStroke.JOIN_ROUND, 1f, new float[] { 5f, 5f }, 0f);
	}

	private Stroke getDottedStroke2() {
		return new BasicStroke(1f, BasicStroke.CAP_ROUND,
				BasicStroke.JOIN_ROUND, 1f, new float[] { 2f, 2f }, 0f);
	}

	private Stroke getDefaultStroke() {
		return new BasicStroke(1f * getSizeScaling(), BasicStroke.CAP_ROUND,
				BasicStroke.JOIN_ROUND);
	}

	private void paintFunction(Graphics g, GraphFunction gf) {
		Vector vBottom = new Vector(new double[] { 0, -getAxisLength() });
		Vector vTop = new Vector(new double[] { 0, getAxisLength() });
		Vector vLeft = new Vector(new double[] { -getAxisLength(), 0 });
		Vector vRight = new Vector(new double[] { getAxisLength(), 0 });

		Point pStart = getPoint(vLeft);
		Point pEnd = getPoint(vRight);
		Vector lastV = null;
		g.setColor(gf.color);
		for (int i = pStart.x; i <= pEnd.x; i += 5) {
			Point p = new Point(i, pStart.y);
			Vector v = getVector(p);
			v.setValue(2, gf.function.f(v.getValue(1)));
			if (lastV == null)
				lastV = v;
			drawLine(g, lastV, v);
			lastV = v;
		}
	}

	@Override
	public void paint(Graphics g) {

		super.paint(g);

		if (vector1 == null || vector2 == null)
			return;

		Stroke normalStroke = getDefaultStroke();

		Graphics2D g2d = (Graphics2D) g;
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_OFF);
		// g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
		// RenderingHints.VALUE_ANTIALIAS_ON);
		g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
				RenderingHints.VALUE_TEXT_ANTIALIAS_ON);// VALUE_TEXT_ANTIALIAS_LCD_HRGB
		// in jdk6
		g2d.setRenderingHint(RenderingHints.KEY_RENDERING,
				RenderingHints.VALUE_RENDER_QUALITY);
		Font font = getFont();
		font = new Font("Arial", Font.PLAIN, Math.max(
				scale(Math.round(9 * getFontScaling())), 9));
		g2d.setFont(font);

		Vector vBottom = getVectorBottom();
		Vector vTop = getVectorTop();
		Vector vLeft = getVectorLeft();
		Vector vRight = getVectorRight();

		for (GraphFunction gf : graphFunctions) {
			paintFunction(g, gf);
		}
		g.setColor(Color.black);

		for (Vector v : comments.keySet()) {
			String comment = comments.get(v);
			g.drawString(comment, getPoint(v).x, getPoint(v).y);
		}

		Color verticalAxisColor = Color.green;
		Color horizontalAxisColor = Color.blue;
		// draw default axes
		// Stroke normalStroke = g2d.getStroke();
		// g2d.setStroke(getDottedStroke1());
		g.setColor(verticalAxisColor);
		drawLine(g, vBottom, vTop);
		g.setColor(horizontalAxisColor);
		// Point pt = getPoint(vLeft);
		// Point pt2 = getPoint(vRight);
		// g.drawLine(pt.x, pt.y, pt2.x, pt2.y);
		drawLine(g, vLeft, vRight);

		g.setColor(Color.black);
		// g.setStroke(normalStroke);

		g.setColor(verticalAxisColor);
		if (displayArrowHeads) {
			drawArrowHead(g, vTop);
			drawArrowHead(g, vBottom);
		} else {
			drawMarker(g, vTop);
			drawMarker(g, vBottom);
		}

		g.setColor(horizontalAxisColor);
		if (displayArrowHeads) {
			drawArrowHead(g, vLeft);
			drawArrowHead(g, vRight);
		} else {
			drawMarker(g, vLeft);
			drawMarker(g, vRight);
		}

		g2d.setColor(Color.BLACK);

		g.drawString(getAxisLength() + "", getPoint(vTop).x
				- g.getFontMetrics().stringWidth(getAxisLength() + "")
				- g.getFontMetrics().stringWidth("A"), getPoint(vTop).y
				+ g.getFontMetrics().getAscent() / 2);
		g.drawString(-getAxisLength() + "", getPoint(vBottom).x
				- g.getFontMetrics().stringWidth(-getAxisLength() + "")
				- g.getFontMetrics().stringWidth("A"), getPoint(vBottom).y
				+ g.getFontMetrics().getAscent() / 2);
		g.drawString(getAxisLength() + "", getPoint(vRight).x
				- g.getFontMetrics().stringWidth(getAxisLength() + "") / 2,
				getPoint(vRight).y - 2 * g.getFontMetrics().getDescent());
		g.drawString(-getAxisLength() + "", getPoint(vLeft).x
				- g.getFontMetrics().stringWidth(-getAxisLength() + "") / 2,
				getPoint(vLeft).y - 2 * g.getFontMetrics().getDescent());

		// draw default labels
		// g.drawString("Y", getPoint(vTop).x + 5, getPoint(vTop).y);
		// g.drawString("X", getPoint(vRight).x + 5, getPoint(vRight).y);
		int len = g.getFontMetrics().stringWidth(xLabel);

		// draw x label
		g.drawString(xLabel, getPoint(vBottom).x - len / 2, getPoint(vBottom).y
				+ 2 * g.getFontMetrics().getHeight());

		// draw y label rotated 90 degrees
		double x = getPoint(vLeft).x - 2 * g.getFontMetrics().getHeight();
		double y = getPoint(vLeft).y + len / 2;
		g2d.translate(x, y);
		// at.translate(getPoint(vLeft).x - 25, getPoint(vLeft).y+len/2 );
		// at.setToRotation(-Math.PI /100);
		len = g.getFontMetrics().stringWidth(yLabel);
		double theta = -Math.PI / 2;
		g2d.rotate(theta);
		g2d.drawString(yLabel, 0, 0);
		g2d.rotate(-theta);
		g2d.translate(-x, -y);

		int dotSize = scale(4);

		List<Double> currentX = new ArrayList<Double>();
		List<Double> currentY = new ArrayList<Double>();

		// draw connecting lines
		double sumX = 0;
		double sumY = 0;
		int numPoints = 0;
		for (int vi = 1; vi < vector1.length; vi++) {
			double lowlim = (vi - 1) * 1.0 / (vector1.length - 1);
			double upplim = vi * 1.0 / (vector1.length - 1);
			if (proportionDrawn >= lowlim) {
				double proportion = 1;
				if (proportionDrawn <= upplim)
					proportion = (proportionDrawn - lowlim) / (upplim - lowlim);
				for (int row = 1; row <= vector1[vi - 1].rowCount(); row++) {
					Vector v = new Vector(new double[] {
							vector1[vi - 1].getValue(row),
							vector2[vi - 1].getValue(row) });
					for (int rowB = 1; rowB <= vector1[vi].rowCount(); rowB++) {
						if (vector1[vi - 1].getRowLabel(row).equals(
								vector1[vi].getRowLabel(rowB))
								&& (vector2[vi - 1].getRowLabel(row)
										.equals(vector2[vi].getRowLabel(rowB)))) {
							Vector vb = new Vector(new double[] {
									vector1[vi].getValue(rowB),
									vector2[vi].getValue(rowB) });
							g.setColor(Color.LIGHT_GRAY);
							drawLine(g, v, vb, proportion);
							Vector between = between(v, vb, proportion);
							sumX += between.getValue(1);
							sumY += between.getValue(2);
							currentX.add(between.getValue(1));
							currentY.add(between.getValue(2));
							numPoints++;
							if (proportion < 1.0) {
								g.setColor(colors[vi % colors.length]);
								Point p = getPoint(between);
								g.fillOval(p.x - dotSize / 2,
										p.y - dotSize / 2, dotSize, dotSize);
								if (labelsVisible) {
									String s1 = vector1[vi].getRowLabel(rowB);
									String s2 = vector2[vi].getRowLabel(rowB);
									String label;
									if (s1.equals(s2))
										label = s1;
									else
										label = s1 + ":" + s2;
									g.drawString(label, p.x + 5, p.y + 5);
								}
							}
						}
					}
				}
			}
		}
		g.setColor(Color.BLACK);

		// draw points
		for (int vi = 0; vi < vector1.length; vi++) {
			double lowlim = (vi) * 1.0 / (vector1.length - 1);
			if (proportionDrawn >= lowlim || vector1.length == 1) {
				for (int row = 1; row <= vector1[vi].rowCount(); row++) {
					Vector v = new Vector(new double[] {
							vector1[vi].getValue(row),
							vector2[vi].getValue(row) });
					String label;
					String s1 = vector1[vi].getRowLabel(row);
					String s2 = vector2[vi].getRowLabel(row);
					if (s1.equals(s2))
						label = s1;
					else
						label = s1 + ":" + s2;
					if (pointLabels != null)
						label = pointLabels.get(row - 1);
					Point p = getPoint(v);
					g.setColor(Color.BLACK);
					if (labelsVisible && vi == vector1.length - 1) {
						g.drawString(label, p.x + 5, p.y + 5);
					}
					if (vector1.length == 1) {
						g.setColor(getColor(vector1[vi].getRowLabels(),
								vector2[vi].getRowLabels(), row));
					} else
						g.setColor(colors[vi % colors.length]);
					g.fillOval(p.x - dotSize / 2, p.y - dotSize / 2, dotSize,
							dotSize);
					if (vector1.length == 1) {
						sumX += v.getValue(1);
						sumY += v.getValue(2);
						currentX.add(v.getValue(1));
						currentY.add(v.getValue(2));
						numPoints++;
					}
				}
			}
		}
		g.setColor(Color.BLACK);

		Vector currentVectorX = new Vector(currentX.size());
		for (int i = 0; i < currentX.size(); i++) {
			currentVectorX.setValue(i + 1, currentX.get(i));
		}
		Vector currentVectorY = new Vector(currentY.size());
		for (int i = 0; i < currentY.size(); i++) {
			currentVectorY.setValue(i + 1, currentY.get(i));
		}

		// draw means
		if (displayMeans && numPoints > 0) {
			g2d.setStroke(getDottedStroke1());

			double meanX = sumX / numPoints;
			double meanY = sumY / numPoints;

			Vector meanLeft = new Vector(
					new double[] { -getAxisLength(), meanY });
			Vector meanRight = new Vector(new double[] { getAxisLength(),
					sumY / numPoints });
			Vector meanBottom = new Vector(new double[] { sumX / numPoints,
					-getAxisLength() });
			Vector meanTop = new Vector(new double[] { sumX / numPoints,
					getAxisLength() });

			drawLine(g, meanLeft, meanRight);
			drawLine(g, meanBottom, meanTop);
			DecimalFormat df = new DecimalFormat("0.00");
			g.drawString(df.format(meanY), getPoint(meanLeft).x,
					getPoint(meanLeft).y - 6);
			g.drawString(df.format(meanX), getPoint(meanTop).x - 10,
					getPoint(meanTop).y);
			g2d.setStroke(normalStroke);

		}

		if (displayRegression && currentVectorX.size() > 0) {
			final SimpleRegression sr = new SimpleRegression();
			double[][] vals = new double[currentX.size()][2];
			for (int i = 0; i < vals.length; i++) {
				vals[i][0] = currentVectorX.getValue(i + 1);
				vals[i][1] = currentVectorY.getValue(i + 1);
			}
			sr.addData(vals);

			final Function interval = new RegressionIntervalFunction(
					currentVectorX, false);
			GraphFunction f1 = new GraphFunction(new Function() {

				@Override
				public double f(double x) {

					return sr.predict(x) + interval.f(x);
				}
			}, Color.orange);
			GraphFunction f2 = new GraphFunction(new Function() {

				@Override
				public double f(double x) {
					return sr.predict(x) - interval.f(x);
				}
			}, Color.orange);

			GraphFunction line = new GraphFunction(new Function() {

				@Override
				public double f(double x) {
					return sr.predict(x);
				}
			}, Color.BLACK);
			paintFunction(g, f1);
			paintFunction(g, f2);
			paintFunction(g, line);

		}
	}

	private Color getColor(String[] rowLabels1, String[] rowLabels2, int row) {
		String s = rowLabels1[row - 1] + ":" + rowLabels2[row - 1];
		if (s == null || s.trim().equals(""))
			return colors[0];
		int j = -1;
		for (int i = 0; i < row; i++) {
			if ((rowLabels1[i] + ":" + rowLabels2[i]).equals(s))
				j++;
		}
		return colors[j % colors.length];
	}

	private Vector getVectorRight() {
		return new Vector(new double[] { getAxisLength(), 0 });
	}

	private Vector getVectorLeft() {
		return new Vector(new double[] { -getAxisLength(), 0 });
	}

	private Vector getVectorTop() {
		return new Vector(new double[] { 0, getAxisLength() });
	}

	private Vector getVectorBottom() {
		return new Vector(new double[] { 0, -getAxisLength() });
	}

	public void drawArrowHead(Graphics g, Vector v) {
		final double arrowAngle = Math.PI / 8;
		final int length = 10;

		double angle = Math.PI - arrowAngle;
		Point p = getPoint(v.rotate(angle));
		p.x = p.x - getOrigin().x;
		p.y = p.y - getOrigin().y;
		double factor = length / Math.sqrt(p.x * p.x + p.y * p.y);
		p.x = (int) Math.floor(p.x * factor);
		p.y = (int) Math.floor(p.y * factor);
		g.drawLine(getPoint(v).x, getPoint(v).y, getPoint(v).x + p.x,
				getPoint(v).y + p.y);

		angle = Math.PI + arrowAngle;
		p = getPoint(v.rotate(angle));
		p.x = p.x - getOrigin().x;
		p.y = p.y - getOrigin().y;
		factor = length / Math.sqrt(p.x * p.x + p.y * p.y);
		p.x = (int) Math.floor(p.x * factor);
		p.y = (int) Math.floor(p.y * factor);
		g.drawLine(getPoint(v).x, getPoint(v).y, getPoint(v).x + p.x,
				getPoint(v).y + p.y);
	}

	public void drawMarker(Graphics g, Vector v) {
		final int length = 5;
		Point p = getPoint(v);
		double distance = Math.sqrt(Math.pow(p.x - getOrigin().x, 2)
				+ Math.pow(p.y - getOrigin().y, 2));
		double angle = Math.atan(length / distance);
		Vector v1 = v.rotate(angle);
		Vector v2 = v.rotate(-angle);
		Point p1 = getPoint(v1);
		Point p2 = getPoint(v2);
		g.drawLine(p1.x, p1.y, p2.x, p2.y);
	}

	public boolean getLabelsVisible() {
		return labelsVisible;
	}

	public void setLabelsVisible(boolean labelsVisible) {
		this.labelsVisible = labelsVisible;
	}

	public void addComment(Vector v, String s) {
		comments.put(v, s);
	}

	public String getXLabel() {
		return xLabel;
	}

	public void setXLabel(String label) {
		xLabel = label;
	}

	public String getYLabel() {
		return yLabel;
	}

	public void setYLabel(String label) {
		yLabel = label;
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

//	public void writeAnimatedImage(OutputStream os) {
//		try {
//			boolean useGif4J = false;
//			if (useGif4J) {
//				// despite all the claims on the website I've found this library
//				// not to work as well
//				int numFrames = 50;
//				// create new GifImage instance
//				GifImage gifImage = new GifImage();
//				// set default delay between gif frames
//				gifImage.setDefaultDelay(200);
//				// set infinite looping (by default only 1 looping iteration is
//				// set)
//				gifImage.setLoopNumber(0);
//				// add comment to gif image
//				gifImage.addComment("Animated GIF image");
//
//				for (int i = 0; i <= numFrames - 1; i++) {
//					setProportionDrawn(i * 1.0 / (numFrames - 1));
//					int delay = 10;
//					if (i == numFrames - 1)
//						delay = 400;
//
//					Image image = getImage();
//					GifFrame nextFrame = new GifFrame(image);
//					// clear logic screen after every frame
//					nextFrame
//							.setDisposalMethod(GifFrame.DISPOSAL_METHOD_RESTORE_TO_PREVIOUS);
//					nextFrame.setDelay(delay);
//					gifImage.addGifFrame(nextFrame);
//
//				}
//				// save animated gif image
//				GifEncoder.encode(gifImage, os);
//			} else {
//				AnimGifEncoder encoder = new AnimGifEncoder(os);
//				encoder.setLoop(true);
//				int numFrames = 50;
//				for (int i = 0; i <= numFrames - 1; i++) {
//					setProportionDrawn(i * 1.0 / (numFrames - 1));
//					int delay = 10;
//					if (i == numFrames - 1)
//						delay = 400;
//					encoder.add(getImage(), delay);
//				}
//				System.out.println("writeAnimatedGif: "
//						+ (Runtime.getRuntime().totalMemory() - Runtime
//								.getRuntime().freeMemory()));
//				encoder.encode();
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//			throw new RuntimeException(e);
//		}
//	}

	public static void main(String[] args) throws IOException {
		JFrame frame = new JFrame();
		final Vector v1 = new Vector(new double[] { 0.1, 0.2, 0.3, 0.4, 0.5 });
		Vector v2 = new Vector(new double[] { 0.3, 0.4, 0.7, 0.6, 0.7 });
		Vector v1b = v1.add(0.1);
		Vector v2b = v2.add(0.1);
		Vector v1c = v1.add(v2);
		Vector v2c = v1.minus(v2);
		GraphPanel gp = new GraphPanel(new Vector[] { v1, v1b, v1c },
				new Vector[] { v2, v2b, v2c }, 0.5);
		// g.setDisplayLinearRegression(true);
		gp.setLabelsVisible(false);
		gp.setXLabel("Intersubjective Agreement (Spearman)");
		gp.setYLabel("Preferences Agreement (Spearman)");

		final SimpleRegression sr = new SimpleRegression();
		double[][] vals = new double[v1.size()][2];
		for (int i = 0; i < vals.length; i++) {
			vals[i][0] = v1.getValue(i + 1);
			vals[i][1] = v2.getValue(i + 1);
		}
		sr.addData(vals);

		final Function interval = new Function() {

			@Override
			public double f(double x) {
				double s = Math.sqrt(v1.getResiduals().getSumSquares()
						/ (v1.size() - 2));
				return 1.7
						* s
						* Math.sqrt(1
								+ 1
								/ v1.size()
								+ Math.pow(x - v1.getMean(), 2)
								/ (v1.getSumSquares() - Math.pow(v1.getSum(), 2)
										/ v1.size()));
			}
		};

		gp.addFunction(new Function() {

			@Override
			public double f(double x) {

				return sr.predict(x) + interval.f(x);
			}
		}, Color.orange);
		gp.addFunction(new Function() {

			@Override
			public double f(double x) {
				return sr.predict(x) - interval.f(x);
			}
		}, Color.orange);
		gp.addFunction(new Function() {

			@Override
			public double f(double x) {
				return sr.predict(x);
			}
		}, Color.BLACK);
		DecimalFormat df = new DecimalFormat("0.00");
		gp.addComment(new Vector(new double[] { -0.5, 0.5 }),
				"r2=" + df.format(Math.pow(sr.getR(), 2)));
		gp.setDisplayMeans(true);
		frame.setSize(300, 300);
		SwingUtil.centre(frame);
		frame.add(gp);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
		// gp.setSize(50, 50);
		gp.setBackground(Color.white);

		gp.getImage();
		// FileOutputStream fos = new FileOutputStream("/animated.gif");
		// // gp.writeAnimatedImage(fos);
		// fos.close();
		System.out.println("finished");
	}

	public double getProportionDrawn() {
		return proportionDrawn;
	}

	public void setProportionDrawn(double proportionDrawn) {
		this.proportionDrawn = proportionDrawn;
	}

	public boolean getDisplayMeans() {
		return displayMeans;
	}

	public void setDisplayMeans(boolean displayMeans) {
		this.displayMeans = displayMeans;
	}

	public List<String> getPointLabels() {
		return pointLabels;
	}

	public void setPointLabels(List<String> pointLabels) {
		this.pointLabels = pointLabels;
	}

	public boolean getDisplayRegression() {
		return displayRegression;
	}

	public void setDisplayRegression(boolean displayRegression) {
		this.displayRegression = displayRegression;
	}

	public boolean getDisplayArrowHeads() {
		return displayArrowHeads;
	}

	public void setDisplayArrowHeads(boolean displayArrowHeads) {
		this.displayArrowHeads = displayArrowHeads;
	}

	public Vector[] getVector1() {
		return vector1;
	}

	public void setVector1(Vector[] vector1) {
		this.vector1 = vector1;
	}

	public Vector[] getVector2() {
		return vector2;
	}

	public void setVector2(Vector[] vector2) {
		this.vector2 = vector2;
	}

	public boolean getUseScaling() {
		return useScaling;
	}

	public void setUseScaling(boolean useScaling) {
		this.useScaling = useScaling;
	}

}