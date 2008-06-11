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
 * $Id$
 */
package pcgen.persistence.lst;

import pcgen.core.Globals;
import pcgen.persistence.SystemLoader;
import pcgen.rules.context.LoadContext;
import pcgen.util.Logging;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

/**
 *
 * @author  Bryan McRoberts <merton_monk@yahoo.com>
 * @version $Revision$
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
	 * @see pcgen.persistence.lst.LstLineFileLoader#parseLine(java.net.URL, java.lang.String)
	 */
	@Override
	public void parseLine(LoadContext context, String lstLine, URI sourceURI)
	{
		final StringTokenizer colToken =
				new StringTokenizer(lstLine, SystemLoader.TAB_DELIM, false);
		Map<String, String> bonus = new HashMap<String, String>();
		bonus.put(BASE_STAT_SCORE, "0");
		bonus.put(STAT_RANGE, "0");
		bonus.put(LEVEL, "0");

		Map<String, LstToken> tokenMap =
				TokenStore.inst().getTokenMap(BonusSpellLstToken.class);
		while (colToken.hasMoreTokens())
		{
			final String colString = colToken.nextToken().trim();
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
			BonusSpellLstToken token = (BonusSpellLstToken) tokenMap.get(key);

			if (token != null)
			{
				final String value = colString.substring(idxColon + 1).trim();
				LstUtils.deprecationCheck(token, "Bonus Spell", sourceURI, value);
				if (!token.parse(bonus, value))
				{
					Logging.errorPrint("Error parsing bonus spell :"
						+ sourceURI.toString() + ':' + colString + "\"");
				}
			}
			else
			{
				Logging.errorPrint("Illegal bonus spell info '" + lstLine
					+ "' in " + sourceURI.toString());
			}
		}

		/*
		 * CONSIDER This is VERY deceptive to use a GET to actually perform a
		 * SET. This should be refactored to allow a set of some sort and to
		 * have GETs actually performing only GET operations. - thpr 11/10/06
		 */
		Globals.getBonusSpellMap().put(bonus.get(LEVEL),
			bonus.get(BASE_STAT_SCORE) + "|" + bonus.get(STAT_RANGE));
	}
}
