// Copyright 2009-2012 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package curlywhirly.gui.dialog;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import curlywhirly.gui.*;

import scri.commons.gui.*;

public class PreferencesDialog extends JDialog implements ActionListener
{
	private JButton bOK, bCancel, bHelp;
	private boolean isOK;

	private final JTabbedPane tabs;

	private final PreferencesPanelNB nbPanel;
	private final WarningTabNB nbWarning;

	public PreferencesDialog(WinMain winMain)
	{
		super(winMain, RB.getString("gui.PreferencesDialog.title"), true);

		nbPanel = new PreferencesPanelNB(winMain, this);
		nbWarning = new WarningTabNB();

		tabs = new JTabbedPane();
		tabs.setBorder(BorderFactory.createEmptyBorder(2, 2, 10, 2));
		tabs.addTab(RB.getString("gui.dialog.PreferencesDialog.generalTab"),
		Icons.getIcon("GENERALTAB"), nbPanel);
		tabs.addTab(RB.getString("gui.dialog.PreferencesDialog.warningTab"),
		Icons.getIcon("WARNINGSTAB"), nbWarning);

		add(tabs);
		add(createButtons(), BorderLayout.SOUTH);

		getRootPane().setDefaultButton(bOK);
		SwingUtils.addCloseHandler(this, bCancel);

		pack();
		setLocationRelativeTo(winMain);
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

		JPanel p1 = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0));
		p1.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 5));
		p1.add(bOK);
		p1.add(bCancel);
//		p1.add(bHelp);

		return p1;
	}

	@Override
	public void actionPerformed(ActionEvent e)
	{
		if (e.getSource() == bOK)
		{
			nbPanel.applySettings();
			nbWarning.applySettings();
			isOK = true;
			setVisible(false);
		}

		else if(e.getSource() == nbPanel.bCustomizeColors)
		{
			new CustomizeColorsDialog();
		}

		else if (e.getSource() == bCancel)
		{
			setVisible(false);
		}
	}

	public boolean isOK()
		{ return isOK; }
}