package curlywhirly.data;

import java.awt.Color;
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

	public DataPoint(String name, ArrayList<Float> values, HashMap<CategoryGroup, Category> categories)
	{
		this.name = name;
		this.values = values;
		this.categories = categories;
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

	// Returns true only when every category that this DataPoint is tagged as
	// having is selected, otherwise returns false.
	public boolean isSelected()
	{
		for (Category category : categories.values())
			if (category.isSelected() == false)
				return false;

		return true;
	}

	// Uses the supplied CategoryGroup to look for an appropriate Category. If
	// one is found and the point is selected it returns the color of this
	// Category. Otherwise it returns Dark Gray.
	public Color getColor(CategoryGroup group)
	{
		Category cat = categories.get(group);

		Color color = Color.DARK_GRAY;
		if (cat != null && isSelected())
			color = cat.getColor();

		return color;
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
