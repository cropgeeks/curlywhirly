// Copyright 2009-2017 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package jhi.curlywhirly.gui.dialog;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.*;
import java.awt.event.*;
import java.util.*;
import java.util.stream.*;

import jhi.curlywhirly.gui.*;

import scri.commons.gui.*;
import scri.commons.gui.matisse.*;

public class DuplicateDataPointsDialog extends JDialog implements ActionListener
{
	private JButton bClose;
	private JButton bClipboard;

	private DuplicateDataPointsPanelNB nbPanel;

	private ArrayList<String> duplicates;

	public DuplicateDataPointsDialog(ArrayList<String> duplicates)
	{
		super(
			CurlyWhirly.winMain,
			RB.getString("gui.dialog.DuplicateDataPointsDialog.title"),
			true
		);

		this.duplicates = duplicates;
		nbPanel = new DuplicateDataPointsPanelNB(duplicates);

		add(nbPanel);
		add(createButtons(), BorderLayout.SOUTH);

		getRootPane().setDefaultButton(bClose);
		SwingUtils.addCloseHandler(this, bClose);

		pack();
		setLocationRelativeTo(CurlyWhirly.winMain);
		setResizable(false);

		bClose.requestFocusInWindow();
		setVisible(true);
	}

	private JPanel createButtons()
	{
		bClose = new JButton(RB.getString("gui.text.close"));
		bClose.addActionListener(this);

		bClipboard = new JButton(RB.getString("gui.dialog.DuplicateDataPointsDialog.clipboard"));
		bClipboard.addActionListener(this);
		RB.setMnemonic(bClipboard, "gui.dialog.DuplicateDataPointsDialog.clipboard");

		JPanel p1 = new DialogPanel();
		p1.add(bClipboard);
		p1.add(bClose);

		return p1;
	}

	public void actionPerformed(ActionEvent e)
	{
		if (e.getSource() == bClose)
			setVisible(false);

		else if (e.getSource() == bClipboard)
		{
			String clipboard = duplicates.stream().collect(Collectors.joining(System.getProperty("line.separator")));
			StringSelection selection = new StringSelection(clipboard);
			Toolkit.getDefaultToolkit().getSystemClipboard().setContents(
				selection, null);
		}
	}
}