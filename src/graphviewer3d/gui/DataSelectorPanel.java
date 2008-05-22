package graphviewer3d.gui;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class DataSelectorPanel extends JPanel implements ActionListener
{

	// ==================================================vars===================================================
	
	JComboBox comboX, comboY, comboZ;
	
	
	// ===================================================c'tor===================================================
	
	public DataSelectorPanel()
	{
		initComponents();
	}
	
	
	// ===================================================methods===================================================
	
	private void initComponents()
	{
		
		//layout 
		setLayout(new GridLayout(3,2));
		
		//data
		String [] values = new String[]{"var1", "var2"};
		
		//x combo
		JLabel xLabel = new JLabel("x-axis:");
		add(xLabel);
		comboX = new JComboBox(values);
		comboX.setSelectedIndex(0);
		comboX.addActionListener(this);
		add(new JPanel().add(comboX));
		

		//y combo
		JLabel yLabel = new JLabel("y-axis:");
		add(yLabel);
		comboY = new JComboBox(values);
		comboY.setSelectedIndex(0);
		comboY.addActionListener(this);
		add(comboY);

		
		//z combo
		JLabel zLabel = new JLabel("z-axis:");
		add(zLabel);
		comboZ = new JComboBox(values);
		comboZ.setSelectedIndex(0);
		comboZ.addActionListener(this);
		add(comboZ);

		
		// make a titled border around it all
		this.setBorder(BorderFactory.createTitledBorder("Data to display: "));
		
	}
	
//--------------------------------------------------------------------------------------------------------------------------------------------------------------	
	
	public void actionPerformed(ActionEvent e)
	{
		
	}
	
	//--------------------------------------------------------------------------------------------------------------------------------------------------------------
	
}//end class
