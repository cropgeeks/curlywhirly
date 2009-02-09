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
	
//	public void recordMovie()
//	{
//		GraphViewer3DCanvas canvas = frame.canvas3D;
//		
//		//this is where we store the images and later the movie
//		String directory = "c:/tmp";
//		
//		frame.setVisible(true);
//		frame.toFront();
//		
//		//spin
//		frame.canvas3D.spin();
//		
//		//record
//		for (int i = 0; i < 10; i++)
//		{
//			canvas.outputFile = new File(directory + System.getProperty("file.separator") + "img" +i+".jpg");
//			canvas.writeJPEG = true;
//			try{Thread.sleep(500);}catch(InterruptedException x){}
//		}
//		
//		//stop
//		frame.canvas3D.stopSpinning();
//		
//		//make the movie
//		JpegImagesToMovie.writeMovie(600, 600, 2, directory);
//	}
	
	// -----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
	
}// end class
