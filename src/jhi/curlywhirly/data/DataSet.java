// Copyright 2009-2017 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package jhi.curlywhirly.data;

import java.util.*;
import java.util.stream.*;

public class DataSet
{
	private final String name;

	private final DataSetPoints dataPoints;
	private final ArrayList<CategoryGroup> categoryGroups;
	private final ArrayList<String> urlNames;
	private final Axes axes;
	private CategoryGroup currentGroup;

	// DB-link/association data
	private DBAssociation dbAssociation = new DBAssociation();

	public DataSet(String name, ArrayList<DataPoint> dataPoints, ArrayList<CategoryGroup> categoryGroups, String[] axisLabels, ArrayList<String> urlNames)
	{
		this.name = name;
		// The sort ensures CategoryGroups are displayed in alphabetical order
		Collections.sort(categoryGroups);
		this.categoryGroups = categoryGroups;

		this.dataPoints = new DataSetPoints(dataPoints);
		this.urlNames = urlNames;

		axes = new Axes(axisLabels);

		setCurrentCategoryGroup(categoryGroups.get(0));
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

	public void selectMultiSelected()
	{
		dataPoints.selectMultiSelected();
	}

	public void deselectMultiSelected()
	{
		dataPoints.deselectMultiSelected();
	}

	public void toggleMultiSelected()
	{
		dataPoints.toggleMultiSelected();
	}

	public void clearMultiSelection()
	{
		dataPoints.clearMultiSelection();
	}

	// Checks for sphere sphere collisions by chcking the distance between all
	// points and the multiselectionpoint and the sphere's radius.
	public void detectMultiSelectedPoints(float minDist)
	{
		clearMultiSelection();
		dataPoints.detectOverlappingPoints(minDist);
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

	public ArrayList<String> getUrlNames()
		{ return urlNames; }

	public ArrayList<DataPoint> getDataPoints()
		{ return (ArrayList<DataPoint>) dataPoints.getDataPoints(); }

	public Stream<DataPoint> selectedPoints()
	{
		return dataPoints.selectedPoints();
	}

	public Stream<DataPoint> deselectedPoints()
	{
		return dataPoints.deselectedPoints();
	}

	public Stream<DataPoint> multiSelectedPoints()
	{
		return dataPoints.multiSelectedPoints();
	}

	public int size()
	{	return dataPoints.size(); }

	public void setMultiSelectionPoint(DataPoint multiSelectionPoint)
	{
		dataPoints.setMultiSelectionPoint(multiSelectionPoint);
	}

	public DataPoint getMultiSelectionPoint()
	{
		return dataPoints.getMultiSelectionPoint();
	}
}