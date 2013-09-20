package au.edu.anu.delibdem.qsort.gui;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTree;
import javax.swing.SpringLayout;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.table.TableModel;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

import moten.david.util.event.Event;
import moten.david.util.event.EventManager;
import moten.david.util.event.EventManagerListener;
import moten.david.util.math.FactorAnalysisResults;
import moten.david.util.math.Matrix;
import moten.david.util.math.MatrixProvider;
import moten.david.util.math.StringFilter;
import moten.david.util.math.Vector;
import moten.david.util.math.gui.JMatrixViewer;
import moten.david.util.math.gui.NamedMatrix;
import au.edu.anu.delibdem.qsort.Data;
import au.edu.anu.delibdem.qsort.DataSelection;
import au.edu.anu.delibdem.qsort.gui.loadings.LoadingsPanel;

public class DataPanel extends JPanel {

	private static final Logger log = Logger.getLogger(DataPanel.class
			.getName());

	private static final long serialVersionUID = -5460095692478216872L;

	private final JTree tree;

	private final EventManager localEventManager;

	private final Data data;

	private final DataViewerPanel dataViewer;

	private DefaultMutableTreeNode referenceNode;

	public DataPanel(final Data data) {
		localEventManager = new EventManager();
		this.data = data;
		setLayout(new GridLayout(1, 1));

		JPanel left = new JPanel();
		SpringLayout layout = new SpringLayout();
		left.setLayout(layout);
		tree = new DataTree(data);
		LinkButton filter = new LinkButton("Filter Participants...");
		left.add(filter);
		// LinkButton newDataSelection = new LinkButton("New...");
		// left.add(newDataSelection);
		JScrollPane scroll = new JScrollPane(tree);
		scroll.setBorder(BorderFactory.createEmptyBorder());
		left.add(scroll);

		filter.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				EventManager.getInstance().notify(
						new Event(data, Events.FILTER));
			}
		});

		// newDataSelection.addActionListener(new ActionListener() {
		// public void actionPerformed(ActionEvent e) {
		// EventManager.getInstance().notify(
		// new Event(data, Events.NEW_DATA_COMBINATION));
		// }
		// });

		layout.putConstraint(SpringLayout.NORTH, filter, 2, SpringLayout.NORTH,
				left);
		layout.putConstraint(SpringLayout.WEST, filter, 6, SpringLayout.WEST,
				left);
		// layout.putConstraint(SpringLayout.NORTH, newDataSelection, 5,
		// SpringLayout.SOUTH, filter);
		// layout.putConstraint(SpringLayout.WEST, newDataSelection, 6,
		// SpringLayout.WEST, left);
		layout.putConstraint(SpringLayout.NORTH, scroll, 5, SpringLayout.SOUTH,
				filter);
		layout.putConstraint(SpringLayout.SOUTH, scroll, 0, SpringLayout.SOUTH,
				left);
		layout.putConstraint(SpringLayout.WEST, scroll, 5, SpringLayout.WEST,
				left);
		layout.putConstraint(SpringLayout.EAST, scroll, -5, SpringLayout.EAST,
				left);

		JSplitPane split = new JSplitPane();
		split.setOneTouchExpandable(true);
		split.setContinuousLayout(true);
		add(split);
		split.setLeftComponent(left);

		dataViewer = new DataViewerPanel(split);
		split.setRightComponent(dataViewer);
		EventManager.getInstance().addListener(Events.DATA_CHANGED,
				createDataChangedListener());
		tree.addMouseListener(createTreeMouseListener());
		tree.addTreeSelectionListener(createTreeSelectionListener());
		split.setDividerLocation(250);
		tree.setSelectionInterval(0, 0);
		tree.requestFocus();
		localEventManager.addListener(Events.ANALYZED, createAnalyzeListener());
		localEventManager.addListener(Events.MATRIX, createMatrixListener());
		localEventManager.addListener(Events.ROTATIONS,
				createRotationListener());
		localEventManager.addListener(Events.VENN, createVennListener());
		localEventManager.addListener(Events.SET_REFERENCE_REQUESTED,
				createReferenceSetterListener());
		localEventManager.addListener(Events.TABLE_CHANGED,
				createTableChangedListener());
		EventManager.getInstance().addListener(Events.ADD_ROTATION,
				createAddRotationListener());
		EventManager.getInstance().addListener(Events.ADD_VENN,
				createAddVennListener());
	}

	private EventManagerListener createTableChangedListener() {
		return new EventManagerListener() {
			@Override
			public void notify(Event event) {
				DefaultMutableTreeNode node = (DefaultMutableTreeNode) tree
						.getLastSelectedPathComponent();
				if (node == null)
					return;
				final Object o = node.getUserObject();
				if (o instanceof Rotations) {
					Rotations rotations = (Rotations) o;
					TableModel model = (TableModel) event.getObject();
					for (int i = 1; i <= model.getRowCount(); i++) {
						rotations.setUseWithReference(i,
								(Boolean) model.getValueAt(i - 1, 1));
					}
				}
			}
		};
	}

	private EventManagerListener createReferenceSetterListener() {
		return new EventManagerListener() {
			@Override
			public void notify(Event event) {
				DefaultMutableTreeNode node = (DefaultMutableTreeNode) tree
						.getLastSelectedPathComponent();
				if (node == null)
					return;
				final Object o = node.getUserObject();
				if (o instanceof Rotations) {
					setReference(node, new MatrixProvider() {
						@Override
						public Matrix getMatrix() {
							return ((Rotations) o).getLoadings();
						}
					});
				} else if (o instanceof MatrixProvider) {
					setReference(node, o);
				}
			}
		};
	}

	private void setReference(DefaultMutableTreeNode node, Object reference) {

		EventManager.getInstance().notify(
				new Event(reference, Events.SET_REFERENCE));

		DefaultTreeModel treeModel = (DefaultTreeModel) tree.getModel();
		if (referenceNode != null)
			treeModel.nodeChanged(referenceNode);
		referenceNode = node;
		treeModel.nodeChanged(referenceNode);
	}

	private EventManagerListener createVennListener() {
		return new EventManagerListener() {
			@Override
			public void notify(Event event) {
				VennInfo v = (VennInfo) event.getObject();
				VennPanel vp = new VennPanel(v);
				dataViewer.setContent(vp);
				repaint();
			}
		};
	}

	private EventManagerListener createAddVennListener() {
		return new EventManagerListener() {
			@Override
			public void notify(Event event) {
				DefaultMutableTreeNode node = (DefaultMutableTreeNode) tree
						.getLastSelectedPathComponent();
				if (node == null)
					return;
				Object o = node.getUserObject();
				if (o instanceof Rotations) {
					FactorAnalysisResults r;
					try {
						r = (FactorAnalysisResults) ((DefaultMutableTreeNode) node
								.getParent().getParent().getParent())
								.getUserObject();
					} catch (Exception e) {
						e.printStackTrace();
						return;
					}
					Rotations rotations = (Rotations) o;
					VennInfo vennInfo = new VennInfo(r.getInitial(), rotations,
							data.getStatements());
					DefaultMutableTreeNode newNode = new DefaultMutableTreeNode(
							vennInfo);
					DefaultTreeModel treeModel = (DefaultTreeModel) tree
							.getModel();
					treeModel.insertNodeInto(newNode, node,
							node.getChildCount());
					tree.scrollPathToVisible(new TreePath(newNode.getPath()));
					int i = tree.getRowForPath(new TreePath(newNode.getPath()));
					tree.setSelectionInterval(i, i);
				}
			}
		};
	}

	private EventManagerListener createDataChangedListener() {
		return new EventManagerListener() {
			@Override
			public void notify(Event event) {
				DefaultMutableTreeNode node = (DefaultMutableTreeNode) tree
						.getLastSelectedPathComponent();
				if (node == null)
					return;
				Object o = node.getUserObject();
				if (o instanceof DataSelection) {
					updateDataViewer((DataSelection) o);
				}
			}
		};
	}

	private MouseListener createTreeMouseListener() {
		return new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				// int selRow = tree.getRowForLocation(e.getX(), e.getY());
				// TreePath selPath = tree.getPathForLocation(e.getX(),
				// e.getY());
				// if (selRow != -1) {
				// if (e.getClickCount() == 1) {
				// // mySingleClick(selRow, selPath);
				// } else if (e.getClickCount() == 2 && false) {
				// DefaultMutableTreeNode node = (DefaultMutableTreeNode)
				// selPath
				// .getLastPathComponent();
				// if (node.getChildCount() == 0)
				// EventManager.getInstance().notify(
				// new Event(node.getUserObject(),
				// Events.OPEN_OBJECT));
				// }
				// }
			}
		};
	}

	private TreeSelectionListener createTreeSelectionListener() {
		return new TreeSelectionListener() {
			@Override
			public void valueChanged(TreeSelectionEvent e) {
				DefaultMutableTreeNode node = (DefaultMutableTreeNode) tree
						.getLastSelectedPathComponent();
				if (node == null)
					return;
				Object o = node.getUserObject();
				log.info((o == null ? null : o.toString()));
				if (o instanceof DataSelection) {
					updateDataViewer((DataSelection) o);
				} else if (o instanceof RotatedLoadings) {
					RotatedLoadings rotatedLoadings = (RotatedLoadings) o;
					localEventManager.notify(new Event(rotatedLoadings,
							Events.MATRIX));
				} else if (o instanceof Rotations) {
					Rotations r = (Rotations) o;
					localEventManager.notify(new Event(r, Events.ROTATIONS));
				} else if (o instanceof VennInfo) {
					VennInfo v = (VennInfo) o;
					localEventManager.notify(new Event(v, Events.VENN));
				} else if (o instanceof MatrixProvider) {
					MatrixProvider matrixProvider = (MatrixProvider) o;
					localEventManager.notify(new Event(matrixProvider,
							Events.MATRIX));
				}
			}
		};
	}

	private EventManagerListener createRotationListener() {
		return new EventManagerListener() {
			@Override
			public void notify(Event event) {
				Rotations rotations = (Rotations) event.getObject();
				LoadingsPanel panel = new LoadingsPanel(rotations,
						createRowLabelsFilter());
				dataViewer.setContent(panel);
			}

			private StringFilter createRowLabelsFilter() {
				return new StringFilter() {
					@Override
					public boolean accept(String s) {
						return data.getFilter().contains(s);
					}
				};
			}
		};
	}

	private EventManagerListener createMatrixListener() {
		return new EventManagerListener() {
			@Override
			public void notify(Event event) {
				boolean[] checked = null;
				Matrix matrix;
				if (event.getObject() instanceof Matrix) {
					matrix = (Matrix) event.getObject();
				} else if (event.getObject() instanceof Rotations) {
					Rotations rotations = (Rotations) event.getObject();
					matrix = rotations.getRotatedLoadings();
					checked = rotations.getUseWithReference();
				} else if (event.getObject() instanceof MatrixProvider) {
					MatrixProvider matrixProvider = (MatrixProvider) event
							.getObject();
					matrix = matrixProvider.getMatrix();
				} else
					throw new RuntimeException(
							"unexpected object to display as Matrix:"
									+ event.getObject());
				NamedMatrix namedMatrix = new NamedMatrix("", matrix);
				if (namedMatrix.getMatrix() == null) {
					dataViewer.removeAll();
				} else {
					JMatrixViewer matrixViewer = new JMatrixViewer();
					matrixViewer.setNamedMatrix(namedMatrix);
					if (checked != null)
						matrixViewer.setChecked(checked);
					else
						matrixViewer.removeCheckBoxColumn();
					dataViewer.setContent(matrixViewer);
					matrixViewer.addEventManager(localEventManager);
				}
			}
		};
	}

	private EventManagerListener createAddRotationListener() {

		return new EventManagerListener() {
			@Override
			public void notify(Event arg0) {
				DefaultMutableTreeNode node = (DefaultMutableTreeNode) tree
						.getLastSelectedPathComponent();
				if (node == null)
					return;
				Object o = node.getUserObject();
				if (o instanceof MatrixProvider) {
					Matrix matrix = ((MatrixProvider) o).getMatrix();
					final Rotations rotations = new Rotations(matrix);
					DefaultMutableTreeNode newNode = new DefaultMutableTreeNode(
							rotations);
					DefaultTreeModel treeModel = (DefaultTreeModel) tree
							.getModel();
					treeModel.insertNodeInto(newNode, node,
							node.getChildCount());
					tree.scrollPathToVisible(new TreePath(newNode.getPath()));
					int i = tree.getRowForPath(new TreePath(newNode.getPath()));
					tree.setSelectionInterval(i, i);

					DefaultMutableTreeNode matrixNode = new DefaultMutableTreeNode(
							new RotatedLoadings(rotations));
					treeModel.insertNodeInto(matrixNode, newNode,
							newNode.getChildCount());

					DefaultMutableTreeNode eigenvaluesNode = new DefaultMutableTreeNode(
							new MatrixProvider() {

								@Override
								public Matrix getMatrix() {
									Matrix rot = rotations.getRotatedLoadings();
									Vector v = rot.transpose().times(rot)
											.getDiagonal();

									v.setRowLabels(rot.getColumnLabels());
									return v;
								}

								@Override
								public String toString() {
									return "Eigenvalues";
								}

							});
					treeModel.insertNodeInto(eigenvaluesNode, newNode,
							newNode.getChildCount());

					final FactorAnalysisResults r;
					try {
						r = (FactorAnalysisResults) ((DefaultMutableTreeNode) node
								.getParent().getParent()).getUserObject();
					} catch (Exception e) {
						e.printStackTrace();
						return;
					}
					final VennInfo vennInfo = new VennInfo(r.getInitial(),
							rotations, data.getStatements());
					DefaultMutableTreeNode vennNode = new DefaultMutableTreeNode(
							vennInfo);
					treeModel.insertNodeInto(vennNode, newNode,
							newNode.getChildCount());
					tree.scrollPathToVisible(new TreePath(vennNode.getPath()));

					DefaultMutableTreeNode zScoresNode = new DefaultMutableTreeNode(
							new MatrixProvider() {
								@Override
								public Matrix getMatrix() {
									return r.getInitial().getFactorScoresZ(
											rotations.getRotatedLoadings(),
											vennInfo.getThreshold(),
											vennInfo.getStrategy());
								}

								@Override
								public String toString() {

									return "Factor Z-Scores";
								}

							});
					treeModel.insertNodeInto(zScoresNode, vennNode,
							vennNode.getChildCount());

					DefaultMutableTreeNode scoresNode = new DefaultMutableTreeNode(
							new MatrixProvider() {
								@Override
								public Matrix getMatrix() {
									return r.getInitial().getFactorScores(
											rotations.getRotatedLoadings(),
											vennInfo.getThreshold(),
											vennInfo.getStrategy());
								}

								@Override
								public String toString() {

									return "Factor Scores";
								}

							});
					treeModel.insertNodeInto(scoresNode, vennNode,
							vennNode.getChildCount());
					tree.scrollPathToVisible(new TreePath(scoresNode.getPath()));
				}
			}
		};
	}

	private static int analysisNumber = 0;

	private static synchronized int nextAnalysisNumber() {
		return ++analysisNumber;
	}

	private EventManagerListener createAnalyzeListener() {
		return new EventManagerListener() {

			@Override
			public synchronized void notify(final Event event) {

				FactorAnalysisResults[] results = (FactorAnalysisResults[]) event
						.getObject();
				DefaultMutableTreeNode node = (DefaultMutableTreeNode) tree
						.getLastSelectedPathComponent();

				if (node != null
						&& node.getUserObject() instanceof DataSelection) {

					// get the tree model
					DefaultTreeModel treeModel = (DefaultTreeModel) tree
							.getModel();

					// create the parent node for all the results called New
					// Analysis
					DefaultMutableTreeNode newAnalysisNode = new DefaultMutableTreeNode(
							new ObjectDecorator(null, "Analysis "
									+ nextAnalysisNumber()));
					treeModel.insertNodeInto(newAnalysisNode, node,
							node.getChildCount());

					List<DefaultMutableTreeNode> newLeaves = new ArrayList<DefaultMutableTreeNode>();
					for (FactorAnalysisResults result : results) {
						// create a new node for the factor analysis result
						DefaultMutableTreeNode newNode = new DefaultMutableTreeNode(
								result);
						// add the node to the currently selected path
						treeModel.insertNodeInto(newNode, newAnalysisNode,
								newAnalysisNode.getChildCount());
						// add children to the new node
						DefaultMutableTreeNode newNode2 = FactorTreePanel
								.createNodes(result);
						treeModel.insertNodeInto(newNode2, newNode,
								newNode.getChildCount());
						newLeaves.add(newNode2);
					}

					tree.expandPath(new TreePath(node.getPath()));
					// tree.expandPath(new TreePath(newAnalysisNode.getPath()));
					// tree.scrollPathToVisible(new TreePath(newAnalysisNode
					// .getPath()));

				}
			}
		};
	}

	/** Returns an ImageIcon, or null if the path was invalid. */
	protected ImageIcon createImageIcon(String path, String description) {
		java.net.URL imgURL = getClass().getResource(path);
		if (imgURL != null) {
			return new ImageIcon(imgURL, description);
		} else {
			System.err.println("Couldn't find file: " + path);
			return null;
		}
	}

	private void updateDataViewer(DataSelection c) {
		DataGraphExtendedPanel d = new DataGraphExtendedPanel(data);
		d.getDataGraphPanel().setCombination(c);
		d.getDataGraphPanel().update();
		d.addEventManager(localEventManager);
		if (dataViewer.getContent() != null
				&& dataViewer.getContent() instanceof DataGraphExtendedPanel) {
			DataGraphExtendedPanel dPrevious = (DataGraphExtendedPanel) dataViewer
					.getContent();
			if (d.getDataGraphPanel().getGraphPanel() != null
					&& dPrevious.getDataGraphPanel().getGraphPanel() != null)
				d.getDataGraphPanel()
						.getGraphPanel()
						.setLabelsVisible(
								dPrevious.getDataGraphPanel().getGraphPanel()
										.getLabelsVisible());
		}
		dataViewer.setContent(d);
	}
}
