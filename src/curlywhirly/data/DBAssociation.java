// Copyright 2009-2014 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package curlywhirly.data;

/**
 * Holds information needed by CurlyWhirly to associate the dataset with a database
 * (of some sort).
 */
public class DBAssociation
{
	private String dbPointUrl = "";

	public DBAssociation()
	{
	}

	public String getDbPointUrl()
		{ return dbPointUrl; }

	public void setDbPointUrl(String dbPointUrl)
		{ this.dbPointUrl = dbPointUrl; }

	public boolean isPointSearchEnabled()
	{
		return dbPointUrl.length() > 0;
	}
}