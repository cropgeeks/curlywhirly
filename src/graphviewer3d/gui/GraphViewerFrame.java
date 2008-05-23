package graphviewer3d.gui;

import graphviewer3d.data.DataLoader;
import graphviewer3d.data.DataSet;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.UIManager;

public class GraphViewerFrame extends JFrame
{
	
	// ===================================================vars =================================================
	
	public static DataSet dataSet;
	public GraphViewer3DCanvas canvas3D;
	JPanel canvasPanel;
	public int controlPanelWidth = 200;
	
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
			frame.setLocationRelativeTo(null);
			//frame.setExtendedState(frame.MAXIMIZED_BOTH);
			
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
		String filePath = "pco_data.txt";
		DataLoader loader = new DataLoader();
		dataSet = loader.getDataFromFile(filePath);
	}
	
	// -------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
	
	private void setupComponents()
	{
		loadData();
		
		canvas3D = new GraphViewer3DCanvas(dataSet);
		
		// make a new panel and add the 3D panel to it	
		canvasPanel = new JPanel(new BorderLayout());
		canvasPanel.add(canvas3D,BorderLayout.CENTER);
		canvasPanel.setPreferredSize(new Dimension(600, 600));
		
		// side panel
		MTControlPanel controlPanel = new MTControlPanel(this);
		
		// Create a split pane with the two components in it
		JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, controlPanel, canvasPanel);
		splitPane.setOneTouchExpandable(true);
		splitPane.setResizeWeight(0.0);
		splitPane.setDividerLocation(controlPanelWidth);

		//controlPanel.setPreferredSize(new Dimension(controlPanelWidth, 600));
		this.getContentPane().add(splitPane);
		
		// menu bar
		this.setJMenuBar(new GraphViewerMenuBar(this));
		
	}
	
	// -------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
	
}// end class
