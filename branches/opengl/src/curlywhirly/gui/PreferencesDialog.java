// Copyright 2009-2012 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package curlywhirly.gui;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import scri.commons.gui.*;

public class PreferencesDialog extends JDialog implements ActionListener
{
	private JButton bOK, bCancel, bHelp;
	private boolean isOK;

	private PreferencesPanelNB nbPanel;

	public PreferencesDialog()
	{
		super(CurlyWhirly.winMain, RB.getString("gui.PreferencesDialog.title"), true);

		nbPanel = new PreferencesPanelNB(CurlyWhirly.winMain);

		add(nbPanel);
		add(createButtons(), BorderLayout.SOUTH);

		getRootPane().setDefaultButton(bOK);
		SwingUtils.addCloseHandler(this, bCancel);

		pack();
		setLocationRelativeTo(CurlyWhirly.winMain);
		setResizable(false);
		setVisible(true);
	}

	private JPanel createButtons()
	{
		bOK = SwingUtils.getButton(RB.getString("gui.text.ok"));
		bOK.addActionListener(this);
		bCancel = SwingUtils.getButton(RB.getString("gui.text.cancel"));
		bCancel.addActionListener(this);
		bHelp = SwingUtils.getButton(RB.getString("gui.text.help"));
		RB.setText(bHelp, "gui.text.help");
//		TabletUtils.setHelp(bHelp, "gui.dialog.prefs.PreferencesDialog");

		JPanel p1 = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0));
		p1.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 5));
		p1.add(bOK);
		p1.add(bCancel);
		p1.add(bHelp);

		return p1;
	}

	public void actionPerformed(ActionEvent e)
	{
		if (e.getSource() == bOK)
		{
			nbPanel.applySettings();
			isOK = true;
		}

		setVisible(false);
	}

	public boolean isOK()
		{ return isOK; }
}