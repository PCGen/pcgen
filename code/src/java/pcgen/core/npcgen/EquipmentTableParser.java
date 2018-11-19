/*
 * Copyright 2006 (C) Aaron Divinsky <boomer70@yahoo.com>
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */
package pcgen.core.npcgen;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import pcgen.core.Equipment;
import pcgen.core.GameMode;
import pcgen.core.Globals;
import pcgen.core.SystemCollections;
import pcgen.util.Logging;

/**
 * Parse a equipment table (random treasure) data file.
 * 
 * 
 */
public class EquipmentTableParser
{
	private final SAXParser theParser;
	private final GameMode theMode;

	private final HashMap<EquipmentItem, String> theLinkTable = new HashMap<>();
	private final HashMap<EqmodItem, String> theEqmodLinkTable = new HashMap<>();

	/**
	 * Creates a new <tt>EquipmentTableParser</tt> for the specified game mode.
	 *
	 * @param aMode The game mode to parse equipment tables for.
	 * @throws ParserConfigurationException the parser configuration exception
	 * @throws SAXException the SAX exception
	 */
	public EquipmentTableParser(final GameMode aMode) throws ParserConfigurationException, SAXException
	{
		theMode = aMode;

		final SAXParserFactory parserFactory = SAXParserFactory.newInstance();
		parserFactory.setValidating(true);
		theParser = parserFactory.newSAXParser();
	}

	/**
	 * Parses an XML equipment table file.
	 *
	 * @param aFileList An array of files to process
	 * @return A <tt>List</tt> of <tt>EquipmentTable</tt> objects representing
	 * the tables in the file.
	 * @throws SAXException the SAX exception
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public List<EquipmentTable> parse(final File[] aFileList) throws SAXException, IOException
	{
		final List<EquipmentTable> ret = new ArrayList<>();

		for (final File fileName : aFileList)
		{
			try
			{
				theParser.parse(fileName, new EquipmentTableHandler(theMode, ret));
			}
			catch (IllegalArgumentException ex)
			{
				// Do nothing, means we weren't the right game mode for this file.
			}
		}

		// Resolve all links now that all tables should have been read in.
		for (final EquipmentItem item : theLinkTable.keySet())
		{
			final EquipmentTable table = EquipmentTable.get(theLinkTable.get(item));
			if (table == null)
			{
				Logging.errorPrint("Could not find linked table " + theLinkTable.get(item) + " for " + item);
				continue;
			}
			item.setLookup(table);
		}

		for (final EqmodItem item : theEqmodLinkTable.keySet())
		{
			final EqmodTable table = EqmodTable.get(theEqmodLinkTable.get(item));
			if (table == null)
			{
				Logging.errorPrint("Could not find linked table " + theEqmodLinkTable.get(item) + " for " + item);
				continue;
			}
			item.setLookup(table);
		}
		return ret;
	}

	/** An enum for the current state in the state machine the parser is in */
	private enum ParserState
	{
		/** The initial state of the parser */
		INIT, EQTABLEDATA, EQMODTABLEDATA, ENTRYDATA, EQUIPMENT
	}

	/**
	 * This is the parsing event handler class.  The methods in this class are
	 * called by the SAX parser as it finds various elements in the XML file.
	 * 
	 */
	class EquipmentTableHandler extends DefaultHandler
	{
		private final List<EquipmentTable> theList;

		private GameMode theGameMode = null;
		private boolean theValidFlag = false;

		private ParserState theState = ParserState.INIT;

		private Table theCurrentData = null;
		private TableEntry theCurrentEntry = null;
		private EquipmentItem theCurrentItem = null;

		/**
		 * Constructs the handler
		 * 
		 * @param aMode The game mode to expect the file to be for.
		 * @param aList The list of <tt>ClassData</tt> objects to fill
		 */
		public EquipmentTableHandler(final GameMode aMode, final List<EquipmentTable> aList)
		{
			theGameMode = aMode;
			theList = aList;
		}

		/**
		 * @throws  SAXException
		 * @throws  IllegalArgumentException if the file being processed is not the same GameMode as requested.
		 */
		@Override
		public void startElement(final String uri, final String localName, final String aName, final Attributes anAttrs)
			throws SAXException
		{
			if (theState == ParserState.INIT && "equipment_tables".equals(aName)) //$NON-NLS-1$
			{
				if (anAttrs != null)
				{
					final String gm = anAttrs.getValue("game_mode"); //$NON-NLS-1$
					if (!SystemCollections.getGameModeNamed(gm).equals(theGameMode))
					{
						throw new IllegalArgumentException("Incorrect game mode"); //$NON-NLS-1$
					}
					theValidFlag = true;
				}
				return;
			}

			if (!theValidFlag)
			{
				throw new SAXException("Generators.Equipment.InvalidFileFormat"); //$NON-NLS-1$
			}

			if (theState == ParserState.INIT)
			{
				if ("table".equals(aName)) //$NON-NLS-1$
				{
					if (anAttrs != null)
					{
						final String name = anAttrs.getValue("name"); //$NON-NLS-1$
						final String id = anAttrs.getValue("id"); //$NON-NLS-1$

						// See if this table already exists
						EquipmentTable table = EquipmentTable.get(id);
						if (table == null)
						{
							// This is not an existing table, so create one.
							table = new EquipmentTable(id);
							table.setName(name);
							EquipmentTable.addTable(table);
							theList.add(table);
						}
						theCurrentData = table;
						theState = ParserState.EQTABLEDATA;
					}
				}
				else if ("eqmod_table".equals(aName))
				{
					if (anAttrs != null)
					{
						final String name = anAttrs.getValue("name"); //$NON-NLS-1$
						final String id = anAttrs.getValue("id"); //$NON-NLS-1$

						// See if this table already exists
						EqmodTable table = EqmodTable.get(id);
						if (table == null)
						{
							// This is not an existing table, so create one.
							table = new EqmodTable(id);
							table.setName(name);
							EqmodTable.addTable(table);
						}
						theCurrentData = table;
						theState = ParserState.EQMODTABLEDATA;
					}
				}
			}
			else if (theState == ParserState.EQTABLEDATA)
			{
				if ("entry".equals(aName))
				{
					// Found an entry for the table
					if (anAttrs != null)
					{
						final String entryName = anAttrs.getValue("name");
						if (entryName != null)
						{
							final TableEntry te = new EquipmentTableEntry(entryName);
							theCurrentEntry = te;
							theState = ParserState.ENTRYDATA;
							final int weight = getWeight(anAttrs);
							theCurrentData.add(weight, te);
						}
					}
				}
			}
			else if (theState == ParserState.EQMODTABLEDATA)
			{
				if ("entry".equals(aName))
				{
					// Found an entry for the table
					if (anAttrs != null)
					{
						final String entryName = anAttrs.getValue("name");
						if (entryName != null)
						{
							final TableEntry te = new EqmodTableEntry(entryName);
							theCurrentEntry = te;
							theState = ParserState.ENTRYDATA;
							final int weight = getWeight(anAttrs);
							theCurrentData.add(weight, te);
						}
					}
				}
			}
			else if (theState == ParserState.ENTRYDATA)
			{
				if ("equipment".equals(aName))
				{
					if (anAttrs != null)
					{
						theCurrentItem = new EquipmentItem();
						final String rolls = anAttrs.getValue("rolls");
						if (rolls != null)
						{
							theCurrentItem.setTimes(rolls);
						}
						final String linkLoc = anAttrs.getValue("link");
						if (linkLoc != null)
						{
							// This entry contains a lookup
							final EquipmentTable table = EquipmentTable.get(linkLoc);
							if (table == null)
							{
								// Store the lookup in a Hashtable until it the end
								// so we can resolve all the references.
								theLinkTable.put(theCurrentItem, linkLoc);
							}
							else
							{
								theCurrentItem.setLookup(table);
							}
						}
						else
						{
							final String choiceStr = anAttrs.getValue("choose");
							if (choiceStr != null)
							{
								String[] choices = choiceStr.split("\\|");
								theCurrentItem.setVariableEquipment(anAttrs.getValue("value"), Arrays.asList(choices));
							}
							else
							{
								final String val = anAttrs.getValue("value");
								if (val != null)
								{
									final Equipment eq = Globals.getContext().getReferenceContext()
										.silentlyGetConstructedCDOMObject(Equipment.class, val);
									if (eq == null)
									{
										Logging.errorPrint("Could not find equipment named: " + val);
									}
									theCurrentItem.setEquipment(eq);
								}
								final String qty = anAttrs.getValue("quantity");
								theCurrentItem.setQuantity(qty);
							}
						}
						theCurrentEntry.addData(theCurrentItem);
					}
					theState = ParserState.EQUIPMENT;
				}
			}
			else if (theState == ParserState.EQUIPMENT)
			{
				if ("eqmod".equals(aName)) //$NON-NLS-1$
				{
					if (anAttrs != null)
					{
						final EqmodItem eqmodItem = new EqmodItem();
						final String link = anAttrs.getValue("link"); //$NON-NLS-1$
						if (link != null)
						{
							final EqmodTable table = EqmodTable.get(link);
							if (table == null)
							{
								theEqmodLinkTable.put(eqmodItem, link);
							}
							else
							{
								eqmodItem.setLookup(table);
							}
						}
						else
						{
							final String rollStr = anAttrs.getValue("roll");
							if (rollStr != null)
							{
								eqmodItem.setRollString(rollStr);
							}
							final String val = anAttrs.getValue("value"); //$NON-NLS-1$
							if (val != null)
							{
								eqmodItem.setEqmod(val);
							}
						}
						theCurrentItem.addEqMod(eqmodItem);
					}
				}
			}
		}

		@Override
		public void endElement(final String uri, final String localName, final String qName)
		{
			if ("equipment_tables".equals(qName)) //$NON-NLS-1$
			{

				theState = ParserState.INIT;
			}
			else if ("table".equals(qName)) //$NON-NLS-1$
			{
				theState = ParserState.INIT;
				TreasureGenerator.addTable(theGameMode, (EquipmentTable) theCurrentData);
			}
			else if ("eqmod_table".equals(qName)) //$NON-NLS-1$
			{
				theState = ParserState.INIT;
			}
			else if ("entry".equals(qName)) //$NON-NLS-1$
			{
				if (theCurrentData instanceof EquipmentTable)
				{
					theState = ParserState.EQTABLEDATA;
				}
				else if (theCurrentData instanceof EqmodTable)
				{
					theState = ParserState.EQMODTABLEDATA;
				}
			}
			else if ("equipment".equals(qName)) //$NON-NLS-1$
			{
				theState = ParserState.ENTRYDATA;
			}
			else if ("eqmod".equals(qName)) //$NON-NLS-1$
			{
				// Do nothing
				;
			}
		}

		private int getWeight(final Attributes anAttrs)
		{
			int weight = 1;
			final String wtStr = anAttrs.getValue("weight"); //$NON-NLS-1$
			if (wtStr != null)
			{
				weight = Integer.parseInt(wtStr.trim());
			}
			return weight;
		}
	}
}
