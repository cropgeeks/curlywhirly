package graphviewer3d.gui;

import java.awt.Desktop;
import java.awt.event.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import javax.swing.*;

import scri.commons.gui.TaskDialog;

public class GraphViewerMenuBar extends JMenuBar implements ActionListener
{
	
	JMenuItem openFileItem;
	JMenuItem aboutItem;
	JMenuItem helpItem;
	JMenuItem importDataItem;
	JMenuItem exitItem;
	
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
		fc = new JFileChooser(System.getProperty("user.dir") + System.getProperty("file.separator") + "data");
		
		// this enables swing components to be drawn on top of the 3D canvas
		JPopupMenu.setDefaultLightWeightPopupEnabled(false);
		
		// the File Menu
		JMenu fileMenu = new JMenu("File");
		this.add(fileMenu);
		// the Open File item
		openFileItem = new JMenuItem("Open...");
		openFileItem.addActionListener(this);
		fileMenu.add(openFileItem);
		// // the data import item
		// importDataItem = new JMenuItem("Import data...");
		// importDataItem.addActionListener(this);
		// fileMenu.add(importDataItem);
		// the Exit item
		exitItem = new JMenuItem("Exit");
		exitItem.addActionListener(this);
		fileMenu.add(exitItem);
		
		// the Help Menu
		JMenu helpMenu = new JMenu("Help");
		this.add(helpMenu);
		// the help item
		helpItem = new JMenuItem("General Help");
		helpItem.addActionListener(this);
		helpMenu.add(helpItem);
		// the about item
		aboutItem = new JMenuItem("About CurlyWhirly");
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
				if (frame.canvas3D != null)
					frame.canvas3D.clearCurrentView();
				new Thread(new FileLoader()).start();
				dataLoadingDialog = new DataLoadingDialog(frame, true);
				dataLoadingDialog.setLocationRelativeTo(frame);
				dataLoadingDialog.setVisible(true);
				dataLoadingDialog.setModal(false);
			}
		}
		
		if (src.equals(exitItem))
		{
			frame.shutdown();
		}
		
		if (src.equals(helpItem))
		{			
//			Desktop desktop = null;
//			// Before more Desktop API is used, first check
//			// whether the API is supported by this particular
//			// virtual machine (VM) on this particular host.
//			if (Desktop.isDesktopSupported())
//			{
//				desktop = Desktop.getDesktop();
//				
//				try
//				{
//					URI uri = new URI("http://gruffalo.scri.sari.ac.uk/curlywhirly");
//					
//					String message = "Please refer to the up to date online help system available at " + uri;
//					TaskDialog.initialize(frame, "CurlyWhirly");
//					TaskDialog.info(message, "Close");
//					
//					//desktop.browse(uri);
//				}
//				catch (URISyntaxException e1)
//				{
//					e1.printStackTrace();
//				}
////				catch (IOException e2)
////				{
////					e2.printStackTrace();
////				}
//			}			
		}
		
		if (src.equals(aboutItem))
		{
			String message = "CurlyWhirly version 0.1 ©  Scottish Crop Research Institute 2008. Developed by " + "Micha Bayer with contributions from Iain Milne.";
			TaskDialog.initialize(frame, "CurlyWhirly");
			TaskDialog.info(message, "Close");
		}
		
	}
	
	class FileLoader implements Runnable
	{
		public void run()
		{
			frame.loadData(fc.getSelectedFile());
			dataLoadingDialog.setVisible(false);
			if (Preferences.show3DControlInstructions)
			{
				Instructions3D instr = new Instructions3D(frame);
				instr.show3DInstructions();
			}
		}
	}
	
}
