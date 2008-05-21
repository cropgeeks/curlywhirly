package graphviewer3d.data;

import java.util.Vector;

public class DataLoaderTest
{
	public static void main(String[] args)
	{
		String filePath = "E:\\SVNSandbox\\graphViewer3D\\pco_data.txt";
		
		DataLoader loader = new DataLoader();		
		DataSet dataSet = loader.getDataFromFile(filePath);
		dataSet.listAllData();
		
		Vector<String> vec = dataSet.getCategories();
		
		System.out.println("num categories = " + vec.size());
		
	}
}
