// Copyright 2009-2015 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package curlywhirly.io;

import java.util.*;
import java.util.stream.*;

public class DataNormalizer
{
	private final float min;
	private final float max;

	public DataNormalizer(ArrayList<Float> values)
	{
		this.min = values.stream().min(Comparator.naturalOrder()).get();
		this.max = values.stream().max(Comparator.naturalOrder()).get();
	}

	public ArrayList<Float> normalizeValues(Stream<Float> values)
	{
		return values.map(value -> normalizeValue(value))
			.collect(Collectors.toCollection(ArrayList::new));
	}

	private float normalizeValue(float value)
	{
		return (((value - min) * (1 - -1)) / (max - min)) + -1;
	}
}