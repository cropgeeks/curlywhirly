package curlywhirly.data;

public class DataNormalizer
{
	// scaling factor to multiply the data with so that everything is normalized to be between 1 and -1
	float scalingFactor;

	//divide the data by a scaling factor so that everything is normalized to be between 1 and -1
	public static DataSet normalizeDataSet(DataSet dataSet)
	{
		//for each column of data we have
		for (int i = 0; i < dataSet.numDataColumns; i++)
		{
			//first iterate over the variable values to find the maximum value
			float absoluteMax = -Float.MAX_VALUE;
			float absoluteMin = Float.MAX_VALUE;
			// for each entry in the dataset
			for (DataEntry dataEntry : dataSet.dataEntries)
			{
				float value = dataEntry.dataValues.get(i);
				if (value > absoluteMax)
					absoluteMax = value;
				if (value < absoluteMin)
					absoluteMin = value;
			}

			// Doe wach value normalize it and store it
			for (DataEntry dataEntry : dataSet.dataEntries)
			{
				float value = dataEntry.dataValues.get(i);
				float normalizedValue = (((value - absoluteMin) * (1 - -1)) / (absoluteMax - absoluteMin)) + -1;
				dataEntry.normalizedDataValues.add(normalizedValue);
			}

		}

		return dataSet;
	}
}