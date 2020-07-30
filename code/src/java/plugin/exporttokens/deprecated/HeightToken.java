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
 */
package plugin.exporttokens.deprecated;

import pcgen.core.Globals;
import pcgen.core.display.CharacterDisplay;
import pcgen.io.ExportHandler;
import pcgen.io.exporttoken.AbstractExportToken;
import pcgen.output.channel.compat.HeightCompat;

/**
 * Deals with Tokens:
 * 
 * HEIGHT
 * HEIGHT.FOOTPART
 * HEIGHT.INCHPART
 */
public class HeightToken extends AbstractExportToken
{
	@Override
	public String getTokenName()
	{
		return "HEIGHT";
	}

	@Override
	public String getToken(String tokenSource, CharacterDisplay display, ExportHandler eh)
	{
		String retString = "";

		if ("HEIGHT".equals(tokenSource))
		{
			retString = getHeightString(display);
		}
		else if ("HEIGHT.FOOTPART".equals(tokenSource))
		{
			retString = getHeightFootPart(display);
		}
		else if ("HEIGHT.INCHPART".equals(tokenSource))
		{
			retString = getHeightInchPart(display);
		}

		return retString;
	}

	private String getHeightInchPart(CharacterDisplay display)
	{
		Integer height = HeightCompat.getCurrentHeight(display.getCharID());
		return Integer.toString(height % 12);
	}

	private String getHeightFootPart(CharacterDisplay display)
	{
		Integer height = HeightCompat.getCurrentHeight(display.getCharID());
		return Integer.toString(height / 12);
	}

	private String getHeightString(CharacterDisplay display)
	{
		Integer height = HeightCompat.getCurrentHeight(display.getCharID());
		String retString;

		if ("ftin".equals(Globals.getGameModeUnitSet().getHeightUnit()))
		{
			retString = Integer.toString(height / 12) + "' " + Integer.toString(height % 12)
				+ '"';
		}
		else
		{
			retString = Globals.getGameModeUnitSet().displayHeightInUnitSet(height) + ' '
				+ Globals.getGameModeUnitSet().getHeightUnit();
		}

		return retString;
	}
}
