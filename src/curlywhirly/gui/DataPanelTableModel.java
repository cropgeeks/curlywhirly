// Copyright 2009-2014 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package curlywhirly.gui;

import java.awt.*;
import java.awt.image.*;
import java.util.*;
import javax.swing.*;
import javax.swing.table.*;

import curlywhirly.data.*;

import scri.commons.gui.*;

public class DataPanelTableModel extends AbstractTableModel
{
	private final String[] columnNames;
	private final ArrayList<DataPoint> dataPoints;
	private final DataSet dataSet;

	DataPanelTableModel(DataSet dataSet)
	{
		this.dataSet = dataSet;

		dataPoints = dataSet.getDataPoints();

		// Retrieve the axis labels for the current x, y and z axes so that they
		// can be displayed in the table header.
		String[] axisLabels = dataSet.getCurrentAxisLabels();

		columnNames = new String[] { "Colour",
									RB.getString("gui.DataPanelTableModel.headers.name"),
									RB.format("gui.DataPanelTableModel.headers.col1", axisLabels[0]),
									RB.format("gui.DataPanelTableModel.headers.col2", axisLabels[1]),
									RB.format("gui.DataPanelTableModel.headers.col3", axisLabels[2]) };
	}

	@Override
	public int getRowCount()
	{
		return dataPoints.size();
	}

	@Override
	public String getColumnName(int column)
	{
		return columnNames[column];
	}

	@Override
	public Class<?> getColumnClass(int column)
	{
		switch (column)
		{
			case 0: return Color.class;
			case 1: return DataPoint.class;
			case 2: return String.class;
			case 3: return String.class;
			case 4: return String.class;

			default: return null;
		}
	}

	public String[] getColumnNames()
	{
		return columnNames;
	}

	@Override
	public int getColumnCount()
	{
		return columnNames.length;
	}

//	@Override
//	public boolean isCellEditable(int rowIndex, int columnIndex)
//	{
//		switch (columnIndex)
//		{
//			case 0: return true;
//
//			default: return false;
//		}
//	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex)
	{
		DataPoint point = dataPoints.get(rowIndex);

		// Find out what the currently selected data for each axis is so that we
		// can pull out the correct data for display in the table.
		switch (columnIndex)
		{
			case 0: return point.getColor(dataSet.getCurrentCategoryGroup());
			case 1: return point;
			case 2: return point.getValues().get(dataSet.getCurrX());
			case 3: return point.getValues().get(dataSet.getCurrY());
			case 4: return point.getValues().get(dataSet.getCurrZ());
		}

		return null;
	}

//	@Override
//	public void setValueAt(Object value, int rowIndex, int columnIndex)
//	{
//		DataPoint point = (DataPoint) getValueAt(rowIndex, 2);
//
//		switch (columnIndex)
//		{
//			case 0: point.setSelected((boolean) value);
//					fireTableDataChanged();
//					break;
//		}
//	}

	public int selectedPointsCount()
	{
		int count = 0;
		for (DataPoint dataPoint : dataPoints)
			if (dataPoint.isSelected())
				count++;

		return count;
	}

	static class ColorListRenderer extends DefaultTableCellRenderer
	{
		// Set the attributes of the class and return a reference
		@Override
		public Component getTableCellRendererComponent(JTable table, Object value,
			boolean isSelected, boolean hasFocus, int row, int column)
		{
			super.getTableCellRendererComponent(table, value, isSelected,
				hasFocus, row, column);

			Color color = (Color) value;

			// Set the icon
			BufferedImage image = new BufferedImage(20, 10, BufferedImage.TYPE_INT_RGB);
			Graphics2D g = image.createGraphics();

			GradientPaint paint;
			if (((DataPoint)table.getValueAt(row, 1)).isSelected())
				paint = new GradientPaint(0, 0, color.brighter(), 20, 10, color.darker());
			else
			{
				int average = (color.getRed() + color.getGreen() + color.getBlue()) / 3;
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

	static class PointSelectedRenderer extends DefaultTableCellRenderer
	{
		// Set the attributes of the class and return a reference
		@Override
		public Component getTableCellRendererComponent(JTable table, Object value,
			boolean isSelected, boolean hasFocus, int row, int column)
		{
			super.getTableCellRendererComponent(table, value, isSelected,
				hasFocus, row, column);

			DataPoint point = (DataPoint) table.getValueAt(row, 1);

			if (point.isSelected())
				setForeground(UIManager.getColor("Label.foreground"));
			else
				setForeground(Color.GRAY);

			return this;
		}
	}

	static TableCellRenderer getCellRenderer(int col)
	{
		switch (col)
		{
			case 0: return new ColorListRenderer();
			default: return new PointSelectedRenderer();
		}
	}
}