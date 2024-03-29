// Copyright 2009-2022 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package jhi.curlywhirly.io;

import java.awt.*;
import java.io.*;
import java.util.*;

import jhi.curlywhirly.data.*;
import jhi.curlywhirly.util.*;
import jhi.curlywhirly.util.ColorPrefs.*;

import scri.commons.gui.*;

public class DataExporter extends SimpleJob
{
	private static final String LABEL_IDENTIFIER = "label";
	private static final String CATEGORY_IDENTIFIER = "categories:";
	private static final String MISSING_CATEGORY = "Uncategorised";
	private static final String URL_IDENTIFIER = "# cwDatabaseLineSearch=";
	private static final String GROUP_IDENTIFIER = "# cwDatabaseGroupPreview=";
	private static final String UPLOAD_IDENTIFIER = "# cwDatabaseGroupUpload=";
	private static final String COLOR_IDENTIFIER = "# color=";

	private final DataSet dataSet;
	private final File outputFile;

	public DataExporter(DataSet dataSet, File outputFile)
	{
		this.dataSet = dataSet;
		this.outputFile = outputFile;
	}

	@Override
	public void runJob(int jobId)
		throws Exception
	{
		System.out.println("Exporting data");
		try (PrintWriter writer = new PrintWriter(outputFile, "UTF-8"))
		{
			// Output a db URL for point lookup if there is one
			String dbUrl = dataSet.getDbAssociation().getDbPointUrl();
			if (dbUrl != null && dbUrl.isEmpty() == false)
				writer.println(URL_IDENTIFIER + dbUrl);

			String groupUrl = dataSet.getDbAssociation().getDbGroupUrl();
			if (groupUrl != null && groupUrl.isEmpty() == false)
				writer.println(GROUP_IDENTIFIER + groupUrl);

			String uploadUrl = dataSet.getDbAssociation().getDbUploadUrl();
			if (uploadUrl != null && uploadUrl.isEmpty() == false)
				writer.println(UPLOAD_IDENTIFIER + uploadUrl);

			for (CategoryGroup group : dataSet.getCategoryGroups())
				for (Category category : group.getCategories())
					writer.println(COLOR_IDENTIFIER + category.getColorKey() + "::CW::" + category.getRGBString());

			// Export user selected colors for UI elements
			HashMap<String, ColorPref> colors = ColorPrefs.getColors();

			for (String key : colors.keySet())
			{
				if (key.startsWith("User"))
				{
					Color color = colors.get(key).getColor();
					writer.println(COLOR_IDENTIFIER + key + "::CW::" + String.format("rgb(%s,%s,%s)", color.getRed(), color.getGreen(), color.getBlue()));
				}
			}

			// Output the header line
			String header = getHeaderString();
			writer.println(header);

			// Get the data for each point and write it out to file
			dataSet.getDataPoints().forEach(dataPoint ->
			{
				String point = getDataPointString(dataPoint);
				writer.println(point);
			});
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	private String getHeaderString()
	{
		StringBuilder header = new StringBuilder();

		// Add categories to the header
		for (CategoryGroup group : dataSet.getCategoryGroups())
			header.append(CATEGORY_IDENTIFIER).append(group.getName()).append('\t');

		header.append(LABEL_IDENTIFIER).append('\t');

		// Add axis labels to the header
		for (String label : dataSet.getAxes().getAxisLabels())
			header.append(label).append('\t');

		return header.toString().trim();
	}

	private String getDataPointString(DataPoint dataPoint)
	{
		StringBuilder point = new StringBuilder("");

		// Output the categories assocaited with this data point (in category
		// group order, hence the loop over the dataset's category groups)
		for (CategoryGroup group : dataSet.getCategoryGroups())
		{
			Category category = group.getCategoryForPoint(dataPoint);
			point.append(category.getName());
			point.append('\t');
		}

		// Add the name, followed by the original (non-normalized) datapoint values
		point.append(dataPoint.getName()).append('\t');
		for (Float value : dataPoint.getValues())
			point.append(value).append('\t');

		return point.toString().trim();
	}
}