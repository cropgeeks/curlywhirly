package graphviewer3d.gui;

import graphviewer3d.data.DataSet;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;
import java.util.Collections;
import java.util.Vector;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

public class CategorySelectorPanel extends JPanel implements ListSelectionListener
{
	
	// ===================================================vars===================================================
	
	JTable selectorTable;
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
		selectorTable.setPreferredScrollableViewportSize(new Dimension(100, 150));
		selectorTable.setFillsViewportHeight(true);
		selectorTable.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		selectorTable.getSelectionModel().addListSelectionListener(this);
		selectorTable.setCellSelectionEnabled(true);
		JScrollPane scrollPane = new JScrollPane(selectorTable);
		add(scrollPane);
		
		// make a titled border around it all
		this.setBorder(BorderFactory.createTitledBorder("Highlight categories: "));
	}
	
	// --------------------------------------------------------------------------------------------------------------------------------------------------
	
	//stores the values selected in the categories table 
	public void valueChanged(ListSelectionEvent e)
	{	
		if(e.getValueIsAdjusting())
			return;
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
	
}// end class
