// Copyright 2009-2015 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package jhi.curlywhirly.analysis;

import java.io.*;
import java.util.*;

import jhi.curlywhirly.data.*;

import scri.commons.gui.*;

public class DataPointSaver extends SimpleJob
{
	private final File file;
	private final ArrayList<DataPoint> dataPoints;

	private final String[] axisLabels;

	// Accepts a file to save to, a list of data points to be saved to that file
	// the integer indices of the currently displayed axes and the axis labels
	// of the currently display axes.
	public DataPointSaver(File file, ArrayList<DataPoint> dataPoints, String[] axisLabels)
	{
		this.file = file;
		this.dataPoints = dataPoints;
		this.axisLabels = axisLabels.clone();
	}

	@Override
	public void runJob(int jobIndex)
		throws Exception
	{
		maximum = dataPoints.size();

		PrintWriter out = new PrintWriter(file, "UTF-8");

		// Write the header for the file
		out.println(RB.getString("analysis.DataPointSaver.label") + "\t"
			+ RB.format("analysis.DataPointSaver.x", axisLabels[0]) + "\t"
			+ RB.format("analysis.DataPointSaver.y", axisLabels[1]) + "\t"
			+ RB.format("analysis.DataPointSaver.z", axisLabels[2]));

		// Then output each dataPoint in turn
		for (DataPoint dataPoint: dataPoints)
		{
			if (!okToRun)
				break;

			String pointString = buildDataPointString(dataPoint);
			out.println(pointString);

			progress++;
		}

		out.close();
	}

	@Override
	public String getMessage()
	{
		return RB.format("analysis.DataPointSaver.status", progress, maximum);
	}

	private String buildDataPointString(DataPoint dataPoint)
		throws IOException
	{
		float[] position = dataPoint.getPosition();

		// Add the name of the point to the string
		StringBuilder builder = new StringBuilder();
		builder.append(dataPoint.getName()).append('\t');

		// Followed by the value for each of the currently visible axes
		for (int i=0; i < position.length; i++)
		{
			builder.append(position[i]);
			if (i < position.length - 1)
				builder.append('\t');
		}

		return builder.toString();
	}
}