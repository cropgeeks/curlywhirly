// Copyright 2009-2021 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package jhi.curlywhirly.gui;

import scri.commons.gui.*;
import scri.commons.io.*;

public class Prefs extends XMLPreferences
{
	// Unique Curlywhirly ID for this user
	public static String curlywhirlyID = SystemUtils.createGUID(32);

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

	// Display localised text in...
	public static String localeText = "auto";

	// When to check for updates
	public static int guiUpdateSchedule = Install4j.STARTUP;

	public static boolean guiChkAxisLabels = true;
	public static boolean guiChkDatasetLabels = false;
	public static int guiAxisLabelsSize = 50;
	public static int guiSelectedPointSize = 50;
	public static int guiDeselectedPointSize = 50;
	public static int guiPointQuality = 2;
	public static int guiDeselectedPointOpacity = 50;
	public static int guiDeselectedRenderer = 0;
	public static int guiDeselectedGrey = 0;
	public static int guiDeselectedTransparent = 1;
	public static int guiDeselectedInvisible = 2;

	public static boolean guiWarnOnExit = true;
	public static boolean guiWarnOnClose = true;

	public static int guiMovieCaptureFrameRate = 30;
	public static int guiMovieCaptureLength = 5;

	public static float guiRotationSpeed = -0.5f;

	public static int guiDataPanelFilter = 0;

	public static boolean guiChkAnchorPoints = true;
	public static boolean guiChkSelectionAxes = false;
	public static float guiSelectionSphereSize = 0.06f;

	public static boolean guiMovieChkColourKey = true;

	public static boolean guiDrawAxisTicks = false;

	public static boolean ioUseFileColors = true;

	// The local working directory for disk caching
	public static String cacheFolder =
		FileUtils.getTempUserDirectory("jhi-curlywhirly").getPath();
}