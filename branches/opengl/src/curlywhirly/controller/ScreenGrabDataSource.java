package curlywhirly.controller;

import java.io.*;
import javax.media.*;
import javax.media.protocol.*;

/**
 * This DataSource captures live frames from the screen. You can specify the location, size and frame rate in the URL string as follows: screen://x,y,width,height/framespersecond Eg:
 * screen://20,40,160,120/12.5 Note: Requires JDK 1.3+ to compile and run
 */

public class ScreenGrabDataSource extends PushBufferDataSource
{

	protected Object[] controls = new Object[0];
	protected boolean started = false;
	protected String contentType = "raw";
	protected boolean connected = false;
	protected Time duration = DURATION_UNBOUNDED;
	protected LiveStream[] streams = null;
	protected LiveStream stream = null;

	public ScreenGrabDataSource(String locatorURL)
	{
		setLocator(new MediaLocator(locatorURL));
	}

	public String getContentType()
	{
		if (!connected)
		{
			System.err.println("Error: DataSource not connected");
			return null;
		}
		return contentType;
	}

	public void connect() throws IOException
	{
		if (connected)
			return;
		connected = true;
	}

	public void disconnect()
	{
		try
		{
			if (started)
				stop();
		}
		catch (IOException e)
		{
		}
		connected = false;
	}

	public void start() throws IOException
	{
		// we need to throw error if connect() has not been called
		if (!connected)
			throw new java.lang.Error("DataSource must be connected before it can be started");
		if (started)
			return;
		started = true;
		stream.start(true);
	}

	public void stop() throws IOException
	{
		if ((!connected) || (!started))
			return;
		started = false;
		stream.start(false);
	}

	public Object[] getControls()
	{
		return controls;
	}

	public Object getControl(String controlType)
	{
		return null;
	}

	public Time getDuration()
	{
		return duration;
	}

	public PushBufferStream[] getStreams()
	{
		if (streams == null)
		{
			streams = new LiveStream[1];
			stream = streams[0] = new LiveStream(getLocator());
		}
		return streams;
	}
}