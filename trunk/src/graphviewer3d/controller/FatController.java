package graphviewer3d.controller;

import graphviewer3d.data.FileLoader;
import graphviewer3d.gui.DataLoadingDialog;
import graphviewer3d.gui.GraphViewer3DCanvas;
import graphviewer3d.gui.GraphViewerFrame;

import java.io.File;

import javax.media.j3d.Canvas3D;

public class FatController
{
	
	// ============================================vars==============================================	
	
	GraphViewerFrame frame;
	
	// ============================================c'tor==============================================
	
	public FatController(GraphViewerFrame frame)
	{
		this.frame = frame;
	}
	
	// ============================================methods==============================================	
	
	public void loadDataInThread(File file)
	{
		//clear view
		if (frame.canvas3D != null)
			frame.canvas3D.clearCurrentView();
		
		//start the load in a separate thread
		DataLoadingDialog dataLoadingDialog = new DataLoadingDialog(frame, true);
		FileLoader loader = new FileLoader(frame,file,dataLoadingDialog);
		loader.setName("curlywhirly_dataload");
		loader.start();
		
		//show a dialog with a progress bar
		dataLoadingDialog.setLocationRelativeTo(frame);
		dataLoadingDialog.setVisible(true);
		dataLoadingDialog.setModal(false);
	}
	
	// -----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
	
	public void cancelMovieCapture()
	{
		//cancel any ongoing movie capture thread
		frame.currentMovieCaptureThread.threadCanceled = true;
		frame.currentMovieCaptureThread.movieFile.delete();
		frame.canvas3D.resetOriginalView();
		frame.canvas3D.repaint();
	}
	
	// -----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------

}// end class
