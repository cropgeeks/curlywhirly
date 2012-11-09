package curlywhirly.controller;

import java.io.*;
import java.sql.*;
import java.util.*;

/**
 * Parses the cgi-bin produced log file to determine who has been using Flapjack and where in the world (by country) they're located.
 */
public class LogParser
{
	private static Connection c = null;
	private HashMap<String, User> hashMap = new HashMap<String, User>();

	public static void main(String[] args) throws Exception
	{
		Class.forName("com.mysql.jdbc.Driver").newInstance();
		c = DriverManager.getConnection("jdbc:mysql://penguin.scri.sari.ac.uk:3306/IP_locator", "root", "");

		new LogParser(args[0]);
	}

	LogParser(String file) throws Exception
	{
		File logFile = new File(file);

		BufferedReader in = new BufferedReader(new FileReader(logFile));
		String str = in.readLine();

		while (str != null && str.length() > 0)
		{
			processLine(str);
			str = in.readLine();
		}

		in.close();
		c.close();

		printResults();
	}

	private void processLine(String str) throws Exception
	{
		String[] tokens = str.split("\t");

		String ip = tokens[1];
		String id = tokens[2];
		String locale = tokens[4];
		String os = tokens[6];
		// Only parse the username if it's actually there
		String username = (tokens.length == 8) ? tokens[7] : "";

		// Check to see if this user has been included already?
		User user = hashMap.get(id);

		// If not, create an entry for them and add to the hashtable
		if (user == null)
		{
			user = new User(id, locale, os, username, ip);
			hashMap.put(id, user);
		}

		user.runCount++;
	}

	private void printResults()
	{
		ArrayList<User> users = (ArrayList<User>) hashMap.values();
		Collections.sort(users);

		System.out.println("\n" + users.size() + " total users:\n");

		for (User user : users)
			System.out.println(user.id + " " + user.ip + "\t" + user.os + "\t" + user.countryCode + "\t" + user.username);
	}

	private static class User implements Comparable<User>
	{
		String id;
		String locale;
		String os;
		String username;
		String ip;

		String country;
		String countryCode;
		int runCount;

		User(String id, String locale, String os, String username, String ip) throws Exception
		{
			this.id = id;
			this.locale = locale;
			this.os = os;
			this.username = username;
			this.ip = ip;

			System.out.println("DB lookup for: " + ip);
			getCountry();
		}

		private void getCountry() throws Exception
		{
			String[] address = ip.split("\\.");

			long[] ip =
			{ Long.parseLong(address[0]), Long.parseLong(address[1]), Long.parseLong(address[2]),
							Long.parseLong(address[3]) };

			long number = ip[0] * 16777216 + ip[1] * 65536 + ip[2] * 256 + ip[3];

			String sql = "SELECT * FROM IP_locations WHERE IP_FROM <= " + number + " AND IP_TO >= " + number;

			Statement sm = c.createStatement();
			ResultSet rs = sm.executeQuery(sql);

			while (rs.next())
			{
				country = rs.getString(5);
				countryCode = rs.getString(3);
				break;
			}
		}

		public int compareTo(User other)
		{
			if (runCount > other.runCount)
				return -1;
			else
				if (runCount == other.runCount)
					return 0;
				else
					return 1;
		}
	}
}