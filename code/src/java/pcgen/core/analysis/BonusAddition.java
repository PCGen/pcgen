/*
 * Copyright 2008 (C) Tom Parker <thpr@users.sourceforge.net>
 * Derived from PObject.java
 * Copyright 2001 (C) Bryan McRoberts <merton_monk@yahoo.com>
 * 
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 * 
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation, Inc.,
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */
package pcgen.core.analysis;

import java.util.List;

import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.enumeration.AssociationListKey;
import pcgen.core.PlayerCharacter;
import pcgen.core.bonus.Bonus;
import pcgen.core.bonus.BonusObj;
import pcgen.util.Logging;

public final class BonusAddition
{
	/**
	 * Apply the bonus to a character. The bonus can optionally only be added
	 * once no matter how many associated choices this object has. This is
	 * normally used where a bonus is added for each associated choice.
	 * 
	 * @param bonusString
	 *            The unparsed bonus to be added.
	 * @param chooseString
	 *            The choice to be added.
	 * @param aPC
	 *            The character to apply thr bonus to.
	 * @param addOnceOnly
	 *            Should the bonus only be added once irrespective of number of
	 *            choices
	 */
	public static void applyBonus(String bonusString, String chooseString,
			PlayerCharacter aPC, CDOMObject target, boolean addOnceOnly)
	{
		bonusString = makeBonusString(bonusString, chooseString, aPC);

		final BonusObj aBonus = Bonus.newBonus(bonusString);
		if (aBonus != null)
		{
			aBonus.setAddOnceOnly(addOnceOnly);
			aPC.addBonus(aBonus, target);
		}
	}

	/**
	 * Remove the bonus from this objects list of bonuses.
	 * 
	 * @param bonusString
	 *            The string representing the bonus
	 * @param chooseString
	 *            The choice that was made.
	 * @param aPC
	 *            The player character to remove th bonus from.
	 */
	public static void removeBonus(String bonusString, String chooseString,
			PlayerCharacter aPC, CDOMObject target)
	{
		String bonus = makeBonusString(bonusString, chooseString, aPC);

		BonusObj toRemove = null;

		BonusObj aBonus = Bonus.newBonus(bonus);
		String bonusStrRep = String.valueOf(aBonus);

		List<BonusObj> bonusList = aPC.getAddedBonusList(target);
		
		if (bonusList != null)
		{
			int count = 0;
			for (BonusObj listBonus : bonusList)
			{
				if (target.equals(aPC.getCreatorObject(listBonus))
						&& listBonus.toString().equals(bonusStrRep))
				{
					toRemove = listBonus;
				}
				count++;
			}
		}

		if (toRemove != null)
		{
			aPC.removeAddedBonus(toRemove, target);
		}
		else
		{
			Logging.errorPrint("removeBonus: Could not find bonus: " + bonus
					+ " in bonusList " + bonusList);
		}
	}

	private static String makeBonusString(String bonusString,
			String chooseString, PlayerCharacter aPC)
	{
		// assumption is that the chooseString is in the form
		// class/type[space]level
		int i = chooseString.lastIndexOf(' ');
		String classString = "";
		String levelString = "";

		if (bonusString.startsWith("BONUS:"))
		{
			bonusString = bonusString.substring(6);
		}

		boolean lockIt = bonusString.endsWith(".LOCK");

		if (lockIt)
		{
			bonusString = bonusString.substring(0, bonusString
					.lastIndexOf(".LOCK"));
		}

		if (i >= 0)
		{
			classString = chooseString.substring(0, i);

			if (i < chooseString.length())
			{
				levelString = chooseString.substring(i + 1);
			}
		}

		while (bonusString.lastIndexOf("TYPE=%") >= 0)
		{
			i = bonusString.lastIndexOf("TYPE=%");
			bonusString = bonusString.substring(0, i + 5) + classString
					+ bonusString.substring(i + 6);
		}

		while (bonusString.lastIndexOf("CLASS=%") >= 0)
		{
			i = bonusString.lastIndexOf("CLASS=%");
			bonusString = bonusString.substring(0, i + 6) + classString
					+ bonusString.substring(i + 7);
		}

		while (bonusString.lastIndexOf("LEVEL=%") >= 0)
		{
			i = bonusString.lastIndexOf("LEVEL=%");
			bonusString = bonusString.substring(0, i + 6) + levelString
					+ bonusString.substring(i + 7);
		}

		if (lockIt)
		{
			i = bonusString.lastIndexOf('|');

			Float val = aPC.getVariableValue(bonusString.substring(i + 1), "");
			bonusString = bonusString.substring(0, i) + "|" + val;
		}

		return bonusString;
	}

}
