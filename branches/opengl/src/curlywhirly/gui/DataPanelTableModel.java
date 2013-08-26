package curlywhirly.gui;

import java.util.*;
import javax.swing.table.*;

import curlywhirly.data.*;

import scri.commons.gui.*;

public class DataPanelTableModel extends AbstractTableModel
{
	private String[] columnNames;
	private ArrayList<DataEntry> dataEntries;
	private DataSet dataSet;

	DataPanelTableModel(DataSet dataSet, CategoryGroup categoryGroup)
	{
		this.dataSet = dataSet;

		dataEntries = new ArrayList<DataEntry>();
		int categorySchemeIndex =  dataSet.categoryGroups.indexOf(categoryGroup);
		for (DataEntry dataEntry : dataSet.dataEntries)
		{
			Category category = dataEntry.categories.get(categorySchemeIndex);

			if (category != null && dataEntry.isSelected())
				dataEntries.add(dataEntry);
		}

		columnNames = new String[] { RB.getString("gui.DataEntryTableModel.headers.name"),
									RB.getString("gui.DataEntryTableModel.headers.col1"),
									RB.getString("gui.DataEntryTableModel.headers.col2"),
									RB.getString("gui.DataEntryTableModel.headers.col3") };
	}

	@Override
	public int getRowCount()
	{
		return dataEntries.size();
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
		DataEntry entry = dataEntries.get(rowIndex);

		// Find out what the currently selected data for each axis is so that we
		// can pull out the correct data for display in the table.
		switch (columnIndex)
		{
			case 0: return entry.label;
			case 1: return entry.dataValues.get(dataSet.getCurrX());
			case 2: return entry.dataValues.get(dataSet.getCurrY());
			case 3: return entry.dataValues.get(dataSet.getCurrZ());
		}

		return null;
	}
}