package curlywhirly.opengl;

import java.awt.*;
import javax.media.opengl.*;

import com.jogamp.opengl.util.awt.*;

import curlywhirly.gui.*;
import curlywhirly.gui.viewer.*;

/**
 * Renders the X in the top right hand corner of the screen which the user can
 * click on to close the currently loaded dataset.
 */
public class CloseOverlay implements GLEventListener
{
	private final WinMain winMain;
	private Overlay overlay;

	private int xLeft;
	private int xRight;
	private int yHigh;
	private int yLow;
	private int canvasWidth;

	public CloseOverlay(WinMain winMain)
	{
		this.winMain = winMain;
	}

	@Override
	public void init(GLAutoDrawable drawable)
	{
		overlay = new Overlay(drawable);
	}

	@Override
	public void dispose(GLAutoDrawable drawable)
	{
		overlay = null;
	}

	@Override
	public void display(GLAutoDrawable drawable)
	{
		Graphics2D g2d = overlay.createGraphics();

		// To forcce the relevant area of the overlay to redraw (allowing colour
		// changes) use mark dirty. Dirty area is wider than drawing area to
		// ensure we update all of the x drawn in the overaly.
		overlay.markDirty(canvasWidth-25, 5, 20, 20);

		 xLeft = canvasWidth - 20;
		 xRight = canvasWidth - 10;
		 yHigh = 10;
		 yLow = 20;

		g2d.setColor(ColorPrefs.get("User.OpenGLPanel.closeButtonColor"));
		g2d.setStroke(new BasicStroke(3));
		// Top left to bottom right stroke of X
		g2d.drawLine(xLeft, yHigh, xRight, yLow);
		// Bottom left to top right stroke of X
		g2d.drawLine(xLeft, yLow, xRight, yHigh);

		overlay.drawAll();

		g2d.dispose();
	}

	@Override
	public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height)
	{
		canvasWidth = width;
	}

	// Called from the CanvasMouseListener to carry out the close operation
	public void handleClick(int x, int y)
	{
		if (x > xLeft && x < xRight && y > yHigh && y < yLow)
		{
			if (winMain.okToClose())
				winMain.closeDataSet();
		}
	}
}