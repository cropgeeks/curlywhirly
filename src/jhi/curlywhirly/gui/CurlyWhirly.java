// Copyright 2009-2022 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package jhi.curlywhirly.gui;

import java.awt.*;
import java.awt.desktop.*;
import jhi.curlywhirly.gui.dialog.*;
import jhi.curlywhirly.io.*;
import jhi.curlywhirly.util.*;
import scri.commons.gui.*;

import java.awt.event.*;
import java.io.*;

public class CurlyWhirly implements OpenFilesHandler
{
	private static final File prefsFile = getPrefsFile();
	private static File mruFile;
	public static final Prefs prefs = new Prefs();

	// Optional path to a file to be loaded when app opens
	public static File initialFile = null;

	public static WinMain winMain;

	public static void main(String[] args)
		throws Exception
	{
		// Some handy debug output...
		System.out.println("CurlyWhirly " + Install4j.getVersion(CurlyWhirly.class) + " on "
			+ System.getProperty("os.name") + " (" + System.getProperty("os.arch") + ")");
		System.out.println("Using " + prefsFile);

		mruFile = new File(prefsFile.getParent(), "curlywhirly-recent.xml");
		CurlyWhirlyFileHandler.loadMRUList(mruFile);

		// preferences
		ColorPrefs.load();
		prefs.loadPreferences(prefsFile, Prefs.class);
		prefs.savePreferences(prefsFile, Prefs.class);

		Icons.initialize("/res/icons", ".png");
		RB.initialize(Prefs.localeText, "res.text.curlywhirly");

		// Start the GUI (either with or without an initial project)
		if (args.length == 1 && args[0] != null)
			initialFile = new File(args[0]);

		install4j();

		new CurlyWhirly();
	}

	// Sets up the install4j environment to check for updates
	private static void install4j()
	{
		Install4j i4j = new Install4j("7308-4813-7424-6439", "281");

		i4j.setUser(Prefs.guiUpdateSchedule, Prefs.curlywhirlyID, 0);
		i4j.setURLs("https://bioinf.hutton.ac.uk/curlywhirly/installers/updates.xml",
			"https://bioinf.hutton.ac.uk/curlywhirly/logs/curlywhirly.pl");

		i4j.doStartUpCheck(CurlyWhirly.class);
	}

	CurlyWhirly()
	{
		try
		{
			// Set System L&F
			Nimbus.customizeNimbus();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

		if (SystemUtils.isMacOS())
			handleOSXStupidities();

		winMain = new WinMain();

		winMain.addWindowListener(new WindowAdapter()
		{
			@Override
			public void windowClosing(WindowEvent e)
			{
				if (winMain.okToExit())
					shutdown();
			}

			@Override
			public void windowOpened(WindowEvent e)
			{
				// Do we want to open an initial project?
				if (initialFile != null)
				{
					winMain.getCommands().openFile(initialFile);
				}
			}

		});

		TaskDialog.initialize(winMain, RB.getString("gui.CurlyWhirly.title"));

		winMain.setVisible(true);
	}

	void shutdown()
	{
		Prefs.isFirstRun = false;
		prefs.savePreferences(prefsFile, Prefs.class);
		ColorPrefs.save();
		CurlyWhirlyFileHandler.saveMRUList(mruFile);

		System.exit(0);
	}

	private static File getPrefsFile()
	{
		// Ensure the .scri-bioinf folder exists
		File fldr = new File(System.getProperty("user.home"), ".scri-bioinf");
		fldr.mkdirs();

		// Color-prefs file
		ColorPrefs.setFile(new File(fldr, "curlywhirly-colors.xml"));

		// This is the file we really want
		File file = new File(fldr, "curlywhirly.xml");
//		System.out.println("prefs file = " + file.getAbsolutePath());

		// So if it exists, just use it
		if (file.exists())
			return file;

		// If not, see if the "old" (pre 21/06/2010) file is available
		File old = new File(System.getProperty("user.home"), ".curlywhirly.xml");

		if (old.exists())
			try
			{
				scri.commons.io.FileUtils.copyFile(old, file, true);
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}

		return file;
	}

	// Methods required for better native support on OS X

	private void handleOSXStupidities()
	{
		Desktop desktop = Desktop.getDesktop();

		// Register handlers to deal with the System menu about/quit options
        desktop.setAboutHandler(e -> osxAbout());
        desktop.setPreferencesHandler(e -> osxPreferences());
        desktop.setQuitHandler((e,r) -> osxShutdown());
		desktop.setOpenFileHandler(this);
	}

	/**
	 * "Preferences" on the OS X system menu.
	 */
	public void osxPreferences()
	{
		winMain.getCommands().showPrefs();
	}

	/**
	 * "About CurlyWhirly" on the OS X system menu.
	 */
	public void osxAbout()
	{
		new AboutDialog();
	}

	/**
	 * "Quit CurlyWhirly" on the OS X system menu.
	 */
	public boolean osxShutdown()
	{
		shutdown();
		return true;
	}

	/** Deal with desktop-double clicking of registered files */
	public void openFiles(OpenFilesEvent e)
	{
		String[] paths = new String[e.getFiles().size()];
		for (int i = 0; i < paths.length; i++)
			paths[i] = e.getFiles().get(i).toString();

		// If CW is already open, then open the file straight away
		if (winMain != null && winMain.isVisible())
		{
			// TODO: If we have project modified checks, do them here too
			winMain.getCommands().openFile(new File(paths[0]));
		}

		// Otherwise, mark it for opening once CW is ready
		else
			initialFile = new File(paths[0]);
	}
}