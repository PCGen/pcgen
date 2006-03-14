/*
 * BonusListToken.java
 * Copyright 2003 (C) Devon Jones <soulcatcher@evilsoft.org>
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.     See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 *
 * Created on December 15, 2003, 12:21 PM
 *
 * Current Ver: $Revision$
 * Last Editor: $Author$
 * Last Edited: $Date$
 *
 */
package plugin.exporttokens;

import pcgen.core.PlayerCharacter;
import pcgen.io.ExportHandler;
import pcgen.io.exporttoken.Token;

import java.util.Iterator;
import java.util.StringTokenizer;

/**
 * Deals with BONUSLIST token
 */
public class BonusListToken extends Token
{
	/** Token name */
	public static final String TOKENNAME = "BONUSLIST";

	/**
	 * @see pcgen.io.exporttoken.Token#getTokenName()
	 */
	public String getTokenName()
	{
		return TOKENNAME;
	}

	/**
	 * @see pcgen.io.exporttoken.Token#getToken(java.lang.String, pcgen.core.PlayerCharacter, pcgen.io.ExportHandler)
	 */
	public String getToken(String tokenSource, PlayerCharacter pc, ExportHandler eh)
	{
		return getBonusListToken(tokenSource, pc);
	}

	/**
	 * Get Bonus List Token
	 * @param tokenSource
	 * @param pc
	 * @return String of Bonus List
	 */
	public static String getBonusListToken(String tokenSource, PlayerCharacter pc)
	{
		StringTokenizer bTok = new StringTokenizer(tokenSource.substring(10), ".", false);
		String bonusString = "";
		String substring = "";
		String typeSeparator = " ";
		String delim = ", ";
		String retString = "";

		if (bTok.hasMoreTokens())
		{
			bonusString = bTok.nextToken();
		}

		if (bTok.hasMoreTokens())
		{
			substring = bTok.nextToken();
		}

		if (bTok.hasMoreTokens())
		{
			typeSeparator = bTok.nextToken();
		}

		if (bTok.hasMoreTokens())
		{
			delim = bTok.nextToken();
		}

		int typeLen = bonusString.length() + substring.length() + 2;

		if ((substring.length() > 0) && (bonusString.length() > 0))
		{
			int total = (int) pc.getTotalBonusTo(bonusString, substring);

			if ("TOTAL".equals(typeSeparator))
			{
				retString += total;

				return "";
			}

			boolean needDelim = false;
			String prefix = bonusString + "." + substring + ".";

			for (Iterator bi = pc.getActiveBonusMap().keySet().iterator(); bi.hasNext();)
			{
				String aKey = bi.next().toString();

				if (aKey.startsWith(prefix))
				{
					if (needDelim)
					{
						retString += delim;
					}

					if (aKey.length() > typeLen)
					{
						retString += aKey.substring(typeLen);
					}
					else
					{
						retString += "None";
					}

					retString += typeSeparator;
					retString += pc.getActiveBonusMap().get(aKey);
					needDelim = true;
				}
			}
		}

		return retString;
	}
}
