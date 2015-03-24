package curlywhirly.data;

import java.util.*;
import java.util.stream.*;

public class DataSetPoints
{
	private final List<DataPoint> points;

	public DataSetPoints(List<DataPoint> points)
	{
		this.points = points;
	}

	public Iterator<DataPoint> createIterator()
	{
		return points.iterator();
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

	public void detectOverlappingPoints(DataPoint selectedPoint, float minDist)
	{
		points.forEach(point ->
		{
			float[] selectCoordinates = selectedPoint.getPosition();
			float[] pointCoordinates = point.getPosition();

			// Find the distance between our two points
			float rX = selectCoordinates[0] - pointCoordinates[0];
			float rY = selectCoordinates[1] - pointCoordinates[1];
			float rZ = selectCoordinates[2] - pointCoordinates[2];
			float dist = rX * rX + rY * rY + rZ * rZ;
			// This should include poinSize but I've fudged it to ensure points
			// look like they are included in the circle before they are selected.
			if (dist < minDist * minDist)
				point.setMultiSelected(true);
		});
	}

	public List<DataPoint> getDataPoints()
		{ return points; }
}