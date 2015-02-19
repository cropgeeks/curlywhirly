// Copyright 2009-2014 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package curlywhirly.data;

import java.awt.*;
import java.util.*;

public class DataSet implements Iterable<DataPoint>
{
	private final String name;

	private final ArrayList<DataPoint> dataPoints;
	private final ArrayList<CategoryGroup> categoryGroups;
	private final HashMap<String, HashMap<CategoryGroup, Category>> pointCategories;
	private final Axes axes;
	private CategoryGroup currentGroup;

	// DB-link/association data
	private DBAssociation dbAssociation = new DBAssociation();

	public DataSet(String name, ArrayList<DataPoint> dataPoints, ArrayList<CategoryGroup> categoryGroups, String[] axisLabels, HashMap<String, HashMap<CategoryGroup, Category>> pointCategories)
	{
		this.name = name;
		this.dataPoints = dataPoints;
		// The sort ensures CategoryGroups are displayed in alphabetical order
		Collections.sort(categoryGroups);
		this.categoryGroups = categoryGroups;
		this.pointCategories = pointCategories;

		axes = new Axes(axisLabels);

		currentGroup = categoryGroups.get(0);
	}

	@Override
	public Iterator<DataPoint> iterator()
	{
		return dataPoints.iterator();
	}

	public void setCurrentCategoryGroup(CategoryGroup currentGroup)
	{
		if (categoryGroups.isEmpty() == false)
			this.currentGroup = currentGroup;
	}

	public CategoryGroup getCurrentCategoryGroup()
		{ return currentGroup; }

	public Axes getAxes()
		{ return axes; }

	public Color getPointColor(DataPoint point)
	{
		return pointCategories.get(point.getName()).get(currentGroup).getColor();
	}

	public HashMap<CategoryGroup, Category> getPointCategories(DataPoint point)
	{
		return pointCategories.get(point.getName());
	}

	public ArrayList<CategoryGroup> getCategoryGroups()
		{ return categoryGroups; }

	public DBAssociation getDbAssociation()
		{ return dbAssociation; }

	public String getName()
		{ return name; }

	public ArrayList<DataPoint> getDataPoints()
		{ return dataPoints; }

	public int size()
	{	return dataPoints.size(); }
}