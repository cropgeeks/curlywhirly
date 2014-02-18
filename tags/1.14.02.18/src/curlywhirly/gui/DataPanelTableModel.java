// Copyright 2009-2014 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package curlywhirly.gui;

import java.util.*;
import javax.swing.table.*;

import curlywhirly.data.*;

import scri.commons.gui.*;

public class DataPanelTableModel extends AbstractTableModel
{
	private String[] columnNames;
	private ArrayList<DataPoint> dataPoints;
	private DataSet dataSet;

	DataPanelTableModel(DataSet dataSet, CategoryGroup categoryGroup)
	{
		this.dataSet = dataSet;

		dataPoints = new ArrayList<DataPoint>();
		for (DataPoint dataPoint : dataSet)
		{
			Category category = dataPoint.getCategoryForGroup(categoryGroup);

			if (category != null && dataPoint.isSelected())
				dataPoints.add(dataPoint);
		}

		// Retrieve the axis labels for the current x, y and z axes so that they
		// can be displayed in the table header.
		String[] axisLabels = dataSet.getCurrentAxisLabels();

		columnNames = new String[] { RB.getString("gui.DataEntryTableModel.headers.name"),
									RB.format("gui.DataEntryTableModel.headers.col1", axisLabels[0]),
									RB.format("gui.DataEntryTableModel.headers.col2", axisLabels[1]),
									RB.format("gui.DataEntryTableModel.headers.col3", axisLabels[2]) };
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

	public String[] getColumnNames()
	{
		return columnNames;
	}

	@Override
	public int getColumnCount()
	{
		return columnNames.length;
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex)
	{
		DataPoint point = dataPoints.get(rowIndex);

		// Find out what the currently selected data for each axis is so that we
		// can pull out the correct data for display in the table.
		switch (columnIndex)
		{
			case 0: return point;
			case 1: return point.getValues().get(dataSet.getCurrX());
			case 2: return point.getValues().get(dataSet.getCurrY());
			case 3: return point.getValues().get(dataSet.getCurrZ());
		}

		return null;
	}
}