// Copyright 2009-2014 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package curlywhirly.gui.viewer;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.vecmath.*;

import curlywhirly.data.*;
import curlywhirly.opengl.*;

import scri.commons.gui.*;

public class CanvasMouseListener extends MouseInputAdapter
{
	private final OpenGLPanel panel;
	private ArcBall arcBall;
	private final Rotation rotation;
	private final DataSet dataSet;

	private final CanvasMenu menu;

	public CanvasMouseListener(OpenGLPanel panel, Rotation rotation, DataSet dataSet)
	{
		this.panel = panel;
		this.rotation = rotation;
		this.dataSet = dataSet;

		// Register this class as the mouse listener for the OpenGLPanel
		panel.addMouseListener(this);
		panel.addMouseMotionListener(this);
		panel.addMouseWheelListener(this);

		initialiseArcBall(OpenGLPanel.CANVAS_WIDTH, OpenGLPanel.CANVAS_HEIGHT);

		menu = new CanvasMenu();
	}

	// Sets up an arcball which deals with user rotation of the model using the mouse
	public final void initialiseArcBall(int width, int height)
	{
		arcBall = new ArcBall(width, height);
	}

	public void startDrag(Point point)
	{
		if (rotation.isSpinning() == false)
		{
			// Update Start Vector
			rotation.updateLastRotation();
			// Prepare For Dragging
			arcBall.click(point);
		}
	}

	public void drag(Point point)
	{
		if (rotation.isSpinning() == false)
		{
			Quat4f rotQuat = new Quat4f();

			// Update end vector
			arcBall.drag(point, rotQuat);

			rotation.updateCurrentRotation(rotQuat);
		}
	}

	@Override
	public void mousePressed(MouseEvent e)
	{
		if (SwingUtilities.isLeftMouseButton(e))
			startDrag(e.getPoint());

		else if (e.isPopupTrigger())
			new CanvasMenu().display(e);
	}

	@Override
	public void mouseClicked(MouseEvent e)
	{
		if (SwingUtilities.isLeftMouseButton(e))
		{
			int x = e.getX();
			int y = e.getY();
			panel.getCloseOverlay().handleClick(x, y);
			panel.selectPoint();
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
			rotation.updateCombinedRotation();

		else if (e.isPopupTrigger())
			menu.display(e);
	}

	@Override
	public void mouseWheelMoved(MouseWheelEvent e)
	{
		int units = e.getWheelRotation();
		panel.getScene().zoom(units);
	}

	@Override
	public void mouseMoved(MouseEvent e)
	{
		panel.setMousePoint(e.getPoint());
	}

	class CanvasMenu implements ActionListener
	{
		private JMenuItem mVisitUrl;

		// Create and display the popup menu
		void display(MouseEvent e)
		{
			mVisitUrl = new JMenuItem();
			RB.setText(mVisitUrl, "gui.viewer.CanvasMouseListener.mVisitUrl");
			mVisitUrl.addActionListener(this);
			mVisitUrl.setEnabled(dataSet.getDbAssociation().isPointSearchEnabled());

			JPopupMenu menu = new JPopupMenu();
			menu.add(mVisitUrl);
			menu.show(e.getComponent(), e.getX(), e.getY());
		}

		@Override
		public void actionPerformed(ActionEvent e)
		{
			if (e.getSource() == mVisitUrl)
			{
				try
				{
					panel.visitUrl();
				}
				catch (UnsupportedEncodingException ex) { ex.printStackTrace(); }
			}
		}
	}
}