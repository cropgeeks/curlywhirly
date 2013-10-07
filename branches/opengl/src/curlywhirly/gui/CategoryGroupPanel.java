package curlywhirly.gui;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;
import javax.swing.event.*;

import curlywhirly.data.*;

class CategoryGroupPanel extends JPanel implements ActionListener, TableModelListener
{
	private ButtonGroup buttonGroup;
	private ArrayList<CategoryPanel> categoryPanels;

	private DataSet dataSet;
	private WinMain winMain;

	private int prefHeight;

	private SelectionPanelNB parent;

	CategoryGroupPanel(SelectionPanelNB parent, WinMain winMain, ArrayList<CategoryGroup> schemes, DataSet dataSet)
	{
		this.dataSet = dataSet;
		this.winMain = winMain;

		this.parent = parent;

		createControls(schemes);

		// Select the first scheme and colour by this scheme.
		if (buttonGroup.getElements().hasMoreElements())
			buttonGroup.getElements().nextElement().setSelected(true);

		if (categoryPanels.size() > 0)
			dataSet.setCurrentCategoryGroup(dataSet.getCategoryGroups().get(0));

		// Setup the table in the dataPanel tab.
		winMain.getDataPanel().updateTableModel();
	}

	private void createControls(ArrayList<CategoryGroup> catGroups)
	{
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

		categoryPanels = new ArrayList<CategoryPanel>();
		buttonGroup = new ButtonGroup();

		prefHeight = 0;

		for (int i=0; i < catGroups.size(); i++)
		{
			CategoryPanel catPanel = createCategoryPanel(catGroups.get(i));
			add(catPanel);
			prefHeight += catPanel.getPreferredSize().height;

			categoryPanels.add(catPanel);
		}

		if (prefHeight < parent.getHeight())
		{
			add(Box.createVerticalStrut(parent.getHeight()-prefHeight));
			setPreferredSize(new Dimension(getPreferredSize().width, prefHeight));
		}
	}

	private CategoryPanel createCategoryPanel(CategoryGroup group)
	{
		CategoryPanel catPanel = new CategoryPanel(this, group, dataSet);
		buttonGroup.add(catPanel.getButton());

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
				CategoryGroup catGroup = dataSet.getCategoryGroups().get(i);
				dataSet.setCurrentCategoryGroup(catGroup);
				container.setVisible(true);
				invalidate();
				repaint();
			}
		}
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