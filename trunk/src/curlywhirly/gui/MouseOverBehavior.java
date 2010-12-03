package curlywhirly.gui;

import java.awt.AWTEvent;
import java.awt.event.MouseEvent;
import java.util.Enumeration;
import java.util.HashMap;

import javax.media.j3d.Behavior;
import javax.media.j3d.BranchGroup;
import javax.media.j3d.CapabilityNotSetException;
import javax.media.j3d.WakeupCriterion;
import javax.media.j3d.WakeupOnAWTEvent;

import com.sun.j3d.utils.geometry.Primitive;
import com.sun.j3d.utils.geometry.Sphere;
import com.sun.j3d.utils.picking.PickCanvas;
import com.sun.j3d.utils.picking.PickResult;

/**
 * Behaviour class that listens for mouseover on marker rings (cylinders) and highlights these in a different colour as well as pops up a label next to them
 * 
 * @author Micha Bayer, Scottish Crop Research Institute
 */
public class MouseOverBehavior extends Behavior
{
	
	// =======================================vars==============================
	
	private PickCanvas pickCanvas;
	private PickResult pickResult;
	
	private Primitive pickedNode;
	private boolean isObjectSelectedBefore = false;
	private Primitive lastPickedNode = null;
	private BranchGroup objRoot;
	
	public HashMap namesHashT = null;
	
	MainCanvas canvas;
	CurlyWhirly frame;
	
	// ========================================c'tor============================
	
	public MouseOverBehavior(CurlyWhirly frame, HashMap _namesHashT, BranchGroup _objRoot, float sphereSize)
	{
		this.namesHashT = _namesHashT;
		this.objRoot = _objRoot;
		this.canvas = frame.canvas3D;
		this.frame = frame;
		
		pickCanvas = new PickCanvas(canvas, objRoot);
		pickCanvas.setTolerance(sphereSize*2);
		pickCanvas.setMode(PickCanvas.GEOMETRY_INTERSECT_INFO);
	}
	
	// ===========================================methods============================
	
	public void initialize()
	{
		wakeupOn(new WakeupOnAWTEvent(MouseEvent.MOUSE_MOVED));
		
	}
	
	// ------------------------------------------------------------------------------------------------------------------------------------------
	
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
										String mName = (String) namesHashT.get(sphere);
										// display the feature name on the status bar of the frame but only if we are not currently recording a movie
										if(frame.currentMovieCaptureThread == null)
											frame.statusBar.setMessage(" Point selected: " + mName);
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
		wakeupOn(new WakeupOnAWTEvent(MouseEvent.MOUSE_MOVED));
	}
	
	// ------------------------------------------------------------------------------------------------------------------------------------------
}// end class
