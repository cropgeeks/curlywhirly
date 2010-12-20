package curlywhirly.data;

import java.io.File;

import javax.swing.JFileChooser;
import curlywhirly.gui.*;


public class DataLoadingThread extends Thread
{
	CurlyWhirly frame;
	public boolean done = false;
	File file;
	DataLoadingDialog dataLoadingDialog;

	public DataLoadingThread(CurlyWhirly frame, File file, DataLoadingDialog dataLoadingDialog)
	{
		this.frame = frame;
		this.file = file;
		this.dataLoadingDialog = dataLoadingDialog;
	}

	public void run()
	{
		//load the actual data
		frame.dataLoader.loadData(file);
		
		//now reset this flag so that user can open another file by different means
		if(CurlyWhirly.dragAndDropDataLoad)
			CurlyWhirly.dragAndDropDataLoad = false;
		
		//show the progress bar
		if (dataLoadingDialog != null)
			dataLoadingDialog.setVisible(false);
		
		//display instructions on use of keyboard/mouse controls if needed
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
