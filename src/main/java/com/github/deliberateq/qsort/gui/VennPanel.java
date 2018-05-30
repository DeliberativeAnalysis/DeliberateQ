package com.github.deliberateq.qsort.gui;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.SpringLayout;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.github.deliberateq.util.math.FactorScoreAnalyzer;
import com.github.deliberateq.util.math.Matrix.FactorScoreStrategy;
import com.github.deliberateq.util.math.gui.VennDiagramEdwardsPanel;

public class VennPanel extends javax.swing.JPanel {

    private static final long serialVersionUID = 5154667144682762180L;
    private final JLabel confidenceLabel;
    private final VennInfo vennInfo;
    private final VennDiagramEdwardsPanel vp;
    private final JSlider confidence;
    private final JSlider threshold;
    private final JLabel thresholdLabel;
    private final JComboBox<FactorScoreStrategy> strategy;
    private final FactorSelector selector;
    private float maxStandardErrors = 3.0f;

    public VennPanel(final VennInfo vennInfo) {

        maxStandardErrors = Preferences.getInstance().getFloat(Preferences.VENN_MAX_STANDARD_ERRORS, maxStandardErrors);

        this.vennInfo = vennInfo;
        final SpringLayout layout = new SpringLayout();
        setLayout(layout);

        List<Object> objects = new ArrayList<Object>();
        for (Double threshold : vennInfo.getThresholds()) {
            objects.add(
                    new NamedNumber(new DecimalFormat(">=0.00").format(threshold) + " Standard Deviations", threshold));
        }

        confidenceLabel = new JLabel(" ");
        add(confidenceLabel);

        thresholdLabel = new JLabel(" ");
        add(thresholdLabel);

        confidence = new JSlider(50, 100, 95);
        add(confidence);
        confidence.setPreferredSize(new Dimension(100, confidence.getPreferredSize().height));

        threshold = new JSlider(0, 250);
        threshold.setValue(Math.round(1 / maxStandardErrors * threshold.getMaximum()));
        add(threshold);

        strategy = new JComboBox<FactorScoreStrategy>(FactorScoreStrategy.values());
        add(strategy);

        JLabel strategyLabel = new JLabel("Strategy:");
        add(strategyLabel);

        int maxSelectable = 5;
        selector = new FactorSelector(vennInfo.getRotations().getLoadings().columnCount(), maxSelectable);
        for (int i = 0; i < vennInfo.getSelected().length; i++) {
            selector.setSelected(i + 1, vennInfo.getSelected()[i]);
        }
        JScrollPane selectorScroll = new JScrollPane(selector);
        selectorScroll.setBorder(BorderFactory.createEmptyBorder());
        add(selectorScroll);

        vp = new VennDiagramEdwardsPanel(Math.min(maxSelectable, vennInfo.getRotations().getLoadings().columnCount()));
        add(vp);

        layout.putConstraint(SpringLayout.NORTH, confidenceLabel, 10, SpringLayout.SOUTH, strategy);
        layout.putConstraint(SpringLayout.WEST, confidenceLabel, 0, SpringLayout.WEST, confidence);
        layout.putConstraint(SpringLayout.EAST, confidenceLabel, 0, SpringLayout.EAST, confidence);

        layout.putConstraint(SpringLayout.NORTH, thresholdLabel, 0, SpringLayout.NORTH, confidenceLabel);
        layout.putConstraint(SpringLayout.WEST, thresholdLabel, 0, SpringLayout.WEST, threshold);
        layout.putConstraint(SpringLayout.EAST, thresholdLabel, 0, SpringLayout.EAST, threshold);

        layout.putConstraint(SpringLayout.NORTH, confidence, 0, SpringLayout.SOUTH, confidenceLabel);
        layout.putConstraint(SpringLayout.WEST, confidence, 10, SpringLayout.WEST, this);

        layout.putConstraint(SpringLayout.NORTH, threshold, 0, SpringLayout.NORTH, confidence);
        layout.putConstraint(SpringLayout.WEST, threshold, 10, SpringLayout.EAST, confidence);
        layout.putConstraint(SpringLayout.EAST, threshold, 0, SpringLayout.EAST, strategy);

        layout.putConstraint(SpringLayout.NORTH, strategy, 10, SpringLayout.NORTH, this);
        layout.putConstraint(SpringLayout.WEST, strategyLabel, 10, SpringLayout.WEST, this);
        layout.putConstraint(SpringLayout.VERTICAL_CENTER, strategyLabel, 0, SpringLayout.VERTICAL_CENTER, strategy);
        layout.putConstraint(SpringLayout.WEST, strategy, 3, SpringLayout.EAST, strategyLabel);

        layout.putConstraint(SpringLayout.NORTH, selectorScroll, 0, SpringLayout.NORTH, strategy);
        layout.putConstraint(SpringLayout.WEST, selectorScroll, 10, SpringLayout.EAST, strategy);
        layout.putConstraint(SpringLayout.SOUTH, selectorScroll, 0, SpringLayout.SOUTH, confidence);
        layout.putConstraint(SpringLayout.EAST, selectorScroll, 0, SpringLayout.EAST, this);
        layout.putConstraint(SpringLayout.NORTH, vp, 5, SpringLayout.SOUTH, confidence);
        layout.putConstraint(SpringLayout.SOUTH, vp, 0, SpringLayout.SOUTH, this);
        layout.putConstraint(SpringLayout.WEST, vp, 0, SpringLayout.WEST, this);
        layout.putConstraint(SpringLayout.EAST, vp, 0, SpringLayout.EAST, this);

        setComponentZOrder(confidenceLabel, 0);
        setComponentZOrder(confidence, 1);
        setComponentZOrder(vp, 2);

        confidence.setValue(Math.round(vennInfo.getConfidence()));
        setThresholdSE(vennInfo.getThresholdSE());
        strategy.setSelectedItem(vennInfo.getStrategy());
        confidence.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent event) {
                updateVenn();
            }
        });
        threshold.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent event) {
                updateVenn();
            }
        });
        strategy.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                updateVenn();
            }
        });

        selector.addChangeListener(new ChangeListener() {

            @Override
            public void stateChanged(ChangeEvent e) {
                updateVenn();
            }
        });
        updateVenn();
    }

    private void updateVenn() {

        float confidenceValue = confidence.getValue();

        float thresholdSE = getThresholdSE();

        FactorScoreStrategy strat = (FactorScoreStrategy) strategy.getSelectedItem();

        boolean[] selected = selector.getSelected();
        vennInfo.setThresholdSE(thresholdSE);
        vennInfo.setConfidence(confidenceValue);
        vennInfo.setStrategy(strat);
        vennInfo.setSelected(selected);
        double thresholdValue = vennInfo.getThreshold();

        FactorScoreAnalyzer fsa = vennInfo.getFactorScoreAnalyzer(thresholdValue, strat);

        Map<String, String> map = fsa.getVennMappings(thresholdValue, selector.getSelected());
        vp.clearLabels();
        int count = 0;
        // set the factor labels in vp
        for (int i = 0; i < selected.length; i++) {
            if (selected[i]) {
                count++;
                vp.setFactorLabel(count, "F" + (i + 1));
            }
            selector.setSignificant(i + 1, !fsa.getScores().getColumnVector(i + 1).isNaN());
        }
        vp.setNumberSets(count);
        for (String s : map.keySet()) {
            vp.setLabel(s, map.get(s));
            String[] items = map.get(s).split(",");
            StringBuffer info = new StringBuffer();
            info.append("<html>");
            info.append("<table width=\"250px\">");
            for (String item : items) {
                Integer statementNo = Integer.parseInt(item);
                String statement = vennInfo.getStatements().get(Math.abs(statementNo));
                if (statement != null)
                    info.append("<font " + (statementNo < 0 ? "color=\"red\">" : "/>") + "<tr><td valign=\"top\">"
                            + "<b>" + Math.abs(statementNo) + "</b></td><td valign=\"top\"> " + statement
                            + "</font></td></tr></font>");
            }
            info.append("</table></html>");
            vp.getLabelInfos().put(s, info.toString());
            vp.update();
        }
        confidenceLabel.setText("Confidence " + new DecimalFormat("0.0").format(confidenceValue) + "%");
        thresholdLabel.setText("Threshold " + new DecimalFormat("0.00").format(thresholdSE) + " SE = "
                + new DecimalFormat("0.00").format(thresholdValue));
        repaint();
    }

    private float getThresholdSE() {
        return threshold.getValue() / (float) threshold.getMaximum() * maxStandardErrors;
    }

    private void setThresholdSE(float thresholdSE) {
        threshold.setValue(Math.round(thresholdSE / maxStandardErrors * threshold.getMaximum()));
    }
}
