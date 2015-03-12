package curlywhirly.gui.viewer;

import curlywhirly.data.DataPoint;
import curlywhirly.data.DataSet;

import java.awt.*;
import java.util.*;
import javax.media.opengl.*;

import static javax.media.opengl.GL.*;
import static javax.media.opengl.fixedfunc.GLLightingFunc.*;
import static javax.media.opengl.fixedfunc.GLMatrixFunc.*;
import static javax.media.opengl.fixedfunc.GLPointerFunc.*;

public class SphereRenderer extends SceneRenderable implements GLEventListener
{
	public static int SPHERE_DETAIL_LEVEL = 3;

	private GLIcoSphere glSphere;

	private DataSet dataSet;
	private Rotation rotation;

	private float pointSize = 0.03f;
	private float deselectedSize = 0.03f;

	private CollisionDetection detector;
	private HashSet<DataPoint> multiSelected = new HashSet<>();

	public void setDataSet(DataSet dataSet, Rotation rotation, CollisionDetection detector)
	{
		this.dataSet = dataSet;
		this.rotation = rotation;
		this.detector = detector;
	}

	private void drawSpheres(GL2 gl)
	{
		// Vertex buffer setup code
		gl.glBindBuffer(GL_ARRAY_BUFFER, glSphere.getVertexBufferId());
		gl.glBufferData(GL_ARRAY_BUFFER, glSphere.getVertexBufferSize(), glSphere.getVertexBuffer(), GL_STATIC_DRAW);
		gl.glVertexPointer(3, GL_FLOAT,0,0);
		gl.glNormalPointer(GL_FLOAT,0,0);
		gl.glBindBuffer(GL_ARRAY_BUFFER, 0);
		gl.glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, glSphere.getIndexBufferId());
		gl.glBufferData(GL_ELEMENT_ARRAY_BUFFER, glSphere.getIndexBufferSize(), glSphere.getIndexBuffer(), GL_STATIC_DRAW);
		gl.glBindBuffer(GL_ARRAY_BUFFER, 0);

		gl.glEnableClientState(GL_VERTEX_ARRAY);
		gl.glEnableClientState(GL_NORMAL_ARRAY);

		// Color spheres appropriately
		for (DataPoint point : dataSet)
		{
			Color color = point.getColorBySelection();
			if (multiSelected.contains(point))
				color = ColorPrefs.get("User.OpenGLPanel.multiSelectColor");

			// Get each color component into the 0-1 range instead of 0-255
			float [] rgba = color.getRGBColorComponents(new float[3]);

			gl.glMaterialfv(GL_FRONT, GL_AMBIENT_AND_DIFFUSE, rgba, 0);
			gl.glMaterialf(GL_FRONT, GL_SHININESS, 128);
			drawSphere(gl, point);
		}

		gl.glDisableClientState(GL_VERTEX_ARRAY);
		gl.glDisableClientState(GL_NORMAL_ARRAY);
	}

	private void drawSphere(GL2 gl, DataPoint point)
	{
		gl.glPushMatrix();

		// Get the position information for each axis so that these can be used
		// to translate our spheres to the correct location
		float[] axes = point.getPosition();
		// Bring our translations into the correct coordinate space
		gl.glTranslatef(axes[0], axes[1], axes[2]);

		float size = point.isSelected() ? pointSize : deselectedSize;

		// Scale our unit sphere down to a more manageable scale
		gl.glScalef(size, size, size);

		float[] modelView = new float[16];
		gl.glGetFloatv(GL_MODELVIEW_MATRIX, modelView, 0);
		detector.updatePointLocation(modelView, point);

		// Draw the triangles using the isosphereIndexBuffer VBO for the
		// element data (as well as the isosphereVertexBuffer).
		gl.glDrawElements(GL_TRIANGLES, glSphere.getIndexCount(), GL_UNSIGNED_INT, 0);
		gl.glPopMatrix();
	}

	@Override
	public void init(GLAutoDrawable drawable)
	{
		glSphere = new GLIcoSphere(drawable.getGL(), SPHERE_DETAIL_LEVEL);
	}

	@Override
	public void dispose(GLAutoDrawable drawable)
	{
	}

	@Override
	public void display(GLAutoDrawable drawable)
	{
		displayRenderable(drawable, rotation);
	}

	@Override
	public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height)
	{
	}

	@Override
	public void render(GL2 gl)
	{
		drawSpheres(gl);
	}

	public void setPointSize(float pointSize)
	{
		this.pointSize = pointSize;
	}

	public void setDeselectedSize(float deselectedSize)
	{
		this.deselectedSize = deselectedSize;
	}

    public float getPointSize()
        { return pointSize; }

	public void setMultiSelected(HashSet<DataPoint> multiSelected)
		{ this.multiSelected = multiSelected; }
}