/*
 * AlignmentToken.java
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
import pcgen.core.PCAlignment;
import pcgen.core.PlayerCharacter;
import pcgen.io.ExportHandler;

/**
 * Class deals with ALIGNMENT and ALIGNMENT.SHORT Token
 */
public class AlignmentToken extends Token
{
	/** Token Name */
	public static final String TOKENNAME = "ALIGNMENT";

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
		
		if (!pc.getSuppressBioField(BiographyField.ALIGNMENT))
		{
			if ("ALIGNMENT".equals(tokenSource))
			{
				retString = getAlignmentToken(pc);
			}
			else if ("ALIGNMENT.SHORT".equals(tokenSource))
			{
				retString = getShortToken(pc);
			}
		}
		
		return retString;
	}

	/**
	 * Get the Alignment Token
	 * @param pc
	 * @return Alignment Token
	 */
	public static String getAlignmentToken(PlayerCharacter pc)
	{
		if (Globals.getGameModeAlignmentText().length() == 0)
		{
			return "";
		}
		final PCAlignment alignment = pc.getPCAlignment();
		return alignment==null?"None":alignment.getDisplayName();
	}

	/**
	 * Get Alignment Short Token
	 * @param pc
	 * @return Alignment Short Token
	 */
	public static String getShortToken(PlayerCharacter pc)
	{
		if (Globals.getGameModeAlignmentText().length() == 0)
		{
			return "";
		}
		
		final PCAlignment alignment = pc.getPCAlignment();
		return alignment==null?"None":alignment.getAbb();
	}
}
