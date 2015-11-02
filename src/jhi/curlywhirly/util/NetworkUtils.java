// Copyright 2009-2015 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package jhi.curlywhirly.util;

import java.awt.*;
import java.io.*;
import java.net.*;

public class NetworkUtils
{
	public static void visitURL(String html)
	{
		try
		{
			Desktop desktop = Desktop.getDesktop();
			URI uri = new URI(html);
			desktop.browse(uri);
		}
		catch (URISyntaxException | IOException e)
		{
			System.out.println(e);
		}
	}

	public static HttpURLConnection multipartPostFile(String url, File file)
		throws IOException
	{
		String boundary = Long.toHexString(System.currentTimeMillis()); // Just generate some unique random value.
		String CRLF = "\r\n"; // Line separator required by multipart/form-data.

		HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
		connection.setRequestMethod("POST");
		connection.setDoOutput(true);
		connection.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);

		OutputStream output = connection.getOutputStream();
		PrintWriter writer = new PrintWriter(new OutputStreamWriter(output, "UTF-8"), true); // true = autoFlush, important!

		// Send text file.
		writer.append("--" + boundary).append(CRLF);
		writer.append("Content-Disposition: form-data; name=\"textfile\"; filename=\"" + "FILENAME" + "\"").append(CRLF);
		writer.append("Content-Type: application/octet-stream; charset=UTF-8").append(CRLF);
		writer.append(CRLF).flush();

		BufferedReader reader = new BufferedReader(new FileReader(file));

		String line;
		while ((line = reader.readLine()) != null)
			writer.println(line);

		writer.flush();

		// End of multipart/form-data.
		writer.append("--" + boundary + "--").append(CRLF);
		writer.close();

		return connection;
	}
}