package graphviewer3d.data;

import java.io.*;

public class DataLoaderTest
{
	public static void main(String[] args)
	{
		try
		{
			File file = new File("data/rubus.txt");		
			DataLoader loader = new DataLoader(null);		
			DataSet dataSet = loader.getDataFromFile(file);
			
			System.out.println("\n\n================printing raw data:");
			dataSet.listAllData();
			
			//this sets the data up so that each axis is normalized to between -1 and 1 and the data fills the whole range 
			DataNormalizer.normalizeDataSet(dataSet);	
			System.out.println("\n\n################printing normalized data:");
			dataSet.listAllData();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}		
	}
}
