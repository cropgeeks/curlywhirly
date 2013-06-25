package curlywhirly.data;

import java.util.*;
import javax.vecmath.*;

import curlywhirly.gui.*;

public class ClassificationScheme
{
	public String name;
	public ArrayList<Category> categories = new ArrayList<Category>();
	public Vector<String> categoryNamesVec = null;
	public int columnIndex = -1;

	public Category getCategoryByName(String categoryName)
	{
		for (Category category : categories)
		{
			if(categoryName.equals(category.name))
			{
				return category;
			}
		}

		return null;
	}

	public void makeNamesVector()
	{
		categoryNamesVec = new Vector<String>();
		for (Category category : categories)
		{
			categoryNamesVec.add(category.name);
		}
		Collections.sort(categoryNamesVec);
	}

	//if we know the number of categories and we can create a colour scheme and apply it
	public void assignColoursToCategories()
	{
//		System.out.println("assigning all colours for scheme " + name);
//		System.out.println("num categories =  " + categories.size());
//
		try
		{
			Color3f[] colours = GUIUtils.generateColours(categories.size());
			int i =0;
			for (Category category : categories)
			{
				category.colour = colours[i];
//				System.out.println("setting colour for category " + category.name + " to " + colours[i]);
				i++;
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
}