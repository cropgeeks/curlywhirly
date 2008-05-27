package graphviewer3d.gui;

import graphviewer3d.data.DataLoader;
import graphviewer3d.data.DataSet;

import java.awt.*;

import java.awt.Dimension;
import java.io.File;

import javax.swing.*;

public class GraphViewerFrame extends JFrame
{
	
	// ===================================================vars =================================================
	
	public static DataSet dataSet;
	public GraphViewer3DCanvas canvas3D;
	public JPanel canvasPanel;
	public int controlPanelWidth = 200;
	public MTControlPanel controlPanel;
	
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
			// Set System L&F
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
			
			// get the GUI set up
			GraphViewerFrame frame = new GraphViewerFrame();
			frame.setVisible(true);
			frame.setTitle("CurlyWhirly");
			frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			frame.pack();
			frame.setLocationRelativeTo(null);
			//frame.setExtendedState(frame.MAXIMIZED_BOTH);
			
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	
	// -------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------	
	
	public void loadData(File file)
	{
		DataLoader loader = new DataLoader();
		dataSet = loader.getDataFromFile(file);
		canvas3D = new GraphViewer3DCanvas(dataSet);
		canvasPanel.add(canvas3D,BorderLayout.CENTER);
		controlPanel.setUpListData();
		controlPanel.doAdditionalComponentConfig();
		repaint();
	}
	
	// -------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
	
	private void setupComponents()
	{
		// make a new panel for the 3D panel 
		canvasPanel = new JPanel(new BorderLayout());
		JLabel openLabel = new JLabel("Open a data file to begin.",JLabel.CENTER);
		canvasPanel.setPreferredSize(new Dimension(600, 600));
		canvasPanel.add(openLabel, BorderLayout.CENTER);
		openLabel.setForeground(new Color(120,120,120));
		
		// side panel
		controlPanel = new MTControlPanel(this);
		
		// Create a split pane with the two components in it
		JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, controlPanel, canvasPanel);
		splitPane.setOneTouchExpandable(true);
		splitPane.setResizeWeight(0.0);
		splitPane.setDividerLocation(controlPanelWidth);

		this.getContentPane().add(splitPane);
		
		// menu bar
		this.setJMenuBar(new GraphViewerMenuBar(this));
		
		// TODO : remove hard coding of file path
		File file = new File("barley_PCOs.txt");
		loadData(file);
		
	}
	
	// -------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
	
}// end class
