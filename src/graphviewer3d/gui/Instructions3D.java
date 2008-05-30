package graphviewer3d.gui;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.JCheckBox;

import scri.commons.gui.TaskDialog;

public class Instructions3D  implements ItemListener
{
	GraphViewerFrame frame;
	JCheckBox instructionsCheckBox;
	
	public Instructions3D(GraphViewerFrame frame)
	{
		this.frame = frame;
	}
	
	public void show3DInstructions()
	{
		instructionsCheckBox = new JCheckBox("Do not show this again");
		instructionsCheckBox.addItemListener(this);
		String message = "Controls for the 3D graph: Left-click and drag to spin," +
				"middle-click and drag or Alt + left-click and drag to zoom";
		String label = "Close";
		TaskDialog.initialize(frame, "CurlyWhirly");
		TaskDialog.info(message, label, instructionsCheckBox);		
	}

	public void itemStateChanged(ItemEvent e)
	{
		if(instructionsCheckBox.isSelected())
		{
			frame.prefs.show3DControlInstructions = false;
		}
	}


	
}
