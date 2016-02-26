// Copyright 2009-2015 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package jhi.curlywhirly.gui;

import java.awt.event.*;
import javax.swing.*;

import scri.commons.gui.*;

public class Actions
{
	private final WinMain winMain;

	public static AbstractAction fileOpen;
	public static AbstractAction fileSample;
    public static AbstractAction dataExport;
	public static AbstractAction reset;
	public static AbstractAction spin;
	public static AbstractAction screenshot;
	public static AbstractAction captureMovie;
	public static AbstractAction showPrefs;
	public static AbstractAction showAbout;

	Actions(WinMain winMain)
	{
		this.winMain = winMain;

		createActions();

		// Set initial states for actions that shouldn't be enabled at the start
		openedNoData();
	}

	public static ImageIcon getIcon(String name)
	{
		ImageIcon icon = Icons.getIcon(name);

        return SystemUtils.isMacOS() ? null : icon;
	}

	private void createActions()
	{
		fileOpen = new AbstractAction(RB.getString("gui.WinMainToolBar.open"), getIcon("OPEN")) {
            @Override
			public void actionPerformed(ActionEvent e) {
				winMain.getCommands().open();
			}
		};

		fileSample = new AbstractAction("", getIcon("SAMPLE")) {
            @Override
			public void actionPerformed(ActionEvent e) {
				winMain.getCommands().openSample();
			}
		};

        dataExport = new AbstractAction(RB.getString("gui.WinMainToolBar.export"), getIcon("EXPORT")) {
            @Override
            public void actionPerformed(ActionEvent e) {
                winMain.getCommands().exportDataSet();
            }
        };

		reset = new AbstractAction(RB.getString("gui.WinMainToolBar.reset"), getIcon("RESET")) {
            @Override
			public void actionPerformed(ActionEvent e) {
				winMain.getCommands().reset();
			}
		};

		spin = new AbstractAction(RB.getString("gui.WinMainToolBar.spin"), getIcon("SPIN")) {
            @Override
			public void actionPerformed(ActionEvent e) {
				winMain.getCommands().spin();
			}
		};

		screenshot = new AbstractAction(RB.getString("gui.WinMainToolBar.screenshot"), getIcon("SCREENSHOT")) {
            @Override
			public void actionPerformed(ActionEvent e) {
				winMain.getCommands().screenshot();
			}
		};

		captureMovie = new AbstractAction(RB.getString("gui.WinMainToolBar.movie"), getIcon("MOVIE")) {
            @Override
			public void actionPerformed(ActionEvent e) {
				winMain.getCommands().captureMovie();
			}
		};

		showPrefs = new AbstractAction(RB.getString("gui.WinMainToolBar.prefs"), getIcon("PREFS")) {
            @Override
			public void actionPerformed(ActionEvent e) {
				winMain.getCommands().showPrefs();
			}
		};

		showAbout = new AbstractAction("", getIcon("HELP")) {
            @Override
			public void actionPerformed(ActionEvent e) {
				winMain.getCommands().showAbout();
			}
		};
	}

	public static void openedNoData()
	{
		fileOpen.setEnabled(true);
		fileSample.setEnabled(true);
        dataExport.setEnabled(false);
		reset.setEnabled(false);
		spin.setEnabled(false);
		screenshot.setEnabled(false);
		captureMovie.setEnabled(false);
		showPrefs.setEnabled(true);
		showAbout.setEnabled(true);
	}

	public static void openedData()
	{
		fileOpen.setEnabled(true);
		fileSample.setEnabled(true);
        dataExport.setEnabled(true);
		reset.setEnabled(true);
		spin.setEnabled(true);
		screenshot.setEnabled(true);
		captureMovie.setEnabled(true);
		showPrefs.setEnabled(true);
		showAbout.setEnabled(true);
	}
}