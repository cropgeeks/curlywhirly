package curlywhirly.gui;

import java.awt.event.*;

public class CanvasKeyListener implements KeyListener
{
	private GraphViewer3DCanvas canvas;
	
	public CanvasKeyListener(GraphViewer3DCanvas canvas)
	{
		this.canvas = canvas;
	}
	
	public void keyPressed(KeyEvent e)
	{
		//ESC has been pressed
		if(e.getKeyCode() == 27)
		{
			//cancel any ongoing movie capture thread
			canvas.frame.fatController.cancelMovieCapture();
		}
	}
	
	// not used for now
	public void keyReleased(KeyEvent e)
	{
	}
	
	public void keyTyped(KeyEvent e)
	{
	}
	
}
