package curlywhirly.data;

import java.util.*;
import com.sun.j3d.utils.geometry.*;
import com.sun.org.apache.bcel.internal.generic.*;
import curlywhirly.gui.*;

public class DataEntry
{
	//contains one or more values of categories associated with this data point
	public LinkedList<Category> categories = new LinkedList<Category>();
	
	//contains at least 3 values, one for each data column
	public LinkedList<Float> dataValues = new LinkedList<Float>();
	
	//same as above but normalized to be between 1 and -1
	public LinkedList<Float> normalizedDataValues = new LinkedList<Float>();
	
	//the label to be used for this data point
	public String label = null;
	
	//the sphere object used to represent this data point
	public DataSphere dataSphere;
	
	public void printAsLine(boolean normalised)
	{
		for(Category category : categories)
			System.out.print(category.name + "\t");
		
		System.out.print(label +"\t");
		
		if(!normalised)
		{
			for(Float value : dataValues)
				System.out.print(value + "\t");
		}
		else
		{
			for(Float value : normalizedDataValues)
				System.out.print(value + "\t");
		}
		
		System.out.println();
	}
}
