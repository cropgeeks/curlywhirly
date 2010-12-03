package curlywhirly.data;

import javax.vecmath.Color3f;

public class Category implements Comparable<Category>
{
	public String name;
	public boolean highlight;
	public Color3f colour;
	
	public int compareTo(Category o)
	{
		return name.compareTo(o.name);
	}	
}
