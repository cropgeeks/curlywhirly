package graphviewer3d.data;

import javax.swing.JFileChooser;

import graphviewer3d.gui.DataLoadingDialog;
import graphviewer3d.gui.GraphViewerFrame;
import graphviewer3d.gui.Instructions3D;
import graphviewer3d.gui.Preferences;

public class FileLoader extends Thread
{
	GraphViewerFrame frame;
	public boolean done = false;
	JFileChooser fileChooser;
	DataLoadingDialog dataLoadingDialog;
	
	public FileLoader(GraphViewerFrame frame, JFileChooser fileChooser, DataLoadingDialog dataLoadingDialog)
	{
		this.frame = frame;
		this.fileChooser = fileChooser;
		this.dataLoadingDialog = dataLoadingDialog;
	}
	
	public void run()
	{
		frame.loadData(fileChooser.getSelectedFile());
		if (dataLoadingDialog != null)
			dataLoadingDialog.setVisible(false);
		if (frame.dataLoaded)
		{
			if (Preferences.show3DControlInstructions)
			{
				Instructions3D instr = new Instructions3D(frame);
				instr.show3DInstructions();
			}
		}
	}
	
}
