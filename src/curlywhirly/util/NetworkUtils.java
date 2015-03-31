// Copyright 2009-2015 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package curlywhirly.util;

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
}