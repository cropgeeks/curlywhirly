// Copyright 2009-2019 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package jhi.curlywhirly.gui;

import scri.commons.gui.*;

import javax.swing.*;
import javax.swing.event.*;
import java.awt.*;

public class WinMainToolBar extends JToolBar
{
	private final JButton open;
	private final JButton sample;
	private final JButton export;
	private final JButton reset;
	private final JToggleButton spin;
	private final JButton screenshot;
	private final JButton movie;
	private final JButton prefs;
	private final JButton about;
	private final JButton help;

	private JSlider slider;

	WinMainToolBar(final WinMain winMain)
	{
		setFloatable(false);

		// Convert rotation speed to our slider model's number scale
		float initial = ((Prefs.guiRotationSpeed - (-0.1f)) / (-1.0f - (-0.1f)) * (100f - 0f) + 0f);
		slider = new JSlider(0, 100, (int) initial)
		{
			@Override
			public Dimension getMaximumSize()
			{
				return new Dimension(175, getPreferredSize().height);
			}
		};
		slider.setToolTipText(RB.getString("gui.WinMainToolBar.sliderTT"));

		slider.addChangeListener(new ChangeListener()
		{
			@Override
			public void stateChanged(ChangeEvent evt)
			{
				int speed = slider.getValue();
				float rotation = ((speed - 0f) / (100f - 0f) * (-1.0f - (-0.1f)) + -0.1f);
				Prefs.guiRotationSpeed = rotation;
				winMain.getOpenGLPanel().getScene().setRotationSpeed(rotation);
			}
		});

		slider.setEnabled(false);

		open = (JButton) getButton(false,
			RB.getString("gui.WinMainToolBar.open"),
			RB.getString("gui.WinMainToolBar.openTT"),
			Icons.getIcon("OPEN"), Actions.fileOpen);

		sample = (JButton) getButton(false, null,
			RB.getString("gui.WinMainToolBar.sampleTT"),
			Icons.getIcon("SAMPLE"), Actions.fileSample);

		export = (JButton) getButton(false,
			RB.getString("gui.WinMainToolBar.export"),
			RB.getString("gui.WinMainToolBar.exportTT"),
			Icons.getIcon("EXPORT"), Actions.dataExport);

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
			Icons.getIcon("INFO"), Actions.showAbout);

		help = (JButton) getButton(false, null,
			RB.getString("gui.WinMainToolBar.helpTT"),
			Icons.getIcon("HELP"), Actions.showHelp);


		if (SystemUtils.isMacOS() == false)
			add(new JLabel(" "));

		add(open);
		add(sample);
		add(export);
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
		add(help);

		add(new JLabel(" "));
	}

	// Utility method to help create the buttons. Sets their text, tooltip, and
	// icon, as well as adding actionListener, defining margings, etc.
	private AbstractButton getButton(boolean toggle, String title,
									 String tt, ImageIcon icon, Action a)
	{
		AbstractButton button = toggle ? new JToggleButton(a) : new JButton(a);

		button.setText(title != null ? title : "");
		button.setToolTipText(tt);
		button.setIcon(icon);
		button.setFocusPainted(false);
		button.setFocusable(false);
		button.setMargin(new Insets(2, 1, 2, 1));

		return button;
	}

	JSlider getSlider()
	{
		return slider;
	}

	JToggleButton getSpin()
	{
		return spin;
	}
}