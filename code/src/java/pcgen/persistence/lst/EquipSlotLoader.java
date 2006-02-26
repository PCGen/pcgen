/*
 * EquipSlotLoader.java
 * Copyright 2001 (C) Bryan McRoberts <merton_monk@yahoo.com>
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
 *
 * Created on February 24, 2003, 10:29 AM
 *
 * Current Ver: $Revision: 1.22 $
 * Last Editor: $Author: binkley $
 * Last Edited: $Date: 2005/10/18 20:23:53 $
 *
 */
package pcgen.persistence.lst;

import pcgen.core.Globals;
import pcgen.core.SystemCollections;
import pcgen.core.character.EquipSlot;
import pcgen.persistence.SystemLoader;
import pcgen.util.Logging;

import java.net.URL;
import java.util.StringTokenizer;

/**
 * @author  Jayme Cox <jaymecox@users.sourceforge.net>
 * @version $Revision: 1.22 $
 **/
final class EquipSlotLoader extends LstLineFileLoader
{
	/** Creates a new instance of EquipSlotLoader */
	public EquipSlotLoader()
	{
	    // Empty Constructor
	}

	/**
	 * @see LstLineFileLoader#parseLine(String, URL)
	 */
	public void parseLine(String lstLine, URL sourceURL)
	{
		final EquipSlot eqSlot = new EquipSlot();

		final StringTokenizer aTok = new StringTokenizer(lstLine, SystemLoader.TAB_DELIM);

		while (aTok.hasMoreTokens())
		{
			final String colString = aTok.nextToken().trim();

			if (lstLine.startsWith("NUMSLOTS:"))
			{
				final StringTokenizer eTok = new StringTokenizer(lstLine.substring(9), SystemLoader.TAB_DELIM);

				while (eTok.hasMoreTokens())
				{
					// parse the default number of each type
					final String cString = eTok.nextToken().trim();
					final StringTokenizer cTok = new StringTokenizer(cString, ":");

					if (cTok.countTokens() == 2)
					{
						final String eqSlotType = cTok.nextToken();
						final String aNum = cTok.nextToken();
						Globals.setEquipSlotTypeCount(eqSlotType, aNum);
					}
				}
			}
			else if (colString.startsWith("EQSLOT:"))
			{
				eqSlot.setSlotName(colString.substring(7));
			}
			else if (colString.startsWith("CONTAINS:"))
			{
				final StringTokenizer bTok = new StringTokenizer(colString.substring(9), "=");

				if (bTok.countTokens() == 2)
				{
					final String aType = bTok.nextToken();
					final String numString = bTok.nextToken();
					final int aNum;

					if (numString.equals("*"))
					{
						aNum = 9999;
					}
					else
					{
						aNum = Integer.parseInt(numString);
					}

					eqSlot.setContainType(aType);
					eqSlot.setContainNum(aNum);
				}
			}
			else if (colString.startsWith("NUMBER:"))
			{
				eqSlot.setSlotNumType(colString.substring(7));
			}
			else
			{
				Logging.errorPrint("Illegal slot info '" + lstLine + "' in " + sourceURL.toString());
			}
		}

		SystemCollections.addToEquipSlotsList(eqSlot, getGameMode());
	}
}
