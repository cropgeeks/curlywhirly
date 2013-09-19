package curlywhirly.gui;

import curlywhirly.gui.dialog.PreferencesDialog;
import curlywhirly.gui.dialog.AboutDialog;
import java.awt.event.*;
import java.io.*;

import apple.dts.samplecode.osxadapter.*;

import curlywhirly.gui.viewer.*;
import curlywhirly.io.*;

import scri.commons.file.*;
import scri.commons.gui.*;

public class CurlyWhirly
{
	private static File prefsFile = getPrefsFile();
	private static File mruFile;
	public static Prefs prefs = new Prefs();

	// Optional path to a file to be loaded when app opens
	public static File initialFile = null;

	public static WinMain winMain;

	public static final String titleString = "CurlyWhirly - " + Install4j.VERSION;


	public static void main(String[] args)
		throws Exception
	{
		// OS X: This has to be set before anything else
		System.setProperty("com.apple.mrj.application.apple.menu.about.name", "CurlyWhirly");

		// Some handy debug output...
		System.out.println("CurlyWhirly " + Install4j.getVersion(CurlyWhirly.class) + " on "
			+ System.getProperty("os.name")	+ " (" + System.getProperty("os.arch") + ")");
		System.out.println("Using " + prefsFile);
//		java.util.Map vuMap = javax.media.j3d.VirtualUniverse.getProperties();
//		System.out.println("Runtime Java Version = " + System.getProperty("java.version"));
//		System.out.println("Java 3D version = " + vuMap.get("j3d.version"));
//		System.out.println("Renderer = " + vuMap.get("j3d.renderer") + "\n");

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
		i4j.setURLs("http://bioinf.hutton.ac.uk/curlywhirly/installers/updates.xml",
				    "http://bioinf.hutton.ac.uk/curlywhirly/logs/curlywhirly.pl");

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
			try { FileUtils.copyFile(old, file, true); }
			catch (IOException e) {}

		return file;
	}

	// Methods required for better native support on OS X

	private void handleOSXStupidities()
	{
		try
		{
			// Register handlers to deal with the System menu about/quit options
//			OSXAdapter.setPreferencesHandler(this,
//				getClass().getDeclaredMethod("osxPreferences", (Class[])null));
			OSXAdapter.setAboutHandler(this,
				getClass().getDeclaredMethod("osxAbout", (Class[])null));
			OSXAdapter.setQuitHandler(this,
				getClass().getDeclaredMethod("osxShutdown", (Class[])null));

			// Dock the menu bar at the top of the screen
			System.setProperty("apple.laf.useScreenMenuBar", "true");
		}
		catch (Exception e) {}
	}

	/** "Preferences" on the OS X system menu. */
	public void osxPreferences()
	{
		new PreferencesDialog(winMain);
	}

	/** "About CurlyWhirly" on the OS X system menu. */
	public void osxAbout()
	{
		new AboutDialog();
	}

	/** "Quit CurlyWhirly" on the OS X system menu. */
	public boolean osxShutdown()
	{
		shutdown();
		return true;
	}
}