/*
 * MoveToken.java
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

import java.util.StringTokenizer;

import pcgen.base.util.NamedValue;
import pcgen.core.PlayerCharacter;
import pcgen.core.SettingsHandler;
import pcgen.io.ExportHandler;
import pcgen.io.exporttoken.MovementToken;
import pcgen.io.exporttoken.Token;

//MOVE prints out all movename/move pairs
//MOVE.x prints out movename/move pair
//MOVE.x.NAME and
//MOVE.x.RATE produce the appropriate parts.
public class MoveToken extends Token
{
	public static final String TOKENNAME = "MOVE";

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
		StringTokenizer aTok = new StringTokenizer(tokenSource, ".");
		aTok.nextToken();

		if (aTok.hasMoreTokens())
		{
			int moveIndex;
			moveIndex = Integer.parseInt(aTok.nextToken());

			if (aTok.hasMoreTokens())
			{
				String subToken = aTok.nextToken();

				if ("NAME".equals(subToken))
				{
					retString = pc.getMovementValues().get(moveIndex).getName();
				}
				else if ("RATE".equals(subToken))
				{
					retString = MovementToken.getRateToken(pc.getMovementValues().get(moveIndex).getWeight());
				}
				else if ("SQUARES".equals(subToken))
				{
					retString = getSquaresToken(pc, moveIndex);
				}
				else
				{
					retString = MovementToken.getMovementToken(pc);
				}
			}
			else
			{
				retString = getMoveXToken(pc, moveIndex);
			}

			//TODO: merge all of MovementToken here, and eliminate MovementToken
		}

		return retString;
	}

	public static String getMoveXToken(PlayerCharacter pc, int moveIndex)
	{
		NamedValue move = pc.getMovementValues().get(moveIndex);
		return move.getName() + " "
				+ MovementToken.getRateToken(move.getWeight());
	}

	public static String getSquaresToken(PlayerCharacter pc, int moveIndex)
	{
		return Integer.toString((int) (pc.getMovementValues().get(moveIndex)
				.getWeight() / SettingsHandler.getGame().getSquareSize()));
	}
}
