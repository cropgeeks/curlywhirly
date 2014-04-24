// Copyright 2009-2014 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package curlywhirly.gui;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;
import javax.swing.event.*;

import curlywhirly.data.*;

class CategoryGroupPanel extends JPanel implements ActionListener, TableModelListener
{
	private final WinMain winMain;
	private final SelectionPanelNB parent;

	private DataSet dataSet;

	private ButtonGroup buttonGroup;
	private ArrayList<CategoryPanel> categoryPanels;

	private int prefHeight;
	private Component vStrut;

	CategoryGroupPanel(final SelectionPanelNB parent, WinMain winMain)
	{
		this.winMain = winMain;
		this.parent = parent;

		addComponentListener();
	}

	// Listens for resize events and adjusts the size of the "vertical padding"
	// vStrut as required. Prevents unwanted whitespace appearing in CategoryPanels
	private void addComponentListener()
	{
		this.addComponentListener(new ComponentAdapter() {
			@Override
			public void componentResized(ComponentEvent e)
			{
				super.componentResized(e);

				// If the scroll pane is larger than our components add a vertical strut
				// to push our components to the top.
				if (prefHeight < parent.getHeight())
				{
					// vStrut might not exist if this gets called as part of the panel setup
					if (vStrut != null)
						remove(vStrut);

					// Adjust the size of vStrut and add it to the panel again
					vStrut = Box.createVerticalStrut(parent.getHeight()-prefHeight);
					add(vStrut);
				}
			}
		});
	}

	void setDataSet(DataSet dataSet)
	{
		this.dataSet = dataSet;

		// Because we tweak the height of the component ourselves so that it
		// renders correctly we need to reset that here to make the scroll bar
		// disappear if it's a null dataset
		prefHeight = 0;
		setPreferredSize(new Dimension(getPreferredSize().width, prefHeight));

		setupCategoryGroupUI();
	}

	private void setupCategoryGroupUI()
	{
		if (dataSet != null)
		{
			createControls(dataSet.getCategoryGroups());

			// Select the first scheme and colour by this scheme.
			if (buttonGroup.getElements().hasMoreElements())
				buttonGroup.getElements().nextElement().setSelected(true);

			if (categoryPanels.size() > 0)
				dataSet.setCurrentCategoryGroup(dataSet.getCategoryGroups().get(0));
		}
	}

	private void createControls(ArrayList<CategoryGroup> catGroups)
	{
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

		categoryPanels = new ArrayList<CategoryPanel>();
		buttonGroup = new ButtonGroup();

		prefHeight = 0;

		for (int i=0; i < catGroups.size(); i++)
			addCategoryPanel(catGroups.get(i));

		// If the scroll pane is larger than our components add a vertical strut
		// to push our components to the top.
		if (prefHeight < parent.getHeight())
		{
			vStrut = Box.createVerticalStrut(parent.getHeight()-prefHeight);
			add(vStrut);
		}

		setPreferredSize(new Dimension(getPreferredSize().width, prefHeight));
	}

	private void addCategoryPanel(CategoryGroup group)
	{
		CategoryPanel catPanel = new CategoryPanel(this, group, dataSet);
		add(catPanel);
		buttonGroup.add(catPanel.getButton());
		categoryPanels.add(catPanel);

		prefHeight += catPanel.getNamePanelHeight();
		prefHeight += catPanel.getTableHeight();
	}

	void selectAll()
	{
		for (CategoryPanel panel : categoryPanels)
			panel.selectAll();
	}

	void selectNone()
	{
		for (CategoryPanel panel : categoryPanels)
			panel.selectNone();
	}

	@Override
	public void actionPerformed(ActionEvent e)
	{
		// Loop over our controls testing each one to see if it has triggered
		// an event.
		for (int i=0; i < categoryPanels.size(); i++)
		{
			CategoryPanel catPanel = categoryPanels.get(i);

			// Change the currently selected category group.
			if (e.getSource() == catPanel.getButton())
				changeSelectedCategoryGroup(dataSet.getCategoryGroups().get(i), catPanel);

			else if (e.getSource() == catPanel.getExpandButton())
				toggleGroupExpanded(catPanel);
		}
	}

	private void changeSelectedCategoryGroup(CategoryGroup catGroup, CategoryPanel catPanel)
	{
		dataSet.setCurrentCategoryGroup(catGroup);
		catPanel.setVisible(true);
		invalidate();
		repaint();
	}

	private void toggleGroupExpanded(CategoryPanel catPanel)
	{
		JPanel tablePanel = catPanel.getTablePanel();

		// Change visibility and update panel size
		tablePanel.setVisible(!catPanel.getTablePanel().isVisible());
		updatePanelSize(catPanel, tablePanel);

		catPanel.repaint();
	}

	private void updatePanelSize(CategoryPanel catPanel, JPanel tablePanel)
	{
		int tableHeight = catPanel.getTableHeight();
		prefHeight = tablePanel.isVisible() ? prefHeight + tableHeight : prefHeight - tableHeight;
		setPreferredSize(new Dimension(getPreferredSize().width, prefHeight));
	}

	// Respond to user interaction with the tables by updating their title
	// panels and updating the table in the dataPanel tab.
	@Override
	public void tableChanged(TableModelEvent e)
	{
		for (CategoryPanel container : categoryPanels)
			container.updateNamePanel();

		// Required to force the counts in tables other than the one that was
		// changed to update.
		repaint();

		winMain.getDataPanel().updateTableModel();
	}
}