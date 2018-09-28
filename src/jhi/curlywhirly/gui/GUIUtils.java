// Copyright 2009-2017 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package jhi.curlywhirly.gui;

import java.awt.*;
import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.*;
import javax.swing.*;

import scri.commons.gui.*;

public class GUIUtils
{
	/**
	 * Returns an array of colours the length of numColours
	 */
	public static Color[] generateColours(int numColours)
	{
		Color[] kellyColors = new Color[] {
			new Color(255, 179, 0),
			new Color(128, 62, 117),
			new Color(255, 104, 0),
			new Color(166, 189,215),
			new Color(193, 0, 32),
			new Color(206, 162, 98),
			new Color(129, 112, 102),
			new Color(0, 125, 52),
			new Color(246, 118, 142),
			new Color(0, 83, 138),
			new Color(255, 122, 92),
			new Color(83, 55, 122),
			new Color(255, 142, 0),
			new Color(179, 40, 81),
			new Color(244, 200, 0),
			new Color(127, 24, 13),
			new Color(147, 170, 0),
			new Color(89, 51, 21),
			new Color(241, 58, 19),
			new Color(35, 44, 22)
		};

		Color[] finalColours = new Color[numColours];

		if (numColours <= kellyColors.length)
		{
			finalColours = shuffleColours(kellyColors, numColours);
		}
		else
		{
			int additionalColours = numColours - kellyColors.length;

			Color [] colours = new Color[additionalColours];
			float increment = 1/(float)additionalColours;
			float currentHue = 0;
			for (int i = 0; i < colours.length; i++)
			{
				colours[i] = Color.getHSBColor(currentHue, 0.8f, 0.8f);
				currentHue += increment;
			}

			System.arraycopy(kellyColors, 0, finalColours, 0, kellyColors.length);
			System.arraycopy(colours, 0, finalColours, kellyColors.length, colours.length);

			finalColours = shuffleColours(finalColours, numColours);
		}

		return finalColours;
	}

	private static Color[] shuffleColours(Color[] colors, int maxColours)
	{
		// Shuffle colours so we don't always see the same palette across all category groups
		Random rnd = ThreadLocalRandom.current();
		for (int i=maxColours-1; i > 0; i--)
		{
			int index = rnd.nextInt(i +1);
			Color color = colors[index];
			colors[index] = colors[i];
			colors[i] = color;
		}

		return colors;
	}

	public static void visitURL(String html)
	{
		try
		{
			Desktop desktop = Desktop.getDesktop();

			URI uri = new URI(html);
			desktop.browse(uri);
		}
		catch(URISyntaxException | IOException e)
		{
			String message = RB.format("gui.GUIUtils.urlError", html);
			TaskDialog.error(message, RB.getString("gui.text.close"));

			e.printStackTrace();
		}
	}

	public static void sendFeedback()
	{
		try
		{
			Desktop desktop = Desktop.getDesktop();
			desktop.mail(new URI("mailto:curlywhirly@hutton.ac.uk?subject=CurlyWhirly%20Feedback"));
		}
		catch (URISyntaxException | IOException e) { System.out.println(e); }
	}

	/*
	 * @param panel the panel to apply the effect to
	 * @param opague if the panel should be opague or not on OS X
	 */
	public static void setPanelColor(JPanel panel, boolean opagueOnOSX)
	{
		if (SystemUtils.isMacOS() == false)
			panel.setBackground(Color.white);

		else
			panel.setOpaque(opagueOnOSX);
	}
}