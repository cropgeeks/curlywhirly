package curlywhirly.data;


import java.io.IOException;
import java.util.*;

import javax.vecmath.Color3f;
import curlywhirly.gui.*;

public class DataSet
{
	
//=======================================================vars==================================	
	
	//a general name for the dataset
	public String name;
	
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
	
	public int numDataColumns;
	public int numCategoryColumns;
	
	//the legacy format also supports an empty first column if there are not category data attached
	//checks whether we have this situation
	boolean emptyClassificationScheme = false;	
	boolean singleClassificationScheme = false;
	boolean missingCategoryColumn = false;
	boolean emptyCategoryColumn = false;
	//this flag indicates whether we have a legacy format file with no "categories:" prefix for the category data 
	boolean noCategoryHeaders = true;
	
	public int labelsColumnIndex;
	public LinkedList<Integer> categoryColumnIndices;
	public LinkedList<Integer> dataColumnIndices;
	public int dataColumnStart = -1;
		
	
//=======================================================methods==================================	
	
	//prints all data to stdout in the original order
	public void listAllData(boolean normalised)
	{
		//print headers
		System.out.println("dataSetName = " + name);

		for (int i = 0; i < allHeaders.length; i++)
		{
			System.out.print(allHeaders[i]);
			System.out.print("\t");
		}
		
		System.out.println();
		
		//print data
		for(DataEntry dataEntry : dataEntries)
			dataEntry.printAsLine(normalised);

	}

//------------------------------------------------------------------------------------------------------------------------------------------------------------------------------	
	
	
	
}//end class
