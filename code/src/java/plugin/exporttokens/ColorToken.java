/*
 * ColorToken.java
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
 * Deal with tokens:
 * COLOR.EYE
 * COLOR.HAIR
 * COLOR.SKIN
 */
public class ColorToken extends Token
{
	/** Token name */
	public static final String TOKENNAME = "COLOR";

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
		String retString = "";

		if ("COLOR.EYE".equals(tokenSource))
		{
			retString = getEyeToken(pc);
		}
		else if ("COLOR.HAIR".equals(tokenSource))
		{
			retString = getHairToken(pc);
		}
		else if ("COLOR.SKIN".equals(tokenSource))
		{
			retString = getSkinToken(pc);
		}

		return retString;
	}

	/**
	 * Get the EYE token
	 * @param pc
	 * @return token
	 */
	public static String getEyeToken(PlayerCharacter pc)
	{
		return pc.getEyeColor();
	}

	/**
	 * Get the Hair token 
	 * @param pc
	 * @return token
	 */
	public static String getHairToken(PlayerCharacter pc)
	{
		return pc.getHairColor();
	}

	/**
	 * Get the skin token
	 * @param pc
	 * @return token
	 */
	public static String getSkinToken(PlayerCharacter pc)
	{
		return pc.getSkinColor();
	}
}
