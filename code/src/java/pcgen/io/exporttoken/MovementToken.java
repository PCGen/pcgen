/*
 * MovementToken.java
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
 *
 *
 */
package pcgen.io.exporttoken;

import java.util.StringTokenizer;

import pcgen.base.util.NamedValue;
import pcgen.core.Globals;
import pcgen.core.display.CharacterDisplay;
import pcgen.io.ExportHandler;

//MOVEMENT
//MOVEMENT.movetype
public class MovementToken extends AbstractExportToken
{
	/**
	 * @see pcgen.io.exporttoken.Token#getTokenName()
	 */
	@Override
	public String getTokenName()
	{
		return "MOVEMENT";
	}

	//TODO: Move the |MOVEMENT| results into MoveToken, and then Eliminate MovementToken
	//      Also add .moveType to movement as a switchout replacement for .x
	/**
	 * @see pcgen.io.exporttoken.Token#getToken(java.lang.String, pcgen.core.PlayerCharacter, pcgen.io.ExportHandler)
	 */
	@Override
	public String getToken(String tokenSource, CharacterDisplay display,
		ExportHandler eh)
	{
		String retString = "";
		StringTokenizer aTok = new StringTokenizer(tokenSource, ".");
		aTok.nextToken();

		if (aTok.hasMoreTokens())
		{
			String moveType = aTok.nextToken();
			retString = getMoveTypeToken(display, moveType);
		}
		else
		{
			retString = getMovementToken(display);
		}

		return retString;
	}

	public static String getMoveTypeToken(CharacterDisplay display, String moveType)
	{
		String retString = "";

		if (display.hasMovement(moveType))
		{
			retString = getRateToken(display.getMovementOfType(moveType));
		}

		return retString;
	}

	public static String getMovementToken(CharacterDisplay display)
	{
		StringBuilder retString = new StringBuilder();
		boolean firstLine = true;

		for (NamedValue move : display.getMovementValues())
		{
			if (!firstLine)
			{
				retString.append(", ");
			}
			firstLine = false;
			retString.append(move.getName()).append(" ");
			retString.append(getRateToken(move.getWeight()));
		}

		return retString.toString();
	}

	public static String getRateToken(double movement)
	{
		return Globals.getGameModeUnitSet().displayDistanceInUnitSet(
			movement)
			+ Globals.getGameModeUnitSet().getDistanceUnit();
	}
}
