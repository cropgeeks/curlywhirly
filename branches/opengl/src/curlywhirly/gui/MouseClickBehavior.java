package curlywhirly.gui;

import java.awt.*;
import java.awt.event.*;
import java.util.*;

import javax.media.j3d.*;

import com.sun.j3d.utils.geometry.*;
import com.sun.j3d.utils.picking.*;
import java.net.URLEncoder;

/**
 * Behaviour class that listens for mouseover on marker rings (cylinders) and highlights these in a different colour as well as pops up a label next to them
 */
public class MouseClickBehavior extends Behavior
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

	public MouseClickBehavior(CurlyWhirly frame, HashMap<Sphere, String> namesHashT, BranchGroup objRoot, float sphereSize)
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
		wakeupOn(new WakeupOnAWTEvent(MouseEvent.MOUSE_CLICKED));
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
					if (eventId == MouseEvent.MOUSE_CLICKED)
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
								// do nothing
							}
							else
							{
								try
								{
									// only do this when we have selected a sphere
									if (Class.forName("com.sun.j3d.utils.geometry.Sphere").isInstance(pickedNode))
									{
										// first find out which marker ring (cylinder) has been picked
										Sphere sphere = (Sphere) pickedNode;
										// display the feature name on the status bar of the frame but only if we are not currently recording a movie
										if(frame.currentMovieCaptureThread == null)
										{
											String dataEntryLabel = ((DataSphere)sphere).dataEntry.label;
											//now we can open a web browser with the stored URL and the current label as a parameter
											if(CurlyWhirly.dataAnnotationURL != null)
											{
												try
												{
													String objName = URLEncoder.encode(dataEntryLabel, "UTF-8");
													String url = CurlyWhirly.dataAnnotationURL.replace("$DATA", objName);
													if (url.indexOf("?") == -1)
														url += "?application=curlywhirly";
													else
														url += "&application=curlywhirly";

													GUIUtils.visitURL(url);
												}
												catch (Exception e)
												{
													e.printStackTrace();
												}
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
							if(frame.currentMovieCaptureThread == null)
								frame.statusBar.setDefaultText();
							pickedNode = null;
							isObjectSelectedBefore = false;
						}
					}
				}
			}
		}
		wakeupOn(new WakeupOnAWTEvent(MouseEvent.MOUSE_CLICKED));
	}
}