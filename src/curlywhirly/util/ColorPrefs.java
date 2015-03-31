// Copyright 2009-2015 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package curlywhirly.util;

import java.awt.*;
import java.io.*;
import java.util.*;

public class ColorPrefs
{
	private static final HashMap<String, ColorPref> colors = new HashMap<>();
	private static final Properties p = new Properties();

	private static File file;

	/**
	 * Returns a color (via a name key) from the database.
	 */
	public static ColorPref get(String key)
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

	public static Color getColor(String key)
	{
		try
		{
			return colors.get(key).getColor();
		}
		catch (Exception e)
		{
			return null;
		}
	}

	public static float[] getColorAsRGB(String key)
	{
		try
		{
			return colors.get(key).getColor().getRGBColorComponents(new float[3]);
		}
		catch (Exception e)
		{
			return null;
		}
	}

	public static HashMap<String, ColorPref> getColors()
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
		initColor("User.OpenGLPanel.axisLabels", Color.WHITE);
		initColor("User.OpenGLPanel.closeButtonColor", Color.DARK_GRAY);
		initColor("User.OpenGLPanel.multiSelectColor", Color.WHITE);
		initColor("User.OpenGLPanel.multiSelectSphereColor", new Color(0.5f, 0.5f, 1.0f, 0.4f));
		initColor("User.OpenGLPanel.multiSelectLineColor", Color.RED);
		initColor("User.OpenGLPanel.multiSelectAxesColor", Color.BLUE);
	}

	/**
	 * Ensures the given color (via its key) is in the database, setting it to
	 * the supplied default if it's not.
	 */
	private static void initColor(String key, Color color)
	{
		Color prevColor = getColor(key);

		if (prevColor == null)
			colors.put(key, new ColorPref(key, color));
	}

	// Helper methods used by the ReadGroupScheme for direct access to the DB
	////////////////////////////////////////////////////////////////////////////
	public static void setColor(String key, Color color)
	{
		colors.get(key).setColor(color);
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
		catch (IOException t) { t.printStackTrace(); }

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

				// This conditional should be removed after the first release
				// after 31/03/2015. It's being used to remove a now unused
				// key from the color preferences on users machines.
				if (key.equals("User.OpenGLPanel.textColor"))
					continue;

				initColor(key, c);
			}
			catch (NumberFormatException e) { e.printStackTrace(); }
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
				Color c = colors.get(key).getColor();
				p.setProperty(key, c.getRed() + "," + c.getGreen() + "," + c.getBlue());
			}

			// Then stream it to disk
			BufferedOutputStream os = new BufferedOutputStream(
				new FileOutputStream(file));
			p.storeToXML(os, null);
			os.close();
		}
		catch (IOException t) { t.printStackTrace(); }
	}

	public static class ColorPref
	{
		private final String key;
		private Color color;

		ColorPref(String key, Color color)
		{
			this.key = key;
			this.color = color;
		}

		public String getKey()
		{
			return key;
		}

		public Color getColor()
		{
			return color;
		}

		public void setColor(Color color)
		{
			this.color = color;
		}
	}
}