package curlywhirly.gui;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.JCheckBox;

import scri.commons.gui.TaskDialog;

public class Instructions3D  implements ItemListener
{
	CurlyWhirly frame;
	JCheckBox instructionsCheckBox;

	public Instructions3D(CurlyWhirly frame)
	{
		this.frame = frame;
	}

	public void show3DInstructions(boolean useCheckbox)
	{
		instructionsCheckBox = new JCheckBox("Hide this message in the future");
		instructionsCheckBox.addItemListener(this);
		String message = "Controls for the 3D graph: Left-click + drag to spin," +
				" middle-click (or Alt) + drag to zoom.";
		String label = "Close";
		
		if (useCheckbox)
			TaskDialog.info(message, label, instructionsCheckBox);
		else
			TaskDialog.info(message, label);
	}

	public void itemStateChanged(ItemEvent e)
	{
		if (instructionsCheckBox.isSelected())
		{
			frame.prefs.show3DControlInstructions = false;
		}
	}
}
