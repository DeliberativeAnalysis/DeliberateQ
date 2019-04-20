package com.github.deliberateq.qsort.gui;

import java.awt.Color;
import java.io.IOException;
import java.io.OutputStream;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.imageio.ImageIO;

import org.apache.commons.math3.stat.regression.SimpleRegression;

import com.github.deliberateq.qsort.Data;
import com.github.deliberateq.qsort.Data.DataComponents;
import com.github.deliberateq.qsort.QSort;
import com.github.deliberateq.util.math.CorrelationCoefficient;
import com.github.deliberateq.util.math.Function;
import com.github.deliberateq.util.math.Matrix;
import com.github.deliberateq.util.math.RegressionIntervalFunction;
import com.github.deliberateq.util.math.Vector;
import com.github.deliberateq.util.math.gui.GraphPanel;

public class DataRenderer {

    public static final String PREDICTION_INTERVAL_95 = "Prediction_Interval_95";

    private final Data data;

    public DataRenderer(Data data) {
        this.data = data;
    }

    public void graph(String stage, String bands, OutputStream imageOutputStream,
            boolean labelPoints, int size, Set<String> filter, CorrelationCoefficient cc)
            throws IOException {
        List<QSort> subList = data.restrictList(stage, filter);
        if (stage.equals("all"))
            graphConnected(subList, imageOutputStream, labelPoints, size, filter, cc);
        else
            graph(subList, imageOutputStream, labelPoints, size, filter, bands, cc);
    }

    public GraphPanel getGraph(List<QSort> list, boolean labelPoints, int size, Set<String> filter,
            final String bands, boolean includeRegressionLines, CorrelationCoefficient cc) {
        DataComponents d = data.buildMatrix(list, filter, cc);
        if (d == null)
            return null;
        Matrix m = d.correlations;
        if (m == null)
            return null;
        // if (textOutputStream != null)
        // textOutputStream.write(m.getDelimited(TAB, true).getBytes());

        final Vector v1 = m.getColumnVector(1);
        final Vector v2 = m.getColumnVector(2);
        for (int i = 1; i <= v1.rowCount(); i++) {
            v1.setRowLabel(i, d.participants2.get(i - 1));
            v2.setRowLabel(i, d.participants1.get(i - 1));
        }

        GraphPanel gp = new GraphPanel(v1, v2);
        gp.setDisplayMeans(true);
        gp.setDisplayArrowHeads(false);
        gp.setBackground(Color.white);
        gp.setLabelsVisible(labelPoints);
        gp.setSize(size, size);
        setXYLabels(gp, cc);
        final SimpleRegression sr = new SimpleRegression();
        double[][] vals = new double[v1.size()][2];
        for (int i = 0; i < vals.length; i++) {
            vals[i][0] = v1.getValue(i + 1);
            vals[i][1] = v2.getValue(i + 1);
        }
        sr.addData(vals);

        double sumDistanceFromXEqualsY = 0;
        double sumDistanceSquaredFromXEqualsY = 0;
        for (int i = 0; i < vals.length; i++) {
            double x = Math.abs(v1.getValue(i + 1) - v2.getValue(i + 1)) / Math.sqrt(2);
            sumDistanceFromXEqualsY += x;
            sumDistanceSquaredFromXEqualsY += x * x;
        }
        final double meanDistanceFromXEqualsY = sumDistanceFromXEqualsY / vals.length;
        final double meanStandardErrorFromXEqualsY = Math
                .sqrt(sumDistanceSquaredFromXEqualsY / vals.length);

        
        final double concordance = v1.getCorrelation(v2, CorrelationCoefficient.CONCORDANCE);
        if (includeRegressionLines) {
            final Function interval = new RegressionIntervalFunction(v1,
                    PREDICTION_INTERVAL_95.equals(bands));

            gp.addFunction(new Function() {

                @Override
                public double f(double x) {

                    return sr.predict(x) + interval.f(x);
                }
            }, Color.lightGray);
            gp.addFunction(new Function() {

                @Override
                public double f(double x) {
                    return sr.predict(x) - interval.f(x);
                }
            }, Color.lightGray);
            gp.addFunction(new Function() {

                @Override
                public double f(double x) {
                    return sr.predict(x);
                }
            }, Color.BLACK);
        }
        DecimalFormat df = new DecimalFormat("0.000");
        // Simon Niemeyer likes to see Concordance here (separate from the 
        // the correlation coefficient type used on the input data)
        gp.addComment(new Vector(new double[] { -0.8, 0.8 }),
                "r2=" + df.format(Math.pow(sr.getR(), 2)) //
                        + ", D=" + df.format(meanDistanceFromXEqualsY) //
        //                + ", D2=" + df.format(meanStandardErrorFromXEqualsY) //
                        + ", " + "Con=" + df.format(concordance));
        return gp;
    }

    private void graph(List<QSort> list, OutputStream imageOutputStream, boolean labelPoints,
            int size, Set<String> filter, String bands, CorrelationCoefficient cc)
            throws IOException {

        GraphPanel gp = getGraph(list, labelPoints, size, filter, bands, true, cc);
        if (gp != null)
            writeImage(gp, size, imageOutputStream);
    }

    private void graphConnected(List<QSort> list, OutputStream imageOutputStream,
            boolean labelPoints, int size, Set<String> participantFilter, CorrelationCoefficient cc)
            throws IOException {
        // split the list into separate lists by stage
        Map<String, List<QSort>> map = new LinkedHashMap<String, List<QSort>>();
        for (QSort q : list) {
            if (map.get(q.getStage()) == null)
                map.put(q.getStage(), new ArrayList<QSort>());
            map.get(q.getStage()).add(q);
        }

        @SuppressWarnings("unchecked")
        GraphPanel gp = getGraphConnected(map.values().toArray(new ArrayList[1]), labelPoints, size,
                participantFilter, cc);

        if (gp != null) {
            gp.setDisplayMeans(true);
            gp.setDisplayRegression(true);
            if (size <= 1000)
                writeImageConnected(gp, size, imageOutputStream);
            else
                writeImage(gp, size, imageOutputStream);
        }
    }

    private void writeImage(GraphPanel gp, int imageSize, OutputStream imageOs) throws IOException {
        gp.setSize(imageSize, imageSize);
        ImageIO.write(gp.getImage(), "jpeg", imageOs);
    }

    private void writeImageConnected(GraphPanel gp, int imageSize, OutputStream imageOs)
            throws IOException {
        gp.setSize(imageSize, imageSize);
        throw new RuntimeException("animated gif not supported");
//      gp.writeAnimatedImage(imageOs);
    }

    public GraphPanel getGraphConnected(List<QSort>[] list, boolean labelPoints, int size,
            Set<String> filter, CorrelationCoefficient cc) {
        List<Vector> vectors1 = new ArrayList<Vector>();
        List<Vector> vectors2 = new ArrayList<Vector>();
        for (int vi = 0; vi < list.length; vi++) {
            DataComponents d = data.buildMatrix(list[vi], filter, cc);
            if (d == null)
                return null;
            Matrix m = d.correlations;
            if (m == null)
                return null;

            Vector v1 = m.getColumnVector(1);
            Vector v2 = m.getColumnVector(2);
            for (int i = 1; i <= v1.rowCount(); i++) {
                v1.setRowLabel(i, d.participants2.get(i - 1));
                v2.setRowLabel(i, d.participants1.get(i - 1));
            }
            vectors1.add(v1);
            vectors2.add(v2);
        }

        GraphPanel gp = new GraphPanel(vectors1.toArray(new Vector[0]),
                vectors2.toArray(new Vector[0]));
        gp.setDisplayArrowHeads(false);
        gp.setBackground(Color.white);
        gp.setLabelsVisible(labelPoints);
        gp.setSize(size, size);
        setXYLabels(gp, cc);
        return gp;
    }

    private static void setXYLabels(GraphPanel gp, CorrelationCoefficient cc) {
        gp.setXLabel("Intersubjective Agreement (" + cc.abbreviatedName() + ")");
        gp.setYLabel("Preferences Agreement (" + cc.abbreviatedName() + ")");
    }
}
