// Copyright 2009-2021 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package jhi.curlywhirly.data;

import java.util.*;
import java.util.stream.*;

public class CategoryGroup implements Comparable<CategoryGroup>, Iterable<Category>
{
	// Currently Category group is basically a nice abstraction around what
	// could be provided via a HashMap<String, ArrayList<Category>>
	private final String name;
	private final ArrayList<Category> categories;

	private final Map<Category, ArrayList<DataPoint>> pointsForCategories;

	public CategoryGroup(String name)
	{
		this.name = name;

		categories = new ArrayList<Category>();
		pointsForCategories = new HashMap<Category, ArrayList<DataPoint>>();
	}

	public String getName()
	{
		return name;
	}

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

	public void addPointsForCategory(Category category, ArrayList<DataPoint> points)
	{
		pointsForCategories.put(category, points);
	}

	public void colorPointsByCategories()
	{
		pointsForCategories.forEach((k, v) -> v.forEach(p -> p.setColor(k.getColor())));
	}

	public Category getCategoryForPoint(DataPoint point)
	{
		for (Category cat : pointsForCategories.keySet())
			if (pointsForCategories.get(cat).contains(point))
				return cat;

		return null;
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
		return (int) pointsForCategories.values().stream()
			.flatMap(Collection::stream)
			.filter(DataPoint::isSelected)
			.count();
	}

	public int totalDataPoints()
	{
		return pointsForCategories.values().stream()
			.mapToInt(ArrayList::size)
			.sum();
	}

	public int getCategoryCount()
	{
		return categories.size();
	}

	public int selectedCategoriesCount()
	{
		return (int) getActiveCategories().count();
	}

	public Stream<Category> getActiveCategories()
	{
		return categories.stream().filter(Category::hasSelectedPoints);
	}

	public ArrayList<Category> getCategories()
	{
		return categories;
	}
}