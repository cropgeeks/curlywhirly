package curlywhirly.data;

import java.awt.*;
import java.util.*;

import curlywhirly.gui.*;

public class CategoryGroup implements Comparable<CategoryGroup>
{
	private String name;
	private ArrayList<Category> categories;
	public int columnIndex = -1;
	private boolean current;

	public CategoryGroup(String name)
	{
		this.name = name;

		categories = new ArrayList<Category>();
		current = false;
	}

	public Category getCategoryByName(String categoryName)
	{
		for (Category category : categories)
			if(categoryName.equals(category.getName()))
				return category;

		return null;
	}

	//if we know the number of categories and we can create a colour scheme and apply it
	public void assignColoursToCategories()
	{
//		System.out.println("assigning all colours for scheme " + name);
//		System.out.println("num categories =  " + categories.size());
//
		Color[] colours = GUIUtils.generateColours(categories.size());
		for (int i=0; i < categories.size(); i++)
			categories.get(i).setColour(colours[i]);
	}

	public void addCategory(Category category)
	{
		categories.add(category);
	}

	@Override
	public int compareTo(CategoryGroup o)
	{
		return name.compareTo(o.name);
	}

	public String getName()
		{ return name; }

	public ArrayList<Category> getCategories()
		{ return categories; }

	public boolean isCurrent()
		{ return current; }

	public void setCurrent(boolean current)
		{ this.current = current; }
}