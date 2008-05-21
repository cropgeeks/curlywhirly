package graphviewer3d.gui;

import graphviewer3d.data.DataLoader;
import graphviewer3d.data.DataSet;

import java.awt.*;
import java.io.*;
import java.net.*;

import javax.swing.*;

public class GraphViewerFrame extends JFrame
{
	
	// ===================================================vars =================================================
	
	public static DataSet dataSet;
	public GraphViewer3DCanvas canvas3D;
	
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
			frame.setTitle("3D Graph Viewer");
			frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			frame.pack();
			//frame.setLocationRelativeTo(null);
			frame.setExtendedState(frame.MAXIMIZED_BOTH);
			
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	
	// -------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------	
	
	private static void loadData()
	{
		// TODO : remove hard coding of file path
		String filePath = "E:\\SVNSandbox\\graphViewer\\pco_data.txt";
		DataLoader loader = new DataLoader();
		dataSet = loader.getDataFromFile(filePath);
	}
	
	// -------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
	
	private void setupComponents()
	{
		loadData();
		
		// make a tabbed pane and add the 3D panel to it
		JTabbedPane tabbedPane = new JTabbedPane();
		tabbedPane.setTabPlacement(JTabbedPane.BOTTOM);
		canvas3D = new GraphViewer3DCanvas(dataSet);
		tabbedPane.addTab("3D", canvas3D);
		
		// side panel
		ControlPanel controlPanel = new ControlPanel(this);
		
		// Create a split pane with the two components in it
		JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, controlPanel, tabbedPane);
		splitPane.setOneTouchExpandable(true);
		splitPane.setResizeWeight(0.0);
		tabbedPane.setPreferredSize(new Dimension(600, 600));
		controlPanel.setPreferredSize(new Dimension(170, 600));
		this.getContentPane().add(splitPane);
		
	}
	
	// -------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
	
}// end class
