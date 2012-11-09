package curlywhirly.controller;

import java.awt.*;
import java.awt.image.*;
import java.io.*;
import javax.imageio.*;
import javax.media.format.*;
import javax.media.protocol.*;

import scri.commons.gui.*;

import curlywhirly.gui.*;

/**
 * Thread class for saving screenshots of the canvas
 */
public class MovieCaptureThread extends Thread
{
	//these parameters are all fixed -- movie and image format etc
	String movieFileExtension = ".avi";
	String videoFormatEncoding = VideoFormat.RGB;
	String contentType = FileTypeDescriptor.MSVIDEO;
	String fileType = "BMP";
	String imageFileExtension = ".bmp";

	//where are we writing this to
	public File movieFile;
	File imageDirectory;

	//other params
	int frameRate = 0;
	int numRotations = 1;
	//time the graph takes to do one full rotation
	int rotationTime = 0;

	CurlyWhirly frame;

	public boolean threadCanceled = false;

	public MovieCaptureThread(CurlyWhirly frame, File movieFile,int frameRate,int rotationTime)
	{
		this.frame = frame;
		this.movieFile = movieFile;
		this.frameRate = frameRate;
		this.rotationTime = rotationTime;
		imageDirectory  = new File(System.getProperty("java.io.tmpdir") + System.getProperty("file.separator") + "imageTempDir");
		imageDirectory.mkdir();
	}

	public void run()
	{
		try
		{
			frame.currentMovieCaptureThread = this;
			frame.canvas3D.requestFocusInWindow();
			frame.setAlwaysOnTop(true);

			//wait to make sure the calling dialog is no longer visible
			try{Thread.sleep(500);}catch(InterruptedException x){}

			//set up the screen capture robot
			MainCanvas canvas = CurlyWhirly.canvas3D;
			Robot robot  = new Robot();
			robot.setAutoWaitForIdle(true);
			Point p = canvas.getLocationOnScreen();
			int canvasWidth = canvas.getWidth();
			int canvasHeight = canvas.getHeight();
			Rectangle screenRect = new Rectangle((int) p.getX(), (int) p.getY(), canvasWidth, canvasHeight);

			//get the canvas ready
			canvas.resetOriginalView();

			//work out total number of frames required
			int animationTimeSecs = numRotations * rotationTime;
			int totalNumFrames = frameRate * animationTimeSecs;

			//work out the number of degrees required for each step of the rotation
			float degrees = 0;
			float degreesIncrement =  (360*numRotations)/(float)totalNumFrames;

			frame.statusBar.progressBar.setVisible(true);

			//now rotate the graph in steps, taking images along the way
			for(int i = 0; i < totalNumFrames; i++)
			{
				//if the user pressed the ESC key we cancel this thread
				if(threadCanceled)
				{
					cleanUp();
					return;
				}

				//increment the degrees rotation
				degrees = degrees + degreesIncrement;

				//rotate by a given number of degrees
				canvas.rotateGraph(degrees);

				//take a screenshot and write it to disk
				BufferedImage image = robot.createScreenCapture(screenRect);
				String fileName =  "img" + new java.text.DecimalFormat("000000").format(i) + imageFileExtension;
				File imageFile = new File(imageDirectory, fileName);
				ImageIO.write(image, fileType, imageFile);

				//update progress bar
				int progressPercent = Math.round((i/(float)totalNumFrames)*100);
				frame.statusBar.progressBar.setValue(progressPercent);

				try{Thread.sleep(300);}catch(InterruptedException x){}
			}

			//now string images together into a movie
			JpegImagesToMovie imageToMovie = new JpegImagesToMovie(frame, videoFormatEncoding, contentType, canvasWidth,
							canvasHeight,  animationTimeSecs,  frameRate, imageDirectory, movieFile);
			imageToMovie.writeMovie();

			//remove all files in temp dir
			boolean allDeleted = deleteDirectory(imageDirectory);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			String message = e.getMessage();
			TaskDialog.error(RB.format("controller.MovieCaptureThread.error", e.getMessage()),
				RB.getString("text.close"));
		}
		finally
		{
			cleanUp();
			frame.setAlwaysOnTop(false);
		}
	}

	private void cleanUp()
	{
		frame.statusBar.setDefaultText();
		frame.statusBar.progressBar.setVisible(false);
		frame.currentMovieCaptureThread = null;
	}

	public boolean deleteDirectory(File path)
	{
		if (path.exists())
		{
			File[] files = path.listFiles();
			for (int i = 0; i < files.length; i++)
			{
				if (files[i].isDirectory())
				{
					deleteDirectory(files[i]);
				}
				else
				{
					files[i].delete();
				}
			}
		}
		return (path.delete());
	}
}