// Copyright 2009-2017 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package jhi.curlywhirly.data;

import jhi.curlywhirly.util.*;

import java.io.*;
import java.net.*;

/**
 * Holds information needed by CurlyWhirly to associate the dataset with a database
 * (of some sort).
 */
public class DBAssociation
{
	private String dbPointUrl;
	private String dbGroupUrl;
	private String dbUploadUrl;

	public DBAssociation()
	{
		dbPointUrl = "";
		dbGroupUrl = "";
		dbUploadUrl = "";
	}

	public String getDbPointUrl()
	{
		return dbPointUrl;
	}

	public void setDbPointUrl(String dbPointUrl)
	{
		this.dbPointUrl = dbPointUrl;
	}

	public boolean isPointSearchEnabled()
	{
		return dbPointUrl.length() > 0;
	}

	public String getDbGroupUrl()
	{
		return dbGroupUrl;
	}

	public void setDbGroupUrl(String dbGroupUrl)
	{
		this.dbGroupUrl = dbGroupUrl;
	}

	public boolean isGroupPreviewEnabled()
	{
		return dbGroupUrl.length() > 0;
	}

	public String getDbUploadUrl()
	{
		return dbUploadUrl;
	}

	public void setDbUploadUrl(String dbUploadUrl)
	{
		this.dbUploadUrl = dbUploadUrl;
	}

	public void visitUrlForPoint(String pointName)
		throws UnsupportedEncodingException
	{
		if (isPointSearchEnabled())
		{
			String lineURL = URLEncoder.encode(pointName, "UTF-8");
			String url = dbPointUrl.replace("$LINE", lineURL);
			NetworkUtils.visitURL(url);
		}
	}

	public void visitUrlForPoint(String urlName, DataPoint point)
		throws UnsupportedEncodingException
	{
		String pointName = URLEncoder.encode(point.getName(), "UTF-8");
		String url = point.getUrlMap().get(urlName);
		String urlWithName = url.replace("$LINE", pointName);
		NetworkUtils.visitURL(urlWithName);
	}

	public void visitUrlForGroup(String groupName)
		throws UnsupportedEncodingException
	{
		if (isGroupPreviewEnabled())
		{
			System.out.println(dbGroupUrl);
			String groupUrl = URLEncoder.encode(groupName, "UTF-8");
			String url = dbGroupUrl.replace("$GROUP", groupUrl);
			NetworkUtils.visitURL(url);
		}
	}

	public void visitUrlForUpload(File file)
	{
		try
		{
			HttpURLConnection connection = NetworkUtils.multipartPostFile(dbUploadUrl, file);

			int code = connection.getResponseCode();

			if (code == HttpURLConnection.HTTP_OK)
			{
				BufferedReader conReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
				String groupFilename = conReader.readLine();

				visitUrlForGroup(groupFilename);
			}
			else if (code == 507)
			{
				// TODO: Something about fileSizeLimitExceeded
				System.out.println("The upload file exceeded the server's file size limit.");
			}
			else
			{
				// TODO: Actually handle this situation in some way
				System.out.println("There was a problem sending the data to the server: " + code);
			}
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
}