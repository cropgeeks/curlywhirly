package graphviewer3d.gui;

import scri.commons.gui.SystemUtils;
import scri.commons.gui.XMLPreferences;

import javax.swing.*;

public class Preferences extends XMLPreferences
{
	//the number of the last version released
	public static String lastVersion = null;
	
	// Unique Curlywhirly ID for this user
	public static String curlywhirlyID = SystemUtils.createGUID(32);

	//boolean to indicate whether we need to show this user instructions for controlling the 3D interface
	public static boolean show3DControlInstructions = true;

	// last "looked at" location when file browsing
	// (default location to user's home, my documents, etc...)
	public static String lastDir = ""
		+ new JFileChooser().getFileSystemView().getDefaultDirectory();
}