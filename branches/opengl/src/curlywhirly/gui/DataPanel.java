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
	private AbstractTableModel model;

	private DataSet dataSet;

	DataPanel()
	{
		setLayout(new BorderLayout());
		add(controls = new DataPanelNB(this));

		model = new DefaultTableModel();

		controls.pointsTable.setModel(model);
	}

	void updateTableModel()
	{
		model = new DataPanelTableModel(dataSet, dataSet.getCurrentCategoryGroup());
		controls.pointsTable.setModel(model);
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
