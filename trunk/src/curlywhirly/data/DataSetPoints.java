package curlywhirly.data;

import java.util.*;

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

	public List<DataPoint> getDataPoints()
		{ return points; }
}