package curlywhirly.gui;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import javax.swing.*;
import curlywhirly.controller.*;
import scri.commons.gui.*;

public class MenuBar extends JMenuBar implements ActionListener
{
	// Returns value for "CTRL" under most OSs, and the "apple" key for OS X
	private int menuShortcut = Toolkit.getDefaultToolkit().getMenuShortcutKeyMask();

	JMenuItem openFileItem;
	JMenuItem aboutItem;
	JMenuItem helpItem;
	JMenuItem helpCtrlItem;
	JMenuItem exampleDataItem;
	JMenuItem exitItem;
	JMenuItem saveItem;
	JMenuItem movieItem;
	JMenuItem dataURLItem;
	CurlyWhirly frame;
	JFileChooser fc;
	DataLoadingDialog dataLoadingDialog;
	public MovieCaptureDialog movieCaptureDialog;
	public URLEntryForm urlEntryForm;

	public MenuBar(CurlyWhirly frame)
	{
		this.frame = frame;
		movieCaptureDialog = new MovieCaptureDialog(frame,true);
		urlEntryForm = new URLEntryForm(frame, true);
		init();
	}

	private void init()
	{
		// this enables swing components to be drawn on top of the 3D canvas
		JPopupMenu.setDefaultLightWeightPopupEnabled(false);

		// //////////////////////////////////////
		// the File Menu
		JMenu fileMenu = new JMenu("File");
		fileMenu.setMnemonic(KeyEvent.VK_F);
		this.add(fileMenu);

		// the Open File item
		openFileItem = new JMenuItem("Open data...");
		openFileItem.addActionListener(this);
		openFileItem.setMnemonic(KeyEvent.VK_O);
		openFileItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, menuShortcut));
		fileMenu.add(openFileItem);

		// the example data import item
		exampleDataItem = new JMenuItem("Load example data");
		exampleDataItem.setMnemonic(KeyEvent.VK_L);
		exampleDataItem.addActionListener(this);
		fileMenu.add(exampleDataItem);
		
		//the item for configuring data URLs
		dataURLItem = new JMenuItem("Annotation URL...");
		dataURLItem.setMnemonic(KeyEvent.VK_A);
		dataURLItem.addActionListener(this);
		fileMenu.add(dataURLItem);

		// separator
		fileMenu.addSeparator();

		// the save view item
		saveItem = new JMenuItem("Capture screenshot");
		saveItem.setMnemonic(KeyEvent.VK_C);
		saveItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, menuShortcut));
		saveItem.addActionListener(this);
		fileMenu.add(saveItem);
		
		// the movie item
		movieItem = new JMenuItem("Capture movie...");
		movieItem.setMnemonic(KeyEvent.VK_M);
		movieItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_M, menuShortcut));
		movieItem.addActionListener(this);
		fileMenu.add(movieItem);

		// separator
		fileMenu.addSeparator();

		// the Exit item
		exitItem = new JMenuItem("Exit");
		exitItem.setMnemonic(KeyEvent.VK_X);
		exitItem.addActionListener(this);
		// We don't add this option to OS X as it is auto-added by Apple
		if (SystemUtils.isMacOS() == false)
			fileMenu.add(exitItem);

		// ////////////////////////////////////////////////////////////////////////////////
		// the Help Menu
		JMenu helpMenu = new JMenu("Help");
		helpMenu.setMnemonic(KeyEvent.VK_H);
		this.add(helpMenu);

		// the help item
		helpItem = new JMenuItem("Online help");
		helpItem.addActionListener(this);
		helpItem.setMnemonic(KeyEvent.VK_O);
		helpItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F1, 0));
		helpMenu.add(helpItem);

		helpCtrlItem = new JMenuItem("Controls summary");
		helpCtrlItem.addActionListener(this);
		helpCtrlItem.setMnemonic(KeyEvent.VK_C);
		helpMenu.add(helpCtrlItem);

		// separator
		helpMenu.addSeparator();

		// the about item
		aboutItem = new JMenuItem("About CurlyWhirly");
		aboutItem.setMnemonic(KeyEvent.VK_A);
		aboutItem.addActionListener(this);
		// We don't add this option to OS X as it is auto-added by Apple
		if (SystemUtils.isMacOS() == false)
			helpMenu.add(aboutItem);
	}

	public void actionPerformed(ActionEvent e)
	{
		Object src = e.getSource();

		if (src.equals(openFileItem))
		{
			// file chooser
			fc = new JFileChooser(Preferences.lastDir);

			int returnVal = fc.showOpenDialog(frame);
			if (returnVal == JFileChooser.APPROVE_OPTION)
			{
				Preferences.lastDir = "" + fc.getSelectedFile().getParent();

				frame.fatController.loadDataInThread(fc.getSelectedFile());
			}
		}

		else if (src.equals(exampleDataItem))
		{
			// load the example dataset provided with the application
			frame.fatController.loadDataInThread(new File("data/randomData.txt"));
		}

		else if (src.equals(saveItem))
		{
			//save the canvas to this file
			new ScreenCaptureThread(new File(System.getProperty("user.dir")+System.getProperty("file.separator") +
							"curlywhirly_screenshot.png"),frame,"png",fc).start();
		}
		
		else if (src.equals(movieItem))
		{
			movieCaptureDialog.setLocationRelativeTo(frame);
			movieCaptureDialog.updateFileFileSize();
			movieCaptureDialog.getSavedFileTF().setText("");
			movieCaptureDialog.movieFile = null;
			movieCaptureDialog.setVisible(true);
		}
		
		else if (src.equals(dataURLItem))
		{
			urlEntryForm.setLocationRelativeTo(frame);
			urlEntryForm.setVisible(true);
		}

		else if (src.equals(exitItem))
		{
			frame.shutdown();
		}


		else if (src.equals(helpItem))
		{
			String url = "http://bioinf.scri.sari.ac.uk/curlywhirly/manual.shtml";

			GUIUtils.visitURL(url);
		}

		else if (src.equals(helpCtrlItem))
		{
			Instructions3D instr = new Instructions3D(frame);
			instr.show3DInstructions(false);
		}

		else if (src.equals(aboutItem))
		{
			new AboutDialog(frame, true);
		}

	}

//-------------------------------------------------------------------------------------------------------------------------------------

}
