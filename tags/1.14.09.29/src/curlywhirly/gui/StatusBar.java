// Copyright 2009-2014 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package curlywhirly.gui;

import java.awt.*;
import javax.swing.*;

import scri.commons.gui.*;

public class StatusBar extends JPanel
{
	JLabel label;
	public JProgressBar progressBar;

	public StatusBar()
	{
		super(new BorderLayout());

		label = new JLabel();
		label.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 10));
		label.setBackground(Color.green);
		progressBar = new JProgressBar();
		progressBar.setPreferredSize(new Dimension(progressBar.getWidth(), 5));

		add(progressBar,BorderLayout.CENTER);
		add(label, BorderLayout.WEST);
		progressBar.setVisible(false);

		setMessage(RB.getString("gui.StatusBar.ready"));
		setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));

	}

	public void setMessage(String message)
	{
		label.setText(" " + message);
	}

	public void setDefaultText()
	{
		setMessage(RB.getString("gui.StatusBar.default"));
	}
}