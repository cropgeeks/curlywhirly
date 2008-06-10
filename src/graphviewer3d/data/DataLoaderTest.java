package graphviewer3d.data;

import java.io.File;
import java.util.Vector;

public class DataLoaderTest
{
	public static void main(String[] args)
	{
		File file = new File("data/barley_PCA_original.txt");
		
		DataLoader loader = new DataLoader(null);		
		DataSet dataSet = loader.getDataFromFile(file);
//		dataSet.listAllData();		
	}
}
