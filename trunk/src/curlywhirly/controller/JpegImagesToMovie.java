package curlywhirly.controller;

import java.io.*;
import java.net.*;
import java.util.*;
import java.awt.*;
import javax.media.*;
import javax.media.control.*;
import javax.media.protocol.*;
import javax.media.datasink.*;
import javax.media.format.*;

import scri.commons.gui.*;

import curlywhirly.gui.*;

/**
 * This program takes a list of JPEG image files and convert them into a QuickTime movie.
 */
public class JpegImagesToMovie implements ControllerListener, DataSinkListener
{
	int canvasWidth;
	int canvasHeight;
	int animationTimeSecs;
	int frameRate;

	String videoFormatEncoding;
	String fileExtension;
	String contentType;
	String movieFilePath;

	File imageDirectory;
	File movieFile;

	CurlyWhirly frame;

	MovieAssembleDialog movieAssembleDialog;

	// These class variables were defined within the body of the class

	Object waitFileSync = new Object();
	boolean fileDone = false;
	boolean fileSuccess = true;

	Object waitSync = new Object();
	boolean stateTransitionOK = true;

	public JpegImagesToMovie(CurlyWhirly frame, String videoFormatEncoding, String contentType, int canvasWidth, int canvasHeight, int animationTimeSecs, int frameRate, File imageDirectory, File movieFile)
	{
		this.frame = frame;
		movieAssembleDialog = new MovieAssembleDialog(frame, false);
		this.videoFormatEncoding = videoFormatEncoding;
		this.contentType = contentType;
		this.canvasWidth = canvasWidth;
		this.canvasHeight = canvasHeight;
		this.animationTimeSecs = animationTimeSecs;
		this.frameRate = frameRate;
		this.imageDirectory = imageDirectory;
		this.movieFile = movieFile;
	}

	public void writeMovie()
	{
		try
		{
			// this is where we will write the movie
			URL outputURL = movieFile.toURI().toURL();

			// assemble the files in a vector
			File[] fileArray = imageDirectory.listFiles();
			ArrayList<String> inputFiles = new ArrayList<String>();
			for (int i = 0; i < fileArray.length; i++)
				inputFiles.add(fileArray[i].getAbsolutePath());

			// Generate the output media locators.
			MediaLocator oml = new MediaLocator(outputURL);
			doIt(canvasWidth, canvasHeight, frameRate, inputFiles, oml);

			movieAssembleDialog.getAssembleLabel().setText("Movie assembly complete");
			movieAssembleDialog.getCloseButton().setEnabled(true);
			movieAssembleDialog.requestFocus();
		}
		catch (Exception e)
		{
			frame.currentMovieCaptureThread.movieFile.delete();
			movieAssembleDialog.getAssembleLabel().setText("Movie assembly failed");
			e.printStackTrace();
		}
	}

	public boolean doIt(int width, int height, int frameRate, ArrayList<String> inFiles, MediaLocator outML)
	{
		ImageDataSource ids = new ImageDataSource(width, height, frameRate, inFiles);

		Processor p;

		try
		{
			p = Manager.createProcessor(ids);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return false;
		}

		p.addControllerListener(this);

		// Put the Processor into configured state so we can set
		// some processing options on the processor.
		p.configure();
		if (!waitForState(p, p.Configured))
		{
			System.err.println("Failed to configure the processor.");
			return false;
		}

		// Set the output content descriptor to QuickTime.
		p.setContentDescriptor(new ContentDescriptor(contentType));

		// Query for the processor for supported formats.
		// Then set it on the processor.
		TrackControl tcs[] = p.getTrackControls();
		Format f[] = tcs[0].getSupportedFormats();
		if (f == null || f.length <= 0)
		{
			System.err.println("The mux does not support the input format: " + tcs[0].getFormat());
			return false;
		}

		tcs[0].setFormat(f[0]);

		// We are done with programming the processor. Let's just
		// realize it.
		p.realize();
		if (!waitForState(p, p.Realized))
		{
			System.err.println("Failed to realize the processor.");
			return false;
		}

		// Now, we'll need to create a DataSink.
		DataSink dsink;
		if ((dsink = createDataSink(p, outML)) == null)
		{
			System.err.println("Failed to create a DataSink for the given output MediaLocator: " + outML);
			return false;
		}

		dsink.addDataSinkListener(this);
		fileDone = false;

		// OK, we can now start the actual transcoding.
		try
		{
			p.start();
			dsink.start();
		}
		catch (IOException e)
		{
			System.err.println("IO error during processing");
			return false;
		}

		// Wait for EndOfStream event.
		waitForFileDone();

		// Cleanup.
		try
		{
			dsink.close();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		p.removeControllerListener(this);

		return true;
	}

	/**
	 * Create the DataSink.
	 */
	DataSink createDataSink(Processor p, MediaLocator outML)
	{

		DataSource ds;

		if ((ds = p.getDataOutput()) == null)
		{
			System.err.println("Something is really wrong: the processor does not have an output DataSource");
			return null;
		}

		DataSink dsink;

		try
		{
			dsink = Manager.createDataSink(ds, outML);
			dsink.open();
		}
		catch (Exception e)
		{
			movieFile.delete();
			movieAssembleDialog.setVisible(false);
			TaskDialog.error(RB.getString("controller.JpegImagesToMovie.error"),
				RB.getString("gui.text.close"));
			System.err.println("Cannot create the DataSink: " + e);
			return null;
		}

		return dsink;
	}

	/**
	 * Block until the processor has transitioned to the given state. Return false if the transition failed.
	 */
	boolean waitForState(Processor p, int state)
	{
		synchronized (waitSync)
		{
			try
			{
				while (p.getState() < state && stateTransitionOK)
					waitSync.wait();
			}
			catch (Exception e)
			{
			}
		}
		return stateTransitionOK;
	}

	/**
	 * Controller Listener.
	 */
	public void controllerUpdate(ControllerEvent evt)
	{

		if (evt instanceof ConfigureCompleteEvent || evt instanceof RealizeCompleteEvent || evt instanceof PrefetchCompleteEvent)
		{
			synchronized (waitSync)
			{
				stateTransitionOK = true;
				waitSync.notifyAll();
			}
		}
		else if (evt instanceof ResourceUnavailableEvent)
		{
			synchronized (waitSync)
			{
				stateTransitionOK = false;
				waitSync.notifyAll();
			}
		}
		else if (evt instanceof EndOfMediaEvent)
		{
			evt.getSourceController().stop();
			evt.getSourceController().close();
		}
	}

	/**
	 * Block until file writing is done.
	 */
	boolean waitForFileDone()
	{
		synchronized (waitFileSync)
		{
			try
			{
				while (!fileDone)
					waitFileSync.wait();
			}
			catch (Exception e)
			{
			}
		}
		return fileSuccess;
	}

	/**
	 * Event handler for the file writer.
	 */
	public void dataSinkUpdate(DataSinkEvent evt)
	{

		if (evt instanceof EndOfStreamEvent)
		{
			synchronized (waitFileSync)
			{
				fileDone = true;
				waitFileSync.notifyAll();
			}
		}
		else if (evt instanceof DataSinkErrorEvent)
		{
			synchronized (waitFileSync)
			{
				fileDone = true;
				fileSuccess = false;
				waitFileSync.notifyAll();
			}
		}
	}

	/**
	 * Create a media locator from the given string.
	 */
	static MediaLocator createMediaLocator(String url)
	{
		MediaLocator ml;

		if (url.indexOf(":") > 0 && (ml = new MediaLocator(url)) != null)
			return ml;

		if (url.startsWith(File.separator))
		{
			if ((ml = new MediaLocator("file:" + url)) != null)
				return ml;
		}
		else
		{
			String file = "file:" + System.getProperty("user.dir") + File.separator + url;
			if ((ml = new MediaLocator(file)) != null)
				return ml;
		}

		return null;
	}

	/**
	 * A DataSource to read from a list of JPEG image files and turn that into a stream of JMF buffers. The DataSource is not seekable or positionable.
	 */
	class ImageDataSource extends PullBufferDataSource
	{
		ImageSourceStream stream;

		ImageDataSource(int width, int height, int frameRate, ArrayList<String> images)
		{
			stream = new ImageSourceStream(width, height, frameRate, images);
		}

		public void setLocator(MediaLocator source)
		{
		}

		public MediaLocator getLocator()
		{
			return null;
		}

		/**
		 * Content type is of RAW since we are sending buffers of video frames without a container format.
		 */
		public String getContentType()
		{
			return ContentDescriptor.RAW;
		}

		public void connect()
		{
		}

		public void disconnect()
		{
		}

		public void start()
		{
		}

		public void stop()
		{
		}

		/**
		 * Return the ImageSourceStreams.
		 */
		public PullBufferStream[] getStreams()
		{
			return new PullBufferStream[] { stream };
		}

		/**
		 * We could have derived the duration from the number of frames and frame rate. But for the purpose of this program, it's not necessary.
		 */
		public Time getDuration()
		{
			return new Time(new Double(animationTimeSecs));
			//return DURATION_UNKNOWN;
		}

		public Object[] getControls()
		{
			return new Object[0];
		}

		public Object getControl(String type)
		{
			return null;
		}
	}

	/**
	 * The source stream to go along with ImageDataSource.
	 */
	class ImageSourceStream implements PullBufferStream
	{
		ArrayList<String> images;
		int width, height;
		VideoFormat format;

		int nextImage = 0; // index of the next image to be read.
		boolean ended = false;

		long seqNo = 1;
		float percentIncrement = 0;

		public ImageSourceStream(int width, int height, int frameRate, ArrayList<String> images)
		{
			this.width = width;
			this.height = height;
			this.images = images;

			format = new VideoFormat(videoFormatEncoding, new Dimension(width, height), Format.NOT_SPECIFIED, Format.byteArray, (float) frameRate);

			//progress monitor
			percentIncrement = 100.0f/(float)images.size();
			movieAssembleDialog.setLocationRelativeTo(frame);
			movieAssembleDialog.setVisible(true);
		}

		/**
		 * We should never need to block assuming data are read from files.
		 */
		public boolean willReadBlock()
		{
			return false;
		}

		/**
		 * This is called from the Processor to read a frame worth of video data.
		 */
		public void read(Buffer buf) throws IOException
		{

			// Check if we've finished all the frames.
			if (nextImage >= images.size())
			{
				// We are done. Set EndOfMedia.
				buf.setEOM(true);
				buf.setOffset(0);
				buf.setLength(0);
				ended = true;
				return;
			}

			String imageFile = images.get(nextImage);
			nextImage++;

			int percentComplete = Math.round(percentIncrement*nextImage);
			//update progress monitor
			movieAssembleDialog.getProgressBar().setValue(percentComplete);

			// Open a random access file for the next image.
			RandomAccessFile raFile;
			raFile = new RandomAccessFile(imageFile, "r");

			byte data[] = null;

			// Check the input buffer type & size.

			if (buf.getData() instanceof byte[])
				data = (byte[]) buf.getData();

			// Check to see the given buffer is big enough for the frame.
			if (data == null || data.length < raFile.length())
			{
				data = new byte[(int) raFile.length()];
				buf.setData(data);
			}

			//added from forum thread at http://forums.sun.com/thread.jspa?threadID=514583
			//bug fix for missing length info in resulting media file
			long time = seqNo * (1000 / frameRate) * 1000000;
			buf.setTimeStamp(time);
			buf.setSequenceNumber(seqNo++);

			// Read the entire JPEG image from the file.
			raFile.readFully(data, 0, (int) raFile.length());

			buf.setOffset(0);
			buf.setLength((int) raFile.length());
			buf.setFormat(format);
			buf.setFlags(buf.getFlags() | buf.FLAG_KEY_FRAME);

			// Close the random access file.
			raFile.close();
		}

		/**
		 * Return the format of each video frame. That will be JPEG.
		 */
		public Format getFormat()
		{
			return format;
		}

		public ContentDescriptor getContentDescriptor()
		{
			return new ContentDescriptor(ContentDescriptor.RAW);
		}

		public long getContentLength()
		{
			return 0;
		}

		public boolean endOfStream()
		{
			return ended;
		}

		public Object[] getControls()
		{
			return new Object[0];
		}

		public Object getControl(String type)
		{
			return null;
		}
	}
}