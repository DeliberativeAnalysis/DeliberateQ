package au.edu.anu.delibdem.qsort.gui;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ButtonGroup;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.SpringLayout;

import moten.david.util.math.EigenvalueThreshold;
import moten.david.util.math.EigenvalueThreshold.PrincipalFactorCriterion;
import moten.david.util.math.FactorExtractionMethod;

public class AnalyzeOptionsPanel extends JPanel {

	private static final long serialVersionUID = -8927311009623161871L;
	private final JTextField maxFactorsField;
	private final JTextField minEigenvalueField;
	private final JRadioButton maxFactors;
	private final JRadioButton minEigenvalue;
	private final JComboBox<FactorExtractionMethod> method;

	public AnalyzeOptionsPanel() {
		SpringLayout layout = new SpringLayout();
		setLayout(layout);
		ButtonGroup group = new ButtonGroup();
		maxFactors = new JRadioButton("Maximum number of factors:");
		minEigenvalue = new JRadioButton("Minimum eigenvalue:");
		// TODO make maxFactors configurable
		int maxFactorsDefault = Preferences.getInstance().getMaxFactors();
		maxFactorsField = new JTextField(maxFactorsDefault + "", 5);
		minEigenvalueField = new JTextField(getDefaultMinEigenvalue() + "", 5);
		group.add(maxFactors);
		group.add(minEigenvalue);
		ActionListener selectionActionListener = createSelectionActionListener();
		maxFactors.addActionListener(selectionActionListener);
		minEigenvalue.addActionListener(selectionActionListener);
		maxFactors.setSelected(true);

		method = createMethodComboBox();
		JLabel methodLabel = new JLabel("Method:");

		add(maxFactors);
		add(maxFactorsField);
		add(minEigenvalue);
		add(minEigenvalueField);
		add(methodLabel);
		add(method);

		layout.putConstraint(SpringLayout.NORTH, methodLabel, 5,
				SpringLayout.NORTH, this);
		layout.putConstraint(SpringLayout.WEST, methodLabel, 5,
				SpringLayout.WEST, this);
		layout.putConstraint(SpringLayout.VERTICAL_CENTER, method, 0,
				SpringLayout.VERTICAL_CENTER, methodLabel);
		layout.putConstraint(SpringLayout.WEST, method, 5, SpringLayout.EAST,
				methodLabel);
		layout.putConstraint(SpringLayout.NORTH, maxFactorsField, 5,
				SpringLayout.SOUTH, method);
		layout.putConstraint(SpringLayout.WEST, maxFactors, 5,
				SpringLayout.WEST, this);
		layout.putConstraint(SpringLayout.VERTICAL_CENTER, maxFactors, 0,
				SpringLayout.VERTICAL_CENTER, maxFactorsField);
		layout.putConstraint(SpringLayout.WEST, maxFactorsField, 5,
				SpringLayout.EAST, maxFactors);

		layout.putConstraint(SpringLayout.VERTICAL_CENTER, minEigenvalue, 0,
				SpringLayout.VERTICAL_CENTER, minEigenvalueField);
		layout.putConstraint(SpringLayout.WEST, minEigenvalue, 0,
				SpringLayout.WEST, maxFactors);
		layout.putConstraint(SpringLayout.NORTH, minEigenvalueField, 5,
				SpringLayout.SOUTH, maxFactorsField);
		layout.putConstraint(SpringLayout.WEST, minEigenvalueField, 5,
				SpringLayout.EAST, minEigenvalue);
		updateDisplay();

	}

	private double getDefaultMinEigenvalue() {
		double minEigenvalue = Preferences.getInstance().getMinEigenvalue();
		return minEigenvalue;
	}

	private JComboBox<FactorExtractionMethod> createMethodComboBox() {
		JComboBox<FactorExtractionMethod> combo = new JComboBox<FactorExtractionMethod>(
				new FactorExtractionMethod[] {
						FactorExtractionMethod.PRINCIPAL_COMPONENTS_ANALYSIS,
						FactorExtractionMethod.CENTROID_METHOD });
		combo.setSelectedItem(FactorExtractionMethod.PRINCIPAL_COMPONENTS_ANALYSIS);
		return combo;
	}

	private ActionListener createSelectionActionListener() {
		return new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				updateDisplay();
			}
		};
	}

	private void updateDisplay() {
		maxFactorsField.setEnabled(maxFactors.isSelected());
		minEigenvalueField.setEnabled(minEigenvalue.isSelected());
	}

	public EigenvalueThreshold getEigenvalueThreshold() {
		EigenvalueThreshold t = new EigenvalueThreshold();
		if (maxFactors.isSelected()) {
			t.setPrincipalFactorCriterion(PrincipalFactorCriterion.MAX_FACTORS);
			t.setMaxFactors(Integer.parseInt(maxFactorsField.getText()));
		} else {
			t.setPrincipalFactorCriterion(PrincipalFactorCriterion.MIN_EIGENVALUE);
			t.setMinEigenvalue(Double.parseDouble(minEigenvalueField.getText()));
		}
		return t;
	}

	public FactorExtractionMethod getFactorExtractionMethod() {
		return (FactorExtractionMethod) method.getSelectedItem();
	}

	public static void main(String[] args) {
		LookAndFeel.setLookAndFeel();
		AnalyzeOptionsPanel panel = new AnalyzeOptionsPanel();
		panel.setPreferredSize(new Dimension(300, 200));
		JOptionPane.showOptionDialog(null, panel, "Analysis Options",
				JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE, null,
				null, null);
	}
}
