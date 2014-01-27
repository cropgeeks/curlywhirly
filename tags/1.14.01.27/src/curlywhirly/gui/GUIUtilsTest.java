package curlywhirly.gui;

import java.awt.*;

public class GUIUtilsTest
{
	public static void main(String[] args)
	{
		int numColours = 20;

		for (int i = 1; i < numColours; i++)
		{
			System.out.println("=====================================");
			System.out.println("colour scheme " + i);
			Color[] colours = GUIUtils.generateColours(i);
			for (int j = 0; j < colours.length; j++)
			{
				System.out.println("colour " + j + " = " + colours[j]);
			}
			System.out.println("=====================================");
		}
	}
}
