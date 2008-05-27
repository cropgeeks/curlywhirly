package graphviewer3d.gui;

import java.awt.event.*;
import javax.swing.*;

public class GraphViewerMenuBar extends JMenuBar implements ActionListener
{
	
	JMenuItem openFileItem;
	JMenuItem aboutItem;
	JMenuItem helpItem;
	JMenuItem importDataItem;
	GraphViewerFrame frame;
	JFileChooser fc;
	DataLoadingDialog dataLoadingDialog;
	
	public GraphViewerMenuBar(GraphViewerFrame frame)
	{
		this.frame = frame;
		init();
	}
	
	private void init()
	{
		// file chooser
		fc = new JFileChooser(System.getProperty("user.dir"));
		
		// this enables swing components to be drawn on top of the 3D canvas
		JPopupMenu.setDefaultLightWeightPopupEnabled(false);
		
		// the File Menu
		JMenu fileMenu = new JMenu("File");
		this.add(fileMenu);
		// the Open File item
		openFileItem = new JMenuItem("Open...");
		openFileItem.addActionListener(this);
		fileMenu.add(openFileItem);
		// the data import item
		importDataItem = new JMenuItem("Import data...");
		importDataItem.addActionListener(this);
		fileMenu.add(importDataItem);
		
		// the Help Menu
		JMenu helpMenu = new JMenu("Help");
		this.add(helpMenu);
		// the help item
		helpItem = new JMenuItem("General Help");
		helpItem.addActionListener(this);
		helpMenu.add(helpItem);
		// the about item
		aboutItem = new JMenuItem("About 3D Graph Viewer");
		aboutItem.addActionListener(this);
		helpMenu.add(aboutItem);
	}
	
	public void actionPerformed(ActionEvent e)
	{
		Object src = e.getSource();
		if (src.equals(openFileItem))
		{
			int returnVal = fc.showOpenDialog(frame);
			if (returnVal == JFileChooser.APPROVE_OPTION)
			{
				System.out.println("file selected = " + fc.getSelectedFile());
				new Thread(new FileLoader()).start();
				dataLoadingDialog = new DataLoadingDialog(frame, true);
				dataLoadingDialog.setLocationRelativeTo(frame);
				dataLoadingDialog.setVisible(true);
				dataLoadingDialog.setModal(false);
			}
		}
	}
	
	class FileLoader implements Runnable
	{
		public void run()
		{
			frame.loadData(fc.getSelectedFile());
			dataLoadingDialog.setVisible(false);
		}
	}
	
}
