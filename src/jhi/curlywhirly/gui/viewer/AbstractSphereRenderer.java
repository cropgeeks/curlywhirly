// Copyright 2009-2022 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package jhi.curlywhirly.gui.viewer;

import jhi.curlywhirly.data.*;

import javax.media.opengl.*;
import java.util.stream.*;

import static javax.media.opengl.fixedfunc.GLMatrixFunc.GL_MODELVIEW_MATRIX;

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
	private Sphere sphere;
	private int sphereDetailLevel = 2;
	private float pointSize = 0.03f;
	protected float transparencyAlpha = 0.5f;
	private boolean sphereDetailChanged = false;

	protected DataSet dataSet;

	protected Rotation rotation;

	private CollisionDetection detector;

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
		sphere.preRender(gl);

		renderSpheres(gl);

		sphere.postRender(gl);
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

		// Scale our unit sphere down to a more manageable scale
		gl.glScalef(pointSize, pointSize, pointSize);

		float[] modelView = new float[16];
		gl.glGetFloatv(GL_MODELVIEW_MATRIX, modelView, 0);
		detector.updatePointLocation(modelView, point);

		sphere.render(gl);
		gl.glPopMatrix();
	}

	@Override
	public void init(GLAutoDrawable drawable)
	{
		sphere = new SphereFactory().createSphere(drawable.getGL(), sphereDetailLevel);
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
			sphere = new SphereFactory().createSphere(drawable.getGL(), sphereDetailLevel);
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
	{
		return pointSize;
	}

	public void setSphereDetailLevel(int sphereDetailLevel)
	{
		this.sphereDetailLevel = sphereDetailLevel;
		sphereDetailChanged = true;
	}

	public Rotation getRotation()
	{
		return rotation;
	}

	public void setTransparencyAlpha(float transparencyAlpha)
	{
		this.transparencyAlpha = transparencyAlpha;
	}

	public float getTransparencyAlpha()
	{
		return transparencyAlpha;
	}
}