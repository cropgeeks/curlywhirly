package curlywhirly.gui;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.table.*;

import curlywhirly.data.*;
import scri.commons.gui.RB;

public class DataPanel extends JPanel implements ActionListener
{
	private DataPanelNB controls;
	private DataPanelTableModel model;

	private DataSet dataSet;

	private TableRowSorter<DataPanelTableModel> sorter;

	DataPanel()
	{
		setLayout(new BorderLayout());
		controls = new DataPanelNB(this);
		add(controls);

		controls.pointsTable.setModel(new DefaultTableModel());
	}

	void updateTableModel()
	{
		model = new DataPanelTableModel(dataSet, dataSet.getCurrentCategoryGroup());
		controls.pointsTable.setModel(model);

		sorter = new TableRowSorter<DataPanelTableModel>(model);
		controls.pointsTable.setRowSorter(sorter);

		controls.lblPoints.setText(RB.format("gui.DataPanel.label", model.getRowCount()));
	}

	@Override
	public void actionPerformed(ActionEvent e)
	{
	}

	public void setDataSet(DataSet dataSet)
	{
		this.dataSet = dataSet;
	}
}
