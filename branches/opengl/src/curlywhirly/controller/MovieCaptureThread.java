package curlywhirly.controller;

import java.awt.*;
import java.awt.image.*;
import java.io.*;
import javax.imageio.*;
import javax.media.format.*;
import javax.media.j3d.*;
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
		imageDirectory = new File(Prefs.cacheFolder + System.getProperty("file.separator") + "imageTempDir");
		imageDirectory.mkdir();
	}

	public void run()
	{
		try
		{
			frame.currentMovieCaptureThread = this;
			frame.canvas3D.requestFocusInWindow();
			frame.setAlwaysOnTop(true);

			//set up the screen capture robot
			MainCanvas canvas = CurlyWhirly.canvas3D;

			int canvasWidth = canvas.getWidth();
			int canvasHeight = canvas.getHeight();

			//get the canvas ready
			canvas.resetOriginalView();

			//work out total number of frames required
			int animationTimeSecs = numRotations * rotationTime;
			int totalNumFrames = frameRate * animationTimeSecs;

			//work out the number of degrees required for each step of the rotation
			float degrees = 0;
			float degreesIncrement = (360 * numRotations) / (float) totalNumFrames;

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

				outputScreenshot(canvas, i);

				// Rotate by a given number of degrees
				degrees += degreesIncrement;
				canvas.rotateGraph(degrees);

				//update progress bar
				int progressPercent = Math.round((i/(float)totalNumFrames)*100);
				frame.statusBar.progressBar.setValue(progressPercent);
			}

			//now string images together into a movie
			JpegImagesToMovie imageToMovie = new JpegImagesToMovie(frame, videoFormatEncoding, contentType, canvasWidth,
							canvasHeight,  animationTimeSecs,  frameRate, imageDirectory, movieFile);

			ProgressDialog dialog = new ProgressDialog(imageToMovie,
				RB.format("controller.MovieCaptureThread.movieAssembly.title"),
				RB.format("controller.MovieCaptureThread.movieAssembly.label"),
				frame);

			// If the operation failed or was cancelled...
			if (dialog.getResult() != ProgressDialog.JOB_COMPLETED)
			{
				if (dialog.getResult() == ProgressDialog.JOB_FAILED)
				{
					dialog.getException().printStackTrace();
					TaskDialog.error(
						RB.format("controller.MovieCaptureThread.movieAssembly.exception",
						dialog.getException().getMessage()),
						RB.getString("gui.text.close"));
				}

				return;
			}

			TaskDialog.showFileOpen(
				RB.format("controller.MovieCaptureThread.movieAssembly.success", movieFile),
				TaskDialog.INF, movieFile);

			//remove all files in temp dir
			boolean allDeleted = deleteDirectory(imageDirectory);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			String message = e.getMessage();
			TaskDialog.error(RB.format("controller.MovieCaptureThread.error", e.getMessage()),
				RB.getString("gui.text.close"));
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
					deleteDirectory(files[i]);
				else
					files[i].delete();
			}
		}
		return (path.delete());
	}

	private void outputScreenshot(MainCanvas canvas, int fileNo)
		throws IOException
	{
		BufferedImage image = getScreenShot(canvas);
		String fileName =  "img" + new java.text.DecimalFormat("000000").format(fileNo) + imageFileExtension;
		File imageFile = new File(imageDirectory, fileName);
		ImageIO.write(image, fileType, imageFile);
	}

	private BufferedImage getScreenShot(Canvas3D canvas3d)
	{
		GraphicsContext3D context = canvas3d.getGraphicsContext3D();
		Dimension dimension = canvas3d.getSize();

		ImageComponent2D image = new ImageComponent2D(ImageComponent.FORMAT_RGB,
			dimension.width, dimension.height);

		javax.media.j3d.Raster ras = new javax.media.j3d.Raster();
		ras.setType(javax.media.j3d.Raster.RASTER_COLOR);
		ras.setCapability(javax.media.j3d.Raster.ALLOW_IMAGE_READ);
		ras.setCapability(javax.media.j3d.Raster.ALLOW_IMAGE_WRITE);

		ras.setSize(dimension);
		ras.setImage(image);

		context.readRaster(ras);
		BufferedImage img = ras.getImage().getImage();

		return img;
	}
}