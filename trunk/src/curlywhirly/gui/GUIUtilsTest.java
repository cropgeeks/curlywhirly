package curlywhirly.gui;

import javax.vecmath.Color3f;

public class GUIUtilsTest
{
	public static void main(String[] args)
	{
		int numColours = 20;

		for (int i = 1; i < numColours; i++)
		{
			System.out.println("=====================================");
			System.out.println("colour scheme " + i);
			Color3f[] colours = GUIUtils.generateColours(i);
			for (int j = 0; j < colours.length; j++)
			{
				System.out.println("colour " + j + " = " + colours[j].get());
			}
			System.out.println("=====================================");
		}
	}
}
