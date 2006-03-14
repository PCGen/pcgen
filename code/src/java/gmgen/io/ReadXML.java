package gmgen.io;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;
import pcgen.util.Logging;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.util.Vector;

/**
 * This class is used to read through XML tables on disk.
 * It will create a <code>Vector</code> in memory.
 * @author Expires 2003
 * @version 2.10
 */
public class ReadXML
{
	/** The document used for XML parsing. */
	private Document d;

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

//testing only

	/**
	 * Displays the table on paper for testing.
	 */
	public void displayTable()
	{
		for (int x = 0; x < rows; x++)
		{
			for (int y = 0; y < cols; y++)
			{
			    // TODO - This loop currently achieves nothing?
			}
		}
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

		try
		{
			db = dbf.newDocumentBuilder();
			d = db.parse(table);
			tableName = d.getElementsByTagName("lookuptable").item(0).getAttributes().getNamedItem("name").getNodeValue();

			rows = d.getElementsByTagName("row").getLength();
			items = d.getElementsByTagName("item").getLength();
			cols = items / rows;

			vt.setName(table.getPath());

			int pos = 0;

			for (int x = 0; x < rows; x++)
			{
				Vector row = new Vector();

				for (int y = 0; y < cols; y++)
				{
					row.add(d.getElementsByTagName("item").item(pos).getChildNodes().item(0).getNodeValue());
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
