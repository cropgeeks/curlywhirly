package graphviewer3d.controller;

import graphviewer3d.gui.GraphViewer3DCanvas;
import graphviewer3d.gui.GraphViewerFrame;

import java.io.File;

import javax.media.j3d.Canvas3D;

public class FatController
{
	
	GraphViewerFrame frame;
	
	// ============================================methods==============================================
	
	public FatController(GraphViewerFrame frame)
	{
		this.frame = frame;
	}
	
	// takes a canvas and saves its current state to an image
	public void saveImageToFile(File outfile, String fileType)
	{
		//new ScreenCaptureThread(outfile,frame,fileType).start();
	}
	
	public void recordMovie()
	{
		GraphViewer3DCanvas canvas = frame.canvas3D;
		
		//this is where we store the images and later the movie
		String directory = "c:/tmp";
		
		frame.setVisible(true);
		frame.toFront();
		
		//spin
		frame.canvas3D.spin();
		
		//record
		for (int i = 0; i < 10; i++)
		{
			canvas.outputFile = new File(directory + System.getProperty("file.separator") + "img" +i+".jpg");
			canvas.writeJPEG = true;
			try{Thread.sleep(500);}catch(InterruptedException x){}
		}
		
		//stop
		frame.canvas3D.stopSpinning();
		
		//make the movie
		JpegImagesToMovie.writeMovie(600, 600, 2, directory);
	}
	
	// -----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
	
}// end class
