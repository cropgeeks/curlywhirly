// Copyright 2009-2014 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package curlywhirly.data;

import java.io.*;
import java.net.*;

import curlywhirly.gui.*;

/**
 * Holds information needed by CurlyWhirly to associate the dataset with a database
 * (of some sort).
 */
public class DBAssociation
{
	private String dbPointBaseUrl;

	public DBAssociation()
	{
		dbPointBaseUrl = "";
	}

	public String getDbPointUrl()
		{ return dbPointBaseUrl; }

	public void setDbPointUrl(String dbPointUrl)
	{
		this.dbPointBaseUrl = dbPointUrl;
	}

	public boolean isPointSearchEnabled()
	{
		return dbPointBaseUrl.length() > 0;
	}

	public void visitUrlForPoint(String pointName)
		throws UnsupportedEncodingException
	{
		if (isPointSearchEnabled())
			CWUtils.visitURL(dbPointBaseUrl + URLEncoder.encode(pointName, "UTF-8"));
	}
}