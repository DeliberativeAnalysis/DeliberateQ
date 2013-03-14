package moten.david.util.math.gui.g3d;


import java.awt.Font;
import java.awt.GraphicsConfiguration;
import java.awt.GridLayout;
import java.awt.Shape;
import java.util.ArrayList;
import java.util.List;

import javax.media.j3d.AlternateAppearance;
import javax.media.j3d.AmbientLight;
import javax.media.j3d.Appearance;
import javax.media.j3d.Background;
import javax.media.j3d.BoundingSphere;
import javax.media.j3d.BranchGroup;
import javax.media.j3d.Canvas3D;
import javax.media.j3d.DirectionalLight;
import javax.media.j3d.Font3D;
import javax.media.j3d.FontExtrusion;
import javax.media.j3d.Group;
import javax.media.j3d.Material;
import javax.media.j3d.Shape3D;
import javax.media.j3d.Text3D;
import javax.media.j3d.Transform3D;
import javax.media.j3d.TransformGroup;
import javax.swing.JPanel;
import javax.vecmath.Color3f;
import javax.vecmath.Point3d;
import javax.vecmath.Vector3f;

import moten.david.util.math.Matrix;
import moten.david.util.math.Vector;

import com.sun.j3d.utils.behaviors.vp.OrbitBehavior;
import com.sun.j3d.utils.universe.SimpleUniverse;
import com.sun.j3d.utils.universe.ViewingPlatform;

public class Graph3dPanel extends JPanel {

	private static final long serialVersionUID = -7874386062497303481L;

	private SimpleUniverse u;

	private GraphicsConfiguration configuration;

	public Graph3dPanel() {

		setLayout(new GridLayout(1, 1));

		configuration = SimpleUniverse.getPreferredConfiguration();

//		SelectionManager.getInstance().addSelectionListener(
//				new SelectionListener() {
//					public void selectionChanged(SelectionManagerEvent event) {
//						if (event.getObject() instanceof JMatrix) {
//							JMatrix jMatrix = (JMatrix) event.getObject();
//							List<Vector> vectors = jMatrix
//									.getColumnSelections();
//							if (vectors.size() == 3)
//								setVectors(vectors);
//						}
//					}
//				});

	}

	public void setVectors(List<Vector> vectors) {

		removeAll();

		Canvas3D c = new Canvas3D(configuration);

		add(c);

		// SimpleUniverse is a Convenience Utility class
		u = new SimpleUniverse(c);

		// add mouse behaviors to the viewingPlatform
		ViewingPlatform viewingPlatform = u.getViewingPlatform();

		// This will move the ViewPlatform back a bit so the
		// objects in the scene can be viewed.
		viewingPlatform.setNominalViewingTransform();

		OrbitBehavior orbit = new OrbitBehavior(c, OrbitBehavior.REVERSE_ALL);
		BoundingSphere bounds = new BoundingSphere(new Point3d(0.0, 0.0, 0.0),
				100.0);
		orbit.setSchedulingBounds(bounds);
		viewingPlatform.setViewPlatformBehavior(orbit);

		BranchGroup scene = createSceneGraph(vectors);
		u.addBranchGraph(scene);

		validate();
	}

	private BranchGroup createSceneGraph(List<Vector> vectors) {
		BranchGroup objRoot = new BranchGroup();

		// Create influencing bounds
		BoundingSphere worldBounds = new BoundingSphere(new Point3d(0.0, 0.0,
				0.0), // Center
				1000.0); // Extent

		//Change Background to white  	
		Color3f white = new Color3f(0.0f/256.0f, 0.0f/256.0f, 128.0f/256.0f); // white color
		Background backg = new Background(white);
		backg.setApplicationBounds(worldBounds);
		objRoot.addChild(backg);

		Transform3D t = new Transform3D();
		// move the object upwards
		t.set(new Vector3f(0.0f, 0.1f, 0.0f));
		// Shrink the object
		t.setScale(0.56);

		TransformGroup trans = new TransformGroup(t);
		trans.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
		trans.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);

		Appearance otherApp = new Appearance();
		Material altMat = new Material();
		altMat.setCapability(Material.ALLOW_COMPONENT_WRITE);
		altMat.setDiffuseColor(new Color3f(0.0f, 1.0f, 0.0f));
		otherApp.setMaterial(altMat);

		AlternateAppearance altApp = new AlternateAppearance();
		altApp.setAppearance(otherApp);
		altApp.setCapability(AlternateAppearance.ALLOW_SCOPE_WRITE);
		altApp.setCapability(AlternateAppearance.ALLOW_SCOPE_READ);
		altApp.setInfluencingBounds(worldBounds);
		objRoot.addChild(altApp);

		addText(trans, vectors.get(0).getColumnLabel(1), 1.1f, 0.0f, 0.0f);
		addText(trans, vectors.get(1).getColumnLabel(1), 0.0f, 1.1f, 0.0f);
		addText(trans, vectors.get(2).getColumnLabel(1), 0.0f, 0.0f, 1.1f);
		// Add points

		// Build foreground geometry into two groups. We'll
		// create three directional lights below, one each with
		// scope to cover the first geometry group only, the
		// second geometry group only, or both geometry groups.
		Appearance app1 = new Appearance();
		Material mat1 = new Material();
		mat1.setCapability(Material.ALLOW_COMPONENT_WRITE);
		mat1.setDiffuseColor(new Color3f(1.0f, 0.0f, 0.0f));
		app1.setMaterial(mat1);
		SphereGroup content1 = new SphereGroup(0.05f, // radius of spheres
				vectors, app1, // appearance
				true); // alt app override = true
		trans.addChild(content1);

		Group group = new Group();

		// Add axes

		// Cylinder cylinder = new Cylinder(0.0f, 0.0f, 0.05f, 1f, 10, app1);
		// group.addChild(cylinder.getShape());
		Appearance app2 = new Appearance();
		Material mat2 = new Material();
		mat2.setCapability(Material.ALLOW_COMPONENT_WRITE);
		mat2.setDiffuseColor(new Color3f(1.0f, 1.0f, 0.0f));
		app2.setMaterial(mat2);

		Box box;
		box = new Box(Math.floor(2 * vectors.get(0).getMaxAbsoluteValue()) + 1,
				.05, 0.05, app2);
		group.addChild(box);
		box = new Box(0.05, Math
				.floor(2 * vectors.get(1).getMaxAbsoluteValue()) + 1, 0.05,
				app2);
		group.addChild(box);
		box = new Box(0.05, 0.05, Math.floor(2 * vectors.get(2)
				.getMaxAbsoluteValue()) + 1, app2);
		group.addChild(box);

		trans.addChild(group);
		// Shape3D[] shapes1 = ((SphereGroup) content1).getShapes();

		// Add lights
		DirectionalLight light1 = null;
		light1 = new DirectionalLight();
		light1.setEnable(true);
		light1.setColor(new Color3f(0.2f, 0.2f, 0.2f));
		light1.setDirection(new Vector3f(1.0f, 0.0f, -1.0f));
		light1.setInfluencingBounds(worldBounds);
		objRoot.addChild(light1);

		DirectionalLight light2 = new DirectionalLight();
		light2.setEnable(true);
		light2.setColor(new Color3f(0.2f, 0.2f, 0.2f));
		light2.setDirection(new Vector3f(-1.0f, 0.0f, 1.0f));
		light2.setInfluencingBounds(worldBounds);
		objRoot.addChild(light2);

		// Add an ambient light to dimly illuminate the rest of
		// the shapes in the scene to help illustrate that the
		// directional lights are being scoped... otherwise it looks
		// like we're just removing shapes from the scene
		AmbientLight ambient = new AmbientLight();
		ambient.setEnable(true);
		ambient.setColor(new Color3f(1.0f, 1.0f, 1.0f));
		ambient.setInfluencingBounds(worldBounds);
		objRoot.addChild(ambient);

		objRoot.addChild(trans);

		return objRoot;
	}

	private void addText(Group group, String s, float x, float y, float z) {
		// Add text
		{
			Font myFont = new Font("TimesRoman", Font.PLAIN, 12);
		
			// use a customized FontExtrusion object to control the depth of the
		    // text
//		    double X1 = 0;
//		    double Y1 = 0;
//		    double X2 = 3;
//		    double Y2 = 0;
//		    Shape extrusionShape = new java.awt.geom.Line2D.Double(X1, Y1, X2, Y2);
//		    FontExtrusion fontEx = new FontExtrusion(extrusionShape);
			Font3D myFont3D = new Font3D(myFont, new FontExtrusion());
			Text3D myText3D = new Text3D(myFont3D, s);
			Shape3D myShape3D = new Shape3D(myText3D, new Appearance());

			BranchGroup contentBranchGroup = new BranchGroup();
			Transform3D t3d = new Transform3D();
			t3d.setTranslation(new Vector3f(x, y, z));
			t3d.setScale(0.01);
			// Transform3D tempTransform3D = new Transform3D();
			// tempTransform3D.rotY(Math.PI / 4.0d);
			// t3d.mul(tempTransform3D);
			TransformGroup tg = new TransformGroup(t3d);

			// We add our child nodes and insert the branch group into
			// the live scene graph. This results in Java 3D rendering
			// the content.
			tg.addChild(myShape3D);
			contentBranchGroup.addChild(tg);
			group.addChild(contentBranchGroup);
		}

	}

	public void setMatrix(Matrix m) {
		List<Vector> vectors = new ArrayList<Vector>();
		vectors.add(m.getColumnVector(1));
		vectors.add(m.getColumnVector(2));
		vectors.add(m.getColumnVector(3));
		setVectors(vectors);
	}

}
