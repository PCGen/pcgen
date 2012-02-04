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

import pcgen.cdom.enumeration.BiographyField;
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
	 * @param pc the character being exported
	 * @return token
	 */
	public static String getEyeToken(PlayerCharacter pc)
	{
		return pc.getEyeColor();
	}

	/**
	 * Get the Hair token 
	 * @param pc the character being exported
	 * @return skin color
	 */
	public static String getHairToken(PlayerCharacter pc)
	{
		if (pc.getSuppressBioField(BiographyField.HAIR_COLOR))
		{
			return "";
		}
		return pc.getHairColor();
	}

	/**
	 * Get the skin token
	 * @param pc the character being exported
	 * @return skin color
	 */
	public static String getSkinToken(PlayerCharacter pc)
	{
		if (pc.getSuppressBioField(BiographyField.SKIN_TONE))
		{
			return "";
		}
		return pc.getSkinColor();
	}
}
