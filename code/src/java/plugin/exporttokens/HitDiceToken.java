/*
 * HitDiceToken.java
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

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Set;

import pcgen.core.PCClass;
import pcgen.core.PlayerCharacter;
import pcgen.core.display.CharacterDisplay;
import pcgen.io.ExportHandler;
import pcgen.io.exporttoken.Token;
import pcgen.util.Delta;

/**
 * Deals with the HITDICE Token
 */
public class HitDiceToken extends Token
{
	/** Token Name */
	public static final String TOKENNAME = "HITDICE";

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

		if ("HITDICE".equals(tokenSource) ||
			"HITDICE.LONG".equals(tokenSource))
		{
			retString = getHitDiceToken(pc);
		}
		else if ("HITDICE.MEDIUM".equals(tokenSource))
		{
			retString = getMediumToken(pc);
		}
		else if ("HITDICE.SHORT".equals(tokenSource))
		{
			retString = getShortToken(pc.getDisplay());
		}

		return retString;
	}

	/**
	 * Get the medium version of the HITDICE token
	 * @param pc
	 * @return the medium version of the HITDICE token
	 */
	public static String getHitDiceToken(PlayerCharacter pc)
	{
		StringBuilder ret = new StringBuilder();
		String del = "";

		CharacterDisplay display = pc.getDisplay();
		for (PCClass pcClass : display.getClassSet())
		{
			HashMap<Integer, Integer> hdMap =
					new LinkedHashMap<>();

			for (int i = 0; i < display.getLevel(pcClass); i++)
			{
				int hitDie = display.getLevelHitDie(pcClass, i + 1).getDie();
				if (hitDie != 0)
				{
					Integer num = hdMap.get(hitDie);
					if (num == null)
					{
						hdMap.put(hitDie, 1);
					}
					else
					{
						hdMap.put(hitDie, num.intValue() + 1);
					}
				}
			}

			Set<Integer> keys = hdMap.keySet();
			for (int key : keys)
			{
				Integer value = hdMap.get(key);
				ret.append(del);
				ret.append('(');
				ret.append(value).append('d').append(key);
				ret.append(')');
				del = "+";
			}
		}

		// Get CON bonus contribution to hitpoint total
		int temp = (int) display.getStatBonusTo("HP", "BONUS") * display.getTotalLevels();

		// Add in feat bonus
		temp += (int) pc.getTotalBonusTo("HP", "CURRENTMAX");

		if (temp != 0)
		{
			ret.append(Delta.toString(temp));
		}

		return ret.toString();
	}

	/**
	 * Get the HITDICE token
	 * @param pc
	 * @return the HITDICE token
	 */
	public static String getMediumToken(PlayerCharacter pc)
	{
		StringBuilder ret = new StringBuilder();
		String del = "";
		Integer total = 0;

		HashMap<Integer, Integer> hdMap =
				new LinkedHashMap<>();

		CharacterDisplay display = pc.getDisplay();
		for (PCClass pcClass : display.getClassSet())
		{
			for (int i = 0; i < display.getLevel(pcClass); i++)
			{
				int hitDie = display.getLevelHitDie(pcClass, i + 1).getDie();
				if (hitDie != 0)
				{
					Integer num = hdMap.get(hitDie);
					if (num == null)
					{
						hdMap.put(hitDie, 1);
					}
					else
					{
						hdMap.put(hitDie, num.intValue() + 1);
					}
				}
			}
		}
		Set<Integer> keys = hdMap.keySet();

		if (keys.size() > 1)
		{
			ret.append(getShortToken(display));
			ret.append(" HD; ");
		}
		for (int key : keys)
		{
			Integer value = hdMap.get(key);
			ret.append(del);
			ret.append(value).append('d').append(key);
			total += value;
			del = "+";
		}
	
		// Get CON bonus contribution to hitpoint total
		int temp = (int) display.getStatBonusTo("HP", "BONUS") * display.getTotalLevels();

		// Add in feat bonus
		temp += (int) pc.getTotalBonusTo("HP", "CURRENTMAX");

		if (temp != 0)
		{
			ret.append(Delta.toString(temp));
		}

		return ret.toString();
	}

	/**
	 * Get the short version of the HITDICE token
	 * @param display
	 * @return the short version of the HITDICE token
	 */
	public static String getShortToken(CharacterDisplay display)
	{
		int dice;

		dice = 0;

		for (PCClass pcClass : display.getClassSet())
		{
			HashMap<Integer, Integer> hdMap =
					new LinkedHashMap<>();

			for (int i = 0; i < display.getLevel(pcClass); i++)
			{
				int hitDie = display.getLevelHitDie(pcClass, i + 1).getDie();
				Integer num = hdMap.get(hitDie);
				if (num == null)
				{
					hdMap.put(hitDie, 1);
				}
				else
				{
					hdMap.put(hitDie, num.intValue() + 1);
				}
			}

			Set<Integer> keys = hdMap.keySet();
			for (int hdSize : keys)
			{
				dice += hdMap.get(hdSize);
			}
		}

		return String.valueOf(dice);
	}
}
