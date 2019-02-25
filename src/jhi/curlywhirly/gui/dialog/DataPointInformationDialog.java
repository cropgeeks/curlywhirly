// Copyright 2009-2017 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package jhi.curlywhirly.gui.dialog;

import jhi.curlywhirly.data.*;
import jhi.curlywhirly.gui.*;
import scri.commons.gui.*;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.datatransfer.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;

public class DataPointInformationDialog extends JDialog implements ActionListener
{
	private final ValueTableModel valuesModel;
	private final CategoryTableModel categoriesModel;

	private final DataSet dataSet;
	private final DataPoint point;

	public DataPointInformationDialog(DataSet dataSet, DataPoint point)
	{
		super(
			CurlyWhirly.winMain,
			RB.format("gui.dialog.DataPointInformationDialog.title", point.getName()),
			true
		);

		this.dataSet = dataSet;
		this.point = point;

		// As we want to set a tablecellrenderer on a column of the valueTable
		// we setup the model here and use custom creation code to set up
		// the table with the model and the cellrenderer
		valuesModel = new ValueTableModel(dataSet.getAxes().getAxisLabels(), point);

		// Tables created here
		initComponents();
		initComponents2();
		getContentPane().setBackground(Color.WHITE);

		valuesTable.setModel(valuesModel);

		categoriesModel = new CategoryTableModel(dataSet.getCategoryGroups(), point, dataSet);
		categoriesTable.setModel(categoriesModel);

		nameLabel.setText(RB.format("gui.dialog.DataPointInformationDialog.nameLabel", point.getName()));
		RB.setText(categoriesLabel, "gui.dialog.DataPointInformationDialog.categoryLabel");
		RB.setText(valuesLabel, "gui.dialog.DataPointInformationDialog.valueLabel");

		RB.setText(dataPointLinkLabel, "gui.dialog.DataPointInformationDialog.linkLabel");
		dataPointLinkLabel.setIcon(Icons.getIcon("WEB"));
		dataPointLinkLabel.setEnabled(dataSet.getDbAssociation().isPointSearchEnabled());
		dataPointLinkLabel.addActionListener(this);

		getRootPane().setDefaultButton(bClose);
		SwingUtils.addCloseHandler(this, bClose);

		pack();
		setLocationRelativeTo(CurlyWhirly.winMain);
		setResizable(false);
		setVisible(true);
	}

	private void initComponents2()
	{
		RB.setText(bClose, "gui.text.close");
		bClose.addActionListener(this);

		RB.setText(bCopy, "gui.dialog.DataPointInformationDialog.bCopy");
		bCopy.addActionListener(this);
	}

	@Override
	public void actionPerformed(ActionEvent e)
	{
		if (e.getSource() == bClose)
			setVisible(false);

		else if (e.getSource() == bCopy)
			copyToClipboard();

		else if (e.getSource() == dataPointLinkLabel)
		{
			try
			{
				dataSet.getDbAssociation().visitUrlForPoint(point.getName());
			}
			catch (UnsupportedEncodingException ex)
			{
				ex.printStackTrace();
			}
		}
	}

	private void copyToClipboard()
	{
		StringBuilder text = new StringBuilder();
		String newline = System.getProperty("line.separator");

		text.append(nameLabel.getText()).append(newline).append(newline);

		String valuesTableString = copyTableData(valuesTable, valuesModel);
		text.append(valuesTableString).append(newline);

		String categoriesTableString = copyTableData(categoriesTable, categoriesModel);
		text.append(categoriesTableString);

		StringSelection selection = new StringSelection(text.toString());
		Toolkit.getDefaultToolkit().getSystemClipboard().setContents(
			selection, null);
	}

	private String copyTableData(JTable table, AbstractTableModel model)
	{
		StringBuilder builder = new StringBuilder();
		String newline = System.getProperty("line.separator");

		// Column headers
		for (int c = 0; c < model.getColumnCount(); c++)
		{
			builder.append(model.getColumnName(c));
			builder.append(c < model.getColumnCount() - 1 ? "\t" : newline);
		}

		// Each row
		for (int r = 0; r < table.getRowCount(); r++)
		{
			int row = table.convertRowIndexToModel(r);

			for (int c = 0; c < model.getColumnCount(); c++)
			{
				builder.append(model.getValueAt(row, c));
				builder.append(c < model.getColumnCount() - 1 ? "\t" : newline);
			}
		}

		return builder.toString();
	}

	private JTable createValueTable()
	{
		// Create a new table with the given table model and the cellrenderer
		// which can be found in that table model.
		final JTable table = new JTable()
		{
			@Override
			public TableCellRenderer getCellRenderer(int row, int col)
			{
				TableCellRenderer tcr = valuesModel.getCellRenderer(col);
				return (tcr != null) ? tcr : super.getCellRenderer(row, col);
			}
		};

		return table;
	}

	// Table model for the value table, displays the coordinates and coordinate
	// values for the current point
	static class ValueTableModel extends AbstractTableModel
	{
		private final String[] axisLabels;
		private final DataPoint point;

		private final String[] columnNames;

		ValueTableModel(String[] axisLabels, DataPoint point)
		{
			this.axisLabels = axisLabels;
			this.point = point;

			columnNames = new String[]{RB.getString("gui.dialog.DataPointInformationDialog.ValueTableModel.col1"),
				RB.getString("gui.dialog.DataPointInformationDialog.ValueTableModel.col2")};
		}

		@Override
		public String getColumnName(int column)
		{
			return columnNames[column];
		}

		@Override
		public int getRowCount()
		{
			return point.getValues().size();
		}

		@Override
		public int getColumnCount()
		{
			return columnNames.length;
		}

		@Override
		public Object getValueAt(int rowIndex, int columnIndex)
		{
			if (columnIndex == 0)
				return axisLabels[rowIndex];

			else
				return point.getValues().get(rowIndex);
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

				return this;
			}
		}

		TableCellRenderer getCellRenderer(int col)
		{
			switch (col)
			{
				case 1:
					return new RightRenderer();
				default:
					return null;
			}
		}
	}

	// The table model for the point's categories. Displays the category groups
	// associated with the point and their category values
	static class CategoryTableModel extends AbstractTableModel
	{
		private final ArrayList<CategoryGroup> categoryGroups;
		private final DataPoint point;
		private final DataSet dataSet;

		private final String[] columnNames;

		CategoryTableModel(ArrayList<CategoryGroup> categoryGroups, DataPoint point, DataSet dataSet)
		{
			this.categoryGroups = categoryGroups;
			this.point = point;
			this.dataSet = dataSet;

			columnNames = new String[]{RB.getString("gui.dialog.DataPointInformationDialog.CategoryTableModel.col1"),
				RB.getString("gui.dialog.DataPointInformationDialog.CategoryTableModel.col2")};
		}

		@Override
		public String getColumnName(int column)
		{
			return columnNames[column];
		}

		@Override
		public int getRowCount()
		{
			return categoryGroups.size();
		}

		@Override
		public int getColumnCount()
		{
			return columnNames.length;
		}

		@Override
		public Object getValueAt(int rowIndex, int columnIndex)
		{
			if (columnIndex == 0)
				return categoryGroups.get(rowIndex);

			else
				return categoryGroups.get(rowIndex).getCategoryForPoint(point);
		}
	}

	/**
	 * This method is called from within the constructor to
	 * initialize the form.
	 * WARNING: Do NOT modify this code. The content of this method is
	 * always regenerated by the Form Editor.
	 */
	@SuppressWarnings("unchecked")
	// <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
	private void initComponents()
	{

		dialogPanel1 = new scri.commons.gui.matisse.DialogPanel();
		bCopy = new javax.swing.JButton();
		bClose = new javax.swing.JButton();
		nameLabel = new javax.swing.JLabel();
		valuesLabel = new javax.swing.JLabel();
		jScrollPane1 = new javax.swing.JScrollPane();
		valuesTable = createValueTable();
		categoriesLabel = new javax.swing.JLabel();
		jScrollPane2 = new javax.swing.JScrollPane();
		categoriesTable = new javax.swing.JTable();
		dataPointLinkLabel = new scri.commons.gui.matisse.HyperLinkLabel();

		setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

		bCopy.setText("Copy to clipboard");
		dialogPanel1.add(bCopy);

		bClose.setText("Close");
		dialogPanel1.add(bClose);

		nameLabel.setText("Name:");

		valuesLabel.setText("Values:");

		valuesTable.setModel(new javax.swing.table.DefaultTableModel(
			new Object[][]
				{

				},
			new String[]
				{

				}
		));
		jScrollPane1.setViewportView(valuesTable);

		categoriesLabel.setText("Categories:");

		categoriesTable.setModel(new javax.swing.table.DefaultTableModel(
			new Object[][]
				{

				},
			new String[]
				{

				}
		));
		jScrollPane2.setViewportView(categoriesTable);

		dataPointLinkLabel.setForeground(new java.awt.Color(68, 106, 156));
		dataPointLinkLabel.setText("More information...");

		javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
		getContentPane().setLayout(layout);
		layout.setHorizontalGroup(
			layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addComponent(dialogPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
				.addGroup(layout.createSequentialGroup()
					.addContainerGap()
					.addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
						.addComponent(jScrollPane2)
						.addComponent(jScrollPane1)
						.addGroup(layout.createSequentialGroup()
							.addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
								.addComponent(nameLabel)
								.addComponent(valuesLabel)
								.addComponent(categoriesLabel)
								.addComponent(dataPointLinkLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
							.addGap(0, 0, Short.MAX_VALUE)))
					.addContainerGap())
		);
		layout.setVerticalGroup(
			layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
					.addContainerGap()
					.addComponent(nameLabel)
					.addGap(18, 18, 18)
					.addComponent(valuesLabel)
					.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
					.addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
					.addGap(18, 18, 18)
					.addComponent(categoriesLabel)
					.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
					.addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
					.addGap(18, 18, 18)
					.addComponent(dataPointLinkLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
					.addComponent(dialogPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
		);

		pack();
	}// </editor-fold>//GEN-END:initComponents


	// Variables declaration - do not modify//GEN-BEGIN:variables
	private javax.swing.JButton bClose;
	private javax.swing.JButton bCopy;
	private javax.swing.JLabel categoriesLabel;
	private javax.swing.JTable categoriesTable;
	private scri.commons.gui.matisse.HyperLinkLabel dataPointLinkLabel;
	private scri.commons.gui.matisse.DialogPanel dialogPanel1;
	private javax.swing.JScrollPane jScrollPane1;
	private javax.swing.JScrollPane jScrollPane2;
	private javax.swing.JLabel nameLabel;
	private javax.swing.JLabel valuesLabel;
	private javax.swing.JTable valuesTable;
	// End of variables declaration//GEN-END:variables

}