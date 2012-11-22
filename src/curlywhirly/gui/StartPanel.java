package curlywhirly.gui;

import java.awt.*;
import javax.swing.*;

import scri.commons.gui.*;

class StartPanel extends JPanel
{
	StartPanel()
	{
		setBorder(BorderFactory.createLineBorder(new Color(119, 126, 143), 3));
		setLayout(new BorderLayout());

		JPanel logoPanel = new LogoPanel(new BorderLayout(0, 0));

		JPanel welcomePanel = new JPanel(new BorderLayout());
		welcomePanel.setOpaque(false);
		welcomePanel.setBorder(BorderFactory.createEmptyBorder(2, 2, 8, 2));
		welcomePanel.add(new TitlePanel3(
			RB.getString("gui.NBStartWelcomePanel.title")), BorderLayout.NORTH);
		welcomePanel.add(new StartPanelWelcomeNB());

/*		JPanel filePanel = new JPanel(new BorderLayout());
		filePanel.setOpaque(false);
		filePanel.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
		filePanel.add(new TitlePanel3(
			RB.getString("gui.NBStartFilePanel.title")), BorderLayout.NORTH);
		filePanel.add(new StartPanelFileNB());

		JPanel helpPanel = new JPanel(new BorderLayout());
		helpPanel.setOpaque(false);
		helpPanel.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
		helpPanel.add(new TitlePanel3(
			RB.getString("gui.NBStartHelpPanel.title")), BorderLayout.NORTH);
		helpPanel.add(new StartPanelHelpNB());

		JPanel pubPanel = new JPanel(new BorderLayout());
		pubPanel.setOpaque(false);
		pubPanel.setBorder(BorderFactory.createEmptyBorder(2, 2, 8, 2));
		pubPanel.add(new TitlePanel3(
			RB.getString("gui.NBStartPublicationPanel.title")), BorderLayout.NORTH);
		pubPanel.add(new StartPanelPublicationNB());

		JPanel huttonPanel = new JPanel(new BorderLayout());
		huttonPanel.setOpaque(false);
		huttonPanel.add(pubPanel);
		huttonPanel.add(getHuttonLabel(), BorderLayout.EAST);

		JPanel centrePanel = new JPanel(new GridLayout(1, 2, 0, 0));
		centrePanel.setOpaque(false);
		centrePanel.add(filePanel);
		centrePanel.add(helpPanel);
*/
		logoPanel.add(welcomePanel, BorderLayout.NORTH);
//		logoPanel.add(centrePanel, BorderLayout.CENTER);
//		logoPanel.add(huttonPanel, BorderLayout.SOUTH);

		add(logoPanel);
	}

	private static class LogoPanel extends JPanel
	{
		private static ImageIcon logo = Icons.getIcon("HUTTONLARGE");

		LogoPanel(LayoutManager lm)
		{
			super(lm);
			setBackground(Color.white);
		}

		public void paintComponent(Graphics graphics)
		{
			super.paintComponent(graphics);

			Graphics2D g = (Graphics2D) graphics;

			int w = getWidth();
			int h = getHeight();

			g.drawImage(logo.getImage(), 0, 0, w, h, null);
		}
	}
}

class TitlePanel3 extends JPanel
{
	private static final Color lineColor = new Color(207, 219, 234);
	private static final Color textColor = new Color(75, 105, 150);

	private static final int h = 30;

	private String title;

	public TitlePanel3(String title)
	{
		this.title = title;
		setOpaque(false);
	}

	public Dimension getPreferredSize()
	{
		return new Dimension(50, h);
	}

	public void paintComponent(Graphics graphics)
	{
		super.paintComponent(graphics);

		Graphics2D g = (Graphics2D) graphics;

		int w = getWidth();

		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g.setFont(new Font("Dialog", Font.BOLD, 13));
		g.setColor(textColor);
		g.drawString(title, 10, 18);

		g.setPaint(new GradientPaint(0, h, lineColor, w, h, Color.white));
		g.setStroke(new BasicStroke(3));
		g.drawLine(10, 26, w-10, 26);
	}
}