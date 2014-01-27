package curlywhirly.gui;

import java.awt.*;
import java.io.*;
import javax.swing.*;
import javax.swing.event.*;

import scri.commons.gui.*;

public class WinMainToolBar extends JToolBar
{
	private JButton open;
	private JButton sample;
	private JButton reset;
	private JToggleButton spin;
	private JButton screenshot;
	private JButton movie;
	private JButton prefs;
	private JButton about;

	private JSlider slider;

	private final WinMain winMain;

	WinMainToolBar(final WinMain winMain)
	{
		this.winMain = winMain;

		setFloatable(false);
//		setBorderPainted(false);

		slider = new JSlider(0, 100, 50)
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
				winMain.getOpenGLPanel().setSpeed(speed);
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
//		add(movie);
		addSeparator();
		add(prefs);
		addSeparator();
		add(about);

		add(new JLabel(" "));
	}

	// Utility method to help create the buttons. Sets their text, tooltip, and
	// icon, as well as adding actionListener, defining margings, etc.
	private AbstractButton getButton(boolean toggle, String title,
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