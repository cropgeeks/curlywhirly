package graphviewer3d.gui;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JComboBox;
import javax.swing.JPanel;

public class BackgroundSelectorPanel extends JPanel implements ActionListener
{
	
	// ===================================================vars===================================================
	
	GraphViewerFrame frame;
	JComboBox bgCombo;
	
	// ===================================================c'tor===================================================
	
	public BackgroundSelectorPanel(GraphViewerFrame frame)
	{
		this.frame = frame;
		initComponents();
	}
	
	// ===================================================methods===================================================
	
	private void initComponents()
	{
		
		bgCombo = new JComboBox(new String [] {"light grey", "dark grey" , "black", "white"});
		bgCombo.setSelectedIndex(0);
		bgCombo.addActionListener(this);
		add(bgCombo);
		
		//space between components
		Dimension spacer = new Dimension(10,10);
		add(new Box.Filler(spacer, spacer, spacer));
		
		// make a titled border around it all
		this.setBorder(BorderFactory.createTitledBorder("Select background colour: "));
	}
	
	// --------------------------------------------------------------------------------------------------------------------------------------------------
	
	public void actionPerformed(ActionEvent e)
	{
		if(e.getSource() == bgCombo)
		{
			int bgColour = bgCombo.getSelectedIndex();
			frame.canvas3D.setBackgroundColour(bgColour);
		}
	}
	
	// --------------------------------------------------------------------------------------------------------------------------------------------------
	
}// end class
