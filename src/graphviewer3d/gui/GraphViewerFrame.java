package graphviewer3d.gui;

import graphviewer3d.data.DataLoader;
import graphviewer3d.data.DataSet;

import java.awt.*;

import java.awt.Dimension;
import java.io.File;

import javax.swing.*;

import scri.commons.gui.TaskDialog;

public class GraphViewerFrame extends JFrame
{
	
	// ===================================================vars =================================================
	
	public static DataSet dataSet;
	public GraphViewer3DCanvas canvas3D;
	public JPanel canvasPanel;
	public int controlPanelWidth = 200;
	public MTControlPanel controlPanel;
	JLabel openLabel;
	static JCheckBox instructionsCheckBox;
	
	private static File prefsFile = new File(System.getProperty("user.home"), ".curlywhirly.xml");
	public static Preferences prefs = new Preferences();
	
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
		System.out.println("Frame.loadData()");
		DataLoader loader = new DataLoader();
		dataSet = loader.getDataFromFile(file);
		canvas3D.dataSet = dataSet;
		canvas3D.createSceneGraph();
		canvasPanel.remove(openLabel);
		canvasPanel.add(canvas3D,BorderLayout.CENTER);
		controlPanel.setUpListData();
		controlPanel.doAdditionalComponentConfig();
		repaint();
		System.out.println("current dataSet = " + dataSet.dataSetName);
	}
	
	// -------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
	
	private void setupComponents()
	{
		// make a new panel for the 3D panel 
		canvasPanel = new JPanel(new BorderLayout());
		openLabel = new JLabel("Open a data file to begin.",JLabel.CENTER);
		canvasPanel.setPreferredSize(new Dimension(600, 600));
		canvas3D = new GraphViewer3DCanvas();
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
		
//		File file = new File("data/barley_PCOs.txt");
//		loadData(file);
				
	}
	
	// -------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
	
}// end class
