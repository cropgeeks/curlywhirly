// Copyright 2009-2015 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package jhi.curlywhirly.gui.viewer;

import jhi.curlywhirly.util.ColorPrefs;
import java.awt.*;
import java.awt.image.*;
import java.util.*;
import java.util.stream.*;

import jhi.curlywhirly.data.*;

public class ColourKeyCreator
{
	private DataSet dataSet;

	public void setDataSet(DataSet dataSet)
	{
		this.dataSet = dataSet;
	}

	public BufferedImage getColourKeyImage()
	{
		// Get category group for key
		CategoryGroup group = dataSet.getCurrentCategoryGroup();

		BufferedImage temp = new BufferedImage(10, 10, BufferedImage.TYPE_INT_ARGB);
		Graphics g = temp.createGraphics();
		FontMetrics metrics = g.getFontMetrics();

		int width = getKeyWidth(metrics, group);

		int lineheight = metrics.getHeight() + 2;
		int categories = dataSet.getCurrentCategoryGroup().selectedCategoriesCount();
		int height = lineheight * categories + lineheight;

		BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		Graphics imageG = image.createGraphics();

		imageG.setColor(ColorPrefs.getColor("User.OpenGLPanel.background"));
		imageG.fillRect(0, 0, width, height);

		drawColorKey(group, imageG);

		return image;
	}

	void drawColorKey(CategoryGroup group, Graphics g)
	{
		FontMetrics metrics = g.getFontMetrics();
		int stringWidth = getKeyWidth(metrics, group);

		int nameWidth = metrics.stringWidth(group.getName());
		int height = metrics.getHeight();
		// Adjust nameWidth so that it lines up centrally over the key when
		// we have long category names
		if (nameWidth < stringWidth)
			nameWidth += height;
		// +2 to push the title out from the edge if it is there
		int titleX = (stringWidth-nameWidth)/2;
		if (titleX == 0)
			titleX = 2;

		g.setColor(Color.WHITE);
		// Draw key's title string
		g.drawString(group.getName(), titleX, height);

		ArrayList<Category> activeCats = group.getActiveCategories().collect(Collectors.toCollection(ArrayList::new));

		// Draw category colors
		for (int i=0; i < activeCats.size(); i++)
		{
			g.setColor(activeCats.get(i).getColor());
			g.fillRect(2, ((i+1)*height+5), height, height-2);
		}

		// Draw category strings
		g.setColor(Color.WHITE);
		for (int i=0; i < activeCats.size(); i++)
			g.drawString(activeCats.get(i).getName(), height+4, height + ((i+1)*height));
	}

	private int getKeyWidth(FontMetrics metrics, CategoryGroup group)
	{
		int width = metrics.stringWidth(group.getName());
		for (int i=0; i < group.size(); i++)
		{
			int catWidth = metrics.stringWidth(group.get(i).getName());
			if (catWidth > width)
				width = catWidth;
		}

		return width;
	}
}