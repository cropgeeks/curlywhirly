// Copyright 2009-2014 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

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
		model = new DataPanelTableModel(dataSet);
		controls.pointsTable.setModel(model);

		sorter = new TableRowSorter<DataPanelTableModel>(model);
		controls.pointsTable.setRowSorter(sorter);
		sorter.setComparator(0, model.getComparator());
		// Need to toggle sort order twice to get a descending list with
		// selected points at the top, ordered by colour
		controls.pointsTable.getRowSorter().toggleSortOrder(0);
		controls.pointsTable.getRowSorter().toggleSortOrder(0);

		controls.lblPoints.setText(getTitle());

		controls.pointsTable.getColumnModel().getColumn(0).setPreferredWidth(20);
	}

	String getTitle()
	{
		int selectedPoints = model.selectedPointsCount();
		return selectedPoints != model.getRowCount() ?
			RB.format("gui.DataPanel.labelFiltered", selectedPoints, model.getRowCount()) :
			RB.format("gui.DataPanel.label", selectedPoints);
	}

	public void setDataSet(DataSet dataSet)
	{
		this.dataSet = dataSet;

		// Reset the row filter to prevent crashes
		controls.clearFilter();

		if (dataSet == null)
		{
			// Blank out the table data and selected points count
			controls.pointsTable.setModel(new DefaultTableModel());
			controls.pointsTable.setRowSorter(null);
			controls.lblPoints.setText(RB.format("gui.DataPanel.label", 0));
		}
		else
			updateTableModel();

		toggleEnabled(dataSet != null);
	}

	private void saveReadsSummary(boolean onlyHighlighted)
	{
		// Loop over the table pulling out the data points to hand off to the
		// DataPointSaver
		ArrayList<DataPoint> dataPoints = new ArrayList<>();
		for (int i=0; i < controls.pointsTable.getRowCount(); i++)
		{
			int row = controls.pointsTable.convertRowIndexToModel(i);
			DataPoint point = (DataPoint) model.getValueAt(row, 1);
			if (onlyHighlighted && point.isSelected() || !onlyHighlighted)
				dataPoints.add(point);
		}

		saveSummary(dataPoints);
	}

	private void saveSummary(ArrayList<DataPoint> dataPoints)
	{
		File saveAs = new File(Prefs.guiCurrentDir, "tablesummary.txt");

		FileNameExtensionFilter filter = new FileNameExtensionFilter(
			RB.getString("gui.text.formats.txt"), "txt");

		// Ask the user for a filename to save the data to
		String filename = CWUtils.getSaveFilename(
			RB.getString("gui.DataPanel.saveDataPoints.saveDialog"), saveAs, filter);

		// Quit if the user cancelled the file selection
		if (filename == null)
			return;

		saveAs = new File(filename);

		DataPointSaver summary = new DataPointSaver(saveAs, dataPoints,
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
		}

		TaskDialog.showFileOpen(RB.getString("gui.DataPanel.saveDataPoints.openFile"), TaskDialog.INF, saveAs);
	}

	private void displayMenu(MouseEvent e)
	{
		int row = controls.pointsTable.rowAtPoint(e.getPoint());
		controls.pointsTable.setRowSelectionInterval(row, row);

		JMenuItem mSaveReads = new JMenuItem("", Icons.getIcon("FILESAVE16"));
		RB.setText(mSaveReads, "gui.DataPanel.mTableSave");
		mSaveReads.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				saveReadsSummary(false);
			}
		});

		JMenuItem mSaveHighlightedReads = new JMenuItem("", Icons.getIcon("FILESAVE16"));
		RB.setText(mSaveHighlightedReads, "gui.DataPanel.mTableSaveHighlighted");
		mSaveHighlightedReads.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				saveReadsSummary(true);
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
		menu.add(mSaveHighlightedReads);
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

	private void toggleEnabled(boolean enabled)
	{
		controls.toggleEnabled(enabled);
	}

	void setTableFilter(RowFilter<DataPanelTableModel, Object> rf)
	{
		sorter.setRowFilter(rf);
//		controls.contigsLabel.setText(getTitle(controls.table.getRowCount()));
	}
}