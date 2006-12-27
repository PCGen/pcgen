/*
 * EquipmentLoader.java
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
 * Created on February 22, 2002, 10:29 PM
 *
 * Current Ver: $Revision$
 * Last Editor: $Author$
 * Last Edited: $Date$
 *
 */
package pcgen.persistence.lst;

import java.net.URL;
import java.util.Map;
import java.util.StringTokenizer;

import pcgen.core.Equipment;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.SystemLoader;
import pcgen.util.Logging;

/**
 * @author David Rice <david-pcgen@jcuz.com>
 * @version $Revision$
 */
public final class EquipmentLoader
{
	/**
	 * Creates a new instance of EquipmentLoader
	 */
	private EquipmentLoader()
	{
		// Empty Constructor
	}

	/**
	 * Parse the line
	 * @param equipment
	 * @param inputLine
	 * @param sourceURL
	 * @param lineNum
	 * @throws PersistenceLayerException
	 */
	public static void parseLine(Equipment equipment, String inputLine,
		URL sourceURL, int lineNum) throws PersistenceLayerException
	{
		if (equipment == null)
		{
			return;
		}

		final StringTokenizer colToken =
				new StringTokenizer(inputLine, SystemLoader.TAB_DELIM);
		int col = 0;

		if (!equipment.isNewItem())
		{
			col = 1; // .MOD skips required fields
			colToken.nextToken(); // skip name
		}

		Map<String, LstToken> tokenMap =
				TokenStore.inst().getTokenMap(EquipmentLstToken.class);
		while (colToken.hasMoreTokens())
		{
			final String colString = colToken.nextToken().trim();
			colString.length();

			final int idxColon = colString.indexOf(':');
			String key = "";
			try
			{
				key = colString.substring(0, idxColon);
			}
			catch (Exception e)
			{
				// TODO Handle Exception
			}
			EquipmentLstToken token = (EquipmentLstToken) tokenMap.get(key);
			if (col == 0)
			{
				equipment.setName(colString);
			}
			else if (token != null)
			{
				final String value = colString.substring(idxColon + 1);
				LstUtils.deprecationCheck(token, equipment, value);
				if (!token.parse(equipment, value))
				{
					Logging.errorPrint("Error parsing ability "
						+ equipment.getName() + ':' + sourceURL.getFile() + ':'
						+ colString + "\"");
				}
			}
			else if (colString.startsWith("Cost:"))
			{
				Logging.errorPrint("Cost deprecated, use COST "
					+ equipment.getName() + ':' + sourceURL.getFile() + ':'
					+ colString + "\"");
				token = (EquipmentLstToken) tokenMap.get("COST");
				final String value = colString.substring(idxColon + 1);
				if (!token.parse(equipment, value))
				{
					Logging.errorPrint("Error parsing ability "
						+ equipment.getName() + ':' + sourceURL.getFile() + ':'
						+ colString + "\"");
				}
			}
			//else if ((aLen > 9) && colString.startsWith("DEFBONUS:"))
			//{
			//	equipment.setDefBonus(colString.substring(7));
			//}
			/*else if ((aLen > 13) && colString.startsWith("TREASURELIST:"))
			 {
			 if (SettingsHandler.isGMGen())
			 {
			 String treasureList = colString.substring(13);
			 StringTokenizer treasureTok = new StringTokenizer(treasureList, "|");

			 while (treasureTok.hasMoreTokens())
			 {
			 StringTokenizer cmdTok = new StringTokenizer(treasureTok.nextToken(), "=");
			 String lists = cmdTok.nextToken();
			 int weight = Integer.parseInt(cmdTok.nextToken().trim());
			 StringTokenizer listTok = new StringTokenizer(lists, ",");

			 while (listTok.hasMoreTokens())
			 {
			 equipment.addTreasureList(listTok.nextToken(), weight);
			 }
			 }
			 }
			 }*/
			/*else if ((aLen > 6) && colString.startsWith("TECHLEVEL:"))
			 {
			 equipment.setTechLevel(colString.substring(10));
			 }*/
			else if (PObjectLoader.parseTag(equipment, colString))
			{
				continue;
			}
			else
			{
				Logging.errorPrint("Illegal equipment info "
					+ sourceURL.toString() + ":" + Integer.toString(lineNum)
					+ " \"" + colString + "\"");
			}
			col++;
		}

		//		final String bonusType = equipment.getBonusType();
		//
		//		if (equipment.isArmor())
		//		{
		//			if (bonusType == null)
		//			{
		//				equipment.setBonusType("Armor");
		//
		//				return;
		//			}
		//
		//			if (bonusType.lastIndexOf("Armor") > -1)
		//			{
		//				return;
		//			}
		//
		//			equipment.setBonusType(bonusType + "Armor");
		//		}
		//		else if (equipment.isShield())
		//		{
		//			if (bonusType == null)
		//			{
		//				equipment.setBonusType("Shield");
		//
		//				return;
		//			}
		//
		//			if (bonusType.lastIndexOf("Shield") > -1)
		//			{
		//				return;
		//			}
		//
		//			equipment.setBonusType(bonusType + "Shield");
		//		}
	}
}
