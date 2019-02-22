// Copyright 2009-2017 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package jhi.curlywhirly.data;

import jhi.curlywhirly.gui.*;

import java.util.*;
import java.util.stream.*;

public class DataSetPoints
{
	private final List<DataPoint> points;

	private DataPoint multiSelectionPoint;

	public DataSetPoints(List<DataPoint> points)
	{
		this.points = points;
	}

	public void updatePositions(int[] axes)
	{
		points.forEach(point -> point.setPositionForAxes(axes));
	}

	public int size()
	{
		return points.size();
	}

	public Stream<DataPoint> selectedPoints()
	{
		return points.stream()
			.filter(point -> !point.isMultiSelected())
			.filter(DataPoint::isSelected);
	}

	public Stream<DataPoint> deselectedPoints()
	{
		return points.stream()
			.filter(point -> !point.isMultiSelected())
			.filter(point -> !point.isSelected());
	}

	public Stream<DataPoint> multiSelectedPoints()
	{
		return points.stream().filter(DataPoint::isMultiSelected);
	}

	public void selectMultiSelected()
	{
		multiSelectedPoints().forEach(point ->
		{
			point.setSelected(true);
			point.setMultiSelected(false);
		});
	}

	public void deselectMultiSelected()
	{
		multiSelectedPoints().forEach(point ->
		{
			point.setSelected(false);
			point.setMultiSelected(false);
		});
	}

	public void toggleMultiSelected()
	{
		multiSelectedPoints().forEach(point ->
		{
			point.toggleSelection();
			point.setMultiSelected(false);
		});
	}

	public void clearMultiSelection()
	{
		multiSelectedPoints().forEach(point -> point.setMultiSelected(false));
	}

	public void detectOverlappingPoints(float minDist)
	{
		if (multiSelectionPoint != null)
		{
			points.forEach(point ->
			{
				float[] selectCoordinates = multiSelectionPoint.getPosition();
				float[] pointCoordinates = point.getPosition();

				// Find the distance between our two points
				float rX = selectCoordinates[0] - pointCoordinates[0];
				float rY = selectCoordinates[1] - pointCoordinates[1];
				float rZ = selectCoordinates[2] - pointCoordinates[2];
				float dist = rX * rX + rY * rY + rZ * rZ;
				// This should include poinSize but I've fudged it to ensure points
				// look like they are included in the circle before they are selected.
				if (dist < minDist * minDist)
				{
					if (Prefs.guiDeselectedRenderer == Prefs.guiDeselectedInvisible)
					{
						if (point.isSelected())
							point.setMultiSelected(true);
					}
					else
						point.setMultiSelected(true);
				}
			});
		}
	}

	public List<DataPoint> getDataPoints()
		{ return points; }

	public void setMultiSelectionPoint(DataPoint multiSelectionPoint)
	{
		this.multiSelectionPoint = multiSelectionPoint;
	}

	public DataPoint getMultiSelectionPoint()
	{
		return multiSelectionPoint;
	}
}