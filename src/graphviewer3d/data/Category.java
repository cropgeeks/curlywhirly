package graphviewer3d.data;

import javax.vecmath.Color3f;

public class Category implements Comparable
{
	public String name;
	public boolean highlight;
	public Color3f colour;
	
	public int compareTo(Object o)
	{
		Category compareItem = (Category)o;
		return name.compareTo(compareItem.name);
	}	
}
