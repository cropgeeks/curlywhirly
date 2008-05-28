package graphviewer3d.gui;

import java.awt.Color;

import javax.vecmath.Color3f;

public class GUIUtils
{
	// ---------------------------------------------------------------------------------------------------------------------	

	/**
	 * Returns an array of colours the length of numColours
	 */
	public static Color3f [] generateColours(int numColours)
	{
		Color3f [] colours = new Color3f[numColours];		
		float increment = 1/(float)numColours;	
		float currentHue = 0;
		for (int i = 0; i < colours.length; i++)
		{
			Color col = Color.getHSBColor(currentHue, 1, 1);
			colours[i] = new Color3f(col);
			currentHue += increment;
		}	
		return colours;
	}
	// ---------------------------------------------------------------------------------------------------------------------	
}
