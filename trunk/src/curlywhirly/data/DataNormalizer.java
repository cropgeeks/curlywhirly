package curlywhirly.data;

import java.util.*;

public class DataNormalizer
{
	
	
	// scaling factor to multiply the data with so that everything is normalized to be between 1 and -1
	float scalingFactor;
	
	//================================================methods=======================================
	
	public static DataSet normalizeDataSet(DataSet dataSet)
	{
		
		//the values of the raw data
		Vector<float[]> data = dataSet.data;
		
		for (float[] variableArray : data)
		{
			//first iterate over the entire array to find the maximum value
			float absoluteMax = 0;
			float absoluteMin = 0;		
			for (int i = 0; i < variableArray.length; i++)
			{
				float value = variableArray[i];
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
			
			//now replace every value in the original data with its normalized equivalent
			for (int i = 0; i < variableArray.length; i++)
			{
				variableArray[i] = (variableArray[i]/scalingFactor);
			}
			
		}

		return dataSet;
	}
	
	// ---------------------------------------------------------------------------------------------------------------------------------------------------------------------------
	
	
}//end class
