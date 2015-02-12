// Copyright 2009-2014 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package curlywhirly.io;

import java.awt.Color;
import java.io.*;
import java.util.*;

import curlywhirly.data.*;
import curlywhirly.gui.*;
import curlywhirly.gui.viewer.ColorPrefs;

import scri.commons.file.*;
import scri.commons.gui.*;

public class DataImporter extends SimpleJob
{
	private static final String LABEL_IDENTIFIER = "label";
	private static final String CATEGORY_IDENTIFIER = "categories:";
	private static final String MISSING_CATEGORY = "Uncategorised";
	private static final String URL_IDENTIFIER = "#url=";
	private static final String COLOR_IDENTIFIER = "#color=";
	private static final String COLOR_DELIMITER = "::CW::";

	private File file;
	private ProgressInputStream is;

	private long totalBytes;

	private DataSet dataSet;
	private ArrayList<DataPoint> dataPoints;

	private int labelColumn = -1;
	private ArrayList<CategoryGroup> categoryGroups;
	private String[] axisLabels;
	private ArrayList<HashMap<String, Category>> categoriesToGroups;
	private final HashMap<DataPoint, HashMap<CategoryGroup, Category>> pointCategories;

	private int lineCount = 0;
	private int expectedTokenCount = -1;

	private String dbURL = "";

	public DataImporter(File file)
	{
		this.file = file;

		pointCategories = new HashMap<DataPoint, HashMap<CategoryGroup, Category>>();

		maximum = 5555;
	}

	@Override
	public void runJob(int i)
		throws Exception
	{
		totalBytes = file.length();

		is = new ProgressInputStream(new FileInputStream(file));
		BufferedReader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));

		// Scan the start of the file for the required header line
		String[] header = getHeaderLine(reader);
		processHeader(header);

		// Once the header has been processed we can process the rest of the file
		readFile(reader);

		// We need to assign colors to our categories
		for (CategoryGroup group : categoryGroups)
		{
			// Also sort the category groups at this point
			group.sort();
			assignColorsToCategories(group);
		}

		dataSet = new DataSet(file.getName(), dataPoints, categoryGroups, axisLabels, pointCategories);

		// The values in the dataset can only been normalized once we've
		// read all the values in the dataset
		DataNormalizer normalizer = new DataNormalizer();
		normalizer.normalize(dataSet);

		// Finally set the link to access the database
		dataSet.getDbAssociation().setDbPointUrl(dbURL);
	}

	// Methods for processing the file's header

	private String[] getHeaderLine(BufferedReader reader)
		throws ReadException, IOException
	{
		String str;
		while ((str = reader.readLine()) != null)
		{
			lineCount++;

			// TODO Consider what to do about URL lines
			if (str.toLowerCase().startsWith(URL_IDENTIFIER))
				dbURL = str.substring(str.indexOf('=')+1);

			if (str.startsWith(COLOR_IDENTIFIER))
			{
				String colorString = str.substring(str.indexOf('=')+1);
				String[] tokens = colorString.split(COLOR_DELIMITER);
				String key = tokens[0];
				Color color = new Color(Integer.parseInt(tokens[1]));
				if (ColorPrefs.get(key) == null || Prefs.ioUseFileColors)
					ColorPrefs.setColor(key, color);
			}

			// Is there a better way to identify the "header" line
			else if (str.toLowerCase().startsWith(CATEGORY_IDENTIFIER))
				return str.split("\t");
		}

		throw new ReadException(file, 0, ReadException.MISSING_HEADER);
	}

	private void processHeader(String[] header)
		throws ReadException
	{
		labelColumn = getLabelColumnIndex(header);
		categoryGroups = getCategoryGroups(header);
		axisLabels = getAxisLabels(header);

		expectedTokenCount = categoryGroups.size() + axisLabels.length + 1;

		categoriesToGroups = new ArrayList<>();
		for (CategoryGroup group : categoryGroups)
			categoriesToGroups.add(new HashMap<String, Category>());
	}

	private int getLabelColumnIndex(String[] columns)
		throws ReadException
	{
		for (int i=0; i < columns.length; i++)
			if (columns[i].trim().equalsIgnoreCase(LABEL_IDENTIFIER))
				labelColumn = i;

		if (labelColumn == -1)
			throw new ReadException(file, lineCount, ReadException.MISSING_NAME_HEADER);

		return labelColumn;
	}

	private ArrayList<CategoryGroup> getCategoryGroups(String[] columns)
	{
		categoryGroups = new ArrayList<>();

		for (int i=0; i < columns.length; i++)
		{
			String column = columns[i];
			if (column.toLowerCase().trim().startsWith(CATEGORY_IDENTIFIER))
			{
				String name = column.substring(column.indexOf(':')+1, column.length());
				if (name.isEmpty())
					name = MISSING_CATEGORY;
				categoryGroups.add(new CategoryGroup(name));
			}
		}

		return categoryGroups;
	}

	private String[] getAxisLabels(String[] columns)
	{
		String[] labels = Arrays.copyOfRange(columns, labelColumn+1, columns.length);
		for (int i=0; i < labels.length; i++)
			labels[i] = labels[i].trim();

		return labels;
	}

	// Methods for reading the body of the file

	private void readFile(BufferedReader reader)
		throws Exception
	{
		dataPoints = new ArrayList<>();

		String str;
		while ((str = reader.readLine()) != null)
		{
			lineCount++;

			// Only attempt to process lines which are likely to be data points.
			if (str.isEmpty() == false && str.charAt(0) != '#' &&
				str.toLowerCase().startsWith(CATEGORY_IDENTIFIER) == false)
			{
				String[] tokens = str.split("\t");
				processDataPoint(tokens);
			}
		}
	}

	private void processDataPoint(String[] tokens)
		throws Exception
	{
		// If the line has the wrong number of tokens throw an appropriate error.
		if (tokens.length != expectedTokenCount)
			throw new ReadException(file, lineCount, ReadException.TOKEN_COUNT_WRONG);

		String name = tokens[labelColumn];

		// Read the category this data point is classified by for each group
		String[] categoryNames = Arrays.copyOf(tokens, labelColumn);
		HashMap<CategoryGroup, Category> categories = getCategoriesForDataPoint(categoryNames);

		// Read all the values associated with the data point
		String[] valuesArray = Arrays.copyOfRange(tokens, labelColumn+1, tokens.length);
		ArrayList<Float> values = getValuesForDataPoint(valuesArray);

		DataPoint dataPoint = new DataPoint(name, values);
		dataPoints.add(dataPoint);
		pointCategories.put(dataPoint, categories);

		// Categories also need to know which points they are associated with.
		for (Category category : categories.values())
			category.addDataPoint(dataPoint);
	}

	private HashMap<CategoryGroup, Category> getCategoriesForDataPoint(String[] names)
		throws ReadException
	{
		// If the line has the wrong number of categories throw an appropriate exception.
		if (names.length != categoryGroups.size())
			throw new ReadException(file, lineCount, ReadException.CATEGORY_COUNT_WRONG);

		// Each data point stores a HashMap of CategoryGroups to Categories to
		// allow fast lookup of categories by CategoryGroup.
		HashMap<CategoryGroup, Category> categories = new HashMap<>();
		for (int i=0; i < names.length; i++)
		{
			CategoryGroup group = categoryGroups.get(i);
			HashMap<String, Category> groupCategories = categoriesToGroups.get(i);

			// If the category is an empty string, replace this with our
			// missing category text.
			String name = names[i].isEmpty() ? MISSING_CATEGORY : names[i].trim();

			Category found = groupCategories.get(name);
			if (found == null)
			{
				found = new Category(name, group.getName());
				groupCategories.put(name, found);
				group.add(found);
			}
			categories.put(group, found);
		}

		return categories;
	}

	private ArrayList<Float> getValuesForDataPoint(String[] vals)
		throws NumberFormatException, ReadException
	{
		// If the incorrect number of values is found, throw an appropriate exception.
		if (vals.length != axisLabels.length)
			throw new ReadException(file, lineCount, ReadException.VALUE_COUNT_WRONG);

		ArrayList<Float> values = new ArrayList<Float>();
		for (String value : vals)
			values.add(Float.parseFloat(value.trim()));

		return values;
	}

	// Assigns colors to each category on a per group basis
	// TODO: If we're offering user selectable color pallete's this might need
	// to go somewhere more generic
	private void assignColorsToCategories(CategoryGroup group)
	{
		Color[] colours = GUIUtils.generateColours(group.size());
		int i=0;
		for (Category category : group)
		{
			// Check if a color preference already exists for this category
			// in this category group
			String key = category.getColorKey();
			Color color = ColorPrefs.get(key);
			if (color == null)
			{
				color = colours[i++];
				ColorPrefs.setColor(key, color);
			}

			category.setColour(color);
		}
	}

	public DataSet getDataSet()
		{ return dataSet; }

	public File getFile()
		{ return file; }

	// Methods overriden from SimpleJob

	@Override
	public int getValue()
	{
		return Math.round((is.getBytesRead() / (float) totalBytes) * 5555);
	}

	@Override
	public String getMessage()
	{
		return RB.format("io.DataImporter.message", dataPoints.size());
	}
}