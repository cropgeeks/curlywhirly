package curlywhirly.gui;

import java.awt.event.*;
import java.util.*;
import javax.swing.*;
import javax.swing.event.*;

import curlywhirly.data.*;

class CategoryGroupPanel implements ActionListener, TableModelListener
{
	private JPanel panel;
	private ButtonGroup bg;
	private ArrayList<CategoryPanel> categoryPanels;

	private DataSet dataSet;
	private CurlyWhirly curlyWhirly;

	CategoryGroupPanel(CurlyWhirly curlyWhirly, ArrayList<CategoryGroup> schemes, DataSet dataSet)
	{
		this.dataSet = dataSet;
		this.curlyWhirly = curlyWhirly;

		createControls(schemes);

		// Select the first scheme and colour by this scheme.
		if (bg.getElements().hasMoreElements())
			bg.getElements().nextElement().setSelected(true);

		if (categoryPanels.size() > 0)
			dataSet.getCategoryGroups().get(0).setCurrent(true);

		// Collapse all but the first table.
		for (int i=1; i < categoryPanels.size(); i++)
			categoryPanels.get(i).setVisible(false);

		// Setup the table in the dataPanel tab.
		curlyWhirly.getDataPanel().updateTableModel();
	}

	private void createControls(ArrayList<CategoryGroup> schemes)
	{
		panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

		categoryPanels = new ArrayList<CategoryPanel>();
		bg = new ButtonGroup();

		for (int i=0; i < schemes.size(); i++)
			categoryPanels.add(createCategoryPanel(schemes.get(i)));
	}

	private CategoryPanel createCategoryPanel(CategoryGroup scheme)
	{
		CategoryPanel catPanel = new CategoryPanel(this, scheme, dataSet);
		// Originally added a single panel from the categoryPanel class, but
		// you can't nest two boxlayouts of the same orientation and other
		// layouts stretched the components out.
		panel.add(catPanel.getNamePanel());
		panel.add(catPanel.getTable());
		bg.add(catPanel.getButton());

		return catPanel;
	}

	@Override
	public void actionPerformed(ActionEvent e)
	{
		// Loop over our controls testing each one to see if it has triggered
		// an event.
		for (int i=0; i < categoryPanels.size(); i++)
		{
			CategoryPanel container = categoryPanels.get(i);

			// Change the currently selected category group.
			if (e.getSource() == container.getButton())
			{
				dataSet.getCurrentCategoryGroup().setCurrent(false);
				CategoryGroup catGroup = dataSet.getCategoryGroups().get(i);
				dataSet.setCurrentCategoryGroup(catGroup);
				catGroup.setCurrent(true);
				container.setVisible(true);
				panel.invalidate();
				panel.repaint();
			}

			// Expand or contract a category group's panel.
			else if (e.getSource() == container.getNameLabel())
				if (container.getButton().isSelected() == false)
					container.setVisible(!container.isVisible());
		}
	}

	// Respond to user interaction with the tables by updating their title
	// panels and updating the table in the dataPanel tab.
	@Override
	public void tableChanged(TableModelEvent e)
	{
		for (CategoryPanel container : categoryPanels)
			container.updateNamePanel();

		curlyWhirly.getDataPanel().updateTableModel();
	}

	public JPanel getPanel()
		{ return panel; }
}