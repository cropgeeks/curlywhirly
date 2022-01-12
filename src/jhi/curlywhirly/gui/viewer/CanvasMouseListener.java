// Copyright 2009-2022 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package jhi.curlywhirly.gui.viewer;

import jhi.curlywhirly.data.*;
import jhi.curlywhirly.gui.*;
import scri.commons.gui.*;

import javax.swing.*;
import javax.swing.event.*;
import javax.vecmath.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;

public class CanvasMouseListener extends MouseInputAdapter
{
	private final OpenGLPanel panel;
	private ArcBall arcBall;
	private final Rotation rotation;
	private final DataSet dataSet;
	private final ArrayList<String> urlNames;
	private final WinMain winMain;

	private final CanvasMenu menu;

	private DataPoint underMouse;

	public CanvasMouseListener(OpenGLPanel panel, Rotation rotation, DataSet dataSet, WinMain winMain)
	{
		this.panel = panel;
		this.rotation = rotation;
		this.dataSet = dataSet;
		this.urlNames = dataSet != null ? dataSet.getUrlNames() : new ArrayList<>();
		this.winMain = winMain;

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

		if ((e.isPopupTrigger() || SwingUtils.isMetaClick(e)) && panel.getUnderMouse() != null && panel.getMultiSelectionRenderer().isMultiSelecting() == false)
		{
			menu.display(e);
			underMouse = panel.getUnderMouse();
		}
	}

	@Override
	public void mouseReleased(MouseEvent e)
	{
		if (SwingUtilities.isLeftMouseButton(e))
			rotation.updateCombinedRotation();

		if ((e.isPopupTrigger() || SwingUtils.isMetaClick(e)) && panel.getUnderMouse() != null && panel.getMultiSelectionRenderer().isMultiSelecting() == false)
		{
			menu.display(e);
			underMouse = panel.getUnderMouse();
		}
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
		private JMenu mVisitUrl;
		private JMenuItem mDatabaseSearch;
		private JMenuItem mPointInf;
		private JMenuItem mMultiSelect;

		// Create and display the popup menu
		void display(MouseEvent e)
		{
			mVisitUrl = new JMenu();
			RB.setText(mVisitUrl, "gui.viewer.CanvasMouseListener.mVisitUrl");

			mDatabaseSearch = new JMenuItem();
			mDatabaseSearch.setText("Database search");
			mDatabaseSearch.addActionListener(this);
			mDatabaseSearch.setEnabled(dataSet.getDbAssociation().isPointSearchEnabled());

			mVisitUrl.add(mDatabaseSearch);

			for (String urlName : urlNames)
			{
				JMenuItem mItem = new JMenuItem(urlName);
				mItem.addActionListener(listener ->
				{
					try
					{
						dataSet.getDbAssociation().visitUrlForPoint(urlName, underMouse);
					}
					catch (UnsupportedEncodingException ex)
					{
						ex.printStackTrace();
					}
				});
				mVisitUrl.add(mItem);
			}

			mPointInf = new JMenuItem();
			RB.setText(mPointInf, "gui.viewer.CanvasMouseListener.mPointInf");
			mPointInf.addActionListener(this);

			mMultiSelect = new JMenuItem();
			RB.setText(mMultiSelect, "gui.viewer.CanvasMouseListener.mMultiSelect");
			mMultiSelect.addActionListener(this);

			JPopupMenu menu = new JPopupMenu();
			menu.add(mVisitUrl);
			menu.add(mPointInf);
			menu.add(mMultiSelect);
			menu.show(e.getComponent(), e.getX(), e.getY());
		}

		@Override
		public void actionPerformed(ActionEvent e)
		{
			if (e.getSource() == mDatabaseSearch)
			{
				try
				{
					panel.visitUrl();
				}
				catch (UnsupportedEncodingException ex)
				{
					ex.printStackTrace();
				}
			}

			else if (e.getSource() == mPointInf)
				panel.showDataPointDialog(underMouse);

			else if (e.getSource() == mMultiSelect)
			{
				dataSet.setMultiSelectionPoint(underMouse);
				winMain.getMultiSelectPanel().setVisible(true);
			}
		}
	}
}