package curlywhirly.data;

import java.awt.*;
import java.util.*;

public class Category implements Comparable<Category>
{
	private String name;
	private boolean selected;
	private Color colour;
	private ArrayList<DataEntry> dataEntries;
	private CategoryGroup catGroup;

	public Category(String name, CategoryGroup catGroup)
	{
		this.name = name;
		this.catGroup = catGroup;
		selected = true;

		dataEntries = new ArrayList<DataEntry>();
	}

	public void addDataEntry(DataEntry entry)
		{ dataEntries.add(entry); }

	@Override
	public int compareTo(Category o)
	{
		return name.compareTo(o.name);
	}

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public boolean isSelected()
	{
		return selected;
	}

	public void setSelected(boolean selected)
	{
		this.selected = selected;
	}

	public Color getColour()
	{
		return colour;
	}

	public void setColour(Color colour)
	{
		this.colour = colour;
	}

	public int getTotal()
	{
		return dataEntries.size();
	}

	// Returns the number of dataEntries which are considered selected (i.e. the
	// data entries whose every category is selected
	public int getNoSelected()
	{
		int noSelected = 0;
		for (DataEntry dataEntry : dataEntries)
			if (dataEntry.isSelected())
				noSelected++;

		return noSelected;
	}

	public String getSelectedText()
	{
		return "" + getNoSelected() + "/" + getTotal();
	}

	public String toString()
		{ return name; }

	public CategoryGroup getCategoryGroup()
		{ return catGroup; }
}