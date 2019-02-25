// Copyright 2009-2017 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package jhi.curlywhirly.data;

import java.awt.*;
import java.util.*;

public class DataPoint
{
	private final String name;
	private final ArrayList<Float> values;
	// Normalized values are used for rendering, values are displayed in tables
	private final ArrayList<Float> normalizedValues;
	private boolean isSelected;
	private boolean isMultiSelected;

	private final Position3D position;

	private Color color = Color.DARK_GRAY;

	private final Map<String, String> urlMap;

	public DataPoint(String name, ArrayList<Float> values, ArrayList<Float> normalizedValues, Map<String, String> urlMap)
	{
		this.name = name;
		this.values = values;
		this.normalizedValues = normalizedValues;
		this.urlMap = urlMap;

		isSelected = true;
		isMultiSelected = false;
		position = new Position3D();
	}

	// Accepts as input an int array of the axes that are currently being
	// displayed. This array needs to be exactly 3 elements long. getPosition
	// will return a 3 element float array.
	public float[] getPosition()
	{
		return position.getPosition();
	}

	public void setPositionForAxes(int[] axes)
	{
		position.setX(normalizedValues.get(axes[0]));
		position.setY(normalizedValues.get(axes[1]));
		position.setZ(normalizedValues.get(axes[2]));
	}

	public String getName()
	{
		return name;
	}

	public ArrayList<Float> getValues()
	{
		return values;
	}

	public boolean isSelected()
	{
		return isSelected;
	}

	public void setSelected(boolean selected)
	{
		isSelected = selected;
	}

	public boolean isMultiSelected()
	{
		return isMultiSelected;
	}

	public void setMultiSelected(boolean isMultiSelected)
	{
		this.isMultiSelected = isMultiSelected;
	}

	public void toggleSelection()
	{
		isSelected = !isSelected;
	}

	public void setColor(Color color)
	{
		this.color = color;
	}

	public Color getColor()
	{
		return color;
	}

	public Map<String, String> getUrlMap()
	{
		return urlMap;
	}

	@Override
	public String toString()
	{
		return name;
	}
}