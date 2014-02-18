// Copyright 2009-2014 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package curlywhirly.data;

import java.awt.*;
import java.util.*;

public class Category implements Comparable<Category>
{
	private final String name;
	private Color color;
	private boolean selected;
	private CategoryGroup group;

	private ArrayList<DataPoint> dataPoints;

	public Category(String name, CategoryGroup group)
	{
		this.name = name;
		this.group = group;

		selected = true;
		dataPoints = new ArrayList<DataPoint>();
	}

	// getTotal and getNoSelected are used in the reporting of the data points
	// which are selected on a per category basis.
	public int getTotal()
		{ return dataPoints.size(); }

	public int getNoSelected()
	{
		int count = 0;
		for (DataPoint point : dataPoints)
			if (point.isSelected())
				count++;

		return count;
	}

	public String getSelectedText()
	{
		return "" + getNoSelected() + "/" + getTotal();
	}

	public String getName()
		{ return name; }

	public void setSelected(boolean selected)
	{
		this.selected = selected;
	}

	public boolean isSelected()
		{ return selected; }

	public void addDataPoint(DataPoint dataPoint)
	{
		dataPoints.add(dataPoint);
	}

	public void setColour(Color color)
	{
		this.color = color;
	}

	public Color getColor()
		{ return color; }

	public void setGroup(CategoryGroup group)
	{
		this.group = group;
	}

	public CategoryGroup getGroup()
		{ return group; }

	@Override
	public String toString()
	{
		return name;
	}

	// Utility method used to get the ColorPrefs key for this category
	public String getColorKey()
	{
		return group.getName() + "." + name;
	}

	@Override
	public int compareTo(Category o)
	{
		return name.compareToIgnoreCase(o.name);
	}
}