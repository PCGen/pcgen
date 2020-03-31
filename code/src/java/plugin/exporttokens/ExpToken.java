/*
 * ExpToken.java
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

import java.util.StringTokenizer;

import pcgen.core.PlayerCharacter;
import pcgen.core.display.CharacterDisplay;
import pcgen.io.ExportHandler;
import pcgen.io.exporttoken.Token;

/** 
 * Deal with Tokens:
 * 
 * EXP.CURRENT
 * EXP.NEXT
 * EXP.FACTOR
 * EXP.PENALTY
 */
public class ExpToken extends Token
{
	@Override
	public String getTokenName()
	{
		return "EXP";
	}

	@Override
	public String getToken(String tokenSource, PlayerCharacter pc, ExportHandler eh)
	{
		String retString = "";
		StringTokenizer aTok = new StringTokenizer(tokenSource, ".");
		aTok.nextToken();

		CharacterDisplay display = pc.getDisplay();
		if (aTok.hasMoreTokens())
		{
			String token = aTok.nextToken();

			if ("CURRENT".equals(token))
			{
				retString = Integer.toString(pc.getXP());
			}
			else if ("NEXT".equals(token))
			{
				retString = Integer.toString(display.minXPForNextECL());
			}
			else if ("FACTOR".equals(token))
			{
				retString = getFactorToken(display);
			}
			else if ("PENALTY".equals(token))
			{
				retString = getPenaltyToken(display);
			}
		}

		return retString;
	}

	/**
	 * Get Factor Sub Token
	 * @param display
	 * @return Factor Sub Token
	 */
	public static String getFactorToken(CharacterDisplay display)
	{

        return String.valueOf((int) (display.multiclassXPMultiplier() * 100.0))
                + '%';
	}

	/**
	 * Get Penalty Sub Token
	 * @param display
	 * @return Penalty Sub Token
	 */
	public static String getPenaltyToken(CharacterDisplay display)
	{

        return String.valueOf(100 - (int) (display.multiclassXPMultiplier() * 100.0))
                + '%';
	}
}
