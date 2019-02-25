// Copyright 2009-2017 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package jhi.curlywhirly.gui;

import jhi.curlywhirly.gui.viewer.*;
import org.monte.media.*;
import org.monte.media.avi.*;
import org.monte.media.math.*;
import scri.commons.gui.*;
import scri.commons.io.*;

import javax.imageio.*;
import java.awt.image.*;
import java.io.*;
import java.util.*;

import static org.monte.media.VideoFormatKeys.*;

public class MovieExporter extends SimpleJob
{
	private final OpenGLPanel panel;
	private final float rotation;
	private final int totalFrames;
	private final String name;
	private final int frameRate;

	private int status;
	private int writtenFrames;

	// Status variables to change the message in the progress dialog
	private static final int CAPTURING = 0;
	private static final int EXPORTING = 1;

	MovieExporter(OpenGLPanel panel, int frameRate, int length, String name)
	{
		this.name = name;
		this.frameRate = frameRate;
		this.panel = panel;

		// Determine variables which govern how the movie is captured
		totalFrames = length * frameRate;
		rotation = 180 / (float) totalFrames;

		// Maximum is totalFrames x 2 as we first capture the images, then
		// export them as movie frames
		maximum = totalFrames * 2;

		status = CAPTURING;
		writtenFrames = 0;
	}

	@Override
	public void runJob(int i)
		throws Exception
	{
		File file = panel.getMovieCapture().startMovieCapture(totalFrames, rotation);

		while (panel.getMovieCapture().getRenderedFrames() < totalFrames)
		{
			// If the user has cancelled we need to reset the screen
			if (!okToRun)
			{
				panel.getMovieCapture().finishMovieCapture();
				panel.getScene().reset();
				break;
			}
		}

		status = EXPORTING;

		write(new File(name), file, frameRate, panel.getWidth(), panel.getHeight());

		if (!okToRun)
			return;

		TaskDialog.showFileOpen(RB.getString("gui.dialog.MovieExporter.viewMovie"),
			TaskDialog.QST, new File(name));

		FileUtils.emptyDirectory(file, false);
		file.delete();
	}

	@Override
	public int getValue()
	{
		return panel.getMovieCapture().getRenderedFrames() + writtenFrames;
	}

	@Override
	public String getMessage()
	{
		if (status == CAPTURING)
			return RB.format("gui.dialog.MovieExporter.capturedMessage", panel.getMovieCapture().getRenderedFrames(), totalFrames);
		else
			return RB.format("gui.dialog.MovieExporter.exportedFrames", writtenFrames, totalFrames);
	}

	public void write(File file, File directory, int frameRate, int width, int height)
	{
		// Get the filepaths for the images
		File[] fileArray = directory.listFiles();
		ArrayList<String> images = new ArrayList<>();
		for (File f : fileArray)
			images.add(f.getAbsolutePath());

		// Setup the recording format
		Format format = new Format(EncodingKey, VideoFormatKeys.ENCODING_AVI_MJPG, DepthKey, 24,
			QualityKey, 1f, MediaTypeKey, MediaType.VIDEO, FrameRateKey,
			new Rational(frameRate, 1), WidthKey, width, HeightKey, height);

		try
		{
			// Create the writer
			AVIWriter out = new AVIWriter(file);

			BufferedImage image = ImageIO.read(new File(images.get(0)));
			// Add a track to the writer
			out.addTrack(format);
			out.setPalette(0, image.getColorModel());

			for (String filename : images)
			{
				if (!okToRun)
					return;

				// Load the file from our temp directory and output it as a
				// movie frame
				image = ImageIO.read(new File(filename));
				out.write(0, image, 1);
				writtenFrames++;
			}

			out.close();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
}