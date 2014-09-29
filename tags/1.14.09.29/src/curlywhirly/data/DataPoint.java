// Copyright 2009-2014 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package curlywhirly.data;

import java.awt.*;
import java.util.*;

public class DataPoint
{
	private final String name;
	private final ArrayList<Float> values;
	// Normalized values are used for rendering, values are displayed in tables
	private ArrayList<Float> normalizedValues;
	// Provides a quick method of querying for a Category by CategoryGroup.
	// Useful for getting the colour to display for the point.
	private final HashMap<CategoryGroup, Category> categories;
	private boolean isSelected;

	public DataPoint(String name, ArrayList<Float> values, HashMap<CategoryGroup, Category> categories)
	{
		this.name = name;
		this.values = values;
		this.categories = categories;

		isSelected = true;
	}

	// Accepts as input an int array of the axes that are currently being
	// displayed. This array needs to be exactly 3 elements long. getPosition
	// will return a 3 element float array.
	public float[] getPosition(int[] currAxes)
	{
		return new float[] { normalizedValues.get(currAxes[0]),
							 normalizedValues.get(currAxes[1]),
							 normalizedValues.get(currAxes[2]) };
	}

	public String getName()
		{ return name; }

	public void setNormalizedValues(ArrayList<Float> normalizedValues)
	{
		this.normalizedValues = normalizedValues;
	}

	public ArrayList<Float> getValues()
		{ return values; }

	public boolean isSelected()
	{
		return isSelected;
	}

	public void setSelected(boolean selected)
	{
		isSelected = selected;
	}

	public void toggleSelection()
	{
		isSelected = !isSelected;
	}

	// Uses the supplied CategoryGroup to look for an appropriate Category. If
	// one is found and the point is selected it returns the color of this
	// Category. Otherwise it returns Dark Gray.
	public Color getColor(CategoryGroup group)
	{
		Category cat = categories.get(group);

		return cat.getColor();
	}

	public Category getCategoryForGroup(CategoryGroup group)
	{
		return categories.get(group);
	}

	@Override
	public String toString()
	{
		return name;
	}
}