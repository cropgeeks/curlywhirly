package curlywhirly.util;

import java.awt.*;
import java.awt.image.*;
import javax.swing.*;
import scri.commons.gui.RB;

public class ColorListRenderer implements ListCellRenderer<ColorPrefs.ColorPref>
{
	// Set the attributes of the class and return a reference
	@Override
	public Component getListCellRendererComponent(JList<? extends ColorPrefs.ColorPref> list,
		ColorPrefs.ColorPref colorPref, int index, boolean isSelected, boolean cellHasFocus)
	{
		DefaultListCellRenderer renderer = new DefaultListCellRenderer();
		JLabel component = (JLabel) renderer.getListCellRendererComponent(list, colorPref, index, isSelected, cellHasFocus);

		// Set the text
		String name = RB.getString(colorPref.getKey()) == null ? colorPref.getKey() : RB.getString(colorPref.getKey());
		component.setText(name);

		// Set the icon
		BufferedImage image = new BufferedImage(20, 10, BufferedImage.TYPE_INT_RGB);
		Graphics2D g = image.createGraphics();

		Color c1 = colorPref.getColor().brighter();
		Color c2 = colorPref.getColor().darker();

		g.setPaint(new GradientPaint(0, 0, c1, 20, 10, c2));
		g.fillRect(0, 0, 20, 10);
		g.setColor(Color.black);
		g.drawRect(0, 0, 20, 10);
		g.dispose();

		component.setIcon(new ImageIcon(image));
		component.setBorder(BorderFactory.createEmptyBorder(0, 3, 0, 0));

		return component;
	}
}