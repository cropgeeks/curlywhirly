// Copyright 2009-2014 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package curlywhirly.analysis;

import java.io.*;
import java.util.*;

import curlywhirly.data.*;

import scri.commons.gui.*;

public class CategoryGroupSaver extends SimpleJob
{
	private final File file;
	private final ArrayList<CategoryGroup> catGroups;

	// Accepts a file to save to, a list of data points to be saved to that file
	// the integer indices of the currently displayed axes and the axis labels
	// of the currently display axes.
	public CategoryGroupSaver(File file, ArrayList<CategoryGroup> catGroups)
	{
		this.file = file;
		this.catGroups = catGroups;
	}

	@Override
	public void runJob(int jobIndex)
		throws Exception
	{
		maximum = catGroups.size();

		PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(file)));
		// Export the name of the category group and the count of slected / total points
		for (CategoryGroup group : catGroups)
		{
			if (group.selectedCategoriesCount() > 0)
			{
				String countString = group.selectedDataPointCount() + "/" + group.totalDataPoints();
				out.println(group.getName() + "\t" + countString);

				// Export the category names and their selection state
				for (Category cat : group)
					if (cat.isSelected())
						out.println("\t" + cat.getName() + "\t" + cat.getSelectedText());
			}
			progress++;
		}

		out.close();
	}

	@Override
	public String getMessage()
	{
		return RB.format("analysis.CategoryGroupSaver.status", progress, maximum);
	}
}