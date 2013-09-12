package curlywhirly.data;

import java.awt.*;
import java.util.*;

public class Category
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
}