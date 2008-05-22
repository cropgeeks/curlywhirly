package graphviewer3d.gui;

import java.awt.Dimension;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JPanel;


public class ControlPanel extends JPanel
{

	public ControlPanel(GraphViewerFrame frame)
	{
		//layout 
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		
		//spacers
		Dimension spacer = new Dimension(20,20);
		Dimension smallSpacer = new Dimension(10,10);
		
		//spacer
		add(new Box.Filler(smallSpacer, smallSpacer, smallSpacer));

		BackgroundSelectorPanel backgroundPanel = new BackgroundSelectorPanel(frame);
		backgroundPanel.setMaximumSize(new Dimension(frame.controlPanelWidth-10, 50));
		this.add(backgroundPanel);
		
		//spacer
		add(new Box.Filler(spacer, spacer, spacer));
		
		DataSelectorPanel dataPanel = new DataSelectorPanel();
		dataPanel.setMaximumSize(new Dimension(frame.controlPanelWidth-10, 100));
		this.add(dataPanel);
		
		//spacer
		add(new Box.Filler(spacer, spacer, spacer));
		
		CategorySelectorPanel selectorPanel = new CategorySelectorPanel(frame);
		selectorPanel.setMaximumSize(new Dimension(frame.controlPanelWidth-10, 500));
		this.add(selectorPanel);
			
	}

}
