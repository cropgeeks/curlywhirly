// Copyright 2009-2014 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package curlywhirly.gui;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.*;
import javax.swing.table.*;

import curlywhirly.data.*;
import curlywhirly.gui.viewer.*;

import scri.commons.gui.RB;

class CategoryPanel extends JPanel
{
	// Components of panel
	private JPanel namePanel;
	private JRadioButton radioButton;
	private JLabel lblCount;
	private JTable catTable;
	private JRadioButton expandButton;
	private JPanel tablePanel;

	private static final Icon EXPANDED = ((Icon) UIManager.get("Tree.expandedIcon"));
	private static final Icon COLLAPSED = ((Icon) UIManager.get("Tree.collapsedIcon"));

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
		namePanel = createNamePanel();
		add(namePanel);

		tablePanel = createTablePanel();
		add(tablePanel);
	}

	private JPanel createNamePanel()
	{
		JPanel panel = new JPanel();
		panel.setLayout(new GridBagLayout());
		panel.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));

		GridBagConstraints con = new GridBagConstraints();
		// The expand / contract button for this CategoryPanel
		expandButton = createExpandButton(con);
		panel.add(expandButton, con);

		// The radio button for choosing if this is the selected category group
		radioButton = createRadioButton(con);
		panel.add(radioButton, con);

		lblCount = createCountLabel(con);
		panel.add(lblCount, con);

		// This prevents the panel changing size when expandButton's state
		// changes.
		panel.setMaximumSize(new Dimension(panel.getMaximumSize().width, panel.getPreferredSize().height));

		return panel;
	}

	// Expands or contracts this category panel. Uses custom icons (taken from
	// the JTree control in a JRadioButton to ape the look of a JTree.
	private JRadioButton createExpandButton(GridBagConstraints con)
	{
		con.gridy = 0;
		con.fill = GridBagConstraints.HORIZONTAL;
		con.gridx = 0;
		con.anchor = GridBagConstraints.LINE_START;
		con.gridy = 0;
		con.gridheight = GridBagConstraints.REMAINDER;
		con.weightx = 0;

		// Need to set the icon and rollover icon for each state
		JRadioButton button = new JRadioButton();
		button.setIcon(EXPANDED);
		button.setRolloverIcon(EXPANDED);
		button.setSelectedIcon(COLLAPSED);
		button.setRolloverSelectedIcon(COLLAPSED);
		button.setBorder(new EmptyBorder(0, 0, 0, -8));

		button.addActionListener(parent);

		return button;
	}

	private JRadioButton createRadioButton(GridBagConstraints con)
	{
		con.gridy = 0;
		con.fill = GridBagConstraints.HORIZONTAL;
		con.gridx = 1;
		con.anchor = GridBagConstraints.LINE_START;
		con.gridy = 0;
		con.gridheight = GridBagConstraints.REMAINDER;
		con.weightx = 1;

		JRadioButton button = new JRadioButton();
		button.setText(catGroup.getName());
		button.setToolTipText(catGroup.getName());
		button.addActionListener(parent);

		return button;
	}

	private JLabel createCountLabel(GridBagConstraints con)
	{
		con.gridx = 2;
		con.anchor = GridBagConstraints.LINE_END;
		con.gridwidth = GridBagConstraints.REMAINDER;
		con.gridy = 0;
		con.gridheight = GridBagConstraints.REMAINDER;
		con.weightx = 0;

		JLabel label = new JLabel();
		label.setText(getCountString());

		return label;
	}

	private String getCountString()
	{
		int total = 0;
		int selected = 0;
		for (Category category : catGroup)
		{
			total += category.getTotal();
			selected += category.getNoSelected();
		}

		return "" + selected + "/" + total;
	}

	private JPanel createTablePanel()
	{
		JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
		panel.setBackground(Color.WHITE);
		panel.add(Box.createHorizontalStrut(12));

		catTable = createTable();
		catTable.addMouseListener(new TableMouseListener());

		panel.add(catTable);
		panel.addMouseListener(new TableMouseListener());

		return panel;
	}

	private JTable createTable()
	{
		final CategoryTableModel classModel = new CategoryTableModel(catGroup, dataSet);
		// Add a model listener so that we can update the name panel when
		// categories are selected and deselected.
		classModel.addTableModelListener(parent);

		// Create a new table with the given table model and the cellrenderer
		// which can be found in that table model.
		final JTable table = new JTable(classModel)
		{
			@Override
			public TableCellRenderer getCellRenderer(int row, int col)
			{
				TableCellRenderer tcr = classModel.getCellRenderer(col);
				return (tcr != null) ? tcr : super.getCellRenderer(row, col);
			}
		};
		// Constrain the column widths on the columns with the checkbox and the
		// colour.
		table.getColumnModel().getColumn(0).setMaxWidth(20);
		table.getColumnModel().getColumn(0).setPreferredWidth(20);
		table.getColumnModel().getColumn(1).setMaxWidth(20);
		table.getColumnModel().getColumn(1).setPreferredWidth(20);

//		addTableMouseListener(table);

		return table;
	}

	void selectAll()
	{
		setCategoriesSelected(true);
	}

	void selectNone()
	{
		setCategoriesSelected(false);
	}

	private void setCategoriesSelected(boolean selected)
	{
		for (int row=0; row < catTable.getRowCount(); row++)
			catTable.setValueAt(selected, row, CategoryTableModel.CHECK_BOX_COL);
	}

	// Should be called whenever the count in the name panel needs to be updated
	// such as from the tableChanged method of the tableModelListener.
	void updateNamePanel()
	{
		lblCount.setText(getCountString());
	}

	public void setVisible(boolean visible)
	{
		catTable.setVisible(visible);
	}

	public boolean isVisible()
		{ return catTable.isVisible(); }

	JRadioButton getButton()
		{ return radioButton; }

	int getNamePanelHeight()
		{ return namePanel.getPreferredSize().height; }

	JRadioButton getExpandButton()
	{
		return expandButton;
	}

	JPanel getTablePanel()
	{
		return tablePanel;
	}

	int getTableHeight()
		{ return catTable.getPreferredSize().height; }

	private void displayMenu(MouseEvent e)
	{
		JMenuItem mAllInGroup = new JMenuItem();
		RB.setText(mAllInGroup, "gui.CategoryPanel.mAllInGroup");
		mAllInGroup.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				selectAll();
			}
		});

		JMenuItem mNoneInGroup = new JMenuItem();
		RB.setText(mNoneInGroup, "gui.CategoryPanel.mNoneInGroup");
		mNoneInGroup.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e)
			{
				selectNone();
			}
		});

		JPopupMenu menu = new JPopupMenu();
		menu.add(mAllInGroup);
		menu.add(mNoneInGroup);
		menu.show(e.getComponent(), e.getX(), e.getY());
	}

	private class TableMouseListener extends MouseInputAdapter
	{
		@Override
		public void mousePressed(MouseEvent e)
		{
			if (e.isPopupTrigger())
				displayMenu(e);
		}

		@Override
		public void mouseReleased(MouseEvent e)
		{
			if (e.isPopupTrigger())
				displayMenu(e);
		}

		@Override
		public void mouseClicked(MouseEvent me)
		{
			if (catTable.getSelectedRows().length == 0 || catTable.getSelectedRows().length > 1)
				return;

			if (me.getClickCount() == 2)
			{
				int row = catTable.getSelectedRow();
				Category category = (Category) catTable.getValueAt(row, 2);

				Color newColor = JColorChooser.showDialog(CurlyWhirly.winMain, RB.getString("gui.CategoryPanel.colourChooser"), category.getColor());
				if (newColor != null)
				{
					ColorPrefs.setColor(category.getColorKey(), newColor);
					category.setColour(newColor);
					// Needed to force an update of the colour displayed
					// within the table
					catTable.repaint();
				}
			}
		}
	}
}