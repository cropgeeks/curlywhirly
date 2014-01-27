// Copyright 2009-2013 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package curlywhirly.data;

/**
 * Holds information needed by CurlyWhirly to associate the dataset with a database
 * (of some sort).
 */
public class DBAssociation
{
	private String pointSearch = "";

	public DBAssociation()
	{
	}

	public String getPointSearch()
		{ return pointSearch; }

	public void setPointSearch(String pointSearch)
		{ this.pointSearch = pointSearch; }

	public boolean isPointSearchEnabled()
	{
		return pointSearch.length() > 0;
	}
}