package curlywhirly.data;

import java.io.*;
import java.text.*;
import java.util.*;
import curlywhirly.gui.*;
import scri.commons.gui.*;

public class DataLoader
{

	// ==========================================vars=========================================

	CurlyWhirly mainWin = CurlyWhirly.curlyWhirly;

	boolean errorInHeaders = false;

	final String categoryHeaderPrefix = "categories:";
	final String unclassifiedCategoriesStr = "unclassified";
	final String commentCharacter = "#";
	final String urlString = "URL=";


	ArrayList<String> comments = new ArrayList<String>();

	ColumnOrderFormatter columnOrderFormatter;


	// ==========================================methods=========================================

	public void loadDataInThread(File file)
	{
		//clear view
		if (mainWin.canvas3D != null)
			mainWin.canvas3D.clearCurrentView();

		//start the load in a separate thread
		DataLoadingDialog dataLoadingDialog = new DataLoadingDialog(mainWin, true);
		DataLoadingThread loader = new DataLoadingThread(mainWin,file,dataLoadingDialog);
		loader.setName("curlywhirly_dataload");
		loader.start();

		//show a dialog with a progress bar
		dataLoadingDialog.setLocationRelativeTo(mainWin);
		dataLoadingDialog.setVisible(true);
		dataLoadingDialog.setModal(false);
	}

	//--------------------------------------------------------------------------------------------------------------------------------------------------------------------------

	public void loadData(File file)
	{
		DataSet dataSet = null;
		CurlyWhirly.dataAnnotationURL = null;
		mainWin.dataLoaded = false;

		//load the data from file
		try
		{
			dataSet = parseFile(file);

			//process the comments
			processComments();
		}
		catch (IOException e)
		{
			e.printStackTrace();
			return;
		}

		//set up the new dataset
		//normalize first
		//this sets the data up so that each axis is normalized to between -1 and 1 and the data fills the whole range
		DataNormalizer.normalizeDataSet(dataSet);
		mainWin.dataSet = dataSet;

		//deal with the combo boxes
		if(mainWin.dataLoaded)
			mainWin.controlPanel.resetComboBoxes();
		else
			mainWin.controlPanel.addComboModels();

		//make a new scene graph
		mainWin.canvas3D.highlightAllCategories = true;
		mainWin.canvas3D.createSceneGraph(true);

		//do the rest of the set up
		//set the title of the window to the name of the dataset
		mainWin.setTitle(mainWin.titleString + "  --  " + dataSet.name);
		mainWin.controlPanel.setUpCategoryLists();
		mainWin.statusBar.setDefaultText();
		mainWin.repaint();

		//flag the fact we have data loaded
		mainWin.dataLoaded = true;


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

	private int getDataFormat(DataSet dataSet, String [] lines, String [] headers)
	{
		//iterate over headers and check how many category schemes we have
		int numPrefixedCategoryHeaders = 0;
		for (int i = 0; i < headers.length; i++)
		{
			//check for any columns that start with the defined categories prefix, and allow for cases where there are quotes around the headers
			if(headers[i].startsWith(categoryHeaderPrefix) || headers[i].startsWith("\"" + categoryHeaderPrefix))
			{
				dataSet.noCategoryHeaders = false;
				numPrefixedCategoryHeaders++;
			}
		}

		DataFormatDetector dataFormatDetector = new DataFormatDetector();
		int dataFormat = dataFormatDetector.detectDataFormat(dataSet, lines, numPrefixedCategoryHeaders, headers, categoryHeaderPrefix);

		return dataFormat;
	}


	// -------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------

	private void makeClassificationSchemesFromPrefixedColumns(String [] headers, DataSet dataSet)
	{
		for (int i = 0; i < headers.length; i++)
		{
			//check for any columns that start with the defined categories prefix, and allow for cases where there are quotes around the headers
			if(headers[i].startsWith(categoryHeaderPrefix) || headers[i].startsWith("\"" + categoryHeaderPrefix))
			{
				String schemeName = null;
				if(headers[i].startsWith("\"" + categoryHeaderPrefix))
					schemeName = headers[i].substring((categoryHeaderPrefix.length()+1));
				else
					schemeName = headers[i].substring(categoryHeaderPrefix.length());

				//make a new scheme object
				makeClassificationScheme(dataSet, schemeName, i);
			}
		}
	}

	// -------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------

	private void makeClassificationSchemes(int dataFormat, String [] headers, DataSet dataSet)
	{

		switch(dataFormat)
		{
			case 1:
			{
				//F1:
				//1. all data present:
				//Category data --> Labels for individual data points --> Data column 1 --> Data column 2 --> ..... Data column n
				//categories:0
				//labels: 1
				//data: 2-n
				//2. No Categories:
				//blank column --> Labels for individual data points --> Data column 1 --> Data column 2 --> ..... Data column n
				//categories:0 (but empty)
				//labels: 1
				//data: 2-n
				if(headers[0].equals(""))
				{
					dataSet.emptyClassificationScheme = true;
					makeClassificationScheme(dataSet, unclassifiedCategoriesStr,0);
				}
				else
				{
					makeClassificationScheme(dataSet, headers[0],0);
				}
				dataSet.numDataColumns = headers.length - (dataSet.classificationSchemes.size() +1);
				dataSet.dataColumnStart = dataSet.classificationSchemes.size() +1;
				dataSet.numCategoryColumns = 1;
			}
			break;
//
			case 2:
			{
				//F2:
				//1. category cols (prefixed) -> label -> data cols
				//categories:0-n
				//labels: categoryCount
				//data: (categoryCount+1)-n
				makeClassificationSchemesFromPrefixedColumns(headers, dataSet);
				dataSet.numDataColumns = headers.length - (dataSet.classificationSchemes.size() +1);
				dataSet.dataColumnStart = dataSet.classificationSchemes.size() +1;
				dataSet.numCategoryColumns = dataSet.classificationSchemes.size();
			}
			break;
//
			case 3:
			{
				if(!dataSet.missingCategoryColumn)
				{
					//F3:
					//1. label -> category cols (prefixed) -> data cols
					//categories:1-n
					//labels: 0
					//data: (categoryCount+1)-n
					makeClassificationSchemesFromPrefixedColumns(headers, dataSet);
					dataSet.numDataColumns = headers.length - (dataSet.classificationSchemes.size() +1);
					dataSet.dataColumnStart = dataSet.classificationSchemes.size() +1;
					dataSet.numCategoryColumns = dataSet.classificationSchemes.size();
				}
				//OR:
				else
				{
					//2. label -> data cols
					//labels: 0
					//data: 1-n
					dataSet.emptyClassificationScheme = true;
					makeClassificationScheme(dataSet, unclassifiedCategoriesStr,0);

					dataSet.numDataColumns = headers.length - 1;
					dataSet.dataColumnStart = 1;
					dataSet.numCategoryColumns = 0;
				}

			}
			break;
		}
	}


	// -------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------

	private void parseHeaders(String [] lines, DataSet dataSet, int commentCount) throws IOException
	{
		// parse the header and read the column names
		String[] headers = lines[commentCount].split("\t");
		//also keep a tab on all the headers we found (for testing mainly)
		dataSet.allHeaders = headers;

		int dataFormat = getDataFormat(dataSet, lines, headers);

		makeClassificationSchemes(dataFormat, headers, dataSet);

		for (int i = 0; i < dataSet.numDataColumns; i++)
		{
			String header = headers[(i + dataSet.dataColumnStart)];
			if (header == null || header.trim().equals(""))
			{
				errorInHeaders = true;
				throw new IOException(RB.getString("data.DataLoader.exception1"));
			}
			dataSet.dataHeaders.add(header);
		}

		columnOrderFormatter = new ColumnOrderFormatter();
		columnOrderFormatter.formatColumnOrders(dataFormat, dataSet);
	}



	// -------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------

	private void parseDataEntries(String [] lines, DataSet dataSet, int commentCount, int numCategorySchemes) throws IOException
	{
		//for each row in the dataset, starting at the line after the comments and headers
		// parse the data
		int numDataLinesParsed = 0;
		for (int i = (commentCount+1); i < lines.length; i++)
		{
			numDataLinesParsed++;

			//parse the line
			String[] line = lines[i].split("\t");

			//create a new data point object and add it to the dataset
			DataEntry dataEntry = new DataEntry();
			dataSet.dataEntries.add(dataEntry);

			//parse the data for it
			try
			{
				//for each category scheme we have
				for (ClassificationScheme scheme : dataSet.classificationSchemes)
				{
					//extract the value in the appropriate data cell
					String categoryValue = line[scheme.columnIndex];

					//this value may be empty if we have a legacy format without category information
					if(dataSet.emptyClassificationScheme || categoryValue.equals(""))
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
				dataEntry.label = line[dataSet.labelsColumnIndex];
			}
			catch (ArrayIndexOutOfBoundsException aix)
			{
				aix.printStackTrace();
				throw new IOException(RB.getString("data.DataLoader.exception2"));
			}
			// check the data labels are there
			if (dataEntry.label == null)
			{
				throw new IOException(RB.getString("data.DataLoader.exception3"));
			}

			//parse the columns with the numerical data
			NumberFormat nf = NumberFormat.getInstance();
			for (Integer j : dataSet.dataColumnIndices)
			{
				float value;
				try
				{
					String valueStr = line[j];
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
					throw new IOException(RB.getString("data.DataLoader.exception4"));
				}
			}
		}
	}


	// --------------------------------------------------------------------------------------------------------------------------------------------------------------------------


	// imports the data from file in the specified location
	public DataSet parseFile(File file) throws IOException
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
			parseHeaders(lines, dataSet, commentCount);
			int numCategorySchemes = dataSet.classificationSchemes.size();

			// parse the data
			parseDataEntries(lines, dataSet, commentCount , numCategorySchemes);

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

			String message = RB.format("data.DataLoader.error", lineWithError, e.getMessage());
			TaskDialog.error(message, RB.getString("text.close"));
			e.printStackTrace();
			throw new IOException(message);
		}

		return dataSet;
	}

	// --------------------------------------------------------------------------------------------------------------------------------------------------------------------------

	private void makeClassificationScheme(DataSet dataSet, String name, int columnIndex)
	{
		ClassificationScheme scheme = new ClassificationScheme();
		scheme.name = name;
		scheme.columnIndex = columnIndex;
		dataSet.classificationSchemes.add(scheme);
		dataSet.categorizationSchemesLookup.put(scheme.name, scheme);
	}

	// --------------------------------------------------------------------------------------------------------------------------------------------------------------------------

}// end class

