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
 *
 * Created on December 15, 2003, 12:21 PM
 *
 * Current Ver: $Revision$
 * Last Editor: $Author$
 * Last Edited: $Date$
 *
 */
package plugin.exporttokens;

import pcgen.core.PCClass;
import pcgen.core.PlayerCharacter;
import pcgen.io.ExportHandler;
import pcgen.io.exporttoken.Token;
import pcgen.util.Delta;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Set;

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
	public String getTokenName()
	{
		return TOKENNAME;
	}

	/**
	 * @see pcgen.io.exporttoken.Token#getToken(java.lang.String, pcgen.core.PlayerCharacter, pcgen.io.ExportHandler)
	 */
	public String getToken(String tokenSource, PlayerCharacter pc, ExportHandler eh)
	{
		String retString = "";

		if ("HITDICE".equals(tokenSource))
		{
			retString = getHitDiceToken(pc);
		}
		else if ("HITDICE.SHORT".equals(tokenSource))
		{
			retString = getShortToken(pc);
		}

		return retString;
	}

	/**
	 * Get the HITDICE token
	 * @param pc
	 * @return the HITDICE token
	 */
	public static String getHitDiceToken(PlayerCharacter pc)
	{
		String retString = "";
		String del = "";

		if (pc.getRace().hitDice(pc) > 0)
		{
			retString += ("(" + Integer.toString(pc.getRace().hitDice(pc)) + "d"
			+ Integer.toString(pc.getRace().getHitDiceSize(pc)) + ")");
			del = "+";
		}

		PCClass aClass;

		for (Iterator it = pc.getClassList().iterator(); it.hasNext();)
		{
			aClass = (PCClass) it.next();
			HashMap hdMap = new LinkedHashMap();

			for(int i = 0; i < aClass.getLevel(); i++) {
				Integer hitDie = new Integer(aClass.getLevelHitDie(pc, i + 1));
				Integer num = (Integer)hdMap.get(hitDie);
				if(num == null) {
					hdMap.put(hitDie, new Integer(1));
				}
				else {
					hdMap.put(hitDie, new Integer(num.intValue() + 1));
				}
			}

			Set keys = hdMap.keySet();
			for(Iterator it1 = keys.iterator(); it1.hasNext();) {
				Integer key = (Integer)it1.next();
				Integer value = (Integer)hdMap.get(key);
				retString += (del + "(" + value + "d" + key + ")");
				del = "+";
			}
		}



		//
		// Get CON bonus contribution to hitpoint total
		//
		int temp = (int) pc.getStatBonusTo("HP", "BONUS");
		int a = pc.getRace().hitDice(pc);
		int b = 0;
		if(pc.isMonsterDefault()) {
			b = pc.totalNonMonsterLevels();
		}
		else {
			b = pc.getTotalLevels();
		}
		temp *= (a + b);
		//temp *= (pc.getTotalLevels() + pc.getRace().hitDice(pc));

		//
		// Add in feat bonus
		//
		temp += (int) pc.getTotalBonusTo("HP", "CURRENTMAX");

		if (temp != 0)
		{
			retString += Delta.toString(temp);
		}

		return retString;
	}

	/**
	 * Get the short version of the HITDICE token
	 * @param pc
	 * @return the short version of the HITDICE token
	 */
	public static String getShortToken(PlayerCharacter pc)
	{
		String retString = "";
		int dice;

		dice = pc.getRace().hitDice(pc);

		PCClass aClass;

		for (Iterator it = pc.getClassList().iterator(); it.hasNext();)
		{
			aClass = (PCClass) it.next();
			HashMap hdMap = new LinkedHashMap();

			for(int i = 0; i < aClass.getLevel(); i++) {
				Integer hitDie = new Integer(aClass.getLevelHitDie(pc, i + 1));
				Integer num = (Integer)hdMap.get(hitDie);
				if(num == null) {
					hdMap.put(hitDie, new Integer(1));
				}
				else {
					hdMap.put(hitDie, new Integer(num.intValue() + 1));
				}
			}

			Set keys = hdMap.keySet();
			for(Iterator it1 = keys.iterator(); it1.hasNext();) {
				Integer key = (Integer)it1.next();
				Integer value = (Integer)hdMap.get(key);
				dice += value.intValue();
			}
		}

		retString = new Integer(dice).toString();
		return retString;
	}
}
