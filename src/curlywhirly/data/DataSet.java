// Copyright 2009-2014 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package curlywhirly.data;

import java.util.*;

public class DataSet implements Iterable<DataPoint>
{
	private final String name;

	private final DataSetPoints dataPoints;
	private final ArrayList<CategoryGroup> categoryGroups;
	private final Axes axes;
	private CategoryGroup currentGroup;

	// DB-link/association data
	private DBAssociation dbAssociation = new DBAssociation();

	public DataSet(String name, ArrayList<DataPoint> dataPoints, ArrayList<CategoryGroup> categoryGroups, String[] axisLabels)
	{
		this.name = name;
		// The sort ensures CategoryGroups are displayed in alphabetical order
		Collections.sort(categoryGroups);
		this.categoryGroups = categoryGroups;

		this.dataPoints = new DataSetPoints(dataPoints);

		axes = new Axes(axisLabels);

		setCurrentCategoryGroup(categoryGroups.get(0));
	}

	@Override
	public Iterator<DataPoint> iterator()
	{
		return dataPoints.createIterator();
	}

	public void updatePointPositions()
	{
		dataPoints.updatePositions(axes.getXYZ());
	}

	public void setCurrentCategoryGroup(CategoryGroup currentGroup)
	{
		if (categoryGroups.isEmpty() == false)
		{
			this.currentGroup = currentGroup;
			currentGroup.colorPointsByCategories();
		}
	}

	public CategoryGroup getCurrentCategoryGroup()
		{ return currentGroup; }

	public Axes getAxes()
		{ return axes; }

	public ArrayList<CategoryGroup> getCategoryGroups()
		{ return categoryGroups; }

	public DBAssociation getDbAssociation()
		{ return dbAssociation; }

	public String getName()
		{ return name; }

	public ArrayList<DataPoint> getDataPoints()
		{ return (ArrayList<DataPoint>) dataPoints.getDataPoints(); }

	public int size()
	{	return dataPoints.size(); }
}