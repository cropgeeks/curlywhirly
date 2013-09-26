package curlywhirly.gui.viewer;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.vecmath.*;

import curlywhirly.opengl.*;

public class CanvasMouseListener extends MouseInputAdapter
{
	private OpenGLPanel panel;
	private ArcBall arcBall;

	public CanvasMouseListener(OpenGLPanel panel)
	{
		this.panel = panel;

		// Register this class as the mouse listener for the OpenGLPanel
		panel.addMouseListener(this);
		panel.addMouseMotionListener(this);
		panel.addMouseWheelListener(this);

		initialiseArcBall(OpenGLPanel.CANVAS_WIDTH, OpenGLPanel.CANVAS_HEIGHT);
	}

	// Sets up an arcball which deals with user rotation of the model using the mouse
	public final void initialiseArcBall(int width, int height)
	{
		arcBall = new ArcBall(width, height);
	}

	public void startDrag(Point point)
	{
		panel.toggleDragging();
		// Update Start Vector
		panel.updateLastRotation();
		// Prepare For Dragging
		arcBall.click(point);
	}

	public void drag(Point point)
	{
		Quat4f rotQuat = new Quat4f();

		// Update end vector
		arcBall.drag(point, rotQuat);
		panel.updateCurrentRotation(rotQuat);
	}

	@Override
	public void mousePressed(MouseEvent e)
	{
		if (SwingUtilities.isLeftMouseButton(e))
		{
			startDrag(e.getPoint());
		}
	}

	@Override
	public void mouseDragged(MouseEvent e)
	{
		if (SwingUtilities.isLeftMouseButton(e))
			drag(e.getPoint());
	}

	@Override
	public void mouseReleased(MouseEvent e)
	{
		if (SwingUtilities.isLeftMouseButton(e))
		{
			panel.toggleDragging();
			panel.mouseUp();
		}
	}

	@Override
	public void mouseWheelMoved(MouseWheelEvent e)
	{
		int units = e.getWheelRotation();
		panel.zoom(units);
	}

	@Override
	public void mouseMoved(MouseEvent e)
	{
		panel.setMousePoint(e.getPoint());
	}
}
