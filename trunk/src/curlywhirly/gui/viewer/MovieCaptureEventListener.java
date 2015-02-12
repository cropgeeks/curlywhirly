package curlywhirly.gui.viewer;

import java.awt.image.*;
import java.io.*;
import java.util.concurrent.atomic.*;
import javax.imageio.*;
import javax.media.opengl.*;

import curlywhirly.gui.*;

import com.jogamp.opengl.util.awt.*;

public class MovieCaptureEventListener implements GLEventListener
{
	private AWTGLReadBufferUtil glBufferUtil;
	private volatile boolean recordMovie = false;
	private volatile AtomicInteger recordedFrames = new AtomicInteger();
	private volatile int totalFrames = 0;

	private File imageDir;
	private final OpenGLPanel panel;

	private float oldRotation;

	public MovieCaptureEventListener(OpenGLPanel panel)
	{
		this.panel = panel;
	}

	@Override
	public void init(GLAutoDrawable drawable)
	{
		glBufferUtil = new AWTGLReadBufferUtil(drawable.getGLProfile(), false);
	}

	@Override
	public void display(GLAutoDrawable drawable)
	{
		final GL gl = drawable.getGL();

		if (recordMovie && recordedFrames.get() < totalFrames)
		{
			try
			{
				// Get image from opengl canvas.
				BufferedImage image = panel.snapshot(gl, glBufferUtil, imageDir);
				// Name the file using the current time (directory listings
				// return files in order in this case).
				final String filename = System.currentTimeMillis() + ".bmp";

				// Output the image
				File imgFile = new File(imageDir, filename);
				imgFile.deleteOnExit();
				ImageIO.write(image, "bmp", imgFile);
			}
			catch (IOException e) { e.printStackTrace(); }

			recordedFrames.incrementAndGet();

			if (recordedFrames.get() == totalFrames)
				finishMovieCapture();
		}
	}

	@Override
	public void dispose(GLAutoDrawable drawable) {}

	@Override
	public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) { }

	public File startMovieCapture(int totalFrames, float rotation)
	{
		imageDir = new File(Prefs.cacheFolder + System.getProperty("file.separator") + System.currentTimeMillis() + "imageTempDir");
		imageDir.mkdir();
		imageDir.deleteOnExit();

		recordMovie=true;

		// Store the old value of rotation to restore it after the movie has
		// been captured
		oldRotation = panel.getScene().getRotationSpeed();
		// Negate rotation as opengl rotates left by default
		panel.getScene().setRotationSpeed(-rotation);
		this.totalFrames = totalFrames;
		panel.getScene().toggleSpin();
		recordMovie = true;
		recordedFrames = new AtomicInteger();

		return imageDir;
	}

	public void finishMovieCapture()
	{
		// Stop spinning and reset the rotation variable
		panel.getScene().toggleSpin();
		recordMovie = false;
		panel.getScene().setRotationSpeed(oldRotation);
	}

	public int getRenderedFrames()
		{ return recordedFrames.get(); }
}