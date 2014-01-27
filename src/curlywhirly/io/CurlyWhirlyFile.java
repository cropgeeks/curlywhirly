package curlywhirly.io;

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

	public boolean equals(Object obj)
	{
		if (obj == null || obj.getClass() != getClass())
            return false;

		CurlyWhirlyFile o = (CurlyWhirlyFile) obj;

		if ((dataFile == null ? o.dataFile == null : dataFile.equals(o.dataFile)) == false)
			return false;

		return true;
	}
}