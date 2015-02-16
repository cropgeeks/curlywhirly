// Copyright 2009-2014 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package curlywhirly.io;

import java.util.*;
import java.util.stream.*;

import curlywhirly.data.*;

public class DataNormalizer
{
	public DataSet normalize(DataSet dataSet)
	{
		float min = getMinimumValue(dataSet);
		float max = getMaximumValue(dataSet);

		normalizeDataPointValues(dataSet, min, max);

		return dataSet;
	}

	private float getMinimumValue(DataSet dataSet)
	{
		return dataSet.getDataPoints().stream()
			.flatMap(point -> point.getValues().stream())
			.min(Comparator.naturalOrder()).get();
	}

	private float getMaximumValue(DataSet dataSet)
	{
		return dataSet.getDataPoints().stream()
			.flatMap(point -> point.getValues().stream())
			.max(Comparator.naturalOrder()).get();
	}

	private void normalizeDataPointValues(DataSet dataSet, float min, float max)
	{
		for (DataPoint dataPoint : dataSet)
		{
			ArrayList<Float> values = getNormalizedValues(dataPoint, min, max);
			dataPoint.setNormalizedValues(values);
		}
	}

	private ArrayList<Float> getNormalizedValues(DataPoint dataPoint, float min, float max)
	{
		return dataPoint.getValues().stream()
			.map(value -> normalizeValue(value, min, max))
			.collect(Collectors.toCollection(ArrayList::new));
	}

	private float normalizeValue(float value, float min, float max)
	{
		return (((value - min) * (1 - -1)) / (max - min)) + -1;
	}
}