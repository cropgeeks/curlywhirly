package curlywhirly.io;

import java.io.*;

import scri.commons.gui.*;

public class ReadException extends Exception
{
	/** Thrown when attempting to parse a file that cannot be understood. */
	public static final int UNKNOWN_FORMAT = 10;
	public static final int TOKEN_COUNT_WRONG = 20;
	public static final int CATEGORY_COUNT_WRONG = 30;
	public static final int VALUE_COUNT_WRONG = 40;
	public static final int MISSING_HEADER = 50;
	public static final int MISSING_NAME_HEADER = 60;

	private int lineNumber;
	private File file;
	private String message;

	ReadException(File file, int lineNumber, Exception exception)
	{
		this.file = file;
		this.lineNumber = lineNumber;
		this.message =  RB.format(
			"io.ReadException.UNKNOWN_ERROR", lineNumber, file.getName(), exception.toString());
	}

	ReadException(File file, int lineNumber, int error)
	{
		this.file = file;
		this.lineNumber = lineNumber;

		message = formatMessage(error);
	}

	@Override
	public String getMessage()
	{
		return message;
	}

	public String formatMessage(int error)
	{
		switch (error)
		{
			case UNKNOWN_FORMAT: return RB.getString(
				"io.ReadException.UNKNOWN_FORMAT");

			case TOKEN_COUNT_WRONG:	return RB.format(
				"io.ReadException.TOKEN_COUNT_WRONG", lineNumber, file.getName());

			case CATEGORY_COUNT_WRONG: return RB.format(
				"io.ReadException.CATEGORY_COUNT_WRONG", lineNumber, file.getName());

			case VALUE_COUNT_WRONG: return RB.format(
				"io.ReadException.CATEGORY_COUNT_WRONG", lineNumber, file.getName());

			case MISSING_HEADER: return RB.format(
				"io.ReadException.MISSING_HEADER", lineNumber, file.getName());

			case MISSING_NAME_HEADER: return RB.format(
				"io.ReadException.MISSING_NAME_HEADER", lineNumber, file.getName());
		}

		return "curlwhirly.io.ReadException";
	}
}
