package moten.david.util.math.gui.g3d;


import java.util.List;

import javax.media.j3d.Appearance;
import javax.media.j3d.Group;
import javax.media.j3d.Material;
import javax.media.j3d.Shape3D;
import javax.media.j3d.Transform3D;
import javax.media.j3d.TransformGroup;
import javax.vecmath.Color3f;
import javax.vecmath.Vector3d;

import moten.david.util.math.Vector;

import com.sun.j3d.utils.geometry.Primitive;
import com.sun.j3d.utils.geometry.Sphere;

public class SphereGroup extends Group {
	Shape3D[] shapes;

	int numShapes = 0;

	// Constructors
	public SphereGroup(List<Vector> vectors) {
		// radius x,y spacing x,y count appearance
		this(0.25f, vectors, null, false);
	}

	public SphereGroup(List<Vector> vectors, Appearance app) {
		// radius x,y spacing x,y count appearance
		this(0.25f, vectors, app, false);
	}

	public SphereGroup(float radius, List<Vector> vectors, boolean overrideflag) {
		this(radius, vectors, null, overrideflag);
	}

	public SphereGroup(float radius, List<Vector> vectors, Appearance app,
			boolean overrideflag) {
		if (app == null) {
			app = new Appearance();
			Material material = new Material();
			material.setDiffuseColor(new Color3f(0.8f, 0.8f, 0.8f));
			material.setSpecularColor(new Color3f(0.0f, 0.0f, 0.0f));
			material.setShininess(0.0f);
			app.setMaterial(material);
		}

		Sphere sphere = null;
		Transform3D t3d = new Transform3D();
		Vector3d vec = new Vector3d();
		shapes = new Shape3D[vectors.get(0).size()];
		for (int i = 1; i < vectors.get(0).size(); i++) {
			vec.set(vectors.get(0).getValue(i), vectors.get(1).getValue(i),
					vectors.get(2).getValue(i));
			t3d.setTranslation(vec);
			TransformGroup trans = new TransformGroup(t3d);
			addChild(trans);

			sphere = new Sphere(radius, // sphere radius
					Primitive.GENERATE_NORMALS, // generate normals
					16, // 16 divisions radially
					app); // it's appearance
			trans.addChild(sphere);
			shapes[numShapes] = sphere.getShape();
			if (overrideflag)
				shapes[numShapes]
						.setCapability(Shape3D.ALLOW_APPEARANCE_OVERRIDE_WRITE);
			numShapes++;
		}
	}

	Shape3D[] getShapes() {
		return shapes;
	}

}
