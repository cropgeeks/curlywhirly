// Copyright 2009-2014 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package curlywhirly.data;

import java.util.*;
import java.util.stream.*;

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
		return categories.stream().mapToInt(Category::getSelectedCount).sum();
	}

	public int totalDataPoints()
	{
		return categories.stream().mapToInt(Category::getTotal).sum();
	}

	public int selectedCategoriesCount()
	{
		return (int) categories.stream().filter(Category::isSelected).count();
	}

	public Stream<Category> getActiveCategories()
	{
		return categories.stream().filter(Category::isSelected);
	}

	public ArrayList<Category> getCategories()
		{ return categories; }
}