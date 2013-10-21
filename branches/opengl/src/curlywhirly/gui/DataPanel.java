package curlywhirly.gui;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.filechooser.*;
import javax.swing.table.*;

import curlywhirly.analysis.*;
import curlywhirly.data.*;

import scri.commons.gui.*;

public class DataPanel extends JPanel
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
		controls.pointsTable.addMouseListener(new TableMouseListener());

		toggleEnabled(false);
	}

	void updateTableModel()
	{
		model = new DataPanelTableModel(dataSet, dataSet.getCurrentCategoryGroup());
		controls.pointsTable.setModel(model);

		sorter = new TableRowSorter<DataPanelTableModel>(model);
		controls.pointsTable.setRowSorter(sorter);

		controls.lblPoints.setText(RB.format("gui.DataPanel.label", model.getRowCount()));
	}

	public void setDataSet(DataSet dataSet)
	{
		this.dataSet = dataSet;
	}

	private void saveReadsSummary()
	{
		// Loop over the table pulling out the data points to hand off to the
		// DataPointSaver
		ArrayList<DataPoint> dataPoints = new ArrayList<>();
		for (int i=0; i < controls.pointsTable.getRowCount(); i++)
		{
			int row = controls.pointsTable.convertRowIndexToModel(i);
			DataPoint point = (DataPoint) model.getValueAt(row, 0);
			dataPoints.add(point);
		}

		File saveAs = new File(Prefs.guiCurrentDir, "tablesummary.txt");

		FileNameExtensionFilter filter = new FileNameExtensionFilter(
			RB.getString("gui.text.formats.txt"), "txt");

		// Ask the user for a filename to save the data to
		String filename = CWUtils.getSaveFilename(
			RB.getString("gui.DataPanel.saveDataPoints.saveDialog"), saveAs, filter);

		// Quit if the user cancelled the file selection
		if (filename == null)
			return;

		DataPointSaver summary = new DataPointSaver(new File(filename), dataPoints,
			dataSet.getCurrentAxes(), dataSet.getCurrentAxisLabels());

		ProgressDialog dialog = new ProgressDialog(summary,
			RB.getString("gui.DataPanel.saveDataPoints.title"),
			RB.getString("gui.DataPanel.saveDataPoints.label"),
			CurlyWhirly.winMain);

		if (dialog.getResult() != ProgressDialog.JOB_COMPLETED)
		{
			if (dialog.getResult() == ProgressDialog.JOB_FAILED)
			{
				dialog.getException().printStackTrace();

				TaskDialog.showOpenLog(RB.format("gui.DataPanel.saveDataPoints.exception",
					dialog.getException()), null);
			}

			return;
		}
	}

	private void displayMenu(MouseEvent e)
	{
		int row = controls.pointsTable.rowAtPoint(e.getPoint());
		controls.pointsTable.setRowSelectionInterval(row, row);

		JMenuItem mSaveReads = new JMenuItem("", Icons.getIcon("FILESAVE16"));
		RB.setText(mSaveReads, "gui.DataPanel.mTableSave");
		mSaveReads.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				saveReadsSummary();
			}
		});

		JMenuItem mTableCopy = new JMenuItem("", Icons.getIcon("CLIPBOARD"));
		RB.setText(mTableCopy, "gui.DataPanel.mTableCopy");
		mTableCopy.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				CWUtils.copyTableToClipboard(controls.pointsTable, model);
			}
		});


		JPopupMenu menu = new JPopupMenu();
		menu.add(mSaveReads);
		menu.addSeparator();
		menu.add(mTableCopy);
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
	}

	void toggleEnabled(boolean enabled)
	{
		controls.toggleEnabled(enabled);
	}
}
