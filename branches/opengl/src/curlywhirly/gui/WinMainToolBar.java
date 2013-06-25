package curlywhirly.gui;

import java.awt.*;
import java.io.*;
import javax.swing.*;
import javax.swing.filechooser.*;

import curlywhirly.controller.*;
import curlywhirly.data.*;

import scri.commons.gui.*;
import scri.commons.file.FileUtils;

class WinMainToolBar extends JToolBar
{
	private CurlyWhirly frame;
	private DataLoadingDialog dataLoadingDialog;

	private JButton open;
	private JButton sample;
	private JButton reset;
	private JToggleButton spin;
	private JButton screenshot;
	private JButton movie;
	private JButton prefs;
	private JButton about;

	static JSlider slider;

	private File imageFile;

	WinMainToolBar(CurlyWhirly frame)
	{
		this.frame = frame;

		setFloatable(false);
//		setBorderPainted(false);

		slider = new JSlider(0, 100, 50) {
			public Dimension getMaximumSize() {
				return new Dimension(175, getPreferredSize().height);
			}
		};
		slider.setToolTipText(RB.getString("gui.WinMainToolBar.sliderTT"));

		new Actions(frame);

		open = (JButton) getButton(false,
			RB.getString("gui.WinMainToolBar.open"),
			RB.getString("gui.WinMainToolBar.openTT"),
			Icons.getIcon("OPEN"), Actions.fileOpen);

		sample = (JButton) getButton(false, null,
			RB.getString("gui.WinMainToolBar.sampleTT"),
			Icons.getIcon("SAMPLE"), Actions.fileSample);

		reset = (JButton) getButton(false,
			RB.getString("gui.WinMainToolBar.reset"),
			RB.getString("gui.WinMainToolBar.resetTT"),
			Icons.getIcon("RESET"), Actions.reset);

		spin = (JToggleButton) getButton(true,
			RB.getString("gui.WinMainToolBar.spin"),
			RB.getString("gui.WinMainToolBar.spinTT"),
			Icons.getIcon("SPIN"), Actions.spin);

		screenshot = (JButton) getButton(false,
			RB.getString("gui.WinMainToolBar.screenshot"),
			RB.getString("gui.WinMainToolBar.screenshotTT"),
			Icons.getIcon("SCREENSHOT"), Actions.screenshot);

		movie = (JButton) getButton(false,
			RB.getString("gui.WinMainToolBar.movie"),
			RB.getString("gui.WinMainToolBar.movieTT"),
			Icons.getIcon("MOVIE"), Actions.captureMovie);

		prefs = (JButton) getButton(false,
			RB.getString("gui.WinMainToolBar.prefs"),
			RB.getString("gui.WinMainToolBar.prefsTT"),
			Icons.getIcon("PREFS"), Actions.showPrefs);

		about = (JButton) getButton(false, null,
			RB.getString("gui.WinMainToolBar.aboutTT"),
			Icons.getIcon("HELP"), Actions.showAbout);


		if (SystemUtils.isMacOS() == false)
			add(new JLabel(" "));

		add(open);
		add(sample);
		addSeparator();
		add(reset);
		add(spin);
		add(slider);
		addSeparator();
		add(screenshot);
		add(movie);
		addSeparator();
		add(prefs);
		addSeparator();
		add(about);

		add(new JLabel(" "));
	}

	void open()
	{
		// file chooser
		JFileChooser fc = new JFileChooser(Prefs.guiCurrentDir);

		int returnVal = fc.showOpenDialog(frame);
		if (returnVal == JFileChooser.APPROVE_OPTION)
		{
			frame.getOpenGLPanel().stopAnimator();
			openFile(fc.getSelectedFile());
		}
	}

	void openFile(File file)
	{
		if (file == null)
			open();
		else
		{
			Prefs.guiCurrentDir = "" + file.getParent();

			CurlyWhirly.dataLoader = new DataLoader();
			CurlyWhirly.dataLoader.loadDataInThread(file);
		}
	}

	void openSample()
	{
		File dir = SystemUtils.getTempUserDirectory("scri-curlywhirly");
		File sample = new File(dir, "sample.txt");

		frame.getOpenGLPanel().stopAnimator();

		try
		{
			FileUtils.writeFile(sample, getClass().getResourceAsStream("/data/randomData.txt"));
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return;
		}

		// load the example dataset provided with the application
		CurlyWhirly.dataLoader = new DataLoader();
		CurlyWhirly.dataLoader.loadDataInThread(sample);
	}

	void reset()
	{
		frame.getOpenGLPanel().reset();
	}

	void spin()
	{
		// Start or stop the spinning
		if (spin.isSelected())
			frame.getOpenGLPanel().toggleSpin();
		else
			frame.getOpenGLPanel().toggleSpin();

		// And enabled/disable the speed slider based on the state
		slider.setEnabled(spin.isSelected());
		frame.getOpenGLPanel().setSpeed(slider.getValue());
	}

	void screenshot()
	{
		if (promptForFilename())
		{
//			ImageExporter exporter = new ImageExporter(imageFile, frame);
//
//			ProgressDialog dialog = new ProgressDialog(exporter,
//				RB.format("gui.WinMainToolBar.screenshot.title"),
//				RB.format("gui.WinMainToolBar.screenshot.label"),
//				frame);
//
//			// If the operation failed or was cancelled...
//			if (dialog.getResult() != ProgressDialog.JOB_COMPLETED)
//			{
//				if (dialog.getResult() == ProgressDialog.JOB_FAILED)
//				{
//					dialog.getException().printStackTrace();
//					TaskDialog.error(
//						RB.format("gui.WinMainToolBar.screenshot.exception",
//						dialog.getException().getMessage()),
//						RB.getString("gui.text.close"));
//				}
//
//				return;
//			}
//
//			TaskDialog.showFileOpen(
//				RB.format("gui.WinMainToolBar.screenshot.success", imageFile),
//				TaskDialog.INF, imageFile);
		}
	}

	void captureMovie()
	{
		new MovieCaptureDialog(frame);
	}

	void showPrefs()
	{
		PreferencesDialog dialog = new PreferencesDialog(frame);
	}

	void showAbout()
	{
		new AboutDialog(frame, true);
	}

	// Utility method to help create the buttons. Sets their text, tooltip, and
	// icon, as well as adding actionListener, defining margings, etc.
//	AbstractButton getButton(boolean toggle, String title, String tt, ImageIcon icon)
//	{
//		AbstractButton button = null;
//
//		if (toggle)
//			button = new JToggleButton();
//		else
//			button = new JButton();
//
//		button.setText(title != null ? title : "");
//		button.setToolTipText(tt);
//		button.setIcon(icon);
//		button.setFocusPainted(false);
//		button.setFocusable(false);
//		button.setMargin(new Insets(2, 1, 2, 1));
//		button.addActionListener(this);
//
//		if (SystemUtils.isMacOS())
//		{
//			button.putClientProperty("JButton.buttonType", "bevel");
//			button.setMargin(new Insets(-2, -1, -2, -1));
//		}
//
//		return button;
//	}


	// Utility method to help create the buttons. Sets their text, tooltip, and
	// icon, as well as adding actionListener, defining margings, etc.
	public static AbstractButton getButton(boolean toggle, String title,
			String tt, ImageIcon icon, Action a)
	{
		AbstractButton button = null;

		if (toggle)
			button = new JToggleButton(a);
		else
			button = new JButton(a);

		button.setText(title != null ? title : "");
		button.setToolTipText(tt);
		button.setIcon(icon);
		button.setFocusPainted(false);
		button.setFocusable(false);
		button.setMargin(new Insets(2, 1, 2, 1));

		if (SystemUtils.isMacOS())
		{
			button.putClientProperty("JButton.buttonType", "bevel");
			button.setMargin(new Insets(-2, -1, -2, -1));
		}

		return button;
	}

	private boolean promptForFilename()
	{
		File basename = new File(Prefs.guiCurrentDir, "Image.png");

		FileNameExtensionFilter filter = new FileNameExtensionFilter(
			RB.getString("other.Filters.png"), "png");

		String filename = CWUtils.getSaveFilename(
			RB.getString("gui.WinMainToolBar.saveDialog"), basename, filter);

		if (filename != null)
			imageFile = new File(filename);

		return imageFile != null;
	}
}