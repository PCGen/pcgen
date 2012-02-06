/*
 * PlayerCharacterUtilities.java
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 *
 * Current Version: $Revision$
 * Last Editor: $Author$
 * Last Edited: $Date$
 * Copyright 2005 Andrew Wilson <nuance@sourceforge.net>
 * Class created by migrating code from PlayerCharacter
 * Copyright 2001 (C) Bryan McRoberts <merton_monk@yahoo.com>
 */
package pcgen.core;


import java.math.BigDecimal;
import java.util.StringTokenizer;

import pcgen.cdom.enumeration.ObjectKey;

/**
 * Utilities class for PlayerCharacter.  Holds various static methods.
 *
 * @author  Andrew Wilson <nuance@sourceforge.net>
 */
public class PlayerCharacterUtilities
{
	/**
	 * Picks the biggest die size from two strings in the form V|WdX, YdZ (where
	 * the WdX represents W X sided dice).  If Z is larger than X, returns
	 * V|YdZ, otherwise it returns V|WdX
	 *
	 * @param   oldString  2|1d3
	 * @param   newString  1d4
	 *
	 * @return  in the example parameters given, will return 2|1d4 (because the
	 *          4 is bigger than the 3). If the last figure in the new string
	 *          isn't larger, it returns the original string.
	 */
	public static String getBestUDamString(final String oldString, final String newString)
	{
		if ((newString == null) || (newString.length() < 2))
		{
			return oldString;
		}
		if (oldString == null)
		{
			StringTokenizer aTok = new StringTokenizer(newString, " dD+-(x)");
			aTok.nextToken();
			return Integer.parseInt(aTok.nextToken()) + "|" + newString;
		}

		StringTokenizer aTok      = new StringTokenizer(oldString, "|");
		int             sides     = Integer.parseInt(aTok.nextToken());
		String          retString = oldString;

		aTok = new StringTokenizer(newString, " dD+-(x)");

		if (aTok.countTokens() > 1)
		{
			aTok.nextToken();

			final int i = Integer.parseInt(aTok.nextToken());

			if (sides < i)
			{
				sides     = i;
				retString = sides + "|" + newString;
			}
		}

		return retString;
	}

	/**
	 * Set the Weapon proficiency of one piece of Equipment to the same as the
	 * Proficiency in another piece of Equipment.  For some bizarre reason, as
	 * well as setting the proficiency,  this zeros out the Weight and cost of
	 * the equipment.
	 *
	 * @param  equip  the Weapon to get the proficiency from
	 * @param  eqm    the weapon to set the proficiency in
	 */
	static void setProf(final Equipment equip, final Equipment eqm)
	{
		eqm.put(ObjectKey.WEAPON_PROF, equip.get(ObjectKey.WEAPON_PROF));
		// In case this is used somewhere it shouldn't be used,
		// set weight and cost to 0
		eqm.put(ObjectKey.WEIGHT, BigDecimal.ZERO);
		eqm.put(ObjectKey.CURRENT_COST, BigDecimal.ZERO);
	}
}
