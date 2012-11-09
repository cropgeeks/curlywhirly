package curlywhirly.gui;

import java.awt.*;
import java.awt.event.*;
import java.util.*;

import javax.media.j3d.*;

import com.sun.j3d.utils.geometry.*;
import com.sun.j3d.utils.picking.*;

import scri.commons.gui.*;

/**
 * Behaviour class that listens for mouseover on marker rings (cylinders) and highlights these in a different colour as well as pops up a label next to them
 */
public class MouseOverBehavior extends Behavior
{
	private PickCanvas pickCanvas;
	private PickResult pickResult;

	private Primitive pickedNode;
	private boolean isObjectSelectedBefore = false;
	private Primitive lastPickedNode = null;
	private BranchGroup objRoot;

	public HashMap<Sphere, String> namesHashT = null;

	MainCanvas canvas;
	CurlyWhirly frame;

	public MouseOverBehavior(CurlyWhirly frame, HashMap<Sphere, String> namesHashT, BranchGroup objRoot, float sphereSize)
	{
		this.namesHashT = namesHashT;
		this.objRoot = objRoot;
		this.canvas = frame.canvas3D;
		this.frame = frame;

		pickCanvas = new PickCanvas(canvas, objRoot);
		pickCanvas.setTolerance(sphereSize*2);
		pickCanvas.setMode(PickCanvas.GEOMETRY_INTERSECT_INFO);
	}

	public void initialize()
	{
		wakeupOn(new WakeupOnAWTEvent(MouseEvent.MOUSE_MOVED));
	}

	public void processStimulus(Enumeration criteria)
	{
		WakeupCriterion wakeup;
		AWTEvent[] event;
		int eventId;

		while (criteria.hasMoreElements())
		{
			wakeup = (WakeupCriterion) criteria.nextElement();
			if (wakeup instanceof WakeupOnAWTEvent)
			{
				event = ((WakeupOnAWTEvent) wakeup).getAWTEvent();
				for (int ii = 0; ii < event.length; ii++)
				{
					eventId = event[ii].getID();
					if (eventId == MouseEvent.MOUSE_MOVED)
					{
						int x = ((MouseEvent) event[ii]).getX();
						int y = ((MouseEvent) event[ii]).getY();

						pickCanvas.setShapeLocation(x, y);

						pickResult = pickCanvas.pickClosest();

						if (pickResult != null && pickResult.getNode(PickResult.PRIMITIVE) != null)
						{
							pickedNode = ((Primitive) pickResult.getNode(PickResult.PRIMITIVE));

							// check whether this is the same object as before or not
							if (lastPickedNode != null)
							{
								// if this primitive picked is not the same as the previously picked primitive
								if (!lastPickedNode.equals(pickedNode))
								{
									isObjectSelectedBefore = false;
								}
							}

							// if this is the object picked last
							if (isObjectSelectedBefore)
							{
								CurlyWhirly.canvas3D.mouseOverX = x;
								CurlyWhirly.canvas3D.mouseOverY = y;
								CurlyWhirly.canvas3D.repaint();
							}
							else
							{
								try
								{
									// only do this when we have selected a sphere
									if (Class.forName("curlywhirly.gui.DataSphere").isInstance(pickedNode))
									{
										// first find out which marker ring (cylinder) has been picked
										DataSphere sphere = (DataSphere) pickedNode;
										String mName = namesHashT.get(sphere);
										// display the feature name on the status bar of the frame but only if we are not currently recording a movie
										if(frame.currentMovieCaptureThread == null)
										{
											frame.statusBar.setMessage(RB.format("gui.MouseOverBehavior.pointSelected", mName));

											//set the appropriate variables on the canvas so we can display a tooltip over the data point
											CurlyWhirly.canvas3D.isMouseOver = true;
											CurlyWhirly.canvas3D.mouseOverSphere = sphere;
											CurlyWhirly.canvas3D.mouseOverX = x;
											CurlyWhirly.canvas3D.mouseOverY = y;
											CurlyWhirly.canvas3D.repaint();


											if (CurlyWhirly.dataAnnotationURL != null)
											{
												CurlyWhirly.canvas3D.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
											}
										}
									}
								}
								catch (CapabilityNotSetException e)
								{
								}
								catch (ClassNotFoundException e)
								{
									e.printStackTrace();
								}

								// set flag to indicate it has been picked before
								isObjectSelectedBefore = true;
								lastPickedNode = pickedNode;
							}
						}
						// this is executed when no primitive has been picked at all
						else
						{
							//do not display a string
							CurlyWhirly.canvas3D.mouseOverSphere = null;
							CurlyWhirly.canvas3D.mouseOverX = -1;
							CurlyWhirly.canvas3D.mouseOverY = -1;
							if(CurlyWhirly.canvas3D.isMouseOver)
								CurlyWhirly.canvas3D.repaint();
							CurlyWhirly.canvas3D.isMouseOver = false;
							frame.statusBar.setDefaultText();

							//show a normal cursor
							CurlyWhirly.canvas3D.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));

							pickedNode = null;
							isObjectSelectedBefore = false;
						}
					}
				}
			}
		}
		wakeupOn(new WakeupOnAWTEvent(MouseEvent.MOUSE_MOVED));
	}
}