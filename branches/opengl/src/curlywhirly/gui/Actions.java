package curlywhirly.gui;

import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;

import scri.commons.gui.*;

public class Actions
{
	private CurlyWhirly winMain;

	public static AbstractAction fileOpen;
	public static AbstractAction fileSample;
	public static AbstractAction reset;
	public static AbstractAction spin;
	public static AbstractAction screenshot;
	public static AbstractAction captureMovie;
	public static AbstractAction showPrefs;
	public static AbstractAction showAbout;

	Actions(CurlyWhirly winMain)
	{
		this.winMain = winMain;

		createActions();

		// Set initial states for actions that shouldn't be enabled at the start
		openedNoData();
	}

	public static ImageIcon getIcon(String name)
	{
		ImageIcon icon = Icons.getIcon(name);

		if (SystemUtils.isMacOS())
			return null;
		else
			return icon;
	}

	private void createActions()
	{
		fileOpen = new AbstractAction(RB.getString("gui.WinMainToolBar.open"), getIcon("OPEN")) {
			public void actionPerformed(ActionEvent e) {
				winMain.toolbar.open();
			}
		};

		fileSample = new AbstractAction("", getIcon("SAMPLE")) {
			public void actionPerformed(ActionEvent e) {
				winMain.toolbar.openSample();
			}
		};

		reset = new AbstractAction(RB.getString("gui.WinMainToolBar.reset"), getIcon("RESET")) {
			public void actionPerformed(ActionEvent e) {
				winMain.toolbar.reset();
			}
		};

		spin = new AbstractAction(RB.getString("gui.WinMainToolBar.spin"), getIcon("SPIN")) {
			public void actionPerformed(ActionEvent e) {
				winMain.toolbar.spin();
			}
		};

		screenshot = new AbstractAction(RB.getString("gui.WinMainToolBar.screenshot"), getIcon("SCREENSHOT")) {
			public void actionPerformed(ActionEvent e) {
				winMain.toolbar.screenshot();
			}
		};

		captureMovie = new AbstractAction(RB.getString("gui.WinMainToolBar.movie"), getIcon("MOVIE")) {
			public void actionPerformed(ActionEvent e) {
				winMain.toolbar.captureMovie();
			}
		};

		showPrefs = new AbstractAction(RB.getString("gui.WinMainToolBar.prefs"), getIcon("PREFS")) {
			public void actionPerformed(ActionEvent e) {
				winMain.toolbar.showPrefs();
			}
		};

		showAbout = new AbstractAction("", getIcon("HELP")) {
			public void actionPerformed(ActionEvent e) {
				winMain.toolbar.showAbout();
			}
		};

		final JSlider slider = WinMainToolBar.slider;

		slider.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent evt)
			{
				int speed = slider.getValue();
				winMain.getOpenGLPanel().setSpeed(speed);
			}
		});
	}

	public static void openedNoData()
	{
		fileOpen.setEnabled(true);
		fileSample.setEnabled(true);
		reset.setEnabled(false);
		spin.setEnabled(false);
		WinMainToolBar.slider.setEnabled(false);
		screenshot.setEnabled(false);
		captureMovie.setEnabled(false);
		showPrefs.setEnabled(true);
		showAbout.setEnabled(true);
	}

	public static void openedData()
	{
		fileOpen.setEnabled(true);
		fileSample.setEnabled(true);
		reset.setEnabled(true);
		spin.setEnabled(true);
//		WinMainToolBar.slider.setEnabled(true);
		screenshot.setEnabled(true);
		captureMovie.setEnabled(true);
		showPrefs.setEnabled(true);
		showAbout.setEnabled(true);
	}
}