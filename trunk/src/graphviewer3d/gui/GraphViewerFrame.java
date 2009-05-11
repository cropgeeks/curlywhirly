package graphviewer3d.gui;

import graphviewer3d.controller.*;
import graphviewer3d.data.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import javax.swing.*;

import apple.dts.samplecode.osxadapter.*;

import scri.commons.gui.*;

public class GraphViewerFrame extends JFrame
{

	// ===================================================vars =================================================

	public FatController fatController = new FatController(this);
	public static DataSet dataSet;
	public static GraphViewer3DCanvas canvas3D;
	public static JPanel canvasPanel;
	public int controlPanelWidth = 200;
	public MTControlPanel controlPanel;
	JLabel openLabel;
	static JCheckBox instructionsCheckBox;
	public boolean dataLoaded = false;

	private static File prefsFile = new File(System.getProperty("user.home"), ".curlywhirly.xml");
	public static Preferences prefs = new Preferences();
	public StatusBar statusBar;
	public GraphViewerMenuBar menuBar;
	
	public MovieCaptureThread currentMovieCaptureThread = null;

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

		new GraphViewerFrame();
	}

	GraphViewerFrame()
	{
		//this initializes all the task dialog instances
		TaskDialog.initialize(this, "CurlyWhirly");
		
		if (SystemUtils.isMacOS())
			handleOSXStupidities();

		Install4j.doStartUpCheck();

		setupComponents();

		// get the GUI set up
		setTitle("CurlyWhirly - " + Install4j.VERSION);
		setIconImage(new ImageIcon("res/curlywurly_icon32px.png").getImage());
		pack();
		setLocationRelativeTo(null);
		setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);

		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				shutdown();
			}
		});
		
		FrameListener frameListener = new FrameListener(this);
		addWindowFocusListener(frameListener);
		addComponentListener(frameListener);

		setVisible(true);
	}

	// -------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------

	void shutdown()
	{
		prefs.savePreferences(prefsFile, Preferences.class);
		System.exit(0);
	}

	// -------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------

	public void loadData(File file)
	{
		//load the data from file
		DataLoader loader = new DataLoader(this);
		try
		{
			dataSet = loader.getDataFromFile(file);
		}
		catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
			return;
		}

		// only add the canvas to the panel if we haven't got data loaded already
		if (!dataLoaded)
			canvasPanel.add(canvas3D, BorderLayout.CENTER);

		//set up the new dataset and make a new scene graph
		//normalize first
		//this sets the data up so that each axis is normalized to between -1 and 1 and the data fills the whole range
		DataNormalizer.normalizeDataSet(dataSet);
		canvas3D.dataSet = dataSet;
		canvas3D.createSceneGraph();

		//do the rest of the set up
		controlPanel.setUpListData();
		controlPanel.resetComboBoxes();
		dataLoaded = true;
		statusBar.setDefaultText();
		repaint();
	}

	// -------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------

	private void setupComponents()
	{
		//workaround for the 3D drawing problem with Swing menus
//		JPopupMenu.setDefaultLightWeightPopupEnabled( false );

		// make a new panel for the 3D panel
		canvasPanel = new JPanel(new BorderLayout());
		canvasPanel.setPreferredSize(new Dimension(600, 600));

		// instantiate the canvas here rather than in the data load method
		// we want to be able to recycle it when we load another dataset over the top of the current one
		canvas3D = new GraphViewer3DCanvas(this);

		// //add a label instructing the user to open a file
		openLabel = new JLabel("Open a data file to begin.",JLabel.CENTER);
		openLabel.setFont(new Font("SANS_SERIF", Font.PLAIN, 18));
		canvasPanel.add(openLabel, BorderLayout.CENTER);
		canvasPanel.setBackground(Color.LIGHT_GRAY);
		openLabel.setForeground(new Color(120,120,120));

		// side panel
		controlPanel = new MTControlPanel(this);

		// main panel
		JPanel mainPanel = new JPanel(new BorderLayout());
		mainPanel.add(controlPanel, BorderLayout.WEST);
		mainPanel.add(canvasPanel, BorderLayout.CENTER);
		this.getContentPane().add(mainPanel);

		// menu bar
		menuBar = new GraphViewerMenuBar(this);
		this.setJMenuBar(menuBar);

		// status bar
		statusBar = new StatusBar();
		getContentPane().add(statusBar, java.awt.BorderLayout.SOUTH);

	}

	// -------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------

	// --------------------------------------------------
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
