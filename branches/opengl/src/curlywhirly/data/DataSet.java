package curlywhirly.data;

import java.util.*;

public class DataSet
{
	//a general name for the dataset
	public String name;

	//the list of categorization schemes that can be applied to the data, and a corresponding name based lookup
	public ArrayList<CategoryGroup> categoryGroups = new ArrayList<CategoryGroup>();

	//the headers for all the columns
	public String[] allHeaders;

	//the headers for data columns only
	public Vector<String> dataHeaders = new Vector<String>();

	//the number of entries in the dataset
	public int numEntries;

	//this contains the actual entries inthe dataset
	public ArrayList<DataEntry> dataEntries = new ArrayList<DataEntry>();

	public int numDataColumns;
	public int numCategoryColumns;

	//the legacy format also supports an empty first column if there are not category data attached
	//checks whether we have this situation
	boolean emptyCategoryGroup = false;
	boolean missingCategoryColumn = false;
	boolean emptyCategoryColumn = false;
	//this flag indicates whether we have a legacy format file with no "categories:" prefix for the category data
	boolean noCategoryHeaders = true;

	public int labelsColumnIndex;
	public ArrayList<Integer> categoryColumnIndices;
	public ArrayList<Integer> dataColumnIndices;
	public int dataColumnStart = -1;

	// Default to first three columns of data in the dataset
	private int currX = 0;
	private int currY = 1;
	private int currZ = 2;

	public CategoryGroup currentCategoryGroup;

	public int getCategorySchemeIndex()
	{
		return categoryGroups.indexOf(currentCategoryGroup);
	}

	public int[] getCurrentAxes()
	{
		return new int[] { currX, currY, currZ };
	}

	public CategoryGroup getCurrentCategoryGroup()
		{ return currentCategoryGroup; }

	public void setCurrentCategoryGroup(CategoryGroup current)
		{ currentCategoryGroup = current; }

	public int getCurrX()
		{ return currX; }

	public void setCurrX(int currX)
		{ this.currX = currX; }

	public int getCurrY()
		{ return currY; }

	public void setCurrY(int currY)
		{ this.currY = currY; }

	public int getCurrZ()
		{ return currZ; }

	public void setCurrZ(int currZ)
		{ this.currZ = currZ; }

	public ArrayList<CategoryGroup> getCategoryGroups()
		{ return categoryGroups; }
}