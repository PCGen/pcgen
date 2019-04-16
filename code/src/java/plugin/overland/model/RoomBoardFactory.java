/*
 * Copyright 2012 Vincent Lhote
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
package plugin.overland.model;

import java.io.File;
import java.text.NumberFormat;
import java.text.ParseException;

import pcgen.util.Logging;
import plugin.overland.gui.XMLFilter;
import plugin.overland.util.PairList;
import plugin.overland.util.RBCost;

import org.jdom2.DocType;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.input.SAXBuilder;
import org.jdom2.input.sax.SAXEngine;

/**
 * Factory for RoomBoard. Read from XML files.
 */
public final class RoomBoardFactory
{

	private static final String DIR_RNBPRICE = "rnbprice"; //$NON-NLS-1$

	private RoomBoardFactory()
	{
	}

	public static RoomBoard load(File dataDir)
	{
		//Create a new list for the room and board
		PairList<RBCost> inns = new PairList<>();
		PairList<RBCost> foods = new PairList<>();
		PairList<RBCost> animals = new PairList<>();

		File path = new File(dataDir, DIR_RNBPRICE);

		if (path.isDirectory())
		{
			File[] dataFiles = path.listFiles(new XMLFilter());
			SAXEngine builder = new SAXBuilder();

			for (File dataFile : dataFiles)
			{
				try
				{
					Document methodSet = builder.build(dataFile);
					DocType dt = methodSet.getDocType();

					if (dt.getElementName().equals("RNBPRICE")) //$NON-NLS-1$
					{
						//Do work here
						loadRBData(methodSet, inns, foods, animals);
					}
				} catch (Exception e)
				{
					Logging.errorPrintLocalised("XML Error with file {0}", dataFile.getName());
					Logging.errorPrint(e.getMessage(), e);
				}
			}
		}
		else
		{
			Logging.errorPrintLocalised("in_plugin_overland_noDatafile", path.getPath()); //$NON-NLS-1$
		}

		return new RoomBoardImplementation(inns, foods, animals);
	}

	private static void loadRBData(Document methodSet, PairList<RBCost> inns, PairList<RBCost> foods,
		PairList<RBCost> animals)
	{
		Element table = methodSet.getRootElement();

		float priceF = 999; //999 is the debugging value

		NumberFormat nf = TravelMethodFactory.getNumberFormat(table);

		for (Object methodObj : table.getChildren("item"))
		{
			Element method = (Element) methodObj;

			String type = method.getChild("type").getTextTrim();
			String name = method.getChild("name").getTextTrim();
			String priceS = method.getChild("price").getTextTrim();

			try
			{
				// TODO add a junit test
				priceF = nf.parse(priceS).floatValue();
			}
			catch (ParseException e1)
			{
				Logging.errorPrintLocalised("Invalid number formating \"{0}\" in XML File", priceS);
			}

			/*
			 * TODO These if-else statements are OK for now.  Eventually, I would
			 * like to make it so that if new types are present in the data
			 * file, the system will automatically add new drop-down boxes.
			 * That, however, is a long-term project.
			 */
			if (type.equals("Inn"))
			{
				inns.add(new RBCost(name, priceF));
			}
			else if (type.equals("Food"))
			{
				foods.add(new RBCost(name, priceF));
			}
			else if (type.equals("Animal"))
			{
				animals.add(new RBCost(name, priceF));
			}
		}
	}
}
