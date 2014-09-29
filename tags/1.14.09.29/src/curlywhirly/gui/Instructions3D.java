// Copyright 2009-2014 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package curlywhirly.gui;

import java.awt.event.*;
import javax.swing.*;

import scri.commons.gui.*;

public class Instructions3D implements ItemListener
{
	CurlyWhirly frame;
	JCheckBox instructionsCheckBox;

	public Instructions3D(CurlyWhirly frame)
	{
		this.frame = frame;
	}

	public void show3DInstructions(boolean useCheckbox)
	{
		instructionsCheckBox = new JCheckBox(RB.getString("gui.Instructions3D.checkbox"));
		instructionsCheckBox.addItemListener(this);
		String message = RB.getString("gui.Instructions3D.instructions");
		String label = RB.getString("gui.text.close");

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