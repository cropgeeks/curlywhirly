// Copyright 2009-2022 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package jhi.curlywhirly.gui;

import jhi.curlywhirly.data.*;
import scri.commons.gui.*;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.image.*;

public class CategoryTableModel extends AbstractTableModel
{
	private final CategoryGroup group;
	private final DataSet dataSet;

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
	public Class<?> getColumnClass(int column)
	{
		switch (column)
		{
			case 0:
				return Integer.class;
			case 1:
				return Color.class;
			case 2:
				return Category.class;
			case 3:
				return String.class;

			default:
				return null;
		}
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex)
	{
		if (rowIndex < 0)
			return null;

		Category category = group.get(rowIndex);

		int state = calculateCategoryState(category);

		switch (columnIndex)
		{
			case 0:
				return state;
			case 1:
				return category;
			case 2:
				return category;
			case 3:
				return category.getSelectedText();

			default:
				return null;
		}
	}

	@Override
	public void setValueAt(Object value, int rowIndex, int columnIndex)
	{
		Category category = group.get(rowIndex);

		switch (columnIndex)
		{
			case 0:
				toggleThreeStateCheckBox(category);
				break;
		}
	}

	private void toggleThreeStateCheckBox(Category cat)
	{
		int state = calculateCategoryState(cat);

		switch (state)
		{
			case 1:
				cat.setSelected(false);
				break;
			case 2:
				cat.setSelected(false);
				break;
			case 3:
				cat.setSelected(true);
				break;
		}

		fireTableDataChanged();
	}

	private int calculateCategoryState(Category cat)
	{
		int situation = 1;
		if (cat.getSelectedCount() != cat.getTotal() && cat.getSelectedCount() > 0)
			situation = 2;
		else if (cat.getSelectedCount() == 0)
			situation = 3;

		return situation;
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
			if (dataSet.getCurrentCategoryGroup().getCategories().contains(category) && category.getSelectedCount() > 0)
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
		{
			return new Insets(0, 3, 0, 0);
		}
	}

	static class RightRenderer extends DefaultTableCellRenderer
	{
		// Set the attributes of the class and return a reference
		@Override
		public Component getTableCellRendererComponent(JTable table, Object value,
													   boolean isSelected, boolean hasFocus, int row, int column)
		{
			super.getTableCellRendererComponent(table, value, isSelected,
				hasFocus, row, column);

			setHorizontalAlignment(SwingConstants.RIGHT);

			if ((((String) value).charAt(0)) == '0')
				setForeground(Color.GRAY);
			else
				setForeground(UIManager.getColor("Label.foreground"));

			return this;
		}
	}

	static class CategoryRenderer extends DefaultTableCellRenderer
	{
		// Set the attributes of the class and return a reference
		@Override
		public Component getTableCellRendererComponent(JTable table, Object value,
													   boolean isSelected, boolean hasFocus, int row, int column)
		{
			super.getTableCellRendererComponent(table, value, isSelected,
				hasFocus, row, column);

			if (((Category) value).getSelectedCount() == 0)
				setForeground(Color.GRAY);
			else
				setForeground(UIManager.getColor("Label.foreground"));

			return this;
		}
	}

	static class TickRenderer extends DefaultTableCellRenderer
	{
		private static final ImageIcon CHECK_ON = Icons.getIcon("CHECK-ON");
		private static final ImageIcon CHECK_MID = Icons.getIcon("CHECK-MID");
		private static final ImageIcon CHECK_OFF = Icons.getIcon("CHECK-OFF");

		// Set the attributes of the class and return a reference
		@Override
		public Component getTableCellRendererComponent(JTable table, Object value,
													   boolean isSelected, boolean hasFocus, int row, int column)
		{
			super.getTableCellRendererComponent(table, value, isSelected,
				hasFocus, row, column);

			setText("");

			switch ((Integer) value)
			{
				case 1:
					setIcon(CHECK_ON);
					break;
				case 2:
					setIcon(CHECK_MID);
					break;
				case 3:
					setIcon(CHECK_OFF);
					break;
			}

			return this;
		}
	}

	TableCellRenderer getCellRenderer(int col)
	{
		switch (col)
		{
			case 0:
				return new TickRenderer();
			case 1:
				return new ColorListRenderer();
			case 2:
				return new CategoryRenderer();
			case 3:
				return new RightRenderer();
			default:
				return null;
		}
	}
}