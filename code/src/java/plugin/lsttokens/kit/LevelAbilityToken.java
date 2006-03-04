/*
 * LevelAbilityToken.java
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
 *
 * Created on March 3, 2006
 *
 * Current Ver: $Revision: $
 * Last Editor: $Author: $
 * Last Edited: $Date: $
 */
package plugin.lsttokens.kit;

import java.util.ArrayList;
import java.util.StringTokenizer;

import pcgen.core.Kit;
import pcgen.core.kit.KitLevelAbility;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.SystemLoader;
import pcgen.persistence.lst.KitLstToken;
import pcgen.util.Logging;

/**
 * Handles the LEVELABILITY and ABILITY subtag for Kits.
 */
public class LevelAbilityToken extends KitLstToken
{
	/**
	 * Gets the name of the tag this class will parse.
	 *
	 * @return Name of the tag this class handles
	 */
	public String getTokenName()
	{
		return "LEVELABILITY";
	}

	/**
	 * Handles parsing the LEVELABILITY and ABILITY subtag for Kit lines.
	 *
	 * @param aKit the Kit object to add this information to
	 * @param value the token string
	 * @return true if parse OK
	 * @throws PersistenceLayerException
	 */
	public boolean parse(Kit aKit, String value)
		throws PersistenceLayerException
	{
		final StringTokenizer colToken = new StringTokenizer(value, SystemLoader.TAB_DELIM);
		KitLevelAbility kLA = new KitLevelAbility();

		String colString = colToken.nextToken();
		String classInfo = colString;
		int levelInd = classInfo.indexOf("=");
		if (levelInd < 0)
		{
			throw new PersistenceLayerException(
				"Invalid level in KitLevelAbility info \"" + colString + "\"");
		}
		kLA.setClass(classInfo.substring(0, levelInd));
		try
		{
			kLA.setLevel(Integer.parseInt(classInfo.substring(levelInd+1)));
		}
		catch (NumberFormatException e)
		{
			throw new PersistenceLayerException(
				"Invalid level in KitLevelAbility info \"" + colString + "\"");
		}

		while (colToken.hasMoreTokens())
		{
			colString = colToken.nextToken();
			if (colString.startsWith("LEVELABILITY:"))
			{
				Logging.errorPrint("Ignoring second LEVELABILITY tag \""
								+ colString +  "\" in LevelAbilityToken.parse");
			}
			else if (colString.startsWith("ABILITY:"))
			{
				StringTokenizer pipeTok = new StringTokenizer(colString.substring(8), "|");
				String ability = pipeTok.nextToken();
				ArrayList choices = new ArrayList();
				while (pipeTok.hasMoreTokens())
				{
					choices.add(pipeTok.nextToken());
				}
				if (choices.size() < 1)
				{
					throw new PersistenceLayerException(
						"Missing choice in KitLevelAbility info \"" + colString + "\"");
				}
				kLA.addAbility(ability, choices);
			}
			else
			{
				if (parseCommonTags(kLA, colString) == false)
				{
					throw new PersistenceLayerException(
						"Unknown KitLevelAbility info \"" + colString + "\"");
				}
			}
		}
		aKit.setDoLevelAbilities(false);
		aKit.addObject(kLA);
		return true;
	}
}
