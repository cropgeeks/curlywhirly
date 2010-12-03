package curlywhirly.gui;

import java.awt.*;

import javax.swing.*;

public class StatusBar extends JPanel
{
	public final String DEFAULT_TEXT = " Position the mouse over a point to see its label.";
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
		
		setMessage(" Ready");
		setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));

	}
	
	public void setMessage(String message)
	{
		label.setText(" " + message);
	}
	
	public void setDefaultText()
	{
		label.setText(DEFAULT_TEXT);
	}
}
