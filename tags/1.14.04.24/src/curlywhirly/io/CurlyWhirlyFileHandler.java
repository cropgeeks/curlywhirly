// Copyright 2009-2014 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package curlywhirly.io;

import java.io.*;
import java.util.*;
import javax.xml.parsers.*;
import javax.xml.transform.*;
import javax.xml.transform.dom.*;
import javax.xml.transform.stream.*;
import org.w3c.dom.*;

/**
 * Handler class for dealing with the MRU file list and .curlywhirly files.
 */
public class CurlyWhirlyFileHandler
{
	// A list of recently accessed CurlyWhirlyFile objects
	public static ArrayList<CurlyWhirlyFile> recentFiles = new ArrayList<>();


	public static void addAsMostRecent(File file)
	{
		CurlyWhirlyFile cwFile = new CurlyWhirlyFile();
		cwFile.dataFile = file;

		addAsMostRecent(cwFile);
	}

	public static void addAsMostRecent(CurlyWhirlyFile cwFile)
	{
		while (recentFiles.contains(cwFile))
			recentFiles.remove(cwFile);
		recentFiles.add(0, cwFile);

		// Restrict the MRU list to no more than fifty entries
		while (recentFiles.size() > 50)
			recentFiles.remove(recentFiles.size()-1);
	}

	public static CurlyWhirlyFile createFromXML(File file)
	{
		try
		{
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(file);
			doc.getDocumentElement().normalize();

			NodeList list = doc.getElementsByTagName("curlywhirly");
			Element eCW = (Element) list.item(0);

			return readCurlyWhirlyElement(eCW);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return null;
		}
	}

	private static CurlyWhirlyFile readCurlyWhirlyElement(Element eCW)
		throws Exception
	{
		CurlyWhirlyFile cwFile = new CurlyWhirlyFile();

		NodeList list = eCW.getElementsByTagName("datafile");
		if (list.getLength() == 1)
		{
			Node node = list.item(0);
			cwFile.dataFile = new File(node.getTextContent());
		}

		return cwFile;
	}

	public static void loadMRUList(File file)
	{
		try
		{
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(new FileInputStream(file));
			doc.getDocumentElement().normalize();

			NodeList list = doc.getElementsByTagName("curlywhirly");
			for (int i = 0; i < list.getLength(); i++)
			{
				Element eCW = (Element) list.item(i);
				CurlyWhirlyFile cwFile = readCurlyWhirlyElement(eCW);

				if (recentFiles.contains(cwFile) == false)
					recentFiles.add(cwFile);
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	public static void saveMRUList(File file)
	{
		try
		{
			// Create an XML document from our recent files list
			Document doc = createXMLDoc();
			DOMSource documentSource = new DOMSource(doc);

			// Setup transformer so that it indents our XML file correctly
			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");

			// Save our document to file using by transforming the document into
			// a stream result.
			StreamResult result = new StreamResult(file);
			transformer.transform(documentSource, result);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	private static Document createXMLDoc()
		throws DOMException, ParserConfigurationException
	{
		DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = docFactory.newDocumentBuilder();
		Document doc = builder.newDocument();
		Element root = doc.createElement("curlywhirly-mru");
		doc.appendChild(root);

		for (CurlyWhirlyFile cwFile: recentFiles)
		{
			Element eCW = doc.createElement("curlywhirly");
			root.appendChild(eCW);

			if (cwFile.dataFile != null)
				eCW.appendChild(makeCWElement(doc, "datafile", cwFile.dataFile.getPath()));
		}

		return doc;
	}

	private static Element makeCWElement(Document doc, String tag, String content)
	{
		Element element = doc.createElement(tag);
		element.appendChild(doc.createTextNode(content));

		return element;
	}
}