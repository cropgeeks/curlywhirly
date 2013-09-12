package curlywhirly.data;

import java.util.*;

public class DataSet implements Iterable<DataPoint>
{
	private final ArrayList<DataPoint> dataPoints;
	private final ArrayList<CategoryGroup> categoryGroups;
	private final String[] axisLabels;
	private CategoryGroup currentGroup;

	private int currX;
	private int currY;
	private int currZ;

	// DB-link/association data
	private DBAssociation dbAssociation = new DBAssociation();

	public DataSet(ArrayList<DataPoint> dataPoints, ArrayList<CategoryGroup> categoryGroups, String[] axisLabels)
	{
		this.dataPoints = dataPoints;
		// The sort ensures CategoryGroups are displayed in alphabetical order
		Collections.sort(categoryGroups);
		this.categoryGroups = categoryGroups;
		this.axisLabels = axisLabels;

		setDefaultAxes(axisLabels.length);

		currentGroup = categoryGroups.get(0);
	}

	// Adjust the default axes displayed based on the number of axes in the
	// dataset. If there are 3 or more, display the first 3, otherwise display
	// as many different axes as is possible. Assumption is made that the first
	// axis is most important as this is where most of the variation should be
	// covered by PCO/PCA data.
	private void setDefaultAxes(int numAxes)
	{
		switch (numAxes)
		{
			case 1:		currY = 0;
						currZ = 0;
						break;
			case 2:		currZ = 1;
						break;

			default:	currX = 0;
						currY = 1;
						currZ = 2;
		}
	}

	@Override
	public Iterator<DataPoint> iterator()
	{
		return dataPoints.iterator();
	}

	public void setCurrentCategoryGroup(CategoryGroup currentGroup)
		{ this.currentGroup = currentGroup; }

	public CategoryGroup getCurrentCategoryGroup()
		{ return currentGroup; }

	public int[] getCurrentAxes()
	{
		return new int[] { currX, currY, currZ };
	}

	public void setCurrX(int currX)
		{ this.currX = currX; }

	public int getCurrX()
		{ return currX; }

	public void setCurrY(int currY)
		{ this.currY = currY; }

	public int getCurrY()
		{ return currY; }

	public void setCurrZ(int currZ)
		{ this.currZ = currZ; }

	public int getCurrZ()
		{ return currZ; }

	public ArrayList<CategoryGroup> getCategoryGroups()
		{ return categoryGroups; }

	public String[] getAxisLabels()
		{ return axisLabels; }

	public DBAssociation getDbAssociation()
		{ return dbAssociation; }
}