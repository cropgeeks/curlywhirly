package graphviewer3d.controller;

import graphviewer3d.gui.Preferences;

import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Enumeration;
import java.util.Locale;

/**
 * Class for sending usage info back to the server
 * Code courtesy of Iain Milne, SCRI
 */
public class UsageLogger
{
	
	public static void logUsage()
	{
		Runnable r = new Runnable()
		{
			public void run()
			{
				try
				{
					// Safely encode the URL's parameters
					String id = URLEncoder.encode(Preferences.curlywhirlyID, "UTF-8");
					String locale = URLEncoder.encode("" + Locale.getDefault(), "UTF-8");
					String os = URLEncoder.encode(System.getProperty("os.name"), "UTF-8");
					String user = URLEncoder.encode(System.getProperty("user.name"), "UTF-8");
					
					String addr = "http://bioinf.scri.ac.uk/cgi-bin/curlywhirly/curlywhirly.cgi" + "?id=" + id + "&locale=" + locale + "&os=" + os;
					
					// We DO NOT log usernames from non-SCRI addresses
					if (isSCRIUser())
						addr += "&user=" + user;
					
					// Nudges the cgi script to log the fact that a version of
					// Flapjack has been run
					URL url = new URL(addr);
					HttpURLConnection c = (HttpURLConnection) url.openConnection();
					
					c.getResponseCode();
					c.disconnect();
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}
			}
		};
		
		// We run this in a separate thread to avoid any waits due to lack of an
		// internet connection or the server being non-responsive
		new Thread(r).start();
	}
	
	// Checks to see if the IP address of the current user is an SCRI one
	static boolean isSCRIUser()
	{
		try
		{
			// Need to check over all network interfaces (LAN/wireless/etc) to
			// try and find a match...
			Enumeration<NetworkInterface> e = NetworkInterface.getNetworkInterfaces();
			
			while (e != null & e.hasMoreElements())
			{
				// And each interface can have multiple IPs...
				Enumeration<InetAddress> e2 = e.nextElement().getInetAddresses();
				while (e2.hasMoreElements())
				{
					String addr = e2.nextElement().getHostAddress();
					
					if (addr.startsWith("143.234.96.") || addr.startsWith("143.234.97.") || addr.startsWith("143.234.98.") || addr.startsWith("143.234.99.") || addr.startsWith("143.234.100.") || addr.startsWith("143.234.101."))
						return true;
				}
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		
		return false;
	}
	
}
