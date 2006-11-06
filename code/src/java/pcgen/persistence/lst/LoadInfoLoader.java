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
 * Current Ver: $Revision$
 * Last Editor: $Author$
 * Last Edited: $Date$
 *
 */
package pcgen.persistence.lst;

import java.net.URL;
import java.util.Map;

import pcgen.core.SystemCollections;
import pcgen.core.system.LoadInfo;
import pcgen.persistence.PersistenceLayerException;
import pcgen.util.Logging;

/**
 * @author Stefan Radermacher <zaister@users.sourceforge.net>
 * @version $Revision$
 **/
public class LoadInfoLoader extends LstLineFileLoader
{

	/** Creates a new instance of LoadInfoLoader */
	public LoadInfoLoader()
	{
	    // Empty Constructor
	}

	@Override
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
	@Override
	public void parseLine(String lstLine, URL sourceURL)
	{

		LoadInfo loadInfo = SystemCollections.getLoadInfo(getGameMode());
		Map<String, LstToken> tokenMap = TokenStore.inst().getTokenMap(LoadInfoLstToken.class);

		final int idxColon = lstLine.indexOf(':');
		String key = "";
		try
		{
			key = lstLine.substring(0, idxColon);
		}
		catch(StringIndexOutOfBoundsException e) {
			// TODO Handle Exception
		}
		LoadInfoLstToken token = (LoadInfoLstToken) tokenMap.get(key);

		if (token != null)
		{
			final String value = lstLine.substring(idxColon + 1);
			LstUtils.deprecationCheck(token, loadInfo.toString(), "level.lst", value);
			if (!token.parse(loadInfo, value))
			{
				Logging.errorPrint("Error parsing ability " + loadInfo + ':' + "level.lst" + ':' + lstLine + "\"");
			}
		}
		else
		{
			LstUtils.deprecationWarning("Using deprecated style of load.lst.  Please consult the docs for information about the new style load.lst");
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
