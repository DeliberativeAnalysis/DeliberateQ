package moten.david.util.math.gui.g3d;


import javax.media.j3d.*;
import javax.vecmath.*;

public class Box extends Shape3D {

	private static final float[] normals = {
		   // Front Face
		      0.0f,  0.0f,  1.0f,     0.0f,  0.0f,  1.0f,
		      0.0f,  0.0f,  1.0f,     0.0f,  0.0f,  1.0f,
		   // Back Face
		      0.0f,  0.0f, -1.0f,     0.0f,  0.0f, -1.0f,
		      0.0f,  0.0f, -1.0f,     0.0f,  0.0f, -1.0f,
		   // Right Face
		      1.0f,  0.0f,  0.0f,     1.0f,  0.0f,  0.0f,
		      1.0f,  0.0f,  0.0f,     1.0f,  0.0f,  0.0f,
		   // Left Face
		     -1.0f,  0.0f,  0.0f,    -1.0f,  0.0f,  0.0f,
		     -1.0f,  0.0f,  0.0f,    -1.0f,  0.0f,  0.0f,
		   // Top Face
		      0.0f,  1.0f,  0.0f,     0.0f,  1.0f,  0.0f,
		      0.0f,  1.0f,  0.0f,     0.0f,  1.0f,  0.0f,
		   // Bottom Face
		      0.0f, -1.0f,  0.0f,     0.0f, -1.0f,  0.0f,
		      0.0f, -1.0f,  0.0f,     0.0f, -1.0f,  0.0f,
		   };

	
	public Box(double xsize, double ysize, double zsize,Appearance app) {
		this(xsize, ysize, zsize);
		setCapability(Shape3D.ALLOW_APPEARANCE_OVERRIDE_WRITE);
		setAppearance(app);
	}
	
    public Box(double xsize, double ysize, double zsize) {
	super();
	double xmin = -xsize/2.0;
	double xmax =  xsize/2.0;
	double ymin = -ysize/2.0;
	double ymax =  ysize/2.0;
	double zmin = -zsize/2.0;
	double zmax =  zsize/2.0;

	QuadArray box = new QuadArray(24, QuadArray.COORDINATES|QuadArray.NORMALS);

	Point3d verts[] = new Point3d[24];

	// front face
	verts[0] = new Point3d(xmax, ymin, zmax);
	verts[1] = new Point3d(xmax, ymax, zmax);
	verts[2] = new Point3d(xmin, ymax, zmax);
	verts[3] = new Point3d(xmin, ymin, zmax);
	// back face
	verts[4] = new Point3d(xmin, ymin, zmin);
	verts[5] = new Point3d(xmin, ymax, zmin);
	verts[6] = new Point3d(xmax, ymax, zmin);
	verts[7] = new Point3d(xmax, ymin, zmin);
	// right face
	verts[8] = new Point3d(xmax, ymin, zmin);
	verts[9] = new Point3d(xmax, ymax, zmin);
	verts[10] = new Point3d(xmax, ymax, zmax);
	verts[11] = new Point3d(xmax, ymin, zmax);
	// left face
	verts[12] = new Point3d(xmin, ymin, zmax);
	verts[13] = new Point3d(xmin, ymax, zmax);
	verts[14] = new Point3d(xmin, ymax, zmin);
	verts[15] = new Point3d(xmin, ymin, zmin);
	// top face
	verts[16] = new Point3d(xmax, ymax, zmax);
	verts[17] = new Point3d(xmax, ymax, zmin);
	verts[18] = new Point3d(xmin, ymax, zmin);
	verts[19] = new Point3d(xmin, ymax, zmax);
	// bottom face
	verts[20] = new Point3d(xmin, ymin, zmax);
	verts[21] = new Point3d(xmin, ymin, zmin);
	verts[22] = new Point3d(xmax, ymin, zmin);
	verts[23] = new Point3d(xmax, ymin, zmax);

	box.setCoordinates(0, verts);
	box.setNormals(0, normals);
        setGeometry(box);
	setAppearance(new Appearance());
    }
}
