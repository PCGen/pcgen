/*
 * GenderToken.java
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

/**
 * Deal with:
 * 
 * GENDER
 * GENDER.SHORT
 * GENDER.LONG
 */
public class GenderToken extends Token
{
	/** Token name */
	public static final String TOKENNAME = "GENDER";

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
	public String getToken(String tokenSource, PlayerCharacter pc,
		ExportHandler eh)
	{
		String retString = "";

		if ("GENDER".equals(tokenSource) || "GENDER.SHORT".equals(tokenSource))
		{
			retString = getShortToken(pc);
		}
		else if ("GENDER.LONG".equals(tokenSource))
		{
			retString = getLongToken(pc);
		}

		return retString;
	}

	/**
	 * Get the gender token
	 * @param pc
	 * @return gender token
	 */
	public static String getGenderToken(PlayerCharacter pc)
	{
		return pc.getGender();
	}

	/**
	 * Get long sub token
	 * @param pc
	 * @return long sub token
	 */
	public static String getLongToken(PlayerCharacter pc)
	{
		String retString = "";

		if ("M".equals(pc.getGender()))
		{
			retString = "Male";
		}
		else if ("F".equals(pc.getGender()))
		{
			retString = "Female";
		}
		else
		{
			retString = pc.getGender();
		}

		return retString;
	}

	/**
	 * Get the short sub token
	 * @param pc
	 * @return short sub token
	 */
	public static String getShortToken(PlayerCharacter pc)
	{
		return pc.getGender();
	}
}
