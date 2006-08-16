/*
 * CompanionModLoader.java
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
 *
 * @author Jayme Cox <jaymecox@users.sourceforge.net>
 * @Created on July 10th, 2002, 3:55 PM
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

import pcgen.core.character.CompanionMod;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.SystemLoader;
import pcgen.util.Logging;

/**
 * Loads the level based Mount and Familiar benefits
 *
 * @author Jayme Cox <jaymecox@users.sourceforge.net>
 * @version $Revision$
 **/
public class CompanionModLoader
{
	/** Creates a new instance of CompanionModLoader */
	private CompanionModLoader()
	{
		// Empty Constructor
	}

	/**
	 * Parse the line
	 * @param cmpMod
	 * @param inputLine
	 * @param sourceURL
	 * @param lineNum
	 * @throws PersistenceLayerException
	 */
	public static void parseLine(CompanionMod cmpMod, String inputLine, 
									URL sourceURL, int lineNum) 
		throws PersistenceLayerException
	{
		if (cmpMod == null)
		{
			return;
		}

		final StringTokenizer colToken = new StringTokenizer(inputLine, SystemLoader.TAB_DELIM, false);
		String colString;

		if (!cmpMod.isNewItem())
		{
			// .MOD skips required fields
			// FOLLOWER:Classname=Level in this case
			colToken.nextToken();
		}

		Map<String, LstToken> tokenMap = TokenStore.inst().getTokenMap(CompanionModLstToken.class);
		while (colToken.hasMoreTokens())
		{
			colString = colToken.nextToken().trim();

			final int idxColon = colString.indexOf(':');
			String key = "";
			try
			{
				key = colString.substring(0, idxColon);
			}
			catch(Exception e) {
				throw new PersistenceLayerException();
			}
			CompanionModLstToken token = (CompanionModLstToken) tokenMap.get(key);
			if (token != null)
			{
				final String value = colString.substring(idxColon + 1);
				LstUtils.deprecationCheck(token, cmpMod, value);
				if (!token.parse(cmpMod, value))
				{
					Logging.errorPrint("Error parsing ability " + cmpMod.getDisplayName() + ':' + sourceURL.toString() + ':' + colString + "\"");
				}
			}
			else if (PObjectLoader.parseTag(cmpMod, colString))
			{
				continue;
			}
			else
			{
				Logging.errorPrint(sourceURL.toString() + ":" + Integer.toString(lineNum) + " \"" + colString + "\"");
			}
		}
	}
}
