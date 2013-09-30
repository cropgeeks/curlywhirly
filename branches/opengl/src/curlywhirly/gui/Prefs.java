package curlywhirly.gui;

import java.awt.Color;
import java.io.*;
import java.util.*;

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
	public static int guiWinMainW = 1000;
	public static int guiWinMainH = 700;
	public static int guiWinMainX = 0;
	public static int guiWinMainY = 0;
	public static boolean guiWinMainMaximized = false;
	public static int guiSplitterLocation = 250;

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

	public static Color guiXAxisColor = Color.GREEN;
	public static Color guiYAxisColor = Color.GREEN;
	public static Color guiZAxisColor = Color.GREEN;

	public static int guiMovieCaptureFrameRate = 30;
	public static int guiMovieCaptureSpinSpeedIndex = 1;

	public static boolean guiAntialiasAxes = true;

	// The local working directory for disk caching
	public static String cacheFolder =
		SystemUtils.getTempUserDirectory("jhi-curlywhirly").getPath();

	// A list of previously accessed documents
	public static String[] guiRecentDocs = new String[10];

	// Updates the array of recently accessed documents so that 'document' is
	// the first element, even if it has been accessed previously
	public static void setRecentDocument(File file)
	{
		// Convert the array of files back into a single string
		String mostRecent = file.getPath();

		LinkedList<String> list = new LinkedList<String>();
		list.addAll(Arrays.asList(guiRecentDocs));

		if (list.contains(mostRecent))
			list.remove(mostRecent);

		list.addFirst(mostRecent);

		for (int i = 0; i < guiRecentDocs.length; i++)
			guiRecentDocs[i] = list.get(i);
	}
}