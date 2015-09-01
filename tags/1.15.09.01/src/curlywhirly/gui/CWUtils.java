// Copyright 2009-2015 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package curlywhirly.gui;

import java.awt.*;
import java.awt.datatransfer.*;
import javax.swing.*;
import javax.swing.table.*;

public class CWUtils
{
	public static void copyTableToClipboard(JTable table, AbstractTableModel model)
	{
		StringBuilder text = new StringBuilder();
		String newline = System.getProperty("line.separator");

		// Column headers
		for (int c = 0; c < model.getColumnCount(); c++)
		{
			text.append(model.getColumnName(c));
			text.append(c < model.getColumnCount()-1 ? "\t" : newline);
		}

		// Each row
		for (int r = 0; r < table.getRowCount(); r++)
		{
			int row = table.convertRowIndexToModel(r);

			for (int c = 0; c < model.getColumnCount(); c++)
			{
				text.append(model.getValueAt(row, c));
				text.append(c < model.getColumnCount()-1 ? "\t" : newline);
			}
		}

		StringSelection selection = new StringSelection(text.toString());
		Toolkit.getDefaultToolkit().getSystemClipboard().setContents(
			selection, null);
	}
}