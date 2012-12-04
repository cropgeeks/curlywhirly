package curlywhirly.gui;

import curlywhirly.controller.*;

import scri.commons.gui.*;

public class Prefs extends XMLPreferences
{
	//the number of the last version released
	public static String lastVersion = null;

	public static boolean isSCRIUser = false;

	// Unique Curlywhirly ID for this user
	public static String curlywhirlyID = SystemUtils.createGUID(32);

	//boolean to indicate whether we need to show this user instructions for controlling the 3D interface
	public static boolean show3DControlInstructions = true;

	// last "looked at" location when file browsing
	// (default location to user's home, my documents, etc...)
	public static String guiCurrentDir = "";

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

	// When to check for updates
	public static int guiUpdateSchedule = Install4j.STARTUP;

	// Graph background color
	public static int guiGraphBackground = 0;

	public static int guiMovieCaptureFrameRate = 30;
	public static int guiMovieCaptureSpinSpeedIndex = 1;

	// The local working directory for disk caching
	public static String cacheFolder =
		SystemUtils.getTempUserDirectory("jhi-curlywhirly").getPath();
}