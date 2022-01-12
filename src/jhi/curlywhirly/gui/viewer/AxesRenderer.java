// Copyright 2009-2022 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package jhi.curlywhirly.gui.viewer;

import com.jogamp.opengl.util.awt.*;
import jhi.curlywhirly.data.*;
import jhi.curlywhirly.gui.*;
import jhi.curlywhirly.util.*;

import javax.media.opengl.*;
import javax.media.opengl.glu.*;
import java.awt.*;

import static javax.media.opengl.GL.GL_FRONT_AND_BACK;
import static javax.media.opengl.GL.GL_LINES;
import static javax.media.opengl.fixedfunc.GLLightingFunc.GL_AMBIENT_AND_DIFFUSE;
import static javax.media.opengl.fixedfunc.GLLightingFunc.GL_SHININESS;

public class AxesRenderer extends SceneRenderable implements GLEventListener
{
	private static final int X_AXIS = 0;
	private static final int Y_AXIS = 1;
	private static final int Z_AXIS = 2;

	private TextRenderer renderer;

	private float axisLabelSize = 0.05f;

	private DataSet dataSet;
	private Rotation rotation;

	private final GLU glu = new GLU();

	public void setDataSet(DataSet dataSet, Rotation rotation)
	{
		this.dataSet = dataSet;
		this.rotation = rotation;
	}

	private void drawAxes(GL2 gl)
	{
		float[] xAxisColor = ColorPrefs.getColorAsRGB("User.OpenGLPanel.xAxisColor");
		float[] yAxisColor = ColorPrefs.getColorAsRGB("User.OpenGLPanel.yAxisColor");
		float[] zAxisColor = ColorPrefs.getColorAsRGB("User.OpenGLPanel.zAxisColor");
		drawAxesLines(gl, xAxisColor, yAxisColor, zAxisColor);

		drawAxesCones(gl);
	}

	void drawAxesLines(GL2 gl, float[] xAxisColor, float[] yAxisColor, float[] zAxisColor)
	{
		gl.glMaterialfv(GL_FRONT_AND_BACK, GL_AMBIENT_AND_DIFFUSE, xAxisColor, 0);
		gl.glMaterialf(GL_FRONT_AND_BACK, GL_SHININESS, 128);
		drawAxisLine(X_AXIS, gl);

		gl.glMaterialfv(GL_FRONT_AND_BACK, GL_AMBIENT_AND_DIFFUSE, yAxisColor, 0);
		gl.glMaterialf(GL_FRONT_AND_BACK, GL_SHININESS, 128);
		drawAxisLine(Y_AXIS, gl);

		gl.glMaterialfv(GL_FRONT_AND_BACK, GL_AMBIENT_AND_DIFFUSE, zAxisColor, 0);
		gl.glMaterialf(GL_FRONT_AND_BACK, GL_SHININESS, 128);
		drawAxisLine(Z_AXIS, gl);

		if (Prefs.guiDrawAxisTicks)
			drawAxisTicks(gl);
	}

	private void drawAxisLine(int axis, GL2 gl)
	{
		switch (axis)
		{
			case X_AXIS:
				gl.glBegin(GL_LINES);
				gl.glVertex3f(-1f, 0, 0);
				gl.glVertex3f(1f, 0, 0);
				gl.glEnd();
				break;
			case Y_AXIS:
				gl.glBegin(GL_LINES);
				gl.glVertex3f(0, -1f, 0);
				gl.glVertex3f(0, 1f, 0);
				gl.glEnd();
				break;
			case Z_AXIS:
				gl.glBegin(GL_LINES);
				gl.glVertex3f(0, 0, -1f);
				gl.glVertex3f(0, 0, 1f);
				gl.glEnd();
				break;
		}
	}

	private void drawXTick(GL2 gl)
	{
		gl.glBegin(GL_LINES);
		gl.glVertex3f(-0.02f, 0, 0);
		gl.glVertex3f(0.02f, 0, 0);
		gl.glEnd();
	}

	private void drawYTick(GL2 gl)
	{
		gl.glBegin(GL_LINES);
		gl.glVertex3f(0, -0.02f, 0);
		gl.glVertex3f(0, 0.02f, 0);
		gl.glEnd();
	}

	private void drawAxisTicks(GL2 gl)
	{
		int startTick = -9;
		int endTick = 9;

		// Ticks for X-Axis
		gl.glPushMatrix();
		for (int i = startTick; i <= endTick; i++)
		{
			if (i == 0)
				continue;

			// Draw ticks along the x-axis;
			gl.glPushMatrix();
			gl.glTranslatef(0.1f * i, 0, 0);

			gl.glPushMatrix();
			invertRotation(gl);

			float rotXAxis = rotation.getCumulativeRotationArray()[1];
			if (rotXAxis > -0.5f && rotXAxis < 0.5f)
				drawYTick(gl);
			else
				drawXTick(gl);

			gl.glPopMatrix();
			gl.glPopMatrix();

			// Draw ticks along the y-axis
			gl.glPushMatrix();
			gl.glTranslatef(0, 0.1f * i, 0);

			gl.glPushMatrix();
			invertRotation(gl);

			float rotYAxis = rotation.getCumulativeRotationArray()[4];

			if (rotYAxis > -0.5f & rotYAxis < 0.5f)
				drawXTick(gl);
			else
				drawYTick(gl);

			gl.glPopMatrix();
			gl.glPopMatrix();

			// Draw ticks along the z-axis
			gl.glPushMatrix();
			gl.glTranslatef(0, 0, 0.1f * i);

			gl.glPushMatrix();
			invertRotation(gl);

			float rotZAxis = rotation.getCumulativeRotationArray()[8];
			if (rotZAxis > -0.5f && rotZAxis < 0.5f)
				drawXTick(gl);
			else
				drawYTick(gl);

			gl.glPopMatrix();
			gl.glPopMatrix();
		}
		gl.glPopMatrix();
	}

	private void invertRotation(GL2 gl)
	{
		float[] invMat = rotation.getInverseDragArray();
		float[] invCombArr = rotation.getInverseCumulativeArray();

		// The order that we apply the combined and mouse rotations is reversed
		// from the order in the display code. This is because we need these
		// rotations to happen in the reverse order.
		gl.glMultMatrixf(invCombArr, 0);
		gl.glMultMatrixf(invMat, 0);
	}

	private void drawAxesCones(GL2 gl)
	{
		GLUquadric quadric = glu.gluNewQuadric();

		float[] xAxisColor = ColorPrefs.getColorAsRGB("User.OpenGLPanel.xAxisColor");
		gl.glMaterialfv(GL_FRONT_AND_BACK, GL_AMBIENT_AND_DIFFUSE, xAxisColor, 0);
		gl.glMaterialf(GL_FRONT_AND_BACK, GL_SHININESS, 128);
		// Draw the cylinders at the positive extent of each axis
		gl.glPushMatrix();
		gl.glTranslatef(1f, 0, 0);
		gl.glScalef(axisLabelSize, axisLabelSize, axisLabelSize);
		gl.glPushMatrix();
		gl.glRotatef(90, 0, 1, 0);
		glu.gluCylinder(quadric, 1, 0, 2, 6, 6);
		gl.glPopMatrix();

		if (Prefs.guiChkAxisLabels)
			billboardText(gl, getAxisLabel(X_AXIS));
		gl.glPopMatrix();

		float[] yAxisColor = ColorPrefs.getColorAsRGB("User.OpenGLPanel.yAxisColor");
		gl.glMaterialfv(GL_FRONT_AND_BACK, GL_AMBIENT_AND_DIFFUSE, yAxisColor, 0);
		gl.glMaterialf(GL_FRONT_AND_BACK, GL_SHININESS, 128);
		gl.glPushMatrix();
		gl.glTranslatef(0, 1f, 0);
		gl.glScalef(axisLabelSize, axisLabelSize, axisLabelSize);
		gl.glPushMatrix();
		gl.glRotatef(-90, 1, 0, 0);
		glu.gluCylinder(quadric, 1, 0, 2, 6, 6);
		gl.glPopMatrix();

		if (Prefs.guiChkAxisLabels)
			billboardText(gl, getAxisLabel(Y_AXIS));
		gl.glPopMatrix();

		float[] zAxisColor = ColorPrefs.getColorAsRGB("User.OpenGLPanel.zAxisColor");
		gl.glMaterialfv(GL_FRONT_AND_BACK, GL_AMBIENT_AND_DIFFUSE, zAxisColor, 0);
		gl.glMaterialf(GL_FRONT_AND_BACK, GL_SHININESS, 128);
		gl.glPushMatrix();
		gl.glTranslatef(0, 0, 1f);
		gl.glScalef(axisLabelSize, axisLabelSize, axisLabelSize);
		glu.gluCylinder(quadric, 1, 0, 2, 6, 6);

		if (Prefs.guiChkAxisLabels)
			billboardText(gl, getAxisLabel(Z_AXIS));
		gl.glPopMatrix();

		glu.gluDeleteQuadric(quadric);
	}

	private String getAxisLabel(int axis)
	{
		switch (axis)
		{
			case X_AXIS:
				return Prefs.guiChkDatasetLabels ? dataSet.getAxes().getXYZLabels()[0] : "X";
			case Y_AXIS:
				return Prefs.guiChkDatasetLabels ? dataSet.getAxes().getXYZLabels()[1] : "Y";
			case Z_AXIS:
				return Prefs.guiChkDatasetLabels ? dataSet.getAxes().getXYZLabels()[2] : "Z";

			default:
				return null;
		}
	}

	// To render billboarded text we must rotate the text that we are drawing
	// by the inverse of the rotations that are applied to the camera. This
	// keeps the text facing the camera at all times.
	private void billboardText(GL2 gl, String text)
		throws GLException
	{
		float[] invMat = rotation.getInverseDragArray();
		float[] invCombArr = rotation.getInverseCumulativeArray();

		// The order that we apply the combined and mouse rotations is reversed
		// from the order in the display code. This is because we need these
		// rotations to happen in the reverse order.
		gl.glMultMatrixf(invCombArr, 0);
		gl.glMultMatrixf(invMat, 0);

		renderer.begin3DRendering();
		renderer.setColor(ColorPrefs.getColor("User.OpenGLPanel.axisLabels"));
		renderer.draw3D(text, 2f, -0.6f, 0, 0.05f);
		renderer.end3DRendering();
	}

	@Override
	public void init(GLAutoDrawable glad)
	{
		renderer = new TextRenderer(new Font("SansSerif", Font.BOLD, 36), true, false);
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
		drawAxes(gl);
	}

	public void setAxisLabelSize(float axisLabelSize)
	{
		this.axisLabelSize = axisLabelSize;
	}
}