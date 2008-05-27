package graphviewer3d.data;

import graphviewer3d.gui.GUIUtils;

import java.util.HashMap;
import java.util.Vector;

import javax.vecmath.Color3f;

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
	
	//a map containing the catgory names as keys and Category objects as values which hold the attributes for each category
	public HashMap<String, Category> categoryMap;
		
	
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
	public HashMap<String, Category> extractCategories()
	{		
		// now add things into a map
		categoryMap = new HashMap<String, Category>();

		for (int i = 0; i < groupIds.length; i++)
		{	
				String groupId = groupIds[i];
				//if the map does not contain the category yet, we add it
				if (categoryMap.get(groupId) == null)
				{
					//make a new category object and add it 
					Category category = new Category();	
					//by default set the highlight flag to true so that  the category shows initially
					category.highlight = true;
					//set its name
					category.name = groupId;
					categoryMap.put(groupId,category);
				}
		}

		//now we know the number of categories and we can create a colour scheme and apply it
		Color3f[] colours = GUIUtils.generateColours(categoryMap.keySet().size());
		int i =0;
		for (Category category : categoryMap.values())
		{
			category.colour = colours[i];
			System.out.println("setting colour for category " + category.name + " to " + colours[i].get().toString());
			i++;
		}	
		
		return categoryMap;
	}
	
//------------------------------------------------------------------------------------------------------------------------------------------------------------------------------	
	
	
	
}//end class
