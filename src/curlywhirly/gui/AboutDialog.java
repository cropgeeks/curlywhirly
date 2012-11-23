// Copyright 2009-2012 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package curlywhirly.gui;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import scri.commons.gui.*;

public class AboutDialog extends JDialog implements ActionListener
{
	private JButton bClose;

	private AboutPanelNB nbPanel;

	public AboutDialog(java.awt.Frame parent, boolean modal)
	{
        super(parent, modal);

		nbPanel = new AboutPanelNB();

		AvatarPanel avatars = new AvatarPanel();

		JTabbedPane tabs = new JTabbedPane();
		tabs.add("About CurlyWhirly", nbPanel);
		tabs.add("Information \u0026 Computational Sciences", avatars);

		add(tabs);
		add(createButtons(), BorderLayout.SOUTH);

		getRootPane().setDefaultButton(bClose);
		SwingUtils.addCloseHandler(this, bClose);

		pack();
		setLocationRelativeTo(parent);
		setTitle("About CurlyWhirly");
		setResizable(false);
		setVisible(true);
	}

	private JPanel createButtons()
	{
		bClose = SwingUtils.getButton("Close");
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
						tooltip += "Micha Bayer";
					else if (x >= 140 && x < 240)
						tooltip += "Iain Milne";
					else if (x >= 250 && x < 350)
						tooltip += "Paul Shaw";
					else if (x >= 360 && x < 450)
						tooltip += "Gordon Stephen";
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