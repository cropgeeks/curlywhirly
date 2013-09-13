// Copyright 2009-2013 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package curlywhirly.analysis;

import java.io.*;
import java.util.*;

import curlywhirly.data.*;

import scri.commons.gui.*;

public class DataPointSaver extends SimpleJob
{
	private File file;
	private ArrayList<DataPoint> dataPoints;

	private int[] currentAxes;
	private String[] axisLabels;

	// Accepts a file to save to, a list of data points to be saved to that file
	// the integer indices of the currently displayed axes and the axis labels
	// of the currently display axes.
	public DataPointSaver(File file, ArrayList<DataPoint> dataPoints, int[] currentAxes, String[] axisLabels)
	{
		this.file = file;
		this.dataPoints = dataPoints;
		this.currentAxes = currentAxes;
		this.axisLabels = axisLabels;
	}

	public void runJob(int jobIndex)
		throws Exception
	{
		maximum = dataPoints.size();

		BufferedWriter out = new BufferedWriter(new FileWriter(file));

		// Write the header for the file
		out.write(RB.getString("analysis.DataPointSaver.label") + "\t"
			+ RB.format("analysis.DataPointSaver.x", axisLabels[0]) + "\t"
			+ RB.format("analysis.DataPointSaver.y", axisLabels[1]) + "\t"
			+ RB.format("analysis.DataPointSaver.z", axisLabels[2]));
		out.newLine();

		// Then output each dataPoint in turn
		for (DataPoint dataPoint: dataPoints)
		{
			if (!okToRun)
				break;

			String pointString = buildDataPointString(dataPoint);
			out.write(pointString);
			out.newLine();

			progress++;
		}

		out.close();
	}

	public String getMessage()
	{
		return RB.format("analysis.DataPointSaver.status", progress, maximum);
	}

	private String buildDataPointString(DataPoint dataPoint)
		throws IOException
	{
		float[] position = dataPoint.getPosition(currentAxes);

		// Add the name of the point to the string
		StringBuilder builder = new StringBuilder();
		builder.append(dataPoint.getName()).append("\t");

		// Followed by the value for each of the currently visible axes
		for (int i=0; i < position.length; i++)
		{
			builder.append(position[i]);
			if (i < position.length - 1)
				builder.append("\t");
		}

		return builder.toString();
	}
}