package graphviewer3d.data;

import java.util.Vector;

public class DataSet
{
	
//=======================================================vars==================================	
	
	//a general name for the dataset
	public String dataSetName;
	
	//the name used as header for the group ids
	public String groupIdHeader;
	
	//the name used as header for the group labels
	public String groupLabelHeader;
	
	//the headers for the data columns
	public Vector<String> dataHeaders = new Vector<String>();
	
	//the actual values of the group ids
	public String[] groupIds;
	
	//actual values of the group labels
	public String[] groupLabels;
	
	//the values of the raw data
	public Vector<float[]> data = new Vector<float[]>();
		
	//variables that store the max and min values of the data in this dataset
	public float absoluteMax = 0;
	public float absoluteMin = 0;
	
	//the number of entries in the dataset
	public int numEntries;
	
	//the index of the float array in the above vector which is currently selected for  display on the x axis
	public int currentXIndex = 0;
	//the index of the float array in the above vector which is currently selected for  display on the y axis
	public int currentYIndex = 1;
	//the index of the float array in the above vector which is currently selected for  display on the z axis
	public int currentZIndex = 2;
	//these default to the first three columns of data in the dataset
	
	
//=======================================================methods==================================	
	
	//prints all data to stdout in the original order
	public void listAllData()
	{
		//print headers
		System.out.println("dataSetName = " + dataSetName);
		System.out.print(groupIdHeader + "\t" + groupLabelHeader + "\t");
		for (String dataHeader : dataHeaders)
		{
			System.out.print(dataHeader);
			System.out.print("\t");
		}
		
		//print data
		for (int i = 0; i < groupIds.length; i++)
		{
			System.out.print(groupIds[i]);
			System.out.print("\t");
			System.out.print(groupLabels[i]);
			System.out.print("\t");
			for (float[] dataArray : data)
			{
				System.out.print(dataArray[i]);
				System.out.print("\t");
			}
			System.out.println();
		}
	}
	
	
	//---------------------------------------------------------------------------------------------------------------------------------------------------------------------------	
	
	//iterates over the data in the categories array and returns the discrete categories we have
	public Vector<String> getCategories()
	{
		//we will store our unique instances of the categories in here
		Vector<String> categories = new Vector<String>();
		
		for (int i = 0; i < groupIds.length; i++)
		{
			String label = groupIds[i].trim();
			//if the vector does not contain the label yet, we add it
			if(!categories.contains(label))
			{
				categories.add(label);
			}
		}
		
		return categories;		
	}
	
//------------------------------------------------------------------------------------------------------------------------------------------------------------------------------	
	
	
	
}//end class
