package curlywhirly.gui.viewer;

import curlywhirly.util.ColorPrefs;
import java.awt.*;
import javax.media.opengl.*;

import curlywhirly.data.*;
import curlywhirly.gui.*;

import com.jogamp.opengl.util.gl2.*;

import static javax.media.opengl.GL.*;
import static javax.media.opengl.fixedfunc.GLLightingFunc.*;

/**
 * Renders a semi-transparent sphere representing the current size of the
 * multi-selection area. Can also optionally render anchor lines from the point
 * at the centre of the selection to the other points encompassed in the
 * selection sphere, and can optionally render axes from the point at the centre
 * of the selection.
 */
public class MultiSelectionRenderer extends AbstractSphereRenderer
{
	private final WinMain winMain;

	// We use GLUT to get a solid sphere that looks good when transparent
	// our more efficient icospheres don't look great semi-transparent
	private final GLUT glut;

	private DataPoint selectedPoint;
	private float selectionSphereSize = Prefs.guiSelectionSphereSize;

	MultiSelectionRenderer(WinMain winMain)
	{
		this.winMain = winMain;
		glut = new GLUT();
	}

	@Override
	public void render(GL2 gl)
	{
		drawSpheres(gl);
		drawSelectLines(gl);
		drawSelectAxes(gl);
		drawSelectSphere(gl);
	}

	@Override
	public void renderSpheres(GL2 gl)
	{
		Color color = ColorPrefs.getColor("User.OpenGLPanel.multiSelectColor");

		// Get each color component into the 0-1 range instead of 0-255
		float [] rgba = color.getRGBColorComponents(new float[3]);
		gl.glMaterialfv(GL_FRONT, GL_AMBIENT_AND_DIFFUSE, rgba, 0);
		gl.glMaterialf(GL_FRONT, GL_SHININESS, 128);

		renderPoints(gl, dataSet.multiSelectedPoints());
	}


	private void drawSelectLines(GL2 gl)
	{
		if (selectedPoint == null || !Prefs.guiChkAnchorPoints)
			return;

		float [] rgba = ColorPrefs.getColorAsRGB("User.OpenGLPanel.multiSelectLineColor");

		gl.glMaterialfv(GL_FRONT, GL_AMBIENT_AND_DIFFUSE, rgba, 0);
		gl.glMaterialf(GL_FRONT, GL_SHININESS, 128);

		gl.glPushMatrix();
		// The datapoint at the centre of the selection sphere
		float[] anchorPointPosition = selectedPoint.getPosition();
		dataSet.multiSelectedPoints().forEach(point ->
		{
			// A point which has been caught within the selection sphere
			float[] selectedPointPosition = point.getPosition();
			// Draw a line between the two points
			gl.glBegin(GL_LINES);
			gl.glVertex3fv(anchorPointPosition, 0);
			gl.glVertex3f(selectedPointPosition[0], selectedPointPosition[1], selectedPointPosition[2]);
			gl.glEnd();
		});
		gl.glPopMatrix();
	}

	private void drawSelectAxes(GL2 gl)
	{
		if (selectedPoint == null || !Prefs.guiChkSelectionAxes)
			return;

		gl.glPushMatrix();

		float[] axes = selectedPoint.getPosition();
		gl.glTranslatef(axes[0], axes[1], axes[2]);
		gl.glScalef(selectionSphereSize, selectionSphereSize, selectionSphereSize);
		float[] glColor = ColorPrefs.getColorAsRGB("User.OpenGLPanel.multiSelectAxesColor");

		// Re-use the axes drawing code from the scene
		winMain.getOpenGLPanel().getAxesRenderer().drawAxesLines(gl, glColor, glColor, glColor);

		gl.glPopMatrix();
	}

	private void drawSelectSphere(GL2 gl)
	{
		// If there's no selectedPoint there's nothing to render
		if (selectedPoint == null)
			return;

		// Ensure OpenGL is set up correctly for transparency
		gl.glEnable(GL_BLEND);
		gl.glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
		gl.glDisable(GL_CULL_FACE);
		gl.glDepthFunc(GL_LEQUAL);

		gl.glPushMatrix();
		// Get the position information for each axis so that these can be used
		// to translate our sphere to the correct location
		float[] axes = selectedPoint.getPosition();
		// Bring our translations into the correct coordinate space
		gl.glTranslatef(axes[0], axes[1], axes[2]);

		// Scale our sphere to match the desired size set in the UI
		gl.glScalef(selectionSphereSize, selectionSphereSize, selectionSphereSize);

		float[] rgba = new float[] { 0.5f, 0.5f, 1.0f, 0.4f };
		gl.glMaterialfv(GL_FRONT_AND_BACK, GL_AMBIENT_AND_DIFFUSE, rgba, 0);
		gl.glMaterialf(GL_FRONT_AND_BACK, GL_SHININESS, 128);
		glut.glutSolidSphere(1, 16, 16);
		gl.glPopMatrix();

		// Return OpenGL back to a state for normal (i.e. non-transparent) drawing
		gl.glEnable(GL_CULL_FACE);
		gl.glDisable(GL_BLEND);
	}

	@Override
	public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height)
	{
	}

	public void multiSelect(DataPoint point)
	{
		selectedPoint = point;
	}

	// Applies the given type of selection (select, deselect or toggle) to all
	// of the datapoints that have been multi-selected
	public void finishedMultiSelect(int selectionType)
	{
		selectedPoint = null;

		switch (selectionType)
		{
			case MultiSelectPanel.SELECT:
				dataSet.selectMultiSelected();
				break;
			case MultiSelectPanel.DESELECT:
				dataSet.deselectMultiSelected();
				break;
			case MultiSelectPanel.TOGGLE:
				dataSet.toggleMultiSelected();
				break;
		}

		// Force a repaint of the selection UI
		winMain.getControlPanel().repaint();
		winMain.getDataPanel().updateTableModel();
	}

	public void cancelMultiSelect()
	{
		selectedPoint = null;
		dataSet.clearMultiSelection();
	}

	public void setSelectPointSize(float selectPointSize)
	{
		this.selectionSphereSize = selectPointSize;
	}

	public float getSelectPointSize()
	{
		return selectionSphereSize;
	}

	// Checks for sphere sphere collisions by chcking the distance between all
	// points and the selection point and the sphere's radius.
	// May move to CollisionDetection class in time
	public void detectMultiSelectedPoints()
	{
		dataSet.clearMultiSelection();
		dataSet.detecteOverlappingPoints(selectedPoint, selectionSphereSize);
	}

	public boolean isMultiSelecting()
	{
		return selectedPoint != null;
	}

	public int multiSelectedPointsCount()
	{
		return (int) dataSet.multiSelectedPoints().count();
	}
}
