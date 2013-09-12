package curlywhirly.gui;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.table.*;

import curlywhirly.data.*;
import scri.commons.gui.RB;

import scri.commons.gui.matisse.*;

class CategoryPanel
{
	// Components of panel
	private JPanel namePanel;
	private JRadioButton button;
	private HyperLinkLabel lblName;
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
		panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
		panel.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));

		// The radio button for choosing if this is the selected category group
		button = new JRadioButton();
		panel.add(button);
		button.addActionListener(parent);
		panel.add(new Box.Filler(new Dimension(2, 0), new Dimension(2, 0), new Dimension(2, 0)));

		createNameLabel();
		panel.add(lblName);

		// Use glue to place count label at the right hand side of the panel
		panel.add(Box.createHorizontalGlue());
		createCountLabel();
		panel.add(lblCount);

		return panel;
	}

	private void createNameLabel()
	{
		lblName = new HyperLinkLabel();
		lblName.setText(catGroup.getName());
		lblName.setBackground(Color.WHITE);
		lblName.setForeground(new Color(68, 106, 156));
		lblName.addActionListener(parent);
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
							category.setColour(newColor);
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

	HyperLinkLabel getNameLabel()
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