// Copyright 2009-2014 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package curlywhirly.gui;

import java.awt.*;
import java.awt.image.*;
import javax.swing.*;
import javax.swing.table.*;

import curlywhirly.data.*;

public class CategoryTableModel extends AbstractTableModel
{
	private CategoryGroup group;
	private DataSet dataSet;

	public static final int CHECK_BOX_COL = 0;

	public CategoryTableModel(CategoryGroup scheme, DataSet dataSet)
	{
		this.group = scheme;
		this.dataSet = dataSet;
	}

	@Override
	public int getRowCount()
	{
		return group.size();
	}

	@Override
	public int getColumnCount()
	{
		return 4;
	}

	@Override
	public Class getColumnClass(int column)
	{
		switch (column)
		{
			case 0: return Boolean.class;
			case 1: return Color.class;
			case 2: return Category.class;
			case 3: return String.class;

			default: return null;
		}
	}

	@Override
	public boolean isCellEditable(int rowIndex, int columnIndex)
	{
		switch (columnIndex)
		{
			case 0:
			case 1: return true;

			default: return false;
		}
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex)
	{
		if (rowIndex < 0)
			return null;

		Category category = group.get(rowIndex);

		switch (columnIndex)
		{
			case 0: return category.isSelected();
			case 1: return category;
			case 2: return category;
			case 3: return category.getSelectedText();

			default: return null;
		}
	}

	@Override
	public void setValueAt(Object value, int rowIndex, int columnIndex)
	{
		Category category = group.get(rowIndex);

		switch (columnIndex)
		{
			case 0: category.setSelected((Boolean)value);
					fireTableDataChanged();
					break;
		}
	}

	class ColorListRenderer extends DefaultTableCellRenderer
	{
		// Set the attributes of the class and return a reference
		@Override
		public Component getTableCellRendererComponent(JTable table, Object value,
			boolean isSelected, boolean hasFocus, int row, int column)
		{
			super.getTableCellRendererComponent(table, value, isSelected,
				hasFocus, row, column);

			Category category = (Category) value;

			// Set the icon
			BufferedImage image = new BufferedImage(20, 10, BufferedImage.TYPE_INT_RGB);
			Graphics2D g = image.createGraphics();

			Color color = category.getColor();
			GradientPaint paint;
			if (category.getGroup() == dataSet.getCurrentCategoryGroup())
				paint = new GradientPaint(0, 0, color.brighter(), 20, 10, color.darker());
			else
			{
				Color temp = category.getColor();
				int average = (temp.getRed() + temp.getGreen() + temp.getBlue()) / 3;
				color = new Color(average, average, average);
				paint = new GradientPaint(0, 0, color.brighter().brighter(), 20, 10, color);
			}

			g.setPaint(paint);
			g.fillRect(0, 0, 20, 10);
			g.setColor(Color.black);
			g.drawRect(0, 0, 20, 10);
			g.dispose();

			setText("");
			setIcon(new ImageIcon(image));

			return this;
		}

		@Override
		public Insets getInsets(Insets i)
			{ return new Insets(0, 3, 0, 0); }
	}

	class RightRenderer extends DefaultTableCellRenderer
	{
		// Set the attributes of the class and return a reference
		@Override
		public Component getTableCellRendererComponent(JTable table, Object value,
			boolean isSelected, boolean hasFocus, int row, int column)
		{
			super.getTableCellRendererComponent(table, value, isSelected,
				hasFocus, row, column);

			setHorizontalAlignment(SwingConstants.RIGHT);

			return this;
		}
	}

	TableCellRenderer getCellRenderer(int col)
	{
		switch (col)
		{
			case 1: return new ColorListRenderer();
			case 3: return new RightRenderer();
			default: return null;
		}
	}
}