/*
 * DefenseToken.java
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

import pcgen.core.PlayerCharacter;
import pcgen.core.display.CharacterDisplay;
import pcgen.io.ExportHandler;
import pcgen.io.exporttoken.Token;

/**
 * Deal with tokens:
 *
 * DEFENSE.TOTAL
 * DEFENSE.FLATFOOTED
 * DEFENSE.TOUCH
 * DEFENSE.BASE
 * DEFENSE.ABILITY
 * DEFENSE.CLASS
 * DEFENSE.DODGE
 * DEFENSE.EQUIPMENT
 * DEFENSE.MISC
 * DEFENSE.NATURAL
 * DEFENSE.SIZE
 */
public class DefenseToken extends Token
{
	/** Token Name */
	public static final String TOKENNAME = "DEFENSE";

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
			String defenseType = aTok.nextToken();

			CharacterDisplay display = pc.getDisplay();
			if (defenseType.equals("TOTAL"))
			{
				retString = Integer.toString(getTotalToken(display));
			}
			else if (defenseType.equals("FLATFOOTED"))
			{
				retString = Integer.toString(display.flatfootedAC());
			}
			else if (defenseType.equals("TOUCH"))
			{
				retString = Integer.toString(display.touchAC());
			}
			else if (defenseType.equals("BASE"))
			{
				retString = Integer.toString(display.baseAC());
			}
			else if (defenseType.equals("ABILITY"))
			{
				retString = Integer.toString(display.abilityAC());
			}
			else if (defenseType.equals("CLASS"))
			{
				retString = Integer.toString(display.classAC());
			}
			else if (defenseType.equals("DODGE"))
			{
				retString = Integer.toString(display.dodgeAC());
			}
			else if (defenseType.equals("EQUIPMENT"))
			{
				retString = Integer.toString(display.equipmentAC());
			}
			else if (defenseType.equals("MISC"))
			{
				retString = Integer.toString(display.miscAC());
			}
			else if (defenseType.equals("NATURAL"))
			{
				retString = Integer.toString(display.naturalAC());
			}
			else if (defenseType.equals("SIZE"))
			{
				retString = Integer.toString(display.sizeAC());
			}
		}

		return retString;
	}

	/**
	 * Get total sub token
	 * @param pc
	 * @return total sub token
	 */
	public static int getTotalToken(CharacterDisplay display)
	{
		return display.getACTotal();
	}
}
