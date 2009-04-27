package graphviewer3d.controller;

import graphviewer3d.gui.*;
import java.awt.*;
import java.awt.image.*;
import java.io.*;
import javax.imageio.*;
import javax.media.format.*;
import javax.media.j3d.*;
import javax.media.protocol.*;
import javax.swing.*;

/**
 * Thread class for saving screenshots of the canvas
 */
public class MovieCaptureThread extends Thread
{
	GraphViewerFrame frame;
	JFileChooser fileChooser;
	
	String movieFileExtension = ".avi";
	String movieFilePath = System.getProperty("user.dir") + System.getProperty("file.separator") + "animation" + movieFileExtension;	
	String imageDirectory  = System.getProperty("user.dir") + System.getProperty("file.separator") + "imageTempDir";
	String videoFormatEncoding = VideoFormat.RGB;
	String contentType = FileTypeDescriptor.MSVIDEO;
	String fileType = "BMP";
	String imageFileExtension = ".bmp";	
	int frameRate = 30;
	int numRotations = 1;
	//time the graph takes to do one full rotation
	int rotationTime = 2;
	
	
	
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
			
			//set up the screen capture robot
			GraphViewer3DCanvas canvas = frame.canvas3D;
			Robot robot  = new Robot();
			robot.setAutoWaitForIdle(true);
			Point p = canvas.getLocationOnScreen();
			int canvasWidth = canvas.getWidth();
			int canvasHeight = canvas.getHeight();
			Rectangle screenRect = new Rectangle((int) p.getX(), (int) p.getY(), canvasWidth, canvasHeight);
			
			//get the canvas ready
			canvas.resetOriginalView();
			canvas.setSpinSpeed(100-rotationTime);
			
			//work out total number of frames required
			int animationTimeSecs = numRotations * rotationTime;
			int totalNumFrames = frameRate * animationTimeSecs + 2;
			//work out the time increment required to achieve the frame rate
			int timeIncrementMillis = Math.round((rotationTime*1000)/totalNumFrames);
			
			System.out.println("timeIncrementMillis = " + timeIncrementMillis);
			System.out.println("totalNumFrames = " + totalNumFrames);
			
			//now rotate the graph in steps, taking images along the way
			for(int i = 0; i < totalNumFrames; i++)
			{			
				//start the spin if necessary
				if(!canvas.isGraphSpinning)
					canvas.spin();
				Alpha alpha = canvas.yRotator.getAlpha();

				//resume the Alpha object if it was paused				
				if(alpha.isPaused())
					alpha.resume();	
				
				//sleep this thread while we spin the graph but only after the first iteration
				if(i > 0)
					try{Thread.sleep(timeIncrementMillis);}catch(InterruptedException x){}
				
				//now pause the spinning							
				alpha.pause();	
				
				//take a screenshot and write it to disk
				BufferedImage image = robot.createScreenCapture(screenRect);
				String fileName = imageDirectory+ System.getProperty("file.separator") + "img" + new java.text.DecimalFormat("000000").format(i) + imageFileExtension;
				ImageIO.write(image, fileType, new File(fileName));
//				while(!new File(fileName).exists())
//				{
//					try{Thread.sleep(500);}catch(InterruptedException x){}	
//				}
			}
			
			//stop the graph spinning
			canvas.stopSpinning();	
			
			//now string images together into a movie
			JpegImagesToMovie imageToMovie = new JpegImagesToMovie(videoFormatEncoding, contentType, canvasWidth, 
							canvasHeight,  animationTimeSecs,  frameRate, imageDirectory, movieFilePath);
			imageToMovie.writeMovie();
			
			System.out.println("done");
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		
	}
	
}
