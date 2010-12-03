package curlywhirly.data;

import java.io.File;

import javax.swing.JFileChooser;
import curlywhirly.gui.*;


public class FileLoader extends Thread
{
	GraphViewerFrame frame;
	public boolean done = false;
	File file;
	DataLoadingDialog dataLoadingDialog;

	public FileLoader(GraphViewerFrame frame, File file, DataLoadingDialog dataLoadingDialog)
	{
		this.frame = frame;
		this.file = file;
		this.dataLoadingDialog = dataLoadingDialog;
	}

	public void run()
	{
		frame.loadData(file);
		if (dataLoadingDialog != null)
			dataLoadingDialog.setVisible(false);
		if (frame.dataLoaded)
		{
			if (Preferences.show3DControlInstructions)
			{
				Instructions3D instr = new Instructions3D(frame);
				instr.show3DInstructions(true);
			}
		}
	}

}
