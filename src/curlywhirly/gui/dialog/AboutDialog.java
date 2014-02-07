// Copyright 2009-2014 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package curlywhirly.gui.dialog;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import curlywhirly.gui.*;

import scri.commons.gui.*;

public class AboutDialog extends JDialog implements ActionListener
{
	private JButton bClose;

	private AboutPanelNB nbPanel;
	private AboutLicencePanelNB licencePanel = new AboutLicencePanelNB();
	private AboutHelpPanelNB helpPanel = new AboutHelpPanelNB();

	public AboutDialog()
	{
        super(CurlyWhirly.winMain, true);

		nbPanel = new AboutPanelNB();

		AvatarPanel avatars = new AvatarPanel();

		JTabbedPane tabs = new JTabbedPane();
		tabs.add(RB.getString("gui.AboutDialog.tab1"), nbPanel);
		tabs.add(RB.getString("gui.AboutDialog.tab2"), licencePanel);
		tabs.add(RB.format("gui.AboutDialog.tab3", "\u0026"), avatars);
		tabs.add(RB.getString("gui.AboutDialog.tab4"), helpPanel);

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
		bClose = SwingUtils.getButton(RB.getString("gui.text.close"));
		bClose.addActionListener(this);

		JPanel p1 = CWUtils.getButtonPanel();
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

					if (x >= 25 && x < 120)
						tooltip += "Gordon Stephen";
					else if (x >= 140 && x < 240)
						tooltip += "Iain Milne";
					else if (x >= 250 && x < 350)
						tooltip += "Micha Bayer";
					else if (x >= 360 && x < 450)
						tooltip += "Paul Shaw";
					else if (x >= 460 && x < 570)
						tooltip += "David Marshall";
					else
						tooltip = null;

					setToolTipText(tooltip);
				}
			});
		}
	}
}