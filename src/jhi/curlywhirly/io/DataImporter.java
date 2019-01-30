// Copyright 2009-2017 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package jhi.curlywhirly.io;

import java.awt.*;
import java.io.*;
import java.nio.charset.*;
import java.util.*;
import java.util.regex.*;
import java.util.stream.*;
import javax.swing.*;

import jhi.curlywhirly.data.*;
import jhi.curlywhirly.gui.*;
import jhi.curlywhirly.gui.dialog.*;
import jhi.curlywhirly.util.*;

import scri.commons.io.*;
import scri.commons.gui.*;

import static java.util.stream.Collectors.*;

public class DataImporter extends SimpleJob
{
	private static final String LABEL_IDENTIFIER = "label";
	private static final String CATEGORY_IDENTIFIER = "categories:";
	private static final String MISSING_CATEGORY = "Uncategorised";
	private static final String POINT_URL = "cwDatabaseLineSearch";
	private static final String GROUP_URL = "cwDatabaseGroupPreview";
	private static final String UPLOAD_URL = "cwDatabaseGroupUpload";
	private static final String COLOR_IDENTIFIER = "color";
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

	private final HashMap<String, ArrayList<Float>> pointValues = new HashMap<>();
	private final HashMap<Category, ArrayList<String>> categoryPoints = new HashMap<>();
	private final HashMap<String, DataPoint> pointsByName = new HashMap<>();

	private int lineCount = 0;
	private int expectedTokenCount = -1;

	private String dbURL = "";
	private String groupUrl = "";
	private String uploadUrl = "";

	private ArrayList<String> duplicates = new ArrayList<>();

	public DataImporter(File file)
	{
		this.file = file;

		maximum = 5555;
	}

	@Override
	public void runJob(int i)
		throws Exception
	{
		totalBytes = file.length();

		is = new ProgressInputStream(new FileInputStream(file));
		BufferedReader reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));

		// Scan the start of the file for the required header line
		String[] header = getHeaderLine(reader);
		processHeader(header);

		// Once the header has been processed we can process the rest of the file
		readFile(reader);

		// We need to assign colors to our categories
		categoryGroups.forEach(group ->
		{
			// Also sort the category groups at this point
			group.sort();
			assignColorsToCategories(group);
		});

		createDataPoints();
		addDataPointsToCategories();
		addDataPointsToCategoryGroups();

		dataSet = new DataSet(file.getName(), dataPoints, categoryGroups, axisLabels);

		// Finally set the link to access the database
		dataSet.getDbAssociation().setDbPointUrl(dbURL);
		dataSet.getDbAssociation().setDbGroupUrl(groupUrl);
		dataSet.getDbAssociation().setDbUploadUrl(uploadUrl);
	}

	private void createDataPoints()
	{
		ArrayList<Float> allPointValues = pointValues.values().stream()
			.flatMap(Collection::stream)
			.collect(toCollection(ArrayList::new));

		DataNormalizer dataNormalizer = new DataNormalizer(allPointValues);

		HashMap<String, ArrayList<Float>> normalizedValues = (HashMap<String, ArrayList<Float>>)pointValues.entrySet().stream()
			.collect(toMap(Map.Entry::getKey, e -> dataNormalizer.normalizeValues(e.getValue().stream())));

		pointValues.keySet().forEach(name ->
		{
			DataPoint point = new DataPoint(name, pointValues.get(name), normalizedValues.get(name));
			dataPoints.add(point);
			pointsByName.put(name, point);
		});
	}

	private void addDataPointsToCategories()
	{
		for (Category category : categoryPoints.keySet())
		{
			for (String name : categoryPoints.get(category))
				category.addDataPoint(pointsByName.get(name));
		}
	}

	private void addDataPointsToCategoryGroups()
	{
		for (CategoryGroup group : categoryGroups)
		{
			for (Category category : group.getCategories())
			{
				ArrayList<DataPoint> points = categoryPoints.get(category).stream()
					.map(pointsByName::get)
					.collect(toCollection(ArrayList::new));
				group.addPointsForCategory(category, points);
			}
		}
	}

	// Methods for processing the file's header

	private String[] getHeaderLine(BufferedReader reader)
		throws ReadException, IOException
	{
		String str;
		while ((str = reader.readLine()) != null)
		{
			lineCount++;

			if (str.startsWith("#"))
			{
				String key = str.toLowerCase().substring(1, str.indexOf('=')).trim();

				// Parse out URL for database point linking functionality
				if (key.equalsIgnoreCase(POINT_URL))
				{
					dbURL = str.substring(str.indexOf('=') + 1).trim();
				}

				// Parse out url needed to create a group preview in germinate
				else if (key.equalsIgnoreCase(GROUP_URL))
				{
					groupUrl = str.substring(str.indexOf('=') + 1).trim();
				}

				// Parse out url needed to upload info needed to create a group preview in germinate
				else if (key.equalsIgnoreCase(UPLOAD_URL))
				{
					uploadUrl = str.substring(str.indexOf('=') + 1).trim();
				}

				else if (key.equalsIgnoreCase(COLOR_IDENTIFIER))
				{
					String colorString = str.substring(str.indexOf('=')+1);
					String[] tokens = colorString.split(COLOR_DELIMITER);
					String colorKey = tokens[0];

					// Parse either an rgb string, or integer based representation of a color
					Color color;
					if (tokens[1].toLowerCase().startsWith("rgb"))
						color = parseRgbColor(tokens[1]);
					else
						color = new Color(Integer.parseInt(tokens[1]));

					if (color != null && (ColorPrefs.get(colorKey) == null || Prefs.ioUseFileColors))
						ColorPrefs.setColor(colorKey, color);
				}
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

		Stream.of(columns)
			.filter(col -> col.toLowerCase().startsWith(CATEGORY_IDENTIFIER))
			.forEach(c ->
			{
				String name = c.substring(c.indexOf(':')+1);
				if (name.isEmpty())
					name = MISSING_CATEGORY;
				categoryGroups.add(new CategoryGroup(name));
			});

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

		// Read all the values associated with the data point
		String[] valuesArray = Arrays.copyOfRange(tokens, labelColumn+1, tokens.length);
		ArrayList<Float> values = getValuesForDataPoint(valuesArray);

		if (pointValues.containsKey(name))
		{
			duplicates.add(name);
		}
		else
		{
			pointValues.put(name, values);

			// Read the category this data point is classified by for each group
			String[] categoryNames = Arrays.copyOf(tokens, labelColumn);
			HashMap<CategoryGroup, Category> categories = getCategoriesForDataPoint(categoryNames);

			// Categories also need to know which points they are associated with.
			for (Category category : categories.values())
			{
				categoryPoints.putIfAbsent(category, new ArrayList<String>());
				ArrayList<String> points = categoryPoints.get(category);
				points.add(name);
				categoryPoints.put(category, points);
			}
		}
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

		ArrayList<Float> values = new ArrayList<>();
		for (String value : vals)
			values.add(Float.parseFloat(value.trim()));

		return values;
	}

	private void assignColorsToCategories(CategoryGroup group)
	{
		Color[] colours = GUIUtils.generateColours(group.size());
		int i=0;
		for (Category category : group)
		{
			// Check if a color preference already exists for this category
			// in this category group
			String key = category.getColorKey();
			Color color = ColorPrefs.getColor(key);
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

	public void displayDuplicates()
	{
		if (!duplicates.isEmpty())
		{
			Runnable r = () -> new DuplicateDataPointsDialog(duplicates);

			try { SwingUtilities.invokeLater(r); }
			catch (Exception e) {}
		}
	}

	// Adapted from the code at the following URL: https://stackoverflow.com/a/7614202
	public Color parseRgbColor(String input)
	{
		Pattern c = Pattern.compile("rgb *\\( *([0-9]+), *([0-9]+), *([0-9]+) *\\)");
		Matcher m = c.matcher(input);

		if (m.matches())
		{
			return new Color(Integer.valueOf(m.group(1)),  // r
				Integer.valueOf(m.group(2)),  // g
				Integer.valueOf(m.group(3))); // b
		}

		return null;
	}

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