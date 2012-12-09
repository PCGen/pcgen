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
package pcgen.io.exporttoken;

import pcgen.core.Globals;
import pcgen.core.PlayerCharacter;
import pcgen.core.SettingsHandler;
import pcgen.io.ExportHandler;

import java.text.DecimalFormat;

//REACH
public class ReachToken extends Token
{
	public static final String TOKENNAME = "REACH";

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

		if ("REACH".equals(tokenSource))
		{
			retString = getToken(pc);
		}
		else if ("REACH.VAL".equals(tokenSource))
		{
			return Integer.toString(getReachToken(pc));
		}
		else if ("REACH.SQUARES".equals(tokenSource))
		{
			retString = getSquaresToken(pc);
		}

		return retString;
	}

	public static int getReachToken(PlayerCharacter pc)
	{
		if (pc != null)
		{
			return pc.getReach();
		}

		return 0;
	}

	public static String getToken(PlayerCharacter pc)
	{
		return Globals.getGameModeUnitSet().displayDistanceInUnitSet(
			getReachToken(pc))
			+ Globals.getGameModeUnitSet().getDistanceUnit();
	}

	public static String getSquaresToken(PlayerCharacter pc)
	{
		return new DecimalFormat("#.#").format(getReachToken(pc)
			/ SettingsHandler.getGame().getSquareSize());
	}
}
