/*
 * HeightToken.java
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
package pcgen.io.exporttoken;

import pcgen.cdom.enumeration.BiographyField;
import pcgen.core.Globals;
import pcgen.core.PlayerCharacter;
import pcgen.io.ExportHandler;

/**
 * Deals with Tokens:
 * 
 * HEIGHT
 * HEIGHT.FOOTPART
 * HEIGHT.INCHPART
 */
public class HeightToken extends Token
{
	/** Token Name */
	public static final String TOKENNAME = "HEIGHT";

	/**
	 * @see pcgen.io.exporttoken.Token#getTokenName()
	 */
	@Override
	public String getTokenName()
	{
		return TOKENNAME;
	}

	/**
	 * @see pcgen.io.exporttoken.Token#getToken(java.lang.String, pcgen.core.PlayerCharacter, pcgen.io.ExportHandler)
	 */
	@Override
	public String getToken(String tokenSource, PlayerCharacter pc,
		ExportHandler eh)
	{
		String retString = "";

		if (!pc.getSuppressBioField(BiographyField.HEIGHT))
		{
			if ("HEIGHT".equals(tokenSource))
			{
				retString = getHeightToken(pc);
			}
			else if ("HEIGHT.FOOTPART".equals(tokenSource))
			{
				retString = getFootPartToken(pc);
			}
			else if ("HEIGHT.INCHPART".equals(tokenSource))
			{
				retString = getInchPartToken(pc);
			}
		}
		
		return retString;
	}

	/**
	 * Get the HEIGHT token
	 * @param pc
	 * @return the HEIGHT token
	 */
	public static String getHeightToken(PlayerCharacter pc)
	{
		String retString = "";

		if ("ftin".equals(Globals.getGameModeUnitSet().getHeightUnit()))
		{
			retString =
					getFootPartToken(pc) + "' " + getInchPartToken(pc) + "\"";
		}
		else
		{
			retString =
					Globals.getGameModeUnitSet().displayHeightInUnitSet(
						pc.getHeight())
						+ " " + Globals.getGameModeUnitSet().getHeightUnit();
		}

		return retString;
	}

	/**
	 * Get the HEIGHT.FOOTPART token
	 * @param pc
	 * @return the HEIGHT.FOOTPART token
	 */
	public static String getFootPartToken(PlayerCharacter pc)
	{
		return Integer.toString(pc.getHeight() / 12);
	}

	/**
	 * Get the HEIGHT.INCHPART token
	 * @param pc
	 * @return the HEIGHT.INCHPART token
	 */
	public static String getInchPartToken(PlayerCharacter pc)
	{
		return Integer.toString(pc.getHeight() % 12);
	}
}
