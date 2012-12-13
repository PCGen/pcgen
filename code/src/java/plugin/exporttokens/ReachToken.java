/*
 * ReachToken.java
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

import java.text.DecimalFormat;

import pcgen.core.Globals;
import pcgen.core.SettingsHandler;
import pcgen.core.display.CharacterDisplay;
import pcgen.io.ExportHandler;
import pcgen.io.exporttoken.AbstractExportToken;

//REACH
public class ReachToken extends AbstractExportToken
{
	/**
	 * @see pcgen.io.exporttoken.Token#getTokenName()
	 */
	@Override
	public String getTokenName()
	{
		return "REACH";
	}

	/**
	 * @see pcgen.io.exporttoken.Token#getToken(java.lang.String, pcgen.core.PlayerCharacter, pcgen.io.ExportHandler)
	 */
	@Override
	public String getToken(String tokenSource, CharacterDisplay display,
		ExportHandler eh)
	{
		String retString = "";

		if ("REACH".equals(tokenSource))
		{
			retString = getToken(display);
		}
		else if ("REACH.VAL".equals(tokenSource))
		{
			return Integer.toString(getReachToken(display));
		}
		else if ("REACH.SQUARES".equals(tokenSource))
		{
			retString = getSquaresToken(display);
		}

		return retString;
	}

	public static int getReachToken(CharacterDisplay display)
	{
		return display.getReach();
	}

	public static String getToken(CharacterDisplay display)
	{
		return Globals.getGameModeUnitSet().displayDistanceInUnitSet(
			getReachToken(display))
			+ Globals.getGameModeUnitSet().getDistanceUnit();
	}

	public static String getSquaresToken(CharacterDisplay display)
	{
		return new DecimalFormat("#.#").format(getReachToken(display)
			/ SettingsHandler.getGame().getSquareSize());
	}
}
