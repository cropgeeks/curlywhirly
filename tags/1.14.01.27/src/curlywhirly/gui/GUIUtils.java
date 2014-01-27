package curlywhirly.gui;

import java.awt.*;
import java.io.*;
import java.net.*;
import javax.swing.*;

import scri.commons.gui.*;

public class GUIUtils
{
	/**
	 * Returns an array of colours the length of numColours
	 */
	public static Color [] generateColours(int numColours)
	{
		Color [] colours = new Color[numColours];
		float increment = 1/(float)numColours;
		float currentHue = 0;
		for (int i = 0; i < colours.length; i++)
		{
			colours[i] = Color.getHSBColor(currentHue, 1, 1);
			currentHue += increment;
		}
		return colours;
	}

	public static void visitURL(String html)
	{
		try
		{
			Desktop desktop = Desktop.getDesktop();

			URI uri = new URI(html);
			desktop.browse(uri);
		}
		catch(URISyntaxException | IOException e)
		{
			String message = RB.format("gui.GUIUtils.urlError", html);
			TaskDialog.error(message, RB.getString("gui.text.close"));

			e.printStackTrace();
		}
	}

	public static void cancelMovieCapture()
	{
		//cancel any ongoing movie capture thread
//		CurlyWhirly.currentMovieCaptureThread.threadCanceled = true;
//		CurlyWhirly.currentMovieCaptureThread.movieFile.delete();
//		CurlyWhirly.canvas3D.resetOriginalView();
//		CurlyWhirly.canvas3D.repaint();
	}

	public static void sendFeedback()
	{
		try
		{
			Desktop desktop = Desktop.getDesktop();
			desktop.mail(new URI("mailto:curlywhirly@hutton.ac.uk?subject=CurlyWhirly%20Feedback"));
		}
		catch (Exception e) { System.out.println(e); }
	}

	/*
	 * @param panel the panel to apply the effect to
	 * @param opague if the panel should be opague or not on OS X
	 */
	public static void setPanelColor(JPanel panel, boolean opagueOnOSX)
	{
		if (SystemUtils.isMacOS() == false)
			panel.setBackground(Color.white);

		else
			panel.setOpaque(opagueOnOSX);
	}
}