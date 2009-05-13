package graphviewer3d.gui;

import java.awt.event.*;
import scri.commons.gui.*;

public class FrameListener implements ComponentListener,WindowFocusListener

{
	
	GraphViewerFrame frame = null;
	
	public boolean windowMoved = false;
	
	public FrameListener(GraphViewerFrame frame)
	{
		this.frame = frame;
	}
	
	
	public void windowLostFocus(WindowEvent e)
	{
	}
	
	public void componentMoved(ComponentEvent e)
	{
		//if the user has moved the window the movie screen capture will be disrupted and
		//we need to cancel the capture
		if(frame.currentMovieCaptureThread != null && !windowMoved)
		{
			frame.fatController.cancelMovieCapture();
			TaskDialog.initialize(null, "CurlyWhirly");
			TaskDialog.error("Window moved -- movie capture failed", "Close");
		}
		windowMoved = true;
	}
	
	
	public void componentResized(ComponentEvent e)
	{
		//if the user has resized the window the movie screen capture will be disrupted and
		//we need to cancel the capture
		if(frame.currentMovieCaptureThread != null)
		{
			frame.fatController.cancelMovieCapture();
			TaskDialog.initialize(frame, "CurlyWhirly");
			TaskDialog.error("Window resized -- movie capture failed", "Close");
		}
	}
	
	//not used
	public void componentShown(ComponentEvent e){}
	public void windowGainedFocus(WindowEvent e){}
	public void componentHidden(ComponentEvent e){}
	
}
