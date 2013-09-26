package curlywhirly.gui;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;
import javax.swing.event.*;

import curlywhirly.data.*;

class CategoryGroupPanel implements ActionListener, TableModelListener
{
	private JPanel panel;
	private ButtonGroup buttonGroup;
	private ArrayList<CategoryPanel> categoryPanels;

	private DataSet dataSet;
	private WinMain winMain;

	CategoryGroupPanel(WinMain winMain, ArrayList<CategoryGroup> schemes, DataSet dataSet)
	{
		this.dataSet = dataSet;
		this.winMain = winMain;

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
		panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

		categoryPanels = new ArrayList<CategoryPanel>();
		buttonGroup = new ButtonGroup();

		for (int i=0; i < catGroups.size(); i++)
		{
			CategoryPanel catPanel = createCategoryPanel(catGroups.get(i));
//			panel.add(catPanel, c);

//			buttonGroup.add(catPanel.getButton());
			categoryPanels.add(catPanel);
		}

//		panel.add(Box.createVerticalGlue());
	}

	private CategoryPanel createCategoryPanel(CategoryGroup group)
	{
//		CPanel catPanel = new CPanel(this, group, dataSet);
		CategoryPanel catPanel = new CategoryPanel(this, group, dataSet);
		// Originally added a single panel from the categoryPanel class, but
		// you can't nest two boxlayouts of the same orientation and other
		// layouts stretched the components out.
		JPanel p = new JPanel();
		p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
		p.add(catPanel.getNamePanel());
		p.add(catPanel.getTable());
		panel.add(p);
//		panel.add(catPanel);
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
//				dataSet.getCurrentCategoryGroup().setCurrent(false);
				CategoryGroup catGroup = dataSet.getCategoryGroups().get(i);
				dataSet.setCurrentCategoryGroup(catGroup);
//				catGroup.setCurrent(true);
				container.setVisible(true);
				panel.invalidate();
				panel.repaint();
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

		winMain.getDataPanel().updateTableModel();
	}

	public JPanel getPanel()
		{ return panel; }
}