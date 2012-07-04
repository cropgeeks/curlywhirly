package curlywhirly.gui;

import java.util.*;
import javax.swing.*;

/**
 * An icon manager for retrieving resource icons. When asked to retrieve an icon
 * it first attempts to see if it's been loaded already (and stored in its
 * hash lookup table). If it is, it returns it. If not, it is loaded from disk
 * and then added to the hash table.
 */
public class Icons2
{
	private static String pth;
	private static String ext;

	private static HashMap<String, ImageIcon> hashtable =
		new HashMap<String, ImageIcon>();

	public static void initialize(String basePath, String extension)
	{
		pth = basePath;
		ext  = extension;
	}

	public static ImageIcon getIcon(String name)
	{
		ImageIcon icon = hashtable.get(name);

		if (icon == null)
		{
			Icons2 icons = new Icons2();
			Class c = icons.getClass();

			String filename = name.toLowerCase() + ext;
			icon = new ImageIcon(c.getResource(pth + "/" + filename));

			hashtable.put(name, icon);
		}

		return icon;
	}
}