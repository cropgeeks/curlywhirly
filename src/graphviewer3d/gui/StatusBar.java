package graphviewer3d.gui;

import java.awt.Color;
import java.awt.Dimension;

import javax.swing.BorderFactory;
import javax.swing.JLabel;

public class StatusBar extends JLabel
{
	public final String DEFAULT_TEXT = " Position the mouse over a point to see its label.";
	
	public StatusBar()
	{
		super();
		super.setPreferredSize(new Dimension(100, 16));
		setMessage(" Ready");
		setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
	}
	
	public void setMessage(String message)
	{
		setText(" " + message);
	}
	
	public void setDefaultText()
	{
		setText(DEFAULT_TEXT);
	}
}
