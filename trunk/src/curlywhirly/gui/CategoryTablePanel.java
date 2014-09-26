package curlywhirly.gui;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.table.*;

import curlywhirly.data.*;
import curlywhirly.gui.viewer.*;

import scri.commons.gui.*;

class CategoryTablePanel extends JPanel
{
	private final JTable catTable;

	private final CategoryGroup catGroup;
	private final DataSet dataSet;
	private final CategoryGroupPanel parent;

	CategoryTablePanel(CategoryGroup catGroup, DataSet dataSet, CategoryGroupPanel parent)
	{
		this.catGroup = catGroup;
		this.dataSet = dataSet;
		this.parent = parent;

		setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
		setBackground(Color.WHITE);
		add(Box.createHorizontalStrut(12));

		catTable = createTable();
		catTable.addMouseListener(new TableMouseListener());

		add(catTable);
		addMouseListener(new TableMouseListener());
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

		return table;
	}

	// This used to utilise the table's setValueAt, but the functionality of
	// has changed to accomodate the three state check box, so now we just
	// select the categories directly.
	void setCategoriesSelected(boolean selected)
	{
		for (Category cat : catGroup)
			cat.setSelected(selected);

		repaint();
	}

	JTable getCatTable()
		{ return catTable; }

	private void displayMenu(MouseEvent e)
	{
		JMenuItem mAllInGroup = new JMenuItem();
		RB.setText(mAllInGroup, "gui.CategoryPanel.mAllInGroup");
		mAllInGroup.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				setCategoriesSelected(true);
			}
		});

		JMenuItem mNoneInGroup = new JMenuItem();
		RB.setText(mNoneInGroup, "gui.CategoryPanel.mNoneInGroup");
		mNoneInGroup.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e)
			{
				setCategoriesSelected(false);
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

			int col = catTable.columnAtPoint(me.getPoint());
			int row = catTable.rowAtPoint(me.getPoint());

			if (me.getClickCount() == 2 && col != 0)
			{
				Category category = (Category) catTable.getValueAt(row, 2);

				Color newColor = JColorChooser.showDialog(CurlyWhirly.winMain,
					RB.getString("gui.CategoryPanel.colourChooser"), category.getColor());
				if (newColor != null)
				{
					ColorPrefs.setColor(category.getColorKey(), newColor);
					category.setColour(newColor);
					// Needed to force an update of the colour displayed
					// within the table
					catTable.repaint();
				}
			}

			else if (col == 0)
				catTable.setValueAt(catTable.getValueAt(row, col), row, col);
		}
	}
}
