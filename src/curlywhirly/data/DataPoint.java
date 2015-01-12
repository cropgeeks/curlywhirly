// Copyright 2009-2014 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package curlywhirly.data;

import java.util.*;

public class DataPoint
{
	private final String name;
	private final ArrayList<Float> values;
	// Normalized values are used for rendering, values are displayed in tables
	private ArrayList<Float> normalizedValues;
	private boolean isSelected;

	public DataPoint(String name, ArrayList<Float> values)
	{
		this.name = name;
		this.values = values;

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

	@Override
	public String toString()
	{
		return name;
	}
}