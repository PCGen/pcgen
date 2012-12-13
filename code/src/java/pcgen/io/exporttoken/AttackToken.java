/*
 * AttackToken.java
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

import java.util.StringTokenizer;

import pcgen.core.PlayerCharacter;
import pcgen.core.display.CharacterDisplay;
import pcgen.io.ExportHandler;
import pcgen.util.Delta;
import pcgen.util.enumeration.AttackType;

/**
 * Class Deals with:
 *
 * ATTACK.GRAPPLE.BASE
 * ATTACK.GRAPPLE.EPIC
 * ATTACK.GRAPPLE.MISC
 * ATTACK.GRAPPLE.SIZE
 * ATTACK.GRAPPLE.STAT
 * ATTACK.GRAPPLE.TOTAL
 * ATTACK.MELEE.BASE
 * ATTACK.MELEE.EPIC
 * ATTACK.MELEE.MISC
 * ATTACK.MELEE.SIZE
 * ATTACK.MELEE.STAT
 * ATTACK.MELEE.TOTAL
 * ATTACK.RANGED.BASE
 * ATTACK.RANGED.EPIC
 * ATTACK.RANGED.MISC
 * ATTACK.RANGED.SIZE
 * ATTACK.RANGED.STAT
 * ATTACK.RANGED.TOTAL
 * ATTACK.UNARMED.BASE
 * ATTACK.UNARMED.EPIC
 * ATTACK.UNARMED.SIZE
 * ATTACK.UNARMED.TOTAL
 */
public class AttackToken extends Token
{
	/** Token name */
	public static final String TOKENNAME = "ATTACK";

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
			String attackType = aTok.nextToken();
			String modifier = aTok.hasMoreTokens() ? aTok.nextToken() : "";
			String format = aTok.hasMoreTokens() ? aTok.nextToken() : "";

			retString = getParsedToken(pc, attackType, modifier);
			
			// SHORT means we only return the first attack bonus
			if ("SHORT".equalsIgnoreCase(format))
			{
				int sepPos = retString.indexOf("/");
				if (sepPos >= 0)
				{
					retString = retString.substring(0, sepPos);
				}
			}
		}

		return retString;
	}

	/**
	 * Translate an already parsed attack token for the supplied character.
	 * This will return the attack token value requested for the supplied
	 * character. Note the return value is always a formatted string such as +8
	 * or +8/+3
	 *
	 * @param pc The character to retrieve the attack value for.
	 * @param attackType The type of attack to the returned
	 *                    - GRAPPLE, MELLEE, RANGED or UNARMED
	 * @param modifier The modified to the attack - BASE, EPIC, MISC,
	 *                  SIZE, STAT, TOTAL or an empty string
	 * @return The token value.
	 */
	public static String getParsedToken(PlayerCharacter pc, String attackType,
		String modifier)
	{
		if (modifier.equals("TOTAL"))
		{
			if (attackType.equals("RANGED"))
			{
				int total = getTotalToken(pc, attackType);
				return pc.getAttackString(AttackType.RANGED, total);
			}
			else if (attackType.equals("UNARMED"))
			{
				int total = getTotalToken(pc, "MELEE");
				// TODO: Is this correct for 3.0 also?
				return pc.getAttackString(AttackType.MELEE, total);
				//return pc.getAttackString(Constants.ATTACKSTRING_UNARMED, total);
			}
			else
			{
				int total = getTotalToken(pc, attackType);
				return pc.getAttackString(AttackType.MELEE, total);
			}
		}
		return getSubToken(pc, attackType, modifier);
	}

	private static String getSubToken(PlayerCharacter pc, String attackType,
		String modifier)
	{
		if (modifier.equals("BASE"))
		{
			return Delta.toString(getBaseToken(pc));
		}
		else if (modifier.equals("EPIC"))
		{
			return Delta.toString(getEpicToken(pc));
		}
		else if (modifier.equals("MISC"))
		{
			return Delta.toString(getMiscToken(pc, attackType));
		}
		else if (modifier.equals("SIZE"))
		{
			return Delta.toString(getSizeToken(pc, attackType));
		}
		else if (modifier.equals("STAT"))
		{
			return Delta.toString(getStatToken(pc.getDisplay(), attackType));
		}
		else if (modifier.equals("TOTAL"))
		{
			// TOTAL is handled in getParsedToken()
			//int total = getTotalToken(pc, "MELEE");
			//return pc.getAttackString(Constants.ATTACKSTRING_MELEE, total);
		}
		else
		{
			return pc.getAttackString(AttackType.valueOf(attackType));
		}
		return "";
	}

	/**
	 * Get the base ATTACK token
	 * @param pc
	 * @return base ATTACK token
	 */
	public static int getBaseToken(PlayerCharacter pc)
	{
		return pc.baseAttackBonus();
	}

	/**
	 * Get the epic ATTACK token
	 * @param pc
	 * @return epic ATTACK token
	 */
	public static int getEpicToken(PlayerCharacter pc)
	{
		return (int) pc.getBonusDueToType("COMBAT", "TOHIT", "EPIC");
	}

	/**
	 * Get the misc ATTACK token
	 * @param pc
	 * @param aType
	 * @return misc ATTACK token
	 */
	public static int getMiscToken(PlayerCharacter pc, String aType)
	{
		int tohitBonus =
				((int) pc.getTotalBonusTo("TOHIT", "TOHIT") + (int) pc
					.getTotalBonusTo("TOHIT", "TYPE." + aType))
					- (int) pc.getDisplay().getStatBonusTo("TOHIT", "TYPE." + aType)
					- (int) pc.getSizeAdjustmentBonusTo("TOHIT", "TOHIT");
		int miscBonus =
				((int) pc.getTotalBonusTo("COMBAT", "TOHIT") + (int) pc
					.getTotalBonusTo("COMBAT", "TOHIT." + aType))
					- (int) pc.getDisplay().getStatBonusTo("COMBAT", "TOHIT." + aType)
					- (int) pc.getSizeAdjustmentBonusTo("COMBAT", "TOHIT")
					- (int) pc.getSizeAdjustmentBonusTo("COMBAT", "TOHIT."
						+ aType)
					- (int) pc.getBonusDueToType("COMBAT", "TOHIT", "EPIC");
		return miscBonus + tohitBonus;
	}

	/**
	 * Get the size ATTACK token
	 * @param pc
	 * @param aType
	 * @return size ATTACK token
	 */
	public static int getSizeToken(PlayerCharacter pc, String aType)
	{
		int tohitBonus =
				(int) pc.getSizeAdjustmentBonusTo("TOHIT", "TOHIT")
					+ (int) pc.getSizeAdjustmentBonusTo("TOHIT", "TYPE."
						+ aType);
		int sizeBonus =
				(int) pc.getSizeAdjustmentBonusTo("COMBAT", "TOHIT")
					+ (int) pc.getSizeAdjustmentBonusTo("COMBAT", "TOHIT."
						+ aType);

		return sizeBonus + tohitBonus;
	}

	/**
	 * get stat ATTACK token
	 * @param pc
	 * @param aType
	 * @return stat ATTACK token
	 */
	public static int getStatToken(CharacterDisplay display, String aType)
	{
		final int tohitBonus =
				(int) display.getStatBonusTo("TOHIT", "TYPE." + aType);
		final int statBonus =
				(int) display.getStatBonusTo("COMBAT", "TOHIT." + aType);

		return statBonus + tohitBonus;
	}

	/**
	 * Get total ATTACK token
	 * @param pc
	 * @param aType
	 * @return total ATTACK token
	 */
	public static int getTotalToken(PlayerCharacter pc, String aType)
	{
		final int tohitBonus =
				(int) pc.getTotalBonusTo("TOHIT", "TOHIT")
					+ (int) pc.getTotalBonusTo("TOHIT", "TYPE." + aType);
		final int totalBonus =
				(int) pc.getTotalBonusTo("COMBAT", "TOHIT")
					+ (int) pc.getTotalBonusTo("COMBAT", "TOHIT." + aType);
		return tohitBonus + totalBonus;
	}
}
