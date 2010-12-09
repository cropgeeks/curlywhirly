package curlywhirly.gui;

import java.awt.*;
import java.lang.reflect.*;
import java.net.*;
import javax.vecmath.Color3f;

import scri.commons.gui.*;

public class GUIUtils
{
	// ---------------------------------------------------------------------------------------------------------------------

	/**
	 * Returns an array of colours the length of numColours
	 */
	public static Color3f [] generateColours(int numColours)
	{
		Color3f [] colours = new Color3f[numColours];
		float increment = 1/(float)numColours;
		float currentHue = 0;
		for (int i = 0; i < colours.length; i++)
		{
			Color col = Color.getHSBColor(currentHue, 1, 1);
			colours[i] = new Color3f(col);
			currentHue += increment;
		}
		return colours;
	}
	// ---------------------------------------------------------------------------------------------------------------------

	public static void visitURL(String html)
	{
		try
		{
			if (SystemUtils.jreVersion() >= 1.6)
				visitURL6(html);
			else
				visitURL5(html);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	// Java6 method for visiting a URL
	private static void visitURL6(String html)
		throws Exception
	{
		Desktop desktop = Desktop.getDesktop();

		URI uri = new URI(html);
		desktop.browse(uri);
	}

	// Java5 (OS X only) method for visiting a URL
	private static void visitURL5(String html)
		throws Exception
	{
		// See: http://www.centerkey.com/java/browser/

		Class<?> fileMgr = Class.forName("com.apple.eio.FileManager");
		Method openURL = fileMgr.getDeclaredMethod("openURL",
			new Class[] {String.class});

		openURL.invoke(null, new Object[] {html});
	}
	
	
	public static void cancelMovieCapture()
	{
		//cancel any ongoing movie capture thread
		CurlyWhirly.currentMovieCaptureThread.threadCanceled = true;
		CurlyWhirly.currentMovieCaptureThread.movieFile.delete();
		CurlyWhirly.canvas3D.resetOriginalView();
		CurlyWhirly.canvas3D.repaint();
	}
	
	
}
