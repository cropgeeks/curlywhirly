// Copyright 2009-2019 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package jhi.curlywhirly.gui;

import scri.commons.gui.*;

import javax.swing.*;
import java.awt.*;

public class StatusBar extends JPanel
{
	private final JLabel label;
	private final JLabel fpsLabel;

	public StatusBar()
	{
		setLayout(new BorderLayout());
		setBorder(BorderFactory.createCompoundBorder(
			BorderFactory.createLineBorder(Color.LIGHT_GRAY),
			BorderFactory.createEmptyBorder(1, 2, 2, 2)));

		JPanel helpPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 2, 2));
		label = new JLabel();
		helpPanel.add(label);
		add(helpPanel, BorderLayout.WEST);

		JPanel renderPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 2));
		fpsLabel = new JLabel();
		renderPanel.add(fpsLabel);
		add(renderPanel, BorderLayout.EAST);

		setReadyText();
	}

	private void setMessage(String message)
	{
		label.setText(" " + message);
	}

	public void setReadyText()
	{
		setMessage(RB.getString("gui.StatusBar.ready"));
	}

	public void setDefaultText()
	{
		setMessage(RB.getString("gui.StatusBar.default"));
	}

	public void updateFps(int fps)
	{
		clearFps();

		if (fps != 0)
			fpsLabel.setText(fps + " FPS");
	}

	void clearFps()
	{
		fpsLabel.setText("");
	}
}