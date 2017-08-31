// Copyright 2009-2017 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package jhi.curlywhirly.io;

import java.io.*;
import java.util.*;

/**
 * Represents a .curlywhirly file; a simple xml wrapper around actual references
 * to a data file
 */
public class CurlyWhirlyFile
{
	public File dataFile;

	CurlyWhirlyFile()
	{
	}

	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
			return true;
		if (!(obj instanceof CurlyWhirlyFile))
			return false;

		CurlyWhirlyFile o = (CurlyWhirlyFile) obj;

		return (dataFile == null ? o.dataFile == null : dataFile.equals(o.dataFile)) != false;
	}

	@Override
	public int hashCode()
	{
		int hash = 7;
		hash = 59 * hash + Objects.hashCode(this.dataFile);
		return hash;
	}
}