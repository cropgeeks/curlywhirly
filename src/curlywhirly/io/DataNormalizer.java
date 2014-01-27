package curlywhirly.io;

import curlywhirly.data.DataPoint;
import curlywhirly.data.DataSet;
import java.util.*;

public class DataNormalizer
{
	public DataSet normalize(DataSet dataSet)
	{
		float min = getMinimumValue(dataSet);
		float max = getMaximumValue(dataSet);

		for (DataPoint dataPoint : dataSet)
		{
			ArrayList<Float> values = getNormalizedValues(dataPoint, min, max);
			dataPoint.setNormalizedValues(values);
		}

		return dataSet;
	}

	private float getMinimumValue(DataSet dataSet)
	{
		float min = Float.MAX_VALUE;

		for (DataPoint dp : dataSet)
		{
			for (Float value : dp.getValues())
				min = Math.min(min, value);
		}

		return min;
	}

	private float getMaximumValue(DataSet dataSet)
	{
		float max = -Float.MAX_VALUE;

		for (DataPoint dp : dataSet)
		{
			for (Float value : dp.getValues())
				max = Math.max(max, value);
		}

		return max;
	}

	private ArrayList<Float> getNormalizedValues(DataPoint dataPoint, float min, float max)
	{
		ArrayList<Float> normalized = new ArrayList<>();
		for (float value : dataPoint.getValues())
			normalized.add(normalizeValue(value, min, max));

		return normalized;
	}

	private float normalizeValue(float value, float min, float max)
	{
		return (((value - min) * (1 - -1)) / (max - min)) + -1;
	}
}