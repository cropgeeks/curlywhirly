package curlywhirly.gui;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import javax.swing.*;

import curlywhirly.controller.*;
import curlywhirly.data.*;

import scri.commons.gui.*;

class WinMainToolBar extends JToolBar implements ActionListener
{
	private CurlyWhirly frame;
	private DataLoadingDialog dataLoadingDialog;

	private JButton open;
	private JButton sample;
	private JButton screenshot;
	private JButton movie;
	private JButton prefs;
	private JButton about;

	WinMainToolBar(CurlyWhirly frame)
	{
		this.frame = frame;

		setFloatable(false);
//		setBorderPainted(false);

		open = (JButton) getButton(false,
			RB.getString("gui.WinMainToolBar.open"),
			RB.getString("gui.WinMainToolBar.openTT"),
			Icons2.getIcon("OPEN"));

		sample = (JButton) getButton(false, null,
			RB.getString("gui.WinMainToolBar.sampleTT"),
			Icons2.getIcon("SAMPLE"));

		screenshot = (JButton) getButton(false,
			RB.getString("gui.WinMainToolBar.screenshot"),
			RB.getString("gui.WinMainToolBar.screenshotTT"),
			Icons2.getIcon("SCREENSHOT"));

		movie = (JButton) getButton(false,
			RB.getString("gui.WinMainToolBar.movie"),
			RB.getString("gui.WinMainToolBar.movieTT"),
			Icons2.getIcon("MOVIE"));

		prefs = (JButton) getButton(false,
			RB.getString("gui.WinMainToolBar.prefs"),
			RB.getString("gui.WinMainToolBar.prefsTT"),
			Icons2.getIcon("PREFS"));

		about = (JButton) getButton(false, null,
			RB.getString("gui.WinMainToolBar.aboutTT"),
			Icons2.getIcon("HELP"));


		if (SystemUtils.isMacOS() == false)
			add(new JLabel(" "));

		add(open);
		add(sample);
		addSeparator();
		add(screenshot);
		add(movie);
		addSeparator();
		add(prefs);
		addSeparator();
		add(about);

		add(new JLabel(" "));
	}

	public void actionPerformed(ActionEvent e)
	{
		Object src = e.getSource();

		if (src.equals(open))
		{
			// file chooser
			JFileChooser fc = new JFileChooser(Prefs.lastDir);

			int returnVal = fc.showOpenDialog(frame);
			if (returnVal == JFileChooser.APPROVE_OPTION)
			{
				Prefs.lastDir = "" + fc.getSelectedFile().getParent();

				CurlyWhirly.dataLoader = new DataLoader();
				CurlyWhirly.dataLoader.loadDataInThread(fc.getSelectedFile());
			}
		}

		else if (src.equals(sample))
		{
			// load the example dataset provided with the application
			CurlyWhirly.dataLoader = new DataLoader();
			CurlyWhirly.dataLoader.loadDataInThread(new File("data/randomData.txt"));
		}

		else if (src.equals(screenshot))
		{
			//save the canvas to this file
			new ScreenCaptureThread(new File(System.getProperty("user.dir")+System.getProperty("file.separator") +
				"curlywhirly_screenshot.png"),frame,"png",new JFileChooser()).start();
		}

		else if (src.equals(movie))
		{
			MovieCaptureDialog dialog = new MovieCaptureDialog(frame, true);
			dialog.setLocationRelativeTo(frame);
			dialog.updateFileFileSize();
			dialog.getSavedFileTF().setText("");
			dialog.movieFile = null;
			dialog.setVisible(true);
		}

		else if (src.equals(prefs))
		{
			PreferencesDialog dialog = new PreferencesDialog(frame);
		}

		else if (src.equals(about))
		{
			new AboutDialog(frame, true);
		}
	}

	// Utility method to help create the buttons. Sets their text, tooltip, and
	// icon, as well as adding actionListener, defining margings, etc.
	AbstractButton getButton(boolean toggle, String title, String tt, ImageIcon icon)
	{
		AbstractButton button = null;

		if (toggle)
			button = new JToggleButton();
		else
			button = new JButton();

		button.setText(title != null ? title : "");
		button.setToolTipText(tt);
		button.setIcon(icon);
		button.setFocusPainted(false);
		button.setFocusable(false);
		button.setMargin(new Insets(2, 1, 2, 1));
		button.addActionListener(this);

		if (SystemUtils.isMacOS())
		{
			button.putClientProperty("JButton.buttonType", "bevel");
			button.setMargin(new Insets(-2, -1, -2, -1));
		}

		return button;
	}
}