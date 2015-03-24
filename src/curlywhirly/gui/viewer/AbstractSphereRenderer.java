package curlywhirly.gui.viewer;

import java.util.stream.*;
import javax.media.opengl.GL2;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLEventListener;

import curlywhirly.data.*;

import static javax.media.opengl.GL.*;
import static javax.media.opengl.fixedfunc.GLMatrixFunc.*;
import static javax.media.opengl.fixedfunc.GLPointerFunc.*;

/**
 * Defines the generic state of a SphereRenderer which can be extended to provide
 * concrete sphere rendering implementations of varying types. AbstractSphereRenderer
 * provides the template method for rendering spheres (drawSpheres, with the method call
 * to renderSpheres deferring to a required concrete implementation. Extends the
 * abstract class SceneRenderable which provides the template method for rendering
 * objects which are part of the scene.
 */
public abstract class AbstractSphereRenderer extends SceneRenderable implements GLEventListener
{
	public int sphereDetailLevel = 2;

	private GLIcoSphere glSphere;

	protected DataSet dataSet;
	protected Rotation rotation;

	private float pointSize = 0.03f;

	private CollisionDetection detector;

	private boolean sphereDetailChanged = false;

	public abstract void renderSpheres(GL2 gl);

	public void setDataSet(DataSet dataSet, Rotation rotation, CollisionDetection detector)
	{
		this.dataSet = dataSet;
		this.rotation = rotation;
		this.detector = detector;
	}

	protected void drawSpheres(GL2 gl)
	{
		// Vertex buffer setup code
		gl.glBindBuffer(GL_ARRAY_BUFFER, glSphere.getVertexBufferId());
		gl.glBufferData(GL_ARRAY_BUFFER, glSphere.getVertexBufferSize(), glSphere.getVertexBuffer(), GL_STATIC_DRAW);
		gl.glVertexPointer(3, GL_FLOAT, 0, 0);
		gl.glNormalPointer(GL_FLOAT, 0, 0);
		gl.glBindBuffer(GL_ARRAY_BUFFER, 0);
		gl.glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, glSphere.getIndexBufferId());
		gl.glBufferData(GL_ELEMENT_ARRAY_BUFFER, glSphere.getIndexBufferSize(), glSphere.getIndexBuffer(), GL_STATIC_DRAW);
		gl.glBindBuffer(GL_ARRAY_BUFFER, 0);

		gl.glEnableClientState(GL_VERTEX_ARRAY);
		gl.glEnableClientState(GL_NORMAL_ARRAY);

		renderSpheres(gl);

		gl.glDisableClientState(GL_VERTEX_ARRAY);
		gl.glDisableClientState(GL_NORMAL_ARRAY);
	}

	protected void renderPoints(GL2 gl, Stream<DataPoint> dataPoints)
	{
		dataPoints.forEach(point -> drawSphere(gl, point));
	}

	private void drawSphere(GL2 gl, DataPoint point)
	{
		gl.glPushMatrix();

		// Get the position information for each axis so that these can be used
		// to translate our spheres to the correct location
		float[] axes = point.getPosition();
		// Bring our translations into the correct coordinate space
		gl.glTranslatef(axes[0], axes[1], axes[2]);

		float size = pointSize;

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
		glSphere = new GLIcoSphere(drawable.getGL(), sphereDetailLevel);
	}

	@Override
	public void dispose(GLAutoDrawable drawable)
	{
	}

	@Override
	public void display(GLAutoDrawable drawable)
	{
		if (sphereDetailChanged)
		{
			glSphere = new GLIcoSphere(drawable.getGL(), sphereDetailLevel);
			sphereDetailChanged = false;
		}
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

    public float getPointSize()
        { return pointSize; }

	public void setSphereDetailLevel(int sphereDetailLevel)
	{
		this.sphereDetailLevel = sphereDetailLevel;
		sphereDetailChanged = true;
	}

	public Rotation getRotation()
		{ return rotation; }
}