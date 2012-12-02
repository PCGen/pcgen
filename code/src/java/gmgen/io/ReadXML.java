/*
 *  GMGen - A role playing utility
 *  Copyright (C) 2003 Devon D Jones
 *
 *  This library is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public
 *  License as published by the Free Software Foundation; either
 *  version 2.1 of the License, or (at your option) any later version.
 *
 *  This library is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *  Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public
 *  License along with this library; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */
package gmgen.io;

import java.io.File;
import java.io.IOException;
import java.util.Vector;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import pcgen.util.Logging;

/**
 * This class is used to read through XML tables on disk.
 * It will create a <code>Vector</code> in memory.
 * @author Expires 2003
 * @version 2.10
 */
public class ReadXML
{
	/** The document used for XML parsing. */
	private Document document;

	/** The name of the table. */
	private String tableName;

	/** The table as a Vector. */
	private VectorTable vt;

	/** The columns of a tabke. */
	private int cols = 0;

	/** The rows of the table. */
	private int rows = 0;

	/**
	 * Creates an instance of this class taking in a file.
	 * @param file the file for the table.
	 */
	public ReadXML(File file)
	{
		this();
		readxmlFile(file);
	}

	/**
	 * Creates an instance of this class.
	 */
	private ReadXML()
	{
		vt = new VectorTable();
		tableName = "";
	}

	/**
	 * Gets the table as a <code>Vector</code>.
	 * @return the table.
	 */
	public VectorTable getTable()
	{
		return vt;
	}

	/**
	 * Gets the name of the table.
	 * @return the name of the table.
	 */
	public String getTableName()
	{
		return tableName;
	}

	/**
	 * Finds the percentage of an entry.
	 * @param value a value to look for in the table.
	 * @return String
	 */
	public String findPercentageEntry(int value)
	{
		String percent = Integer.toString(value);

		for (int x = value; x <= 100; x++)
		{
			percent = Integer.toString(x);

			if (vt.crossReference(percent, "d%") != null)
			{
				break;
			}
		}

		return percent;
	}

	/**
	 * Reads through the file and parses the XML.
	 * @param table the file that is the table.
	 */
	private void readxmlFile(File table)
	{
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db;
		int items;
		Logging.debugPrint("readxmlFile called.");

		try
		{
			db = dbf.newDocumentBuilder();
			document = db.parse(table);
			tableName =
					document.getElementsByTagName("lookuptable").item(0)
						.getAttributes().getNamedItem("name").getNodeValue();

			rows = document.getElementsByTagName("row").getLength();
			items = document.getElementsByTagName("item").getLength();
			cols = items / rows;

			vt.setName(table.getPath());

			int pos = 0;

			Vector<String> row;
			for (int x = 0; x < rows; x++)
			{
				row = new Vector<String>();

				for (int y = 0; y < cols; y++)
				{
					row.add(document.getElementsByTagName("item").item(pos)
						.getChildNodes().item(0).getNodeValue());
					pos++;
				}

				vt.add(row);
			}
		}
		catch (ParserConfigurationException e)
		{
			Logging.errorPrint("ParserConfigurationException!");
			Logging.errorPrint("Could not parse xml file " + table.getPath());
			Logging.errorPrint("IO", e);
		}
		catch (IOException io)
		{
			Logging.errorPrint("IOException!");
			Logging.errorPrint("Could not parse xml file " + table.getPath());
			Logging.errorPrint("IO", io);
		}
		catch (SAXException sax)
		{
			Logging.errorPrint("SAXException!");
			Logging.errorPrint("Could not parse xml file " + table.getPath());
			Logging.errorPrint("IO", sax);
		}
		catch (IllegalArgumentException iae)
		{
			Logging.errorPrint("IllegalArgumentException!");
			Logging.errorPrint("Could not parse xml file " + table.getPath());
			Logging.errorPrint("IO", iae);
		}
	}
}
