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
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

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

	@Override
	public String getTokenName()
	{
		return TOKENNAME;
	}

	@Override
	public String getToken(String tokenSource, PlayerCharacter pc, ExportHandler eh)
	{
		String retString = "";

		if ("HITDICE".equals(tokenSource) || "HITDICE.LONG".equals(tokenSource))
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
			HashMap<Integer, Integer> hdMap = new LinkedHashMap<>();

			IntStream.range(0, display.getLevel(pcClass))
					.map(i -> display.getLevelHitDie(pcClass, i + 1).getDie())
					.filter(hitDie -> hitDie != 0).forEach(hitDie ->
						hdMap.merge(hitDie, 1, Integer::sum)
			);

			for (final Map.Entry<Integer, Integer> entry : hdMap.entrySet())
			{
				Integer value = entry.getValue();
				ret.append(del);
				ret.append('(');
				ret.append(value).append('d').append((int) entry.getKey());
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
		// Integer total = 0;

		HashMap<Integer, Integer> hdMap = new LinkedHashMap<>();

		CharacterDisplay display = pc.getDisplay();
		display.getClassSet().forEach(pcClass ->
				IntStream.range(0, display.getLevel(pcClass))
						.map(i -> display.getLevelHitDie(pcClass, i + 1).getDie())
						.filter(hitDie -> hitDie != 0)
						.forEach(hitDie -> hdMap.merge(hitDie, 1, Integer::sum)));

		if (hdMap.size() > 1)
		{
			ret.append(getShortToken(display));
			ret.append(" HD; ");
		}

		for (final Map.Entry<Integer, Integer> entry : hdMap.entrySet())
		{
			Integer value = entry.getValue();
			ret.append(del);
			ret.append(value).append('d').append((int) entry.getKey());
			// total += value;
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
		int dice = display.getClassSet().stream()
				.map(pcClass ->
						IntStream.range(0, display.getLevel(pcClass))
								.map(i -> display.getLevelHitDie(pcClass, i + 1).getDie())
								.boxed()
								.collect(Collectors.toMap(Function.identity(),
										hitDie -> 1,
                                        Integer::sum,
										LinkedHashMap::new)))
				.mapToInt(hdMap -> hdMap.entrySet().stream().mapToInt(Map.Entry::getValue).sum())
				.sum();

		return String.valueOf(dice);
	}
}
