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
}