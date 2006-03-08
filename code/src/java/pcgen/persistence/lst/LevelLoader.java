/*
 * LevelLoader.java
 * Copyright 2002 (C) James Dempsey
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
 * Created on August 16, 2002, 10:00 PM
 *
 * Current Ver: $Revision: 1.21 $
 * Last Editor: $Author: binkley $
 * Last Edited: $Date: 2005/10/18 20:23:53 $
 *
 */
package pcgen.persistence.lst;

import java.util.Map;
import java.util.StringTokenizer;

import pcgen.core.LevelInfo;
import pcgen.persistence.SystemLoader;
import pcgen.util.Logging;

/**
 * <code>LevelLoader</code> loads up the level system file
 * by processing each line passed to it.
 *
 * @author James Dempsey <jdempsey@users.sourceforge.net>
 * @version $Revision: 1.21 $
 **/
final class LevelLoader
{
	/** Creates a new instance of LevelLoader */
	private LevelLoader()
	{
	    // Empty Constructor
	}

	/**
	 * Parse the line from the level.lst file, populating the
	 * levelInfo object with the info found.
	 *
	 * @param levelInfo  The LevelInfo object to be populated
	 * @param inputLine  The line to be parsed
	 * @param lineNum    The number of the line being parsed.
	 */
	public static void parseLine(LevelInfo levelInfo, String inputLine, int lineNum)
	{
		if (levelInfo == null)
		{
			return;
		}

		final StringTokenizer colToken = new StringTokenizer(inputLine, SystemLoader.TAB_DELIM);

		Map tokenMap = TokenStore.inst().getTokenMap(LevelLstToken.class);
		while (colToken.hasMoreTokens())
		{
			final String colString = colToken.nextToken().trim();

			final int idxColon = colString.indexOf(':');
			String key = "";
			try
			{
				key = colString.substring(0, idxColon);
			}
			catch(StringIndexOutOfBoundsException e) {
				// TODO Handle Exception
			}
			LevelLstToken token = (LevelLstToken) tokenMap.get(key);

			if (token != null)
			{
				final String value = colString.substring(idxColon + 1);
				LstUtils.deprecationCheck(token, levelInfo.getLevelString(), "level.lst", value);
				if (!token.parse(levelInfo, value))
				{
					Logging.errorPrint("Error parsing ability " + levelInfo.getLevelString() + ':' + "level.lst" + ':' + colString + "\"");
				}
			}
			else
			{
				Logging.errorPrint("LevelLoader got unexpected token of '" + colString + "' at line " + lineNum + ". Token ignored.");
			}
		}
	}
}