// Copyright 2009-2017 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package jhi.curlywhirly.gui;

import jhi.curlywhirly.data.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

class CategoryPanel extends JPanel implements ActionListener
{
	// Components of panel
	private CategoryNamePanel namePanel;
	private CategoryTablePanel tablePanel;

	// Data which backs the components
	private final CategoryGroupPanel parent;
	private final CategoryGroup catGroup;
	private final DataSet dataSet;

	CategoryPanel(CategoryGroupPanel parent, CategoryGroup catGroup, DataSet dataSet)
	{
		this.parent = parent;
		this.catGroup = catGroup;
		this.dataSet = dataSet;

		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		setBackground(Color.WHITE);

		createControls();
	}

	private void createControls()
	{
		namePanel = new CategoryNamePanel(catGroup, this);
		add(namePanel);

		tablePanel = new CategoryTablePanel(catGroup, dataSet, parent);
		add(tablePanel);
	}

	private void toggleGroupExpanded()
	{
		// Change visibility and update panel size
		tablePanel.setVisible(!tablePanel.isVisible());
		parent.updatePanelSize(this, tablePanel);
		invalidate();

		repaint();
	}

	@Override
	public void actionPerformed(ActionEvent e)
	{
		if (e.getSource() == namePanel.getExpandButton())
			toggleGroupExpanded();

		else if (e.getSource() == namePanel.getRadioButton())
		{
			dataSet.setCurrentCategoryGroup(catGroup);
			setVisible(true);
			parent.repaint();
		}
	}

	void selectAll()
	{
		tablePanel.setCategoriesSelected(true);
	}

	void selectNone()
	{
		tablePanel.setCategoriesSelected(false);
	}

	// Should be called whenever the count in the name panel needs to be updated
	// such as from the tableChanged method of the tableModelListener.
	void updateNamePanel()
	{
		namePanel.updateCountLabel();
	}

	CategoryTablePanel getTablePanel()
	{
		return tablePanel;
	}

	public CategoryNamePanel getNamePanel()
	{
		return namePanel;
	}

	CategoryGroup getCategoryGroup()
	{
		return catGroup;
	}

	int getTableHeight()
	{
		return tablePanel.getCatTable().getPreferredSize().height;
	}
}