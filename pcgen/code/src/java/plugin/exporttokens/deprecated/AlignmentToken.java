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
 */
package plugin.exporttokens.deprecated;

import pcgen.cdom.enumeration.BiographyField;
import pcgen.core.Globals;
import pcgen.core.PCAlignment;
import pcgen.core.display.CharacterDisplay;
import pcgen.io.ExportHandler;
import pcgen.io.exporttoken.AbstractExportToken;

/**
 * Class deals with ALIGNMENT and ALIGNMENT.SHORT Token
 */
public class AlignmentToken extends AbstractExportToken
{
	/**
	 * @see pcgen.io.exporttoken.Token#getTokenName()
	 */
	@Override
	public String getTokenName()
	{
		return "ALIGNMENT";
	}

	/**
	 * @see pcgen.io.exporttoken.Token#getToken(java.lang.String, pcgen.core.PlayerCharacter, pcgen.io.ExportHandler)
	 */
	@Override
	public String getToken(String tokenSource, CharacterDisplay display,
		ExportHandler eh)
	{
		String retString = "";
		
		if (!display.getSuppressBioField(BiographyField.ALIGNMENT))
		{
			if ("ALIGNMENT".equals(tokenSource))
			{
				retString = getAlignmentDisplay(display);
			}
			else if ("ALIGNMENT.SHORT".equals(tokenSource))
			{
				retString = getShortToken(display);
			}
		}
		
		return retString;
	}

	private String getAlignmentDisplay(CharacterDisplay display)
	{
		if (Globals.getGameModeAlignmentText().isEmpty())
		{
			return "";
		}
		final PCAlignment alignment = display.getPCAlignment();
		return alignment == null ? "None" : alignment.getDisplayName();
	}

	/**
	 * Get Alignment Short Token
	 * @param display
	 * @return Alignment Short Token
	 */
	public static String getShortToken(CharacterDisplay display)
	{
		if (Globals.getGameModeAlignmentText().isEmpty())
		{
			return "";
		}
		
		final PCAlignment alignment = display.getPCAlignment();
		return alignment==null?"None":alignment.getKeyName();
	}
}
