package au.edu.anu.delibdem.qsort.gui;

import java.awt.GridLayout;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JPanel;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeSelectionModel;

import moten.david.util.event.Event;
import moten.david.util.event.EventManager;
import moten.david.util.math.EigenvalueThreshold;
import moten.david.util.math.EigenvalueThreshold.PrincipalFactorCriterion;
import moten.david.util.math.FactorAnalysisResults;
import moten.david.util.math.Matrix;
import moten.david.util.math.MatrixProvider;

public class FactorTreePanel extends JPanel {

	private static final long serialVersionUID = -1191102361903304632L;

	private final JTree tree;

	private final List<EventManager> eventManagers = new ArrayList<EventManager>();

	public FactorTreePanel(FactorAnalysisResults results) {
		setLayout(new GridLayout(1, 1));
		DefaultMutableTreeNode top = createNodes(results);
		tree = new JTree(top);
		tree.getSelectionModel().setSelectionMode(
				TreeSelectionModel.SINGLE_TREE_SELECTION);
		add(tree);
	}

	private static String getCapitalized(String s) {
		return Character.toUpperCase(s.charAt(0)) + s.substring(1);
	}

	private static DefaultMutableTreeNode createGetterNode(final String name,
			final Object o, String fieldName) throws SecurityException,
			NoSuchFieldException {
		String methodName = "get" + getCapitalized(fieldName);
		final Method method;
		try {
			method = o.getClass().getMethod(methodName, new Class[] {});
		} catch (NoSuchMethodException e) {
			throw new RuntimeException(e);
		}
		MatrixProvider matrixProvider = new MatrixProvider() {
			@Override
			public Matrix getMatrix() {
				try {
					Object result = method.invoke(o, (Object[]) new Class[] {});
					if (result instanceof Matrix)
						return (Matrix) result;
					else if (result instanceof Double) {
						Matrix m = new Matrix(
								new double[][] { { (Double) result } });
						m.setRowLabel(1, "Item");
						m.setColumnLabel(1, "Value");
						return m;
					} else if (result instanceof EigenvalueThreshold) {
						EigenvalueThreshold et = (EigenvalueThreshold) result;
						return getMatrix(et);
					} else
						throw new RuntimeException("not sure how to implement "
								+ result);
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
			}

			private Matrix getMatrix(EigenvalueThreshold et) {
				Matrix m = new Matrix(new double[][] { { 0 } });
				m.setColumnLabel(1, "Value");
				if (et.getPrincipalFactorCriterion().equals(
						PrincipalFactorCriterion.MAX_FACTORS)) {
					m.setValue(1, 1, et.getMaxFactors());
					m.setRowLabel(1, "Max factors");
				} else {
					m.setValue(1, 1, et.getMinEigenvalue());
					m.setRowLabel(1, "Min eigenvalue");
				}
				return m;
			}

			@Override
			public String toString() {
				return name;
			}

		};
		return new DefaultMutableTreeNode(matrixProvider);
	}

	public static DefaultMutableTreeNode createNodes(
			FactorAnalysisResults results) {
		DefaultMutableTreeNode top = new DefaultMutableTreeNode(
				new ObjectDecorator(results,
						results.extractionMethod.toString()));

		try {
			top.add(createGetterNode("Raw Data", results, "initial"));
			top.add(createGetterNode("Correlations", results, "correlations"));
			top.add(createGetterNode("Eigenvalues", results,
					"eigenvaluesVector"));
			top.add(createGetterNode("Eigenvectors", results, "eigenvectors"));
			top.add(createGetterNode("Loadings", results, "loadings"));
			top.add(createGetterNode("Percent Variance", results,
					"percentVariance"));
			top.add(createGetterNode("Eigenvalue Threshold", results,
					"eigenvalueThreshold"));
			top.add(createGetterNode("Principal Eigenvalues", results,
					"principalEigenvaluesVector"));
			top.add(createGetterNode("Principal Eigenvectors", results,
					"principalEigenvectors"));
			top.add(createGetterNode("Principal Loadings", results,
					"principalLoadings"));
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return top;
	}

	public void addEventManager(final EventManager eventManager) {
		eventManagers.add(eventManager);
		tree.addTreeSelectionListener(new TreeSelectionListener() {

			@Override
			public void valueChanged(TreeSelectionEvent e) {
				DefaultMutableTreeNode node = (DefaultMutableTreeNode) tree
						.getLastSelectedPathComponent();

				/* if nothing is selected */
				if (node == null)
					return;

				/* retrieve the node that was selected */
				Object nodeInfo = node.getUserObject();
				if (nodeInfo instanceof MatrixProvider) {
					eventManager.notify(new Event(((MatrixProvider) nodeInfo)
							.getMatrix(), Events.MATRIX));

				}
			}
		});
	}

}
