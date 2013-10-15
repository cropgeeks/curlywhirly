// Copyright 2009-2013 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package curlywhirly.gui.viewer;

import java.awt.*;
import java.io.*;
import java.util.*;

public class ColorPrefs
{
	private static HashMap<String,Color> colors = new HashMap<>();

	private static File file;
	private static Properties p = new Properties();

	/**
	 * Returns a color (via a name key) from the database.
	 */
	public static Color get(String key)
	{
		try
		{
			return colors.get(key);
		}
		catch (Exception e)
		{
			return null;
		}
	}

	public static HashMap<String,Color> getColors()
	{
		return colors;
	}

	public static void resetUserColors()
	{
		// Search (and clear) all colours beginning with "User." from the hash
		ArrayList<String> toRemove = new ArrayList<>();
		for (String key: colors.keySet())
			if (key.startsWith("User."))
				toRemove.add(key);
		for (String key: toRemove)
			colors.remove(key);

		// Then rebuild them
		initializeColors();
	}

	private static void initializeColors()
	{
		// Main canvas colors
		initColor("User.OpenGLPanel.xAxisColor", Color.GREEN);
		initColor("User.OpenGLPanel.yAxisColor", Color.GREEN);
		initColor("User.OpenGLPanel.zAxisColor", Color.GREEN);
		initColor("User.OpenGLPanel.background", Color.BLACK);
		initColor("User.OpenGLPanel.textColor", Color.WHITE);
	}


	/**
	 * Ensures the given color (via its key) is in the database, setting it to
	 * the supplied default if it's not.
	 */
	private static void initColor(String key, Color defaultColor)
	{
		Color color = get(key);

		if (color == null)
			colors.put(key, defaultColor);
	}

	// Helper methods used by the ReadGroupScheme for direct access to the DB
	////////////////////////////////////////////////////////////////////////////
	public static void setColor(String key, Color color)
	{
		colors.put(key, color);
	}

	public static void removeColor(String key)
	{
		colors.remove(key);
	}
	////////////////////////////////////////////////////////////////////////////

	public static void setFile(File colorFile)
	{
		file = colorFile;
	}

	public static void load()
		throws Exception
	{
		Properties p = new Properties();

		try
		{
			// Load in the color (strings) from the file
			BufferedInputStream in = new BufferedInputStream(
				new FileInputStream(file));
			p.loadFromXML(in);
			in.close();
		}
		catch (Throwable t) {}

		// Assign them to the main hashmap
		for (Enumeration<?> keys = p.keys(); keys.hasMoreElements();)
		{
			try
			{
				String key = (String) keys.nextElement();
				String[] s = p.getProperty(key).split(",");

				Color c = new Color(
					Integer.parseInt(s[0]),
					Integer.parseInt(s[1]),
					Integer.parseInt(s[2]));

				colors.put(key, c);
			}
			catch (Exception e) {}
		}

		initializeColors();
	}

	public static void save()
	{
		try
		{
			Properties p = new Properties();

			// Dump the hashmap back into a properties object
			for (String key: colors.keySet())
			{
				Color c = colors.get(key);
				p.setProperty(key, c.getRed() + "," + c.getGreen() + "," + c.getBlue());
			}

			// Then stream it to disk
			BufferedOutputStream os = new BufferedOutputStream(
				new FileOutputStream(file));
			p.storeToXML(os, null);
			os.close();
		}
		catch (Throwable t) {}
	}
}