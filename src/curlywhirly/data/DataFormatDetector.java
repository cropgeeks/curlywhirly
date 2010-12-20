package curlywhirly.data;

import java.util.*;

public class DataFormatDetector
{
	
	public int detectDataFormat(DataSet dataSet, String [] lines, int numPrefixedCategoryHeaders, String [] headers, String categoryHeaderPrefix)
	{
		int dataFormat = -1;
		
		//if categories prefixes found -> f2/f3
		if(numPrefixedCategoryHeaders > 0)
		{
			//labels column is either index 0 or last one after the categories headers and before the data cols
			//if header col 0 starts with category prefix -> f2
			if(headers[0].startsWith(categoryHeaderPrefix) || headers[0].startsWith("\"" + categoryHeaderPrefix))
				dataFormat = 2;
			else
				dataFormat = 3;
		}
		else //no category prefixes found
		{
			//if column 0 is blank -> f1 and no category data in it
			if(headers[0].equals(""))
			{
				dataFormat = 1;
				dataSet.emptyCategoryColumn = true;
			}
			//else test for redundancy in column 0
			else
			{
				//if this is redundant -> f1 and has category data in col 0 
				boolean col0Redundancy = columnHasRedundantData(0, lines);

				if(col0Redundancy)
					dataFormat = 1;
				else//col 0 is labels and there is no category data -> f3
				{
					dataFormat = 3;
					dataSet.missingCategoryColumn = true;
				}
			}
		}
		
		return dataFormat;
	}
	
	// -------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
	
	private static boolean columnHasRedundantData(int columnIndex, String [] lines)
	{
		//parse all the lines and store the value from tokens[0] on each line
		ArrayList<String> col0Values = new ArrayList<String>();
		for (int i = 0; i < lines.length; i++)
		{
			String [] tokens = lines[i].split("\t");
			boolean valueExists = col0Values.contains(tokens[columnIndex]);

			//if the value does not exist, add it
			if(!valueExists)
				col0Values.add(tokens[columnIndex]);
			//else we have redundancy and can return
			else
			{
				return true;
			}
		}	

		return false;
	}
	
	// -------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
	
}
