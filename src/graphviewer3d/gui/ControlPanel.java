package graphviewer3d.gui;

import javax.swing.JPanel;


public class ControlPanel extends JPanel
{

	public ControlPanel(GraphViewerFrame frame)
	{
		CategorySelectorPanel selectorPanel = new CategorySelectorPanel(frame);
		this.add(selectorPanel);
	}

}
