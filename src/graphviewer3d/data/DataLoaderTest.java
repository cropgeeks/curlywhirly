package graphviewer3d.data;

import java.io.File;
import java.util.Vector;

public class DataLoaderTest
{
	public static void main(String[] args)
	{
		File file = new File("pco_data.txt");
		
		DataLoader loader = new DataLoader();		
		DataSet dataSet = loader.getDataFromFile(file);
//		dataSet.listAllData();		
	}
}
