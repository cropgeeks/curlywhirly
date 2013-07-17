package curlywhirly.data;

import java.awt.*;
import java.util.*;
import javax.vecmath.*;

public class DataEntry
{
	//contains one or more values of categories associated with this data point
	public ArrayList<Category> categories = new ArrayList<Category>();

	//contains at least 3 values, one for each data column
	public ArrayList<Float> dataValues = new ArrayList<Float>();

	//same as above but normalized to be between 1 and -1
	public ArrayList<Float> normalizedDataValues = new ArrayList<Float>();

	//the label to be used for this data point
	public String label = null;

	public float[] getPosition(int[] currAxes)
	{
		float[] indices = new float[3];
		indices[0] = normalizedDataValues.get(currAxes[0]);
		indices[1] = normalizedDataValues.get(currAxes[1]);
		indices[2] = normalizedDataValues.get(currAxes[2]);

		return indices;
	}

	public Color3f getColor(int currentCategory)
	{
		Category category = categories.get(currentCategory);

		Color3f color = null;
		if (category != null)
		{
			if(category.highlight)
				color = category.colour;
			else
				color = new Color3f(Color.DARK_GRAY);
		}

		return color;
	}
}