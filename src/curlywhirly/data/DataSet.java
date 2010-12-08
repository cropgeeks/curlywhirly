package curlywhirly.data;


import java.io.IOException;
import java.util.*;

import javax.vecmath.Color3f;
import curlywhirly.gui.*;

public class DataSet
{
	
//=======================================================vars==================================	
	
	//a general name for the dataset
	public String dataSetName;
	
	//the list of categorization schemes that can be applied to the data, and a corresponding name based lookup
	public LinkedList<ClassificationScheme> classificationSchemes = new LinkedList<ClassificationScheme>();
	public HashMap<String, ClassificationScheme> categorizationSchemesLookup = new HashMap<String, ClassificationScheme>();
	
	//the headers for all the columns
	public String[] allHeaders;
	
	//the headers for data columns only
	public Vector<String> dataHeaders = new Vector<String>();

	//the number of entries in the dataset
	public int numEntries;
	
	//this contains the actual entries inthe dataset
	public LinkedList<DataEntry> dataEntries = new LinkedList<DataEntry>();
		
	
//=======================================================methods==================================	
	
	//prints all data to stdout in the original order
	public void listAllData()
	{
		//print headers
		System.out.println("dataSetName = " + dataSetName);

		for (int i = 0; i < allHeaders.length; i++)
		{
			System.out.print(allHeaders[i]);
			System.out.print("\t");
		}
		
		//print data
		for(DataEntry dataEntry : dataEntries)
			dataEntry.printAsLine();

	}

//------------------------------------------------------------------------------------------------------------------------------------------------------------------------------	
	
	
	
}//end class
