package curlywhirly.data;

import java.io.*;
import java.util.*;
import javax.vecmath.*;
import curlywhirly.gui.*;

public class ClassificationScheme
{
	public String name;
	public ArrayList<Category> categories = new ArrayList<Category>();	
	

	public Category categoryExists(String categoryName)
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
	
	
	//---------------------------------------------------------------------------------------------------------------------------------------------------------------------------	
	
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
	
	//---------------------------------------------------------------------------------------------------------------------------------------------------------------------------	
	
}
