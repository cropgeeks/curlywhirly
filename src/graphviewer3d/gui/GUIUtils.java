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
		int red=0;
		int green = 0;
		int blue =0;
		
		//the amount by which we want to increment the values for each colour channel 
		int increment = 255/(numColours/3);
		
		Color3f [] colours = new Color3f[numColours];
		//make a colour gradient by initially ramping up the red only, then the green and then the blue
		for (int i = 0; i < colours.length; i++)
		{
		
			if(i<colours.length/3)
			{
				red+= increment;
			}
			if(i>= colours.length/3 && i<((colours.length/3)*2))
			{
				red = 0;
				green+=increment;
			}
			if(i>=((colours.length/3)*2))
			{
				red = 0;
				green = 0;
				blue+= increment;
			}
			
			colours[i] = new Color3f(new Color(red,green,blue));
		}	

		return colours;
	}
}
