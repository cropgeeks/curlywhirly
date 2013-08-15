package curlywhirly.gui;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import javax.swing.*;

import scri.commons.gui.*;

import curlywhirly.data.*;

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
//	DataLoadingDialog dataLoadingDialog;
	public URLEntryForm urlEntryForm;

	public MenuBar(CurlyWhirly frame)
	{
		this.frame = frame;
		urlEntryForm = new URLEntryForm(frame, true);
		init();
	}

	private void init()
	{
		// this enables swing components to be drawn on top of the 3D canvas
		JPopupMenu.setDefaultLightWeightPopupEnabled(false);

		// the File Menu
		JMenu fileMenu = new JMenu("");
		RB.setText(fileMenu, "gui.MenuBar.file");
		this.add(fileMenu);

		// the Open File item
		openFileItem = new JMenuItem("");
		RB.setText(openFileItem, "gui.MenuBar.fileOpenFile");
		openFileItem.addActionListener(this);
		openFileItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, menuShortcut));
		fileMenu.add(openFileItem);

		// the example data import item
		exampleDataItem = new JMenuItem("");
		RB.setText(exampleDataItem, "gui.MenuBar.fileExampleData");
		exampleDataItem.addActionListener(this);
		fileMenu.add(exampleDataItem);

		//the item for configuring data URLs
		dataURLItem = new JMenuItem("");
		RB.setText(dataURLItem, "gui.MenuBar.fileDataURL");
		dataURLItem.addActionListener(this);
		fileMenu.add(dataURLItem);

		// separator
		fileMenu.addSeparator();

		// the save view item
		saveItem = new JMenuItem("");
		RB.setText(saveItem, "gui.MenuBar.fileSave");
		saveItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, menuShortcut));
		saveItem.addActionListener(this);
		fileMenu.add(saveItem);

		// the movie item
		movieItem = new JMenuItem("");
		RB.setText(movieItem, "gui.MenuBar.fileMovie");
		movieItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_M, menuShortcut));
		movieItem.addActionListener(this);
		fileMenu.add(movieItem);

		// separator
		fileMenu.addSeparator();

		// the Exit item
		exitItem = new JMenuItem("");
		RB.setText(exitItem, "gui.MenuBar.fileExit");
		exitItem.addActionListener(this);
		// We don't add this option to OS X as it is auto-added by Apple
		if (SystemUtils.isMacOS() == false)
			fileMenu.add(exitItem);

		// the Help Menu
		JMenu helpMenu = new JMenu("");
		RB.setText(helpMenu, "gui.MenuBar.help");
		this.add(helpMenu);

		// the help item
		helpItem = new JMenuItem("");
		RB.setText(helpItem, "gui.MenuBar.helpHelp");
		helpItem.addActionListener(this);
		helpItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F1, 0));
		helpMenu.add(helpItem);

		helpCtrlItem = new JMenuItem("Controls summary");
		RB.setText(helpCtrlItem, "gui.MenuBar.helpCtrl");
		helpCtrlItem.addActionListener(this);
		helpMenu.add(helpCtrlItem);

		// separator
		helpMenu.addSeparator();

		// the about item
		aboutItem = new JMenuItem("");
		RB.setText(aboutItem, "gui.MenuBar.helpAbout");
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
			fc = new JFileChooser(Prefs.guiCurrentDir);

			int returnVal = fc.showOpenDialog(frame);
			if (returnVal == JFileChooser.APPROVE_OPTION)
			{
				Prefs.guiCurrentDir = "" + fc.getSelectedFile().getParent();

				CurlyWhirly.dataLoader = new DataLoader();
				CurlyWhirly.dataLoader.loadDataInThread(fc.getSelectedFile());
			}
		}

		else if (src.equals(exampleDataItem))
		{
			// load the example dataset provided with the application
			CurlyWhirly.dataLoader = new DataLoader();
			CurlyWhirly.dataLoader.loadDataInThread(new File("data/randomData.txt"));
		}

		else if (src.equals(saveItem))
		{
			//save the canvas to this file
//			new ImageExporter(new File(System.getProperty("user.dir")+System.getProperty("file.separator") +
//							"curlywhirly_screenshot.png"),frame,"png",fc).start();
		}

		else if (src.equals(dataURLItem))
		{
			urlEntryForm.setLocationRelativeTo(frame);
			urlEntryForm.getDataURLTextField().requestFocusInWindow();
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
}