// Copyright 2009-2017 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package jhi.curlywhirly.data;

import java.awt.*;
import java.util.*;

public class Category implements Comparable<Category>
{
	private final String name;
	private final String groupName;
	private Color color;
	private boolean selected;

	private final ArrayList<DataPoint> dataPoints;

	public Category(String name, String groupName)
	{
		this.name = name;
		this.groupName = groupName;

		selected = true;
		dataPoints = new ArrayList<DataPoint>();
	}

	// getTotal and getNoSelected are used in the reporting of the data points
	// which are selected on a per category basis.
	public int getTotal()
		{ return dataPoints.size(); }

	public int getSelectedCount()
	{
		return (int) dataPoints.stream().filter(DataPoint::isSelected).count();
	}

	public boolean hasSelectedPoints()
	{
		return ((int) dataPoints.stream().filter(DataPoint::isSelected).count() != 0);
	}

	public String getSelectedText()
	{
		return "" + getSelectedCount() + "/" + getTotal();
	}

	public String getName()
		{ return name; }

	public void setSelected(boolean selected)
	{
		this.selected = selected;

		dataPoints.forEach(point -> point.setSelected(selected));
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

	@Override
	public String toString()
	{
		return name;
	}

	// Utility method used to get the ColorPrefs key for this category
	public String getColorKey()
	{
		return groupName + "." + name;
	}

	@Override
	public int compareTo(Category o)
	{
		return name.compareToIgnoreCase(o.name);
	}
}