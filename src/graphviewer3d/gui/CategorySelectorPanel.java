package graphviewer3d.gui;

import graphviewer3d.data.DataSet;

import java.awt.BorderLayout;
import java.awt.ComponentOrientation;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;
import java.util.Collections;
import java.util.Vector;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

public class CategorySelectorPanel extends JPanel implements ListSelectionListener, ActionListener
{
	
	// ===================================================vars===================================================
	
	JTable selectorTable;
	JButton resetButton;
	DataSet dataSet;
	String[][] data;
	public int[] indexes;
	GraphViewerFrame frame;
	Vector<String> categories;
	
	// ===================================================c'tor===================================================
	
	public CategorySelectorPanel(GraphViewerFrame frame)
	{
		this.frame = frame;
		dataSet = frame.dataSet;
		initComponents();
	}
	
	// ===================================================methods===================================================
	
	private void initComponents()
	{

		//layout 
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		
		//get the categories and sort them into a usable array for this
		categories = dataSet.getCategories();
		Collections.sort(categories);
		String [] categoriesArray = new String[categories.size()];
		categories.toArray(categoriesArray);		
		
		//the data to display in the selector table
		data = new String[categoriesArray.length][1];		
		for (int i = 0; i < categories.size(); i++)
		{
			data[i][0] = categoriesArray[i];
		}
		String[] columnNames = new String[]
		{ "Categories" };
		
		// table for selecting categories to highlight
		selectorTable = new JTable(data, columnNames);
		selectorTable.setPreferredScrollableViewportSize(new Dimension(80, 190));
		selectorTable.setFillsViewportHeight(true);
		selectorTable.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		selectorTable.getSelectionModel().addListSelectionListener(this);
		selectorTable.setCellSelectionEnabled(true);
		JScrollPane scrollPane = new JScrollPane(selectorTable);
		this.add(scrollPane,BorderLayout.NORTH);
		
		//space between components
		Dimension spacer = new Dimension(10,10);
		add(new Box.Filler(spacer, spacer, spacer));
		
		//reset button
		resetButton = new JButton("Restore all colours");
		resetButton.addActionListener(this);
		this.add(resetButton, BorderLayout.CENTER);
		
		// make a titled border around it all
		this.setBorder(BorderFactory.createTitledBorder("Highlight categories: "));
	}
	
	// --------------------------------------------------------------------------------------------------------------------------------------------------
	
	//stores the values selected in the categories table 
	public void valueChanged(ListSelectionEvent e)
	{	
		if(e.getValueIsAdjusting())
			return;
		updateColourCoding();
	}
	
	// --------------------------------------------------------------------------------------------------------------------------------------------------
	
	private void updateColourCoding()
	{
		indexes = selectorTable.getSelectedRows();
		//get the view to update itself
		//pass it a vector of categories to highlight
		Vector<String> updatableCategories = new Vector();
		for (int i = 0; i < indexes.length; i++)
		{
			updatableCategories.add((String)categories.get(indexes[i]));
		}
		frame.canvas3D.colourSpheres(updatableCategories);		
	}
	
	// --------------------------------------------------------------------------------------------------------------------------------------------------

	public void actionPerformed(ActionEvent e)
	{
		if(e.getSource() == resetButton)
		{
			System.out.println("reset button clicked");
			frame.canvas3D.colourSpheres(null);		
		}
	}
	
	// --------------------------------------------------------------------------------------------------------------------------------------------------	
	
}// end class
