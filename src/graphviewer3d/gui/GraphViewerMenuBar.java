package graphviewer3d.gui;

import graphviewer3d.controller.ScreenCaptureThread;
import graphviewer3d.data.FileLoader;

import java.awt.Desktop;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import javax.swing.JFileChooser;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.KeyStroke;

import scri.commons.gui.TaskDialog;

public class GraphViewerMenuBar extends JMenuBar implements ActionListener
{
	
	JMenuItem openFileItem;
	JMenuItem aboutItem;
	JMenuItem helpItem;
	JMenuItem exampleDataItem;
	JMenuItem exitItem;
	JMenuItem saveItem;
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
		
		// //////////////////////////////////////
		// the File Menu
		JMenu fileMenu = new JMenu("File");
		fileMenu.setMnemonic(KeyEvent.VK_F);
		this.add(fileMenu);
		
		// the Open File item
		openFileItem = new JMenuItem("Open...");
		openFileItem.addActionListener(this);
		openFileItem.setMnemonic(KeyEvent.VK_O);
		openFileItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, ActionEvent.CTRL_MASK));
		fileMenu.add(openFileItem);
		
		// // the example data import item
		exampleDataItem = new JMenuItem("Load example data");
		exampleDataItem.setMnemonic(KeyEvent.VK_E);
		exampleDataItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_E, ActionEvent.CTRL_MASK));
		exampleDataItem.addActionListener(this);
		fileMenu.add(exampleDataItem);
		
		// separator
		fileMenu.addSeparator();
		
		// the save view item
		saveItem = new JMenuItem("Capture view screenshot");
		saveItem.setMnemonic(KeyEvent.VK_S);
		saveItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, ActionEvent.CTRL_MASK));
		saveItem.addActionListener(this);
		fileMenu.add(saveItem);
				
		// separator
		fileMenu.addSeparator();
		
		// the Exit item
		exitItem = new JMenuItem("Exit");
		exitItem.setMnemonic(KeyEvent.VK_X);
		exitItem.addActionListener(this);
		fileMenu.add(exitItem);
		
		// ////////////////////////////////////////////////////////////////////////////////
		// the Help Menu
		JMenu helpMenu = new JMenu("Help");
		helpMenu.setMnemonic(KeyEvent.VK_H);
		this.add(helpMenu);
		
		// the help item
		helpItem = new JMenuItem("Online Help");
		helpItem.addActionListener(this);
		helpItem.setMnemonic(KeyEvent.VK_O);
		helpItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F1, 0));
		helpMenu.add(helpItem);
		
		// separator
		helpMenu.addSeparator();
		
		// the about item
		aboutItem = new JMenuItem("About CurlyWhirly");
		aboutItem.setMnemonic(KeyEvent.VK_A);
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
				frame.controller.loadDataInThread(fc.getSelectedFile());
			}
		}
		
		if (src.equals(exampleDataItem))
		{
			// load the example dataset provided with the application
			frame.controller.loadDataInThread(new File("randomData.txt"));
		}
		
		if (src.equals(saveItem))
		{						
			//save the canvas to this file
			new ScreenCaptureThread(new File(System.getProperty("user.dir")+System.getProperty("file.separator") + 
							"curlywhirly_screenshot.png"),frame,"png",fc).start();	
		}
		
		
		if (src.equals(exitItem))
		{
			frame.shutdown();
		}
		
		
		if (src.equals(helpItem))
		{
			Desktop desktop = Desktop.getDesktop();
			try
			{
				URI uri = new URI("http://bioinf.scri.sari.ac.uk/curlywhirly/manual.html");
				desktop.browse(uri);
			}
			catch (URISyntaxException e1)
			{
				e1.printStackTrace();
			}
			catch (IOException e1)
			{
				e1.printStackTrace();
			}
		}
		
		if (src.equals(aboutItem))
		{
			String message = "CurlyWhirly version 0.1 �  Scottish Crop Research Institute 2008. Developed by " + "Micha Bayer with contributions from Iain Milne.";
			TaskDialog.initialize(frame, "CurlyWhirly");
			TaskDialog.info(message, "Close");
		}
		
	}
	
//-------------------------------------------------------------------------------------------------------------------------------------
	
}
