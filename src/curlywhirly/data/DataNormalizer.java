package curlywhirly.data;

import java.util.*;
import curlywhirly.gui.*;

public class DataNormalizer
{
	
	
	// scaling factor to multiply the data with so that everything is normalized to be between 1 and -1
	float scalingFactor;
	
	//================================================methods=======================================
	
	//divide the data by a scaling factor so that everything is normalized to be between 1 and -1
	public static DataSet normalizeDataSet(DataSet dataSet)
	{
		//for each column of data we have
		for (int i = 0; i < CurlyWhirly.dataLoader.numDataColumns; i++)
		{
			//first iterate over the variable values to find the maximum value
			float absoluteMax = 0;
			float absoluteMin = 0;
			// for each entry in the dataset
			for (DataEntry dataEntry : dataSet.dataEntries)
			{
				float value = dataEntry.dataValues.get(i);
				if (value > absoluteMax)
					absoluteMax = value;
				if (value < absoluteMin)
					absoluteMin = value;
			}

			//then we need to work out a scaling factor by which to multiply the data so that they get normalized to between -1 and 1 
			float scalingFactor = 0;
			if (absoluteMax > Math.abs(absoluteMin))
				scalingFactor = absoluteMax;
			else
				scalingFactor = Math.abs(absoluteMin);
			
			for (DataEntry dataEntry : dataSet.dataEntries)
			{
				float value = dataEntry.dataValues.get(i);
				float normalizedValue = value/scalingFactor;
				//store this 
				dataEntry.normalizedDataValues.add(normalizedValue);
			}
		
		}

		return dataSet;
	}
	
	// ---------------------------------------------------------------------------------------------------------------------------------------------------------------------------
	
	
}//end class
