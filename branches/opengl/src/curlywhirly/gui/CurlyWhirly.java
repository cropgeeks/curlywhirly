package curlywhirly.gui;

import curlywhirly.gui.viewer.CanvasController;
import java.awt.*;
import java.awt.dnd.*;
import java.awt.event.*;
import java.io.*;
import javax.swing.*;

import apple.dts.samplecode.osxadapter.*;

import scri.commons.file.*;
import scri.commons.gui.*;

import curlywhirly.controller.*;
import curlywhirly.data.*;
import curlywhirly.opengl.*;

public class CurlyWhirly extends JFrame
{
	public static DataSet dataSet;
//	public static MainCanvas canvas3D;
	public static OpenGLPanel canvas3D;
	public static StartPanel startPanel;
	public JSplitPane splitPane;

	public int controlPanelWidth = 200;

	public static MTControlPanel controlPanel;
	static JCheckBox instructionsCheckBox;
	public static boolean dataLoaded = false;

	private static File prefsFile = getPrefsFile();
	public static Prefs prefs = new Prefs();
	public StatusBar statusBar;
	public static MenuBar menuBar;
	public static WinMainToolBar toolbar;

	private CanvasController controller;

//	public static MovieCaptureThread currentMovieCaptureThread = null;

	public FrameListener frameListener = null;

	public static DataLoader dataLoader =null;

	public static String dataAnnotationURL = null;

	// Optional path to a file to be loaded when app opens
	public static String initialFile = null;
	public static boolean dragAndDropDataLoad = false;

	public static CurlyWhirly curlyWhirly = null;

	public static final String titleString = "CurlyWhirly - " + Install4j.VERSION;


	public static void main(String[] args)
	{
		// OS X: This has to be set before anything else
		System.setProperty("com.apple.mrj.application.apple.menu.about.name", "CurlyWhirly");

		// Some handy debug output...
		System.out.println("CurlyWhirly " + Install4j.getVersion() + " on "
			+ System.getProperty("os.name")	+ " (" + System.getProperty("os.arch") + ")");
		System.out.println("Using " + prefsFile);
//		java.util.Map vuMap = javax.media.j3d.VirtualUniverse.getProperties();
		System.out.println("Runtime Java Version = " + System.getProperty("java.version"));
//		System.out.println("Java 3D version = " + vuMap.get("j3d.version"));
//		System.out.println("Renderer = " + vuMap.get("j3d.renderer") + "\n");

		try
		{
			// preferences
			prefs.loadPreferences(prefsFile, Prefs.class);

			// Set System L&F
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

		Icons.initialize("/res/icons", ".png");
		RB.initialize(Prefs.localeText, "res.text.curlywhirly");

		curlyWhirly = new CurlyWhirly();
		dataLoader = new DataLoader();
	}

	CurlyWhirly()
	{
		//this initializes all the task dialog instances
		TaskDialog.initialize(this, RB.getString("gui.CurlyWhirly.title"));

		if (SystemUtils.isMacOS())
			handleOSXStupidities();

		// And use Nimbus for all non-Apple systems
		else
		{
			try {

			Nimbus.customizeNimbus();
			}
			catch (Exception e) {}
//			winKey = RB.getString("gui.text.ctrl");
		}

		Install4j.doStartUpCheck();

		setupComponents();
		pack();

		// get the GUI set up
		setTitle(RB.getString("gui.CurlyWhirly.title") + " - " + Install4j.VERSION);
		setSize(Prefs.guiWinMainWidth, Prefs.guiWinMainHeight);

		// Determine where on screen to display
		if (Prefs.isFirstRun)
			setLocationRelativeTo(null);
		else
			setLocation(Prefs.guiWinMainX, Prefs.guiWinMainY);

		// Maximize the frame if neccassary
		if (Prefs.guiWinMainMaximized)
			setExtendedState(Frame.MAXIMIZED_BOTH);

		setIconImage(Icons.getIcon("curlywurly_icon32px").getImage());


		setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);

		addWindowListener(new WindowAdapter()
		{
			@Override
			public void windowClosing(WindowEvent e)
			{
				Prefs.isFirstRun = false;
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
					Prefs.guiWinMainWidth  = getSize().width;
					Prefs.guiWinMainHeight = getSize().height;
					Prefs.guiWinMainX = getLocation().x;
					Prefs.guiWinMainY = getLocation().y;

					Prefs.guiWinMainMaximized = false;
				}
				else
					Prefs.guiWinMainMaximized = true;
			}

			public void componentMoved(ComponentEvent e)
			{
				Prefs.guiWinMainX = getLocation().x;
				Prefs.guiWinMainY = getLocation().y;
			}

		});

		initialise();

		frameListener = new FrameListener(this);
		addWindowFocusListener(frameListener);
		addComponentListener(frameListener);

		setVisible(true);

		//start a thread that fades in a label on the canvas prompting the user to open a file
//		CanvasLabelFadeInThread t = new CanvasLabelFadeInThread(canvas3D);
//		t.start();
	}

	private void initialise()
	{
		controller = new CanvasController(this);
	}

	void shutdown()
	{
		prefs.savePreferences(prefsFile, Prefs.class);
		System.exit(0);
	}

	private void setupComponents()
	{
		//workaround for the 3D drawing problem with Swing menus
		JPopupMenu.setDefaultLightWeightPopupEnabled( false );
		ToolTipManager.sharedInstance().setLightWeightPopupEnabled(false);
		ToolTipManager.sharedInstance().setInitialDelay(0);

		// toolbar
		toolbar = new WinMainToolBar(this);
		add(toolbar, BorderLayout.NORTH);

		// control panel
		controlPanel = new MTControlPanel(this);
		controlPanel.setPreferredSize(new Dimension(controlPanelWidth, Prefs.guiWinMainHeight));

		// instantiate the canvas here rather than in the data load method
		// we want to be able to recycle it when we load another dataset over the top of the current one
//		canvas3D = new MainCanvas(this);
		canvas3D = new OpenGLPanel(this);
		canvas3D.setPreferredSize(new Dimension((Prefs.guiWinMainWidth-controlPanelWidth), Prefs.guiWinMainHeight));

		startPanel = new StartPanel();

		// main comp is split pane with control panel on the left and canvas on the right
		splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, controlPanel, startPanel);
		splitPane.setOneTouchExpandable(true);
		add(splitPane);

		//in the absence of data this creates an empty scene graph and we then paint a label onto the canvas that prompts the user to open a file
//		canvas3D.createSceneGraph(false);

		// status bar
		statusBar = new StatusBar();
		add(statusBar, java.awt.BorderLayout.SOUTH);

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

	public CanvasController getCanvasController()
		{ return controller; }

	public DataSet getDataSet()
		{ return dataSet; }

	public OpenGLPanel getOpenGLPanel()
		{ return canvas3D; }
}