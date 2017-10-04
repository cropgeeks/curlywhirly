// Copyright 2009-2017 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package jhi.curlywhirly.gui.dialog;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import jhi.curlywhirly.gui.*;

import scri.commons.gui.*;
import scri.commons.gui.matisse.*;

public class AboutDialog extends JDialog implements ActionListener
{
	private JButton bClose;

	private AboutPanelNB nbPanel;
	private AboutLicencePanelNB licencePanel = new AboutLicencePanelNB();

	public AboutDialog()
	{
        super(CurlyWhirly.winMain, true);

		nbPanel = new AboutPanelNB();

		AvatarPanel avatars = new AvatarPanel();

		JTabbedPane tabs = new JTabbedPane();
		tabs.add(RB.getString("gui.AboutDialog.tab1"), nbPanel);
		tabs.add(RB.getString("gui.AboutDialog.tab2"), licencePanel);
		tabs.add(RB.format("gui.AboutDialog.tab3", "\u0026"), avatars);

		add(tabs);
		add(createButtons(), BorderLayout.SOUTH);

		getRootPane().setDefaultButton(bClose);
		SwingUtils.addCloseHandler(this, bClose);

		pack();
		setLocationRelativeTo(CurlyWhirly.winMain);
		setTitle(RB.getString("gui.AboutDialog.title"));
		setResizable(false);
		setVisible(true);
	}

	private JPanel createButtons()
	{
		bClose = new JButton(RB.getString("gui.text.close"));
		bClose.addActionListener(this);

		JPanel p1 = new DialogPanel();
		p1.add(bClose);

		return p1;
	}

	public void actionPerformed(ActionEvent e)
	{
		setVisible(false);
	}

	private class AvatarPanel extends JPanel
	{
		AvatarPanel()
		{
			setBackground(Color.white);
			add(new JLabel(Icons.getIcon("AVATARS")));

			addMouseMotionListener(new MouseMotionAdapter()
			{
				public void mouseMoved(MouseEvent e)
				{
					int x = e.getX();

					String tooltip = "<html>";

					if (x >= 20 && x < 100)
						tooltip += "Gordon Stephen";
					else if (x >= 110 && x < 210)
						tooltip += "Iain Milne";
					else if (x >= 220 && x < 310)
						tooltip += "Micha Bayer";
					else if (x >= 320 && x < 410)
						tooltip += "Paul Shaw";
					else if (x >= 420 && x < 510)
						tooltip += "Sebastian Raubach";
					else if (x >= 520)
						tooltip += "David Marshall";
					else
						tooltip = null;

					setToolTipText(tooltip);
				}
			});
		}
	}
}