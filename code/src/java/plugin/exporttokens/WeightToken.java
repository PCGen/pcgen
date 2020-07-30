/*
 * WeightToken.java
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
package plugin.exporttokens;

import pcgen.core.Globals;
import pcgen.core.display.CharacterDisplay;
import pcgen.io.ExportHandler;
import pcgen.io.exporttoken.AbstractExportToken;

/**
 * {@code WeightToken}.
 * 
 * Formats:	WEIGHT
 * 			WEIGHT.NOUNIT
 * 			WEIGHT.x
 * 
 */
public class WeightToken extends AbstractExportToken
{
	/**
	 * Gets the token name
	 * 
	 * @return The token name.
	 */
	@Override
	public String getTokenName()
	{
		return "WEIGHT";
	}

	/**
	 * Get the value of the token.
	 *
	 * @param tokenSource The full source of the token
	 * @param display The character to retrieve the value for.
	 * @param eh The ExportHandler that is managing the export.
	 * @return The value of the token.
	 */
	@Override
	public String getToken(String tokenSource, CharacterDisplay display, ExportHandler eh)
	{
		String retString;
		if ("WEIGHT".equals(tokenSource))
		{
			retString = getWeightToken(display);
		}
		else if ("WEIGHT.NOUNIT".equals(tokenSource))
		{
			retString = getNoUnitWeight(display);
		}
		else
		{
			String type = tokenSource.substring(tokenSource.lastIndexOf('.') + 1);
			retString = Globals.getGameModeUnitSet().displayWeightInUnitSet(display.getLoadToken(type));
		}

		return retString;
	}

	private String getNoUnitWeight(CharacterDisplay display)
	{
		return Globals.getGameModeUnitSet().displayWeightInUnitSet(display.getWeight());
	}

	private String getWeightToken(CharacterDisplay display)
	{
		return Globals.getGameModeUnitSet().displayWeightInUnitSet(display.getWeight())
			+ Globals.getGameModeUnitSet().getWeightUnit();
	}
}
