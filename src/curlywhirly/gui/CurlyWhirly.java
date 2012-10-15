package curlywhirly.gui;

import java.awt.*;
import java.awt.dnd.*;
import java.awt.event.*;
import java.io.*;
import javax.swing.*;
import curlywhirly.controller.*;
import curlywhirly.data.*;

import apple.dts.samplecode.osxadapter.*;

import scri.commons.file.*;
import scri.commons.gui.*;

public class CurlyWhirly extends JFrame
{

	// ===================================================vars =================================================

	public static DataSet dataSet;
	public static MainCanvas canvas3D;

	public int controlPanelWidth = 200;

	public static MTControlPanel controlPanel;
	static JCheckBox instructionsCheckBox;
	public static boolean dataLoaded = false;

	private static File prefsFile = getPrefsFile();
	public static Preferences prefs = new Preferences();
	public StatusBar statusBar;
	public static MenuBar menuBar;

	public static MovieCaptureThread currentMovieCaptureThread = null;

	public FrameListener frameListener = null;

	public static DataLoader dataLoader =null;

	public static String dataAnnotationURL = null;

	// Optional path to a file to be loaded when app opens
	public static String initialFile = null;
	public static boolean dragAndDropDataLoad = false;

	public static CurlyWhirly curlyWhirly = null;

	public static final String titleString = "CurlyWhirly - " + Install4j.VERSION;


	//==========================================================


	public static void main(String[] args)
	{
		// OS X: This has to be set before anything else
		System.setProperty("com.apple.mrj.application.apple.menu.about.name", "CurlyWhirly");

		try
		{
			// preferences
			prefs.loadPreferences(prefsFile, Preferences.class);

			// Set System L&F
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

		Icons2.initialize("/res/icons", ".png");

		RB.initialize(Preferences.localeText, "res.text.curlywhirly");

		curlyWhirly = new CurlyWhirly();
		dataLoader = new DataLoader();
	}

	CurlyWhirly()
	{
		//this initializes all the task dialog instances
		TaskDialog.initialize(this, RB.getString("gui.CurlyWhirly.title"));

		if (SystemUtils.isMacOS())
			handleOSXStupidities();

		Install4j.doStartUpCheck();

		setupComponents();
		pack();

		// get the GUI set up
		setTitle(RB.getString("gui.CurlyWhirly.title") + " - " + Install4j.VERSION);
		setSize(Preferences.guiWinMainWidth, Preferences.guiWinMainHeight);

		// Work out the current screen's width and height
		int scrnW = SwingUtils.getVirtualScreenDimension().width;
		int scrnH = SwingUtils.getVirtualScreenDimension().height;

		// Determine where on screen to display
		if (Preferences.isFirstRun)
			setLocationRelativeTo(null);
		else
			setLocation(Preferences.guiWinMainX, Preferences.guiWinMainY);

		// Maximize the frame if neccassary
		if (Preferences.guiWinMainMaximized)
			setExtendedState(Frame.MAXIMIZED_BOTH);

		setIconImage(Icons2.getIcon("curlywurly_icon32px").getImage());


		setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);

		addWindowListener(new WindowAdapter()
		{
			@Override
			public void windowClosing(WindowEvent e)
			{
				Preferences.isFirstRun = false;
				shutdown();
			}

			@Override
			public void windowOpened(WindowEvent e)
			{
				// Do we want to open an initial project?
				if (initialFile != null)
				{
					CurlyWhirly.dataLoader = new DataLoader();
					CurlyWhirly.dataLoader.loadDataInThread(new File(initialFile));
				}
			}

		});


		addComponentListener(new ComponentAdapter() {
			@Override
			public void componentResized(ComponentEvent e)
			{
				if (getExtendedState() != Frame.MAXIMIZED_BOTH)
				{
					Preferences.guiWinMainWidth  = getSize().width;
					Preferences.guiWinMainHeight = getSize().height;
					Preferences.guiWinMainX = getLocation().x;
					Preferences.guiWinMainY = getLocation().y;

					Preferences.guiWinMainMaximized = false;
				}
				else
					Preferences.guiWinMainMaximized = true;
			}

			public void componentMoved(ComponentEvent e)
			{
				Preferences.guiWinMainX = getLocation().x;
				Preferences.guiWinMainY = getLocation().y;
			}

		});

		frameListener = new FrameListener(this);
		addWindowFocusListener(frameListener);
		addComponentListener(frameListener);

		setVisible(true);

		//start a thread that fades in a label on the canvas prompting the user to open a file
//		CanvasLabelFadeInThread t = new CanvasLabelFadeInThread(canvas3D);
//		t.start();
	}

	// -------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------

	void shutdown()
	{
		prefs.savePreferences(prefsFile, Preferences.class);
		System.exit(0);
	}



	// -------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------

	private void setupComponents()
	{
		//workaround for the 3D drawing problem with Swing menus
		JPopupMenu.setDefaultLightWeightPopupEnabled( false );
		ToolTipManager.sharedInstance().setLightWeightPopupEnabled(false);
		ToolTipManager.sharedInstance().setInitialDelay(0);

		// side panel
		controlPanel = new MTControlPanel(this);
		controlPanel.setPreferredSize(new Dimension(controlPanelWidth, Preferences.guiWinMainHeight));

		// instantiate the canvas here rather than in the data load method
		// we want to be able to recycle it when we load another dataset over the top of the current one
		canvas3D = new MainCanvas(this);
		canvas3D.setPreferredSize(new Dimension((Preferences.guiWinMainWidth-controlPanelWidth), Preferences.guiWinMainHeight));

		// main comp is split pane with control panel on the left and canvas on the right
		JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, controlPanel, canvas3D);
		splitPane.setOneTouchExpandable(true);
		this.getContentPane().add(splitPane);

		//in the absence of data this creates an empty scene graph and we then paint a label onto the canvas that prompts the user to open a file
		canvas3D.createSceneGraph(false);

		// menu bar
		menuBar = new MenuBar(this);
		this.setJMenuBar(menuBar);

		// status bar
		statusBar = new StatusBar();
		getContentPane().add(statusBar, java.awt.BorderLayout.SOUTH);

		//drag and drop support
		FileDropAdapter dropAdapter = new FileDropAdapter(this);
		setDropTarget(new DropTarget(this, dropAdapter));

	}

	private static File getPrefsFile()
	{
		// Ensure the .scri-bioinf folder exists
		File fldr = new File(System.getProperty("user.home"), ".scri-bioinf");
		fldr.mkdirs();

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

	// -------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------

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
	}

	/** "About CurlyWhirly" on the OS X system menu. */
	public void osxAbout()
	{
		new AboutDialog(this, true);
	}

	/** "Quit CurlyWhirly" on the OS X system menu. */
	public boolean osxShutdown()
	{
		shutdown();
		return true;
	}


}// end class
