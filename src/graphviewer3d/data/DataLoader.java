package graphviewer3d.data;

import graphviewer3d.gui.*;
import java.io.*;
import java.text.*;
import scri.commons.gui.*;

public class DataLoader
{

	// ==========================================vars=========================================

	GraphViewerFrame frame;
	DataSet dataSet = new DataSet();

	boolean errorInHeaders = false;

	// ==========================================c'tor=========================================

	public DataLoader(GraphViewerFrame frame)
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
			dataSet.groupIdHeader = headers[0];
			dataSet.groupLabelHeader = headers[1];
			// check they are there
			if (headers[0].trim().equals(""))
			{

			}
			if(headers[1].trim().equals(""))
			{
				errorInHeaders = true;
				throw new IOException("Missing group label header ");
			}

			// rest of headers are data column headers
			int numDataColumns = headers.length - 2;
			for (int i = 0; i < numDataColumns; i++)
			{
				String header = headers[i + 2];
				if (header == null || header.trim().equals(""))
				{
					errorInHeaders = true;
					throw new IOException("Missing data header ");
				}
				dataSet.dataHeaders.add(header);
				// add a new float array to the data Vector
				dataSet.data.add(new float[numEntries]);
			}

			// parse the data
			// strip off the data header for this purpose
			sb = sb.delete(0, sb.indexOf("\n") + 1);
			lines = sb.toString().split("\n");

			// init arrays first
			dataSet.groupIds = new String[numEntries];
			dataSet.groupLabels = new String[numEntries];

			for (int i = 0; i < numEntries; i++)
			{
				lastLineParsed = i;
				String[] line = lines[i].split("\t");
				try
				{
					dataSet.groupIds[i] = line[0];
					dataSet.groupLabels[i] = line[1];
				}
				catch (ArrayIndexOutOfBoundsException aix)
				{
					aix.printStackTrace();
					throw new IOException("Empty line ");
				}
				// check they are there
				if (line[0].equals(""))
				{
					line[0] = "unspecified category";
//					throw new IOException("Missing category label value");
				}
				if (line[1].equals(""))
				{
					throw new IOException("Missing data label value");
				}

				NumberFormat nf = NumberFormat.getInstance();
				for (int j = 0; j < numDataColumns; j++)
				{
					float[] array = dataSet.data.get(j);
					float value;
					try
					{
						value = nf.parse(line[j + 2]).floatValue();
					}
					catch (NumberFormatException e)
					{
						e.printStackTrace();
						throw new IOException("Missing or invalid numerical data ");
					}
					array[i] = value;
				}
			}


			// set up the vector of discrete categories on the dataset object itself
			dataSet.extractCategories();

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

