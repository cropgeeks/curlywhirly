package curlywhirly.gui;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.table.*;

import curlywhirly.data.*;
import curlywhirly.gui.viewer.ColorPrefs;
import scri.commons.gui.RB;

class CategoryPanel
{
	// Components of panel
	private JPanel namePanel;
	private JRadioButton button;
	private JLabel lblName;
	private JLabel lblCount;
	private JTable catTable;

	// Data which backs the components
	private CategoryGroupPanel parent;
	private CategoryGroup catGroup;
	private DataSet dataSet;

	CategoryPanel(CategoryGroupPanel parent, CategoryGroup catGroup, DataSet dataSet)
	{
		this.parent = parent;
		this.catGroup = catGroup;
		this.dataSet = dataSet;

		createControls();
	}

	private void createControls()
	{
		namePanel = createNamePanel();
		catTable = createTable();
	}

	private JPanel createNamePanel()
	{
		JPanel panel = new JPanel();
		panel.setLayout(new GridBagLayout());
		panel.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));

		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 0;
		c.anchor = GridBagConstraints.LINE_START;
		c.weightx = 1;

		// The radio button for choosing if this is the selected category group
		button = new JRadioButton();
		button.setText(catGroup.getName());
		panel.add(button, c);
		button.addActionListener(parent);
//		panel.add(new Box.Filler(new Dimension(2, 0), new Dimension(2, 0), new Dimension(2, 0)));

//		lblName = createNameLabel();
//		panel.add(lblName, c);

		// Use glue to place count label at the right hand side of the panel
//		panel.add(Box.createHorizontalGlue());
		c.gridx = 1;
		c.anchor = GridBagConstraints.LINE_END;
		c.gridwidth = GridBagConstraints.REMAINDER;
		c.weightx = 0;
		createCountLabel();
		panel.add(lblCount, c);

		return panel;
	}

	private JLabel createNameLabel()
	{
		JLabel name = new JLabel();
		name.setText(catGroup.getName());
		name.setBackground(Color.WHITE);
		name.setForeground(new Color(68, 106, 156));

		return name;
	}

	private void createCountLabel()
	{
		int total = 0;
		int selected = 0;
		for (Category category : catGroup)
		{
			total += category.getTotal();
			selected += category.getNoSelected();
		}

		lblCount = new JLabel("" + selected + "/" + total);
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

			@Override
			public void doLayout()
			{
				// On resize assign space to the second last column
				TableColumnModel tcm = getColumnModel();
				int delta = getParent().getWidth() - tcm.getTotalColumnWidth();
				TableColumn last = tcm.getColumn(tcm.getColumnCount() - 2);
				last.setPreferredWidth(last.getPreferredWidth() + delta);
				last.setWidth(last.getPreferredWidth());
			}
		};
		// Constrain the column widths on the columns with the checkbox and the
		// colour.
		table.getColumnModel().getColumn(0).setMaxWidth(20);
		table.getColumnModel().getColumn(0).setPreferredWidth(20);
		table.getColumnModel().getColumn(1).setMaxWidth(20);
		table.getColumnModel().getColumn(1).setPreferredWidth(20);

		addTableMouseListener(table);

		return table;
	}

	private void addTableMouseListener(final JTable table)
	{
		table.addMouseListener(new MouseAdapter()
		{
			@Override
			public void mouseClicked(MouseEvent me)
			{
				if (table.getSelectedRows().length > 1)
					return;

				if (me.getClickCount() == 2)
				{
					int row = table.getSelectedRow();
					Category category = (Category) table.getValueAt(row, 2);

					if (category != null)
					{
						Color newColor = JColorChooser.showDialog(CurlyWhirly.winMain, RB.getString("gui.CategoryPanel.colourChooser"), category.getColor());
						if (newColor != null)
						{
							ColorPrefs.setColor(category.getColorKey(), newColor);
							category.setColour(newColor);
							// Needed to force an update of the colour displayed
							// within the table
							table.repaint();
						}
					}
				}
			}
		});
	}

	// Should be called whenever the count in the name panel needs to be updated
	// such as from the tableChanged method of the tableModelListener.
	JPanel updateNamePanel()
	{
		namePanel.remove(lblCount);
		createCountLabel();
		namePanel.add(lblCount);

		return namePanel;
	}

	JLabel getNameLabel()
		{ return lblName; }

	void setVisible(boolean visible)
	{
		catTable.setVisible(visible);
	}

	boolean isVisible()
		{ return catTable.isVisible(); }

	JRadioButton getButton()
		{ return button; }

	JPanel getNamePanel()
		{ return namePanel; }

	JTable getTable()
		{ return catTable; }
}