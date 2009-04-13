package graphviewer3d.controller;

import java.util.*;
import graphviewer3d.gui.*;
import javax.media.*;
import javax.media.format.*;
import javax.media.protocol.*;
import javax.swing.*;

/**
 * Thread class for saving screenshots of the canvas
 */
public class MovieCaptureThread extends Thread
{
	GraphViewerFrame frame;
	JFileChooser fileChooser;
	
	public MovieCaptureThread(GraphViewerFrame frame, JFileChooser fileChooser)
	{
		this.frame = frame;
		this.fileChooser = fileChooser;
	}
	
	// take a screenshot of the canvas only
	public void run()
	{
		try
		{
			System.out.println("starting capture.....");
			
			// get the canvas and itsd dimensions
			GraphViewer3DCanvas canvas = frame.canvas3D;
			JPanel canvasPanel = frame.canvasPanel;
			canvas.setSize(400, 400);
			canvasPanel.setSize(400, 400);
			canvas.setVisible(true);

			int frameRate = 30;
			
			// the output media file
			String outputURL = "file:/" + System.getProperty("user.dir") + System.getProperty("file.separator") + "/animation.mov";
			// the input URL
			// You can specify the location, size and frame rate in the URL string as follows:
			// screen://x,y,width,height/framespersecond
			// Eg: screen://20,40,160,120/12.5
			String inputURL = "screen://" + canvasPanel.getX() + ","+ canvasPanel.getY() + "," + 
			canvasPanel.getWidth() + "," + canvasPanel.getHeight() + "/" + frameRate;
			
			System.out.println("inputURL = " + inputURL);
			
			// start the data source
//			Manager.createCloneableDataSource(new ScreenGrabDataSource(inputURL));
//			Manager.createPlayer(new ScreenGrabDataSource(inputURL));
			 ScreenGrabDataSource screenGrabber = new ScreenGrabDataSource(inputURL);
			 screenGrabber.connect();
			
			Format formats[] = new Format[1];
			formats[0] = new VideoFormat(VideoFormat.RGB);
			FileTypeDescriptor outputType = new FileTypeDescriptor(FileTypeDescriptor.QUICKTIME);
			Processor p = Manager.createRealizedProcessor(new ProcessorModel(screenGrabber,formats, outputType));
			
			// get the output of the processor
			DataSource source = p.getDataOutput();
			// create a File protocol MediaLocator with the location
			// of the file to
			// which bits are to be written
			MediaLocator dest = new MediaLocator(outputURL);
			// create a datasink to do the file writing & open the
			// sink to make sure
			// we can write to it.
			DataSink filewriter = null;
			filewriter = Manager.createDataSink(source, dest);
			filewriter.open();
			filewriter.start();
			p.start();
			canvas.setSpinSpeed(90);
			canvas.spin();
			
			// stop and close the processor when done capturing...
			// close the datasink when EndOfStream event is received...
			try{Thread.sleep(canvas.spinSpeed);}catch(InterruptedException x){}
			
			canvas.stopSpinning();
			p.stop();
			p.close();
			filewriter.stop();
			filewriter.close();
			
			System.out.println("done");
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		
	}
	
}
