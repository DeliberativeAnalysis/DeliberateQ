package com.github.deliberateq.qsort.gui;

import java.util.Collection;
import java.util.List;

import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;

import com.github.deliberateq.qsort.Data;
import com.github.deliberateq.qsort.Data.DataComponents;
import com.github.deliberateq.qsort.DataSelection;
import com.github.deliberateq.qsort.QSort;
import com.github.deliberateq.qsort.gui.injection.ApplicationInjector;
import com.github.deliberateq.util.event.Event;
import com.github.deliberateq.util.event.EventManager;
import com.github.deliberateq.util.event.EventManagerListener;
import com.github.deliberateq.util.math.CorrelationCoefficient;
import com.github.deliberateq.util.math.Matrix;
import com.github.deliberateq.util.math.MatrixProvider;

public class DataTree extends JTree {

	private static final long serialVersionUID = 2392669756231377460L;

	private DefaultMutableTreeNode referenceNode;

	public DataTree(Data data, CorrelationCoefficient cc) {
		super(getRoot(data, cc));
		this.setEditable(true);
		this.setCellEditor(new MyCellEditor());

		setRootVisible(false);
		setShowsRootHandles(true);
		DefaultTreeCellRenderer renderer = new MyRenderer();
		setCellRenderer(renderer);
		EventManager.getInstance().addListener(Events.REFERENCE_SET,
				new EventManagerListener() {
					@Override
					public void notify(Event event) {
						DefaultTreeModel treeModel = (DefaultTreeModel) getModel();
						if (referenceNode != null)
							treeModel.nodeChanged(referenceNode);
						DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) getLastSelectedPathComponent();
						if (selectedNode.getUserObject() instanceof MatrixProvider //
						        && selectedNode.getUserObject().equals(
									Model.getInstance().getReference())) {
							treeModel.nodeChanged(selectedNode);
							referenceNode = selectedNode;
						}
					}
				});
	}

	private static void addDataSelectionNode(final Data data,
			DefaultMutableTreeNode parent, final DataSelection combination, CorrelationCoefficient cc) {
		DefaultMutableTreeNode node = new DefaultMutableTreeNode(combination);
		parent.add(node);
		DefaultMutableTreeNode matrixNode = new DefaultMutableTreeNode(
				new MatrixProvider() {
					@Override
					public Matrix getMatrix() {
						List<QSort> list = data.restrictList(
								combination.getStage(), combination.getParticipantFilter());
						DataComponents d = data.buildMatrix(list, null, cc);
						return d.correlations;
					}

					@Override
					public String toString() {
						return "Intersubjective Correlation";
					}
				});
		node.add(matrixNode);
	}

	private static TreeNode getRoot(Data data, CorrelationCoefficient cc) {
		Collection<String> stageTypes = data.getStageTypes();
		Collection<String> participantTypes = data.getParticipantTypes();
		DefaultMutableTreeNode root = new DefaultMutableTreeNode();
		Configuration configuration = ApplicationInjector.getInjector()
				.getInstance(Configuration.class);
		// nodes by stage and participant type
		if (configuration.provideDataSelectionForEveryVariable())
			for (String participantType : participantTypes) {
				for (String stage : stageTypes) {
					DataSelection combination = new DataSelection(
							data.getParticipantIds(participantType), stage);
					addDataSelectionNode(data, root, combination, cc);
				}
				if (stageTypes.size() > 1) {
					DataSelection combination = new DataSelection(
							data.getParticipantIds(participantType), "all");
					addDataSelectionNode(data, root, combination, cc);
				}
			}
		// nodes by stage
		if (!configuration.provideDataSelectionForEveryVariable()
				|| participantTypes.size() == 0 || participantTypes.size() > 1) {
			for (String stage : stageTypes) {
				DataSelection combination = new DataSelection(
						data.getParticipantIds(), stage);
				addDataSelectionNode(data, root, combination, cc);
			}
		}
		// nodes across all
		if ((!configuration.provideDataSelectionForEveryVariable() || participantTypes
				.size() > 1) && stageTypes.size() > 1) {
			DataSelection combination = new DataSelection(
					data.getParticipantIds(), "all");
			addDataSelectionNode(data, root, combination, cc);
		}
		return root;
	}
}
