package graphviewer3d.gui;

import graphviewer3d.data.DataSet;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collections;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

public class CategorySelectorPanel extends JPanel implements ListSelectionListener, ActionListener
{
	
	// ===================================================vars===================================================
	
	JList selectorList;
	JButton resetButton;
	DataSet dataSet;
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
		
		// table for selecting categories to highlight
		selectorList = new JList(categoriesArray);
		selectorList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		selectorList.getSelectionModel().addListSelectionListener(this);

		JScrollPane scrollPane = new JScrollPane(selectorList);
		this.add(scrollPane);
		
		//space between components
		Dimension spacer = new Dimension(10,10);
		add(new Box.Filler(spacer, spacer, spacer));
		
		//reset button
		resetButton = new JButton("Restore all colours");
		resetButton.addActionListener(this);
		this.add(resetButton);
		
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
		indexes = selectorList.getSelectedIndices();
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
			selectorList.clearSelection();
			frame.canvas3D.colourSpheres(null);	
		}
	}
	
	// --------------------------------------------------------------------------------------------------------------------------------------------------	
	
}// end class
