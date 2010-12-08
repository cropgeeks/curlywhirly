package curlywhirly.data;

import java.io.*;
import java.text.*;
import curlywhirly.gui.*;
import scri.commons.gui.*;

public class DataLoader
{
	
	// ==========================================vars=========================================
	
	CurlyWhirly frame;
	DataSet dataSet = new DataSet();
	
	boolean errorInHeaders = false;
	String categoryHeaderPrefix = "categories:";
	
	public int numDataColumns;
	
	// ==========================================c'tor=========================================
	
	public DataLoader(CurlyWhirly frame)
	{
		this.frame = frame;
	}
	
	// ==========================================methods=========================================
	
	// imports the data from file in the specified location
	public DataSet getDataFromFile(File file) throws IOException
	{
		int lastLineParsed = 0;
		
		try
		{
			// read the file content
			StringBuilder sb = new StringBuilder((int) file.length());
			BufferedInputStream in = new BufferedInputStream(new FileInputStream(file));
			byte[] b = new byte[4096];
			for (int n; (n = in.read(b)) != -1;)
				sb.append(new String(b, 0, n));
			in.close();
			
			// set the name of the dataset to be the name of the file
			dataSet.dataSetName = file.getName();
			
			// parse the file content
			// we assume a data structure of tab delimited text files where column 0 contains the group ID for the data, column 1 contains
			// the label to display, and the remainder of the columns contains data to be read in as floats
			String[] lines = sb.toString().split("\n");
			int numEntries = lines.length - 1;
			dataSet.numEntries = numEntries;
			
			// parse the header and read the column names
			String[] headers = lines[0].split("\t");
			//also keep a tab on all the headers we found (for testing mainly)
			dataSet.allHeaders = headers;
			
			//iterate over headers and check how many category schemes we have 
			for (int i = 0; i < headers.length; i++)
			{
				if(headers[i].startsWith(categoryHeaderPrefix))
				{				
					ClassificationScheme scheme = new ClassificationScheme();
					scheme.name = headers[i].substring(categoryHeaderPrefix.length());
					dataSet.classificationSchemes.add(scheme);
					dataSet.categorizationSchemesLookup.put(scheme.name, scheme);

					System.out.println("new categorization scheme found: " + scheme.name);
				}
			}
						
			// rest of headers are data column headers except for first one after the category ones
			//that column contains the labels
			numDataColumns = headers.length - (dataSet.classificationSchemes.size() +1);
			for (int i = 0; i < numDataColumns; i++)
			{
				String header = headers[i + (dataSet.classificationSchemes.size() +1)];
				if (header == null || header.trim().equals(""))
				{
					errorInHeaders = true;
					throw new IOException("Missing data header ");
				}
				dataSet.dataHeaders.add(header);
			}
			
			// parse the data
			// strip off the data header for this purpose
			sb = sb.delete(0, sb.indexOf("\n") + 1);
			lines = sb.toString().split("\n");
			
			int numCategorySchemes = dataSet.classificationSchemes.size();
			
			//for each row in the dataset
			for (int i = 0; i < numEntries; i++)
			{
//				System.out.println("\n=====new row in dataset");
				
				//parse the line
				lastLineParsed = i;
				String[] line = lines[i].split("\t");
				
				//create a new data point object and add it to the dataset
				DataEntry dataEntry = new DataEntry();
				dataSet.dataEntries.add(dataEntry);
				
				//parse the data for it
				try
				{
					//for each category scheme we have 
					for (int j = 0; j < dataSet.classificationSchemes.size(); j++)
					{
						//retrieve the appropriate scheme
						ClassificationScheme scheme = dataSet.classificationSchemes.get(j);
						//extract the value in this data cell
						String categoryValue = line[j];
						//check whether there is a Category object with this name
						Category category = scheme.categoryExists(categoryValue);
						//if there isn't
						if(category == null)
						{
							//make a new one and add it
							category = new Category();
							category.name = categoryValue;
							scheme.categories.add(category);
						}
						//set this on the data point
						dataEntry.categories.add(category);
					}
					
					//this is the label for the data point
					dataEntry.label = line[numCategorySchemes + 1];
				}
				catch (ArrayIndexOutOfBoundsException aix)
				{
					aix.printStackTrace();
					throw new IOException("Empty line ");
				}
				// check the data labels are there
				if (line[1].equals(""))
				{
					throw new IOException("Missing data label value");
				}
				
				NumberFormat nf = NumberFormat.getInstance();
				for (int j = 0; j < numDataColumns; j++)
				{
					float value;
					try
					{
						String valueStr = line[j + numCategorySchemes + 1];
//						System.out.println("parsing value " + valueStr);
						value = nf.parse(valueStr).floatValue();
						dataEntry.dataValues.add(value);
					}
					catch (NumberFormatException e)
					{
						e.printStackTrace();
						throw new IOException("Missing or invalid numerical data ");
					}
				}
			}
			
			//now that all the data is parsed we can associate colours with each of the categories
			//for each category scheme we have 
			for (ClassificationScheme scheme : dataSet.classificationSchemes)
				scheme.assignColoursToCategories();

			//now choose the first categorizationscheme as the one that is currently selected
			//the user can switch to another one later if they so wish
			MainCanvas.currentClassificationScheme = dataSet.classificationSchemes.get(0);
			System.out.println("current category scheme = " + MainCanvas.currentClassificationScheme.name);
		}
		catch (Exception e)
		{
			int lineWithError = lastLineParsed +2;
			if(errorInHeaders)
				lineWithError = lastLineParsed+1;
			
			String message = "Error in data file on line " + lineWithError + ":\n" + e.getMessage() + " -- please correct your data and try again.";
			TaskDialog.initialize(frame, "Data error");
			TaskDialog.info(message, "Close");
			e.printStackTrace();
			throw new IOException(message);
		}
		
		System.out.println("data loaded successfully");
		
		return dataSet;
	}
	
	// --------------------------------------------------------------------------------------------------------------------------------------------------------------------------
}// end class

