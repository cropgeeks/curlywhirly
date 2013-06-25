package curlywhirly.data;

import java.util.*;

public class ColumnOrderFormatter
{
	public void formatColumnOrders(int dataFormat, DataSet dataSet)
	{
		switch(dataFormat)
		{
			case 1:
			{
				//F1:
				//1. all data present:
				//Category data --> Labels for individual data points --> Data column 1 --> Data column 2 --> ..... Data column n
				//categories:0
				//labels: 1
				//data: 2-n
				//2. No Categories:
				//blank column --> Labels for individual data points --> Data column 1 --> Data column 2 --> ..... Data column n
				//categories:0 (but empty)
				//labels: 1
				//data: 2-n
				dataSet.categoryColumnIndices = getListOfInts(0, 1); //new int[]{0};
				dataSet.labelsColumnIndex = 1;
				dataSet.dataColumnIndices = getListOfInts(2, dataSet.numDataColumns);
			}
			break;
			case 2:
			{
				//F2:
				//1. category cols (prefixed) -> label -> data cols
				//categories:0-n
				//labels: categoryCount
				//data: (categoryCount+1)-n
				dataSet.categoryColumnIndices = getListOfInts(0, dataSet.numCategoryColumns);
				dataSet.labelsColumnIndex = dataSet.categoryColumnIndices.size();
				dataSet.dataColumnIndices = getListOfInts((dataSet.labelsColumnIndex+1), dataSet.numDataColumns);
			}
			break;
			case 3:
			{
				if(!dataSet.missingCategoryColumn)
				{
					//F3:
					//1. label -> category cols (prefixed) -> data cols
					//categories:1-n
					//labels: 0
					//data: (categoryCount+1)-n
					dataSet.labelsColumnIndex = 0 ;
					dataSet.categoryColumnIndices = getListOfInts(1, dataSet.numCategoryColumns);
					dataSet.dataColumnIndices = getListOfInts((dataSet.categoryColumnIndices.size() + 1), dataSet.numDataColumns);
				}
				else
				{
					//2. label -> data cols
					//labels: 0
					//data: 1-n
					dataSet.labelsColumnIndex = 0 ;
					dataSet.dataColumnIndices = getListOfInts(1, dataSet.numDataColumns);
				}
			}
			break;
		}
	}

	private ArrayList<Integer> getListOfInts(int start, int length)
	{
		ArrayList<Integer> list = new ArrayList<Integer>();

		for (int i = start; i < (start+length); i++)
			list.add(i);

		return list;
	}
}