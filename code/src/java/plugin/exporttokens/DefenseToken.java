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

import pcgen.core.PlayerCharacter;
import pcgen.io.ExportHandler;
import pcgen.io.exporttoken.Token;

import java.util.StringTokenizer;

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
	public String getTokenName()
	{
		return TOKENNAME;
	}

	/**
	 * @see pcgen.io.exporttoken.Token#getToken(java.lang.String, pcgen.core.PlayerCharacter, pcgen.io.ExportHandler)
	 */
	public String getToken(String tokenSource, PlayerCharacter pc,
		ExportHandler eh)
	{
		String retString = "";
		StringTokenizer aTok = new StringTokenizer(tokenSource, ".");
		aTok.nextToken();

		if (aTok.hasMoreTokens())
		{
			String defenseType = aTok.nextToken();

			if (defenseType.equals("TOTAL"))
			{
				retString = Integer.toString(getTotalToken(pc));
			}
			else if (defenseType.equals("FLATFOOTED"))
			{
				retString = Integer.toString(getFlatFootedToken(pc));
			}
			else if (defenseType.equals("TOUCH"))
			{
				retString = Integer.toString(getTouchToken(pc));
			}
			else if (defenseType.equals("BASE"))
			{
				retString = Integer.toString(getBaseToken(pc));
			}
			else if (defenseType.equals("ABILITY"))
			{
				retString = Integer.toString(getAbilityToken(pc));
			}
			else if (defenseType.equals("CLASS"))
			{
				retString = Integer.toString(getClassToken(pc));
			}
			else if (defenseType.equals("DODGE"))
			{
				retString = Integer.toString(getDodgeToken(pc));
			}
			else if (defenseType.equals("EQUIPMENT"))
			{
				retString = Integer.toString(getEquipmentToken(pc));
			}
			else if (defenseType.equals("MISC"))
			{
				retString = Integer.toString(getMiscToken(pc));
			}
			else if (defenseType.equals("NATURAL"))
			{
				retString = Integer.toString(getNaturalToken(pc));
			}
			else if (defenseType.equals("SIZE"))
			{
				retString = Integer.toString(getSizeToken(pc));
			}
		}

		return retString;
	}

	/**
	 * Get Ability sub token
	 * @param pc
	 * @return Ability sub token
	 */
	public static int getAbilityToken(PlayerCharacter pc)
	{
		return pc.abilityAC();
	}

	/**
	 * Get base sub token
	 * @param pc
	 * @return base sub token
	 */
	public static int getBaseToken(PlayerCharacter pc)
	{
		return pc.baseAC();
	}

	/**
	 * Get class sub token
	 * @param pc
	 * @return class sub token
	 */
	public static int getClassToken(PlayerCharacter pc)
	{
		return pc.classAC();
	}

	/**
	 * Get dodge sub token
	 * @param pc
	 * @return dodge sub toke
	 */
	public static int getDodgeToken(PlayerCharacter pc)
	{
		return pc.dodgeAC();
	}

	/**
	 * Get Equipment sub token
	 * @param pc
	 * @return Equipment sub token
	 */
	public static int getEquipmentToken(PlayerCharacter pc)
	{
		return pc.equipmentAC();
	}

	/**
	 * Get flatfooted sub token
	 * @param pc
	 * @return flatfooted sub token
	 */
	public static int getFlatFootedToken(PlayerCharacter pc)
	{
		return pc.flatfootedAC();
	}

	/**
	 * Get misc sub token
	 * @param pc
	 * @return misc sub token
	 */
	public static int getMiscToken(PlayerCharacter pc)
	{
		return pc.miscAC();
	}

	/**
	 * Get natural sub token
	 * @param pc
	 * @return natural sub token
	 */
	public static int getNaturalToken(PlayerCharacter pc)
	{
		return pc.naturalAC();
	}

	/**
	 * Get size sub token
	 * @param pc
	 * @return size sub token
	 */
	public static int getSizeToken(PlayerCharacter pc)
	{
		return pc.sizeAC();
	}

	/**
	 * Get total sub token
	 * @param pc
	 * @return total sub token
	 */
	public static int getTotalToken(PlayerCharacter pc)
	{
		return pc.getACTotal();
	}

	/**
	 * Get touch sub token
	 * @param pc
	 * @return touch sub toke
	 */
	public static int getTouchToken(PlayerCharacter pc)
	{
		return pc.touchAC();
	}
}
