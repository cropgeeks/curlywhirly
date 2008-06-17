package graphviewer3d.gui;

import graphviewer3d.controller.FatController;
import graphviewer3d.data.DataLoader;
import graphviewer3d.data.DataSet;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.Toolkit;
import java.io.File;

import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.UIManager;

public class GraphViewerFrame extends JFrame
{
	
	// ===================================================vars =================================================
	
	public FatController controller = new FatController(this); 
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
	
	// ===================================================c'tor=================================================
	
	public GraphViewerFrame()
	{
		setupComponents();
	}
	
	// ============================================methods====================================================
	
	public static void main(String[] args)
	{
		try
		{
			//preferences
			prefs.loadPreferences(prefsFile, Preferences.class);
			
			// Set System L&F
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
			
			// get the GUI set up
			GraphViewerFrame frame = new GraphViewerFrame();
			frame.setVisible(true);
			frame.setTitle("CurlyWhirly");
			Image img = Toolkit.getDefaultToolkit().getImage("curlywurly_icon.gif");
			frame.setIconImage(img);
			frame.pack();
			frame.setLocationRelativeTo(null);
			frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			//frame.setExtendedState(frame.MAXIMIZED_BOTH);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	
	// -------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------		
	
	public void shutdown()
	{
		prefs.savePreferences(prefsFile, Preferences.class);
		System.exit(0);
	}
	
	
	
	// -------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------	
	
	public void loadData(File file)
	{
		DataLoader loader = new DataLoader(this);
		dataSet = loader.getDataFromFile(file);
		canvas3D.dataSet = dataSet;
		canvas3D.createSceneGraph();
		canvasPanel.remove(openLabel);
		canvasPanel.add(canvas3D,BorderLayout.CENTER);
		controlPanel.setUpListData();
		controlPanel.resetComboBoxes();
		dataLoaded = true;
		statusBar.setDefaultText();
		repaint();
	}
	
	// -------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
	
	private void setupComponents()
	{
		// make a new panel for the 3D panel 
		canvasPanel = new JPanel(new BorderLayout());
		openLabel = new JLabel("Open a data file to begin.",JLabel.CENTER);
		canvasPanel.setPreferredSize(new Dimension(600, 600));
		canvas3D = new GraphViewer3DCanvas(this);
		canvasPanel.add(openLabel, BorderLayout.CENTER);
		canvasPanel.setBackground(Color.LIGHT_GRAY);
		openLabel.setForeground(new Color(120,120,120));
		
		// side panel
		controlPanel = new MTControlPanel(this);
		
		//main panel
		JPanel mainPanel = new JPanel(new BorderLayout());
		mainPanel.add(controlPanel,BorderLayout.WEST);
		mainPanel.add(canvasPanel,BorderLayout.CENTER);
		this.getContentPane().add(mainPanel);
		
		// menu bar
		this.setJMenuBar(new GraphViewerMenuBar(this));
		
		//status bar
		statusBar = new StatusBar();
		getContentPane().add(statusBar, java.awt.BorderLayout.SOUTH);
		
		File file = new File("data/barley_PCA.txt");
		loadData(file);	
		
	}
	
	// -------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
	
}// end class
