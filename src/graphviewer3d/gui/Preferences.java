package graphviewer3d.gui;

import scri.commons.gui.SystemUtils;
import scri.commons.gui.XMLPreferences;

public class Preferences extends XMLPreferences
{
	// Unique Curlywhirly ID for this user
	public static String curlywhirlyID = SystemUtils.createGUID(32);
	
	//boolean to indicate whether we need to show this user instructions for controlling the 3D interface
	public static boolean show3DControlInstructions = true;
}
