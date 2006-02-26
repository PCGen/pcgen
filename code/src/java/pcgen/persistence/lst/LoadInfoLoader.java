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
 * Current Ver: $Revision: 1.9 $
 * Last Editor: $Author: karianna $
 * Last Edited: $Date: 2006/01/30 15:48:32 $
 *
 */
package pcgen.persistence.lst;

import pcgen.core.SystemCollections;
import pcgen.core.system.LoadInfo;
import pcgen.persistence.PersistenceLayerException;
import pcgen.util.Logging;

import java.net.URL;

/**
 * @author Stefan Radermacher <zaister@users.sourceforge.net>
 * @version $Revision: 1.9 $
 **/
public class LoadInfoLoader extends LstLineFileLoader
{

	/** Creates a new instance of LoadInfoLoader */
	public LoadInfoLoader()
	{
	    // Empty Constructor
	}

	public void loadLstFile(String source, String gameModeIn) throws PersistenceLayerException
	{
		super.loadLstFile(source, gameModeIn);

		if (SystemCollections.getLoadInfo(gameModeIn).getLoadMultiplierCount() == 0)
		{
			Logging.errorPrint("Warning: load.lst for game mode " + gameModeIn +
				" does not contain load category definitions. No weight categories will be available. " +
				"Please refer to the documentation for the Load List file.");
		}
		else if (SystemCollections.getLoadInfo(gameModeIn).getLoadMultiplier("LIGHT") == null ||
				SystemCollections.getLoadInfo(gameModeIn).getLoadMultiplier("MEDIUM") == null ||
				SystemCollections.getLoadInfo(gameModeIn).getLoadMultiplier("HEAVY") == null )
		{
			Logging.errorPrint("Warning: load.lst for game mode " + gameModeIn +
				" does not contain load category definitions for 'Light', 'Medium' and 'Heavy'. " +
				"Please refer to the documentation for the Load List file.");
		}
	}

	/**
	 * @see LstLineFileLoader#parseLine(String, URL)
	 */
	public void parseLine(String lstLine, URL sourceURL)
	{

		LoadInfo loadInfo = SystemCollections.getLoadInfo(getGameMode());

		if (lstLine.startsWith("ENCUMBRANCE:") || lstLine.startsWith("ENCUMBERANCE:"))
		{

			String[] fields = lstLine.substring(lstLine.indexOf(':') + 1 ).split("\\|");
			if ((fields.length < 2) || (fields.length > 4))
			{
				Logging.errorPrint("LoadInfoLoader got unexpected line '" + lstLine + ". Line ignored.");
				return;
			}

			String moveFormula = "";
			if (fields.length >= 3)
			{
				moveFormula = fields[2];
			}

			Integer checkPenalty = new Integer(0);
			if (fields.length == 4)
			{
				checkPenalty = new Integer(fields[3]);
			}

			String type = fields[0];
			String[] number = fields[1].split("/");
			if (number.length == 1)
			{
				Float a = new Float(fields[1]);
				loadInfo.addLoadMultiplier(type.toUpperCase(), new Float(a.doubleValue()), moveFormula, checkPenalty);
			}
			else if (number.length == 2)
			{
				Float a = new Float(number[0]);
				Float b = new Float(number[1]);
				loadInfo.addLoadMultiplier(type.toUpperCase(), new Float(a.doubleValue() / b.doubleValue()), moveFormula, checkPenalty);
			}
			else
			{
				Logging.errorPrint("LoadInfoLoader got unexpected line '" + lstLine + ". Line ignored.");
				return;
			}
		}
		else if (lstLine.startsWith("MODIFIER:"))
		{
			loadInfo.setLoadModifierFormula(lstLine.substring(9));
		}
		else
		{
			String[] sets = lstLine.split(",");
			if (sets.length > 1)
			{
				String[] fields = sets[0].split("\\|");
				if (fields.length == 2)
				{
					// size adjustments
					for (int i = 0; i < sets.length; i++)
					{
						fields = sets[i].split("\\|");
						if (fields.length != 2)
						{
							Logging.errorPrint("LoadInfoLoader got unexpected line '" + lstLine + ". Line ignored.");
							return;
						}
						String size = fields[0];
						Float value = new Float(fields[1]);
						loadInfo.addSizeAdjustment(size, value);
					}
				}
				else
				{
					Logging.errorPrint("LoadInfoLoader got unexpected line '" + lstLine + ". Line ignored.");
					return;
				}
			}
			else
			{
				// load values
				String[] values = lstLine.split("\t");
				if (values.length == 2)
				{
					if ("x".equals(values[0]))
					{
						loadInfo.setLoadScoreMultiplier(new Float(values[1]));
					}
					else
					{
						loadInfo.addLoadScoreValue(Integer.parseInt(values[0]), new Float(values[1]));
					}

				}
				else
				{
					Logging.errorPrint("LoadInfoLoader got unexpected line '" + lstLine + ". Line ignored.");
					return;
					}
			}
		}
	}
}
