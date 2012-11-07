package curlywhirly.gui;

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

	// The width, height, location and maximized status of the main window
	public static int guiWinMainWidth = 900;
	public static int guiWinMainHeight = 800;
	public static int guiWinMainX = 0;
	public static int guiWinMainY = 0;
	public static boolean guiWinMainMaximized = false;

	// Is this the first time the program has ever been run (by this user)?
	public static boolean isFirstRun = true;

	//this boolean indicates whether we should display data labels on mouseOver
	public static boolean showMouseOverLabels = true;

	// Display localised text in...
	public static String localeText = "auto";
}