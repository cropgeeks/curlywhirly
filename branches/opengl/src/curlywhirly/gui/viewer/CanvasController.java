package curlywhirly.gui.viewer;

import java.awt.*;
import java.util.*;

import curlywhirly.data.*;
import curlywhirly.gui.*;
import curlywhirly.opengl.*;

import javax.vecmath.*;

public class CanvasController
{
	private CurlyWhirly frame;
	private OpenGLPanel panel;

	public CanvasController(CurlyWhirly frame)
	{
		this.frame = frame;
		panel = frame.getOpenGLPanel();
	}

	//update the current scene graph with new settings
	public void updateSelected(ArrayList<String> selected)
	{
		if (selected.size() < 0)
			return;

		// for each category
		ArrayList<Category> categories = frame.getDataSet().getCurrentClassificationScheme().categories;

		for (Category category : categories)
		{
			if (selected.contains(category.name))
				category.highlight = true;
			else
				category.highlight = false;
		}
	}

		//changes the background colour
	public void setBackgroundColour(int newColour)
	{
		Color bgColor = Color.BLACK;
		switch (newColour)
		{
			case 0:
				bgColor = Color.BLACK;
				break;
			case 1:
				bgColor = Color.DARK_GRAY;
				break;
			case 2:
				bgColor = Color.LIGHT_GRAY;
				break;
			case 3:
				bgColor = Color.WHITE;
				break;
		}

		panel.setClearColor(bgColor);
	}
}
