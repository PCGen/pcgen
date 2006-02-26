/*
 * BonusSpellLevelLoader.java
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
 * Created on August 12, 2002, 10:29 PM
 *
 * $Id: BonusSpellLoader.java,v 1.23 2006/02/16 10:32:44 karianna Exp $
 */
package pcgen.persistence.lst;

import pcgen.core.Globals;
import pcgen.persistence.SystemLoader;
import pcgen.util.Logging;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

/**
 *
 * @author  Bryan McRoberts <merton_monk@yahoo.com>
 * @version $Revision: 1.23 $
 */
public final class BonusSpellLoader extends LstLineFileLoader
{
	/** Constant representing the base stat score */
	public static final String BASE_STAT_SCORE = "baseStatScore";
	/** Constant representing the stat range */
	public static final String STAT_RANGE = "statRange";
	/** Constant representing the level */
	public static final String LEVEL = "level";
	
	/** Creates a new instance of PCStatLoader */
	public BonusSpellLoader()
	{
	    // Empty Constructor
	}

	/**
	 * @see pcgen.persistence.lst.LstLineFileLoader#parseLine(java.lang.String, java.net.URL)
	 */
	public void parseLine(String lstLine, URL sourceURL)
	{
		final StringTokenizer colToken = new StringTokenizer(lstLine, SystemLoader.TAB_DELIM, false);
		Map bonus = new HashMap();
		bonus.put(BASE_STAT_SCORE, "0");
		bonus.put(STAT_RANGE, "0");
		bonus.put(LEVEL, "0");

		Map tokenMap = TokenStore.inst().getTokenMap(BonusSpellLstToken.class);
		while (colToken.hasMoreTokens())
		{
			final String colString = colToken.nextToken().trim();
			final int idxColon = colString.indexOf(':');
			String key = "";
			try
			{
				key = colString.substring(0, idxColon);
			}
			catch(Exception e) {
				// TODO Handle Exception
			}
			BonusSpellLstToken token = (BonusSpellLstToken) tokenMap.get(key);

			if (token != null)
			{
				final String value = colString.substring(idxColon + 1).trim();
				LstUtils.deprecationCheck(token, "Bonus Spell", sourceURL.toString(), value);
				if (!token.parse(bonus, value))
				{
					Logging.errorPrint("Error parsing bonus spell :" + sourceURL.toString() + ':' + colString + "\"");
				}
			}
			else
			{
				Logging.errorPrint("Illegal bonus spell info '" + lstLine + "' in " + sourceURL.toString());
			}
		}

		Globals.getBonusSpellMap().put(bonus.get(LEVEL), bonus.get(BASE_STAT_SCORE) + "|" + bonus.get(STAT_RANGE));
	}
}
