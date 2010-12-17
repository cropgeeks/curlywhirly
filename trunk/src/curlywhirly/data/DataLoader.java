package curlywhirly.data;

import java.io.*;
import java.text.*;
import java.util.*;
import curlywhirly.gui.*;
import scri.commons.gui.*;

public class DataLoader
{
	
	// ==========================================vars=========================================
	
	CurlyWhirly frame;

	
	boolean errorInHeaders = false;
	String categoryHeaderPrefix = "categories:";
	String unclassifiedCategoriesStr = "unclassified";
	
	public int numDataColumns;
	
	String commentCharacter = "#";
	String urlString = "URL=";
	
	
	ArrayList<String> comments = new ArrayList<String>(); 
	
	// ==========================================c'tor=========================================
	
	public DataLoader(CurlyWhirly frame)
	{
		this.frame = frame;
	}
	
	// ==========================================methods=========================================

	public void loadDataInThread(File file)
	{
		//clear view
		if (frame.canvas3D != null)
			frame.canvas3D.clearCurrentView();
		
		//start the load in a separate thread
		DataLoadingDialog dataLoadingDialog = new DataLoadingDialog(frame, true);
		DataLoadingThread loader = new DataLoadingThread(frame,file,dataLoadingDialog);
		loader.setName("curlywhirly_dataload");
		loader.start();
		
		//show a dialog with a progress bar
		dataLoadingDialog.setLocationRelativeTo(frame);
		dataLoadingDialog.setVisible(true);
		dataLoadingDialog.setModal(false);
	}
	
	//--------------------------------------------------------------------------------------------------------------------------------------------------------------------------
	
	public void loadData(File file)
	{
		DataSet dataSet = null;
		CurlyWhirly.dataAnnotationURL = null;
		frame.dataLoaded = false;
		
		//load the data from file
		try
		{
			dataSet = getDataFromFile(file);
			
			//process the comments
			processComments();
		}
		catch (IOException e)
		{
			e.printStackTrace();
			return;
		}

//		System.out.println("data loaded successfully");
		
		//set up the new dataset and make a new scene graph
		//normalize first
		//this sets the data up so that each axis is normalized to between -1 and 1 and the data fills the whole range
		DataNormalizer.normalizeDataSet(dataSet);
		frame.dataSet = dataSet;
				
		//deal with the combo boxes
		if(frame.dataLoaded)
			frame.controlPanel.resetComboBoxes();
		else
			frame.controlPanel.addComboModels();

		//make a new scene graph
		frame.canvas3D.createSceneGraph(true);

		//do the rest of the set up
//		frame.controlPanel.getTabbedPane().removeAll();
		frame.controlPanel.setUpCategoryLists();
//		frame.controlPanel.getSchemeSelectorCombo().setModel(null);		
		
		frame.statusBar.setDefaultText();
		frame.repaint();
		
		//flag the fact we have data loaded
		frame.dataLoaded = true;
	}
	
	// -------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------

	private void processComments()
	{
		for (String comment : comments)
		{
			//check for a data annotation URL
			if(comment.startsWith((commentCharacter+urlString)))
			{
				String url = comment.substring((commentCharacter.length() + urlString.length()));
				//trim it in case there are any whitespace etc chars
				url = url.trim();
				//store this as the system wide url for this dataset
				CurlyWhirly.dataAnnotationURL = url;
				//and update the dialog box for the URLs in case the user wants to change it
				CurlyWhirly.menuBar.urlEntryForm.getDataURLTextField().setText(url);
			}
		}
	}
	
	
	// -------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------

	public String [] readFile(File file)
	{
		String[] lines = null;
		
		try
		{
			// read the file content
			StringBuilder sb = new StringBuilder((int) file.length());
			BufferedInputStream in = new BufferedInputStream(new FileInputStream(file));
			byte[] b = new byte[4096];
			for (int n; (n = in.read(b)) != -1;)
				sb.append(new String(b, 0, n));
			in.close();
			
			//split the file into lines
			lines = sb.toString().split("\n");
		}
		catch (FileNotFoundException e)
		{
			e.printStackTrace();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		
		return lines;
	}

	// -------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
	
	//check for comment lines in the header
	private int parseComments(String [] lines)
	{		
		int commentCount = 0;
		while(lines[commentCount].startsWith(commentCharacter))
		{
			comments.add(lines[commentCount]);
			commentCount++;
		}	
		
		return commentCount;
	}
	
	
	// -------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
	
	private boolean parseHeaders(String [] lines, DataSet dataSet, int commentCount) throws IOException
	{
		// parse the header and read the column names
		String[] headers = lines[commentCount].split("\t");
		//also keep a tab on all the headers we found (for testing mainly)
		dataSet.allHeaders = headers;
		
		//this flag indicates whether we have a legacy format file with no "categories:" prefix for the category data 
		boolean noCategoryHeaders = true;
		
		//iterate over headers and check how many category schemes we have 
		for (int i = 0; i < headers.length; i++)
		{
			//check for any columns that start with the defined categories prefix, and allow for cases where there are quotes around the headers
			if(headers[i].startsWith(categoryHeaderPrefix) || headers[i].startsWith("\"" + categoryHeaderPrefix))
			{			
				noCategoryHeaders = false;
				
				ClassificationScheme scheme = new ClassificationScheme();
				if(headers[i].startsWith("\"" + categoryHeaderPrefix))
					scheme.name = headers[i].substring((categoryHeaderPrefix.length()+1));
				else
					scheme.name = headers[i].substring(categoryHeaderPrefix.length());
				dataSet.classificationSchemes.add(scheme);
				dataSet.categorizationSchemesLookup.put(scheme.name, scheme);
//				System.out.println("new categorization scheme found: " + scheme.name);
			}
		}
		
		boolean singleClassificationScheme = true;
		if(dataSet.classificationSchemes.size() > 1)
			singleClassificationScheme = false;
		
		//we have a legacy format if we have no categories headers and only a single classification scheme
		boolean isLegacyData = noCategoryHeaders && singleClassificationScheme;		
		//the legacy format also supports an empty first column if there are not category data attached
		//check whether we have this situation
		// check they are there
		boolean emptyClassificationScheme = false;
		if(headers[0].equals(""))
		{
			emptyClassificationScheme = true;
			makeClassificationScheme(dataSet, unclassifiedCategoriesStr);
		}
		//also support the case of the missing "categories:" prefix and single classific. scheme in legacy data
		if(!headers[0].equals("") && isLegacyData)
			makeClassificationScheme(dataSet, headers[0]);
	
		// rest of headers are data column headers except for first one after the category ones
		//that column contains the labels
		numDataColumns = headers.length - (dataSet.classificationSchemes.size() +1);
//		System.out.println("numDataColumns = " + numDataColumns);
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
		
		return emptyClassificationScheme;
	}
	
	// -------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
	
	private void parseDataEntries(String [] lines, DataSet dataSet, int commentCount , boolean emptyClassificationScheme,int numCategorySchemes) throws IOException
	{
		//for each row in the dataset, starting at the line after the comments and headers
		// parse the data	
		int lastLineParsed = 0;
		int numDataLinesParsed = 0;
		for (int i = (commentCount+1); i < lines.length; i++)
		{		
			numDataLinesParsed++;
			
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
					
					//this value may be empty if we have a legacy format without category information
					if(emptyClassificationScheme || categoryValue.equals(""))
						categoryValue = unclassifiedCategoriesStr;
					
					//check whether there is a Category object with this name
					Category category = scheme.getCategoryByName(categoryValue);
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
				dataEntry.label = line[numCategorySchemes];
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
//					System.out.println("parsing value " + valueStr);
					value = nf.parse(valueStr).floatValue();
					dataEntry.dataValues.add(value);
				}
				catch (ParseException e)
				{
					e.printStackTrace();
				}
				catch (NumberFormatException e)
				{
					e.printStackTrace();
					throw new IOException("Missing or invalid numerical data ");
				}
			}
		}
	}
	
	
	// imports the data from file in the specified location
	public DataSet getDataFromFile(File file) throws IOException
	{
//		System.out.println("\n\n============loading new dataset: "+ file.getName());
		
		DataSet dataSet = new DataSet();
		
		int lastLineParsed = 0;
		
		try
		{
			// parse the file content
			// we assume a data structure of tab delimited text files where column 0 contains the group ID for the data, column 1 contains
			// the label to display, and the remainder of the columns contains data to be read in as floats
			String[] lines = readFile(file);
			
			// set the name of the dataset to be the name of the file
			dataSet.name = file.getName();
			
			//check for comment lines in the header
			int commentCount = parseComments(lines);
			
			//the number of data entries is the number of lines minus 1 for the header minus the number of comment lines
			int numEntries = lines.length - 1 - commentCount;
			dataSet.numEntries = numEntries;
			
			//parse the headers
			boolean emptyClassificationScheme  = parseHeaders(lines, dataSet, commentCount);
			int numCategorySchemes = dataSet.classificationSchemes.size();
			
			// parse the data	
			parseDataEntries(lines, dataSet, commentCount , emptyClassificationScheme, numCategorySchemes);
			
			//now that all the data is parsed we can associate colours with each of the categories
			//for each category scheme we have 
			for (ClassificationScheme scheme : dataSet.classificationSchemes)
			{
				scheme.assignColoursToCategories();
				scheme.makeNamesVector();
			}

			//now choose the first categorizationscheme as the one that is currently selected
			//the user can switch to another one later if they so wish
			CurlyWhirly.canvas3D.currentClassificationScheme = dataSet.classificationSchemes.get(0);			
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

		return dataSet;
	}
	
	// --------------------------------------------------------------------------------------------------------------------------------------------------------------------------
	private void makeClassificationScheme(DataSet dataSet, String name)
	{
		ClassificationScheme scheme = new ClassificationScheme();
		scheme.name = name;
		dataSet.classificationSchemes.add(scheme);
		dataSet.categorizationSchemesLookup.put(scheme.name, scheme);
	}
	
	// --------------------------------------------------------------------------------------------------------------------------------------------------------------------------
	
}// end class

