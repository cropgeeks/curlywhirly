// Copyright 2009-2014 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package curlywhirly.data;

import java.util.*;

public class CategoryGroup implements Comparable<CategoryGroup>, Iterable<Category>
{
	// Currently Category group is basically a nice abstraction around what
	// could be provided via a HashMap<String, ArrayList<Category>>
	private final String name;
	private final ArrayList<Category> categories;

	public CategoryGroup(String name)
	{
		this.name = name;

		categories = new ArrayList<Category>();
	}

	public String getName()
		{ return name; }

	public Category get(int index)
	{
		return categories.get(index);
	}

	public int size()
	{
		return categories.size();
	}

	public void add(Category category)
	{
		categories.add(category);
	}

	@Override
	public String toString()
	{
		return name;
	}

	@Override
	public int compareTo(CategoryGroup o)
	{
		return name.compareTo(o.name);
	}

	@Override
	public Iterator<Category> iterator()
	{
		return categories.iterator();
	}

	public void sort()
	{
		Collections.sort(categories);
	}

	public int selectedDataPointCount()
	{
		int selected = 0;
		for (Category cat : categories)
			selected += cat.getSelectedCount();

		return selected;
	}

	public int totalDataPoints()
	{
		int total = 0;
		for (Category cat : categories)
			total += cat.getTotal();

		return total;
	}

	public int selectedCategoriesCount()
	{
		int count = 0;
		for (Category cat : categories)
			count = cat.isSelected() ? count+1 : count;

		return count;
	}

	public ArrayList<Category> getActiveCategories()
	{
		ArrayList<Category> activeCategories = new ArrayList<>();
		for (Category cat : categories)
			if (cat.isSelected())
				activeCategories.add(cat);

		return activeCategories;
	}

	public ArrayList<Category> getCategories()
		{ return categories; }
}