package graphviewer3d.controller;

/*
 * @(#)JpegImagesToMovie.java	1.3 01/03/13
 *
 * Copyright (c) 1999-2001 Sun Microsystems, Inc. All Rights Reserved.
 *
 * Sun grants you ("Licensee") a non-exclusive, royalty free, license to use,
 * modify and redistribute this software in source and binary code form,
 * provided that i) this copyright notice and license appear on all copies of
 * the software; and ii) Licensee does not utilize the software in a manner
 * which is disparaging to Sun.
 *
 * This software is provided "AS IS," without a warranty of any kind. ALL
 * EXPRESS OR IMPLIED CONDITIONS, REPRESENTATIONS AND WARRANTIES, INCLUDING ANY
 * IMPLIED WARRANTY OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE OR
 * NON-INFRINGEMENT, ARE HEREBY EXCLUDED. SUN AND ITS LICENSORS SHALL NOT BE
 * LIABLE FOR ANY DAMAGES SUFFERED BY LICENSEE AS A RESULT OF USING, MODIFYING
 * OR DISTRIBUTING THE SOFTWARE OR ITS DERIVATIVES. IN NO EVENT WILL SUN OR ITS
 * LICENSORS BE LIABLE FOR ANY LOST REVENUE, PROFIT OR DATA, OR FOR DIRECT,
 * INDIRECT, SPECIAL, CONSEQUENTIAL, INCIDENTAL OR PUNITIVE DAMAGES, HOWEVER
 * CAUSED AND REGARDLESS OF THE THEORY OF LIABILITY, ARISING OUT OF THE USE OF
 * OR INABILITY TO USE SOFTWARE, EVEN IF SUN HAS BEEN ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGES.
 *
 * This software is not designed or intended for use in on-line control of
 * aircraft, air traffic, aircraft navigation or aircraft communications; or in
 * the design, construction, operation or maintenance of any nuclear
 * facility. Licensee represents and warrants that it will not use or
 * redistribute the Software for such purposes.
 */

import java.awt.Dimension;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Vector;

import javax.media.Buffer;
import javax.media.ConfigureCompleteEvent;
import javax.media.ControllerEvent;
import javax.media.ControllerListener;
import javax.media.DataSink;
import javax.media.EndOfMediaEvent;
import javax.media.Format;
import javax.media.Manager;
import javax.media.MediaLocator;
import javax.media.PrefetchCompleteEvent;
import javax.media.Processor;
import javax.media.RealizeCompleteEvent;
import javax.media.ResourceUnavailableEvent;
import javax.media.Time;
import javax.media.control.TrackControl;
import javax.media.datasink.DataSinkErrorEvent;
import javax.media.datasink.DataSinkEvent;
import javax.media.datasink.DataSinkListener;
import javax.media.datasink.EndOfStreamEvent;
import javax.media.format.VideoFormat;
import javax.media.protocol.ContentDescriptor;
import javax.media.protocol.DataSource;
import javax.media.protocol.FileTypeDescriptor;
import javax.media.protocol.PullBufferDataSource;
import javax.media.protocol.PullBufferStream;

/**
 * This program takes a list of JPEG image files and convert them into a QuickTime movie.
 */
public class JpegImagesToMovie implements ControllerListener, DataSinkListener
{
	
	float frameRate;
	long seqNo = 0;
	
	public JpegImagesToMovie()
	{
		this.frameRate = (float) frameRate;
	}
	
	// ==============================================methods===============================================
	
	public static void writeMovie(int width, int height, int frameRate, String directory)
	{
		System.out.println("creating movie");
		// this is where we will write the movie
		String outputURL = "file:///" + directory + "/" + "animation.avi";
		// File movieFile = new File(outputURL);
		// if(!movieFile.exists())
		// System.out.println("no movie file made");
		
		// assemble the files in a vector
		File[] fileArray = new File(directory).listFiles();
		Vector<String> inputFiles = new Vector<String>();
		for (int i = 0; i < fileArray.length; i++)
		{
			inputFiles.add(fileArray[i].getAbsolutePath());
		}
		
		// Generate the output media locators.
		MediaLocator oml = new MediaLocator(outputURL);
		
		JpegImagesToMovie imageToMovie = new JpegImagesToMovie();
		imageToMovie.doIt(width, height, frameRate, inputFiles, oml);
	}
	
	// --------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
	
	public boolean doIt(int width, int height, int frameRate, Vector inFiles, MediaLocator outML)
	{
		ImageDataSource ids = new ImageDataSource(width, height, frameRate, inFiles);
		
		Processor p;
		
		try
		{
			System.err.println("- create processor for the image datasource ...");
			p = Manager.createProcessor(ids);
		}
		catch (Exception e)
		{
			System.err.println("Yikes!  Cannot create a processor from the data source.");
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
		p.setContentDescriptor(new ContentDescriptor(FileTypeDescriptor.MSVIDEO));
		
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
		
		System.err.println("Setting the track format to: " + f[0]);
		
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
		
		System.err.println("start processing...");
		
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
		}
		p.removeControllerListener(this);
		
		System.err.println("...done processing.");
		
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
			System.err.println("- create DataSink for: " + outML);
			dsink = Manager.createDataSink(ds, outML);
			dsink.open();
		}
		catch (Exception e)
		{
			System.err.println("Cannot create the DataSink: " + e);
			return null;
		}
		
		return dsink;
	}
	
	Object waitSync = new Object();
	boolean stateTransitionOK = true;
	
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
		else
			if (evt instanceof ResourceUnavailableEvent)
			{
				synchronized (waitSync)
				{
					stateTransitionOK = false;
					waitSync.notifyAll();
				}
			}
			else
				if (evt instanceof EndOfMediaEvent)
				{
					evt.getSourceController().stop();
					evt.getSourceController().close();
				}
	}
	
	Object waitFileSync = new Object();
	boolean fileDone = false;
	boolean fileSuccess = true;
	
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
		else
			if (evt instanceof DataSinkErrorEvent)
			{
				synchronized (waitFileSync)
				{
					fileDone = true;
					fileSuccess = false;
					waitFileSync.notifyAll();
				}
			}
	}
	
	static void prUsage()
	{
		System.err.println("Usage: java JpegImagesToMovie -w <width> -h <height> -f <frame rate> -o <output URL> <input JPEG file 1> <input JPEG file 2> ...");
		System.exit(-1);
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
	
	// /////////////////////////////////////////////
	//
	// Inner classes.
	// /////////////////////////////////////////////
	
	/**
	 * A DataSource to read from a list of JPEG image files and turn that into a stream of JMF buffers. The DataSource is not seekable or positionable.
	 */
	class ImageDataSource extends PullBufferDataSource
	{
		
		ImageSourceStream streams[];
		
		ImageDataSource(int width, int height, int frameRate, Vector images)
		{
			streams = new ImageSourceStream[1];
			streams[0] = new ImageSourceStream(width, height, frameRate, images);
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
			return streams;
		}
		
		/**
		 * We could have derived the duration from the number of frames and frame rate. But for the purpose of this program, it's not necessary.
		 */
		public Time getDuration()
		{
			return DURATION_UNKNOWN;
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
		
		Vector images;
		int width, height;
		VideoFormat format;
		
		int nextImage = 0; // index of the next image to be read.
		boolean ended = false;
		
		public ImageSourceStream(int width, int height, int frameRate, Vector images)
		{
			this.width = width;
			this.height = height;
			this.images = images;
			
			format = new VideoFormat(VideoFormat.JPEG, new Dimension(width, height), Format.NOT_SPECIFIED, Format.byteArray, (float) frameRate);
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
				System.err.println("Done reading all images.");
				buf.setEOM(true);
				buf.setOffset(0);
				buf.setLength(0);
				ended = true;
				return;
			}
			
			String imageFile = (String) images.elementAt(nextImage);
			nextImage++;
			
			System.err.println("  - reading image file: " + imageFile);
			
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
			
			long time = (long) (seqNo * (1000 / frameRate) * 1000000);
			buf.setTimeStamp(time);
			buf.setSequenceNumber(seqNo++);
			
			// Read the entire JPEG image from the file.
			raFile.readFully(data, 0, (int) raFile.length());
			
			System.err.println("    read " + raFile.length() + " bytes.");
			
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
