package curlywhirly.gui;

import java.awt.*;

public class CanvasLabelFadeInThread extends Thread
{
	MainCanvas mainCanvas = null;
	
	public CanvasLabelFadeInThread(MainCanvas mainCanvas)
	{
		this.mainCanvas = mainCanvas;
	}

	public void run()
	{
		float fontColor = mainCanvas.getBackground().getRed();
		float step = fontColor / 15f;
		
		//fade the font to black
		for (int i = 0; i < 16; i++)
		{
			int c = (int) fontColor;
//			System.out.println("new colour = " + c);
			mainCanvas.openFileLabelColour = new Color(c, c, c);
			fontColor -= step;
			mainCanvas.repaint();

			try { Thread.sleep(100); }
			catch (Exception ex) {}
		}
	}
	
}
