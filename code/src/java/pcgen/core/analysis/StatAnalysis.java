/*
 * Copyright 2009 (c) Tom Parker <thpr@users.sourceforge.net>
 * Derived from StatList.java
 * Copyright 2002 (C) Bryan McRoberts <merton_monk@yahoo.com>
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
 */
package pcgen.core.analysis;

import java.util.regex.Pattern;

import pcgen.cdom.enumeration.AssociationKey;
import pcgen.core.PCStat;
import pcgen.core.PlayerCharacter;

public class StatAnalysis
{
	public static int getBaseStatFor(PlayerCharacter aPC, PCStat stat)
	{
		// Only check for a lock if the stat hasn't been unlocked
		if (!aPC.hasUnlockedStat(stat))
		{
			Number val = aPC.getLockedStat(stat);
			if (val != null)
			{
				return val.intValue();
			}
		}
		
		int z = aPC.getVariableValue("BASE." + stat.getAbb(), "").intValue();

		if (z != 0)
		{
			return z;
		}
		Integer score = aPC.getAssoc(stat, AssociationKey.STAT_SCORE);
		return score == null ? 0 : score;
	}

	public static int getModForNumber(PlayerCharacter ownerPC, int aNum, PCStat stat)
	{
		String aString = stat.getStatMod();

		/////////////////////////////////////////////////////////////////////////
		// Need to replace all occurances of 'SCORE' in the formula, not just the
		// first. For some systems (High Adventure Role Playing for example), it
		// is necessary to have multiple 'SCORE' values in the formula.
		//
		// This whole method should probably be revisited as a valid variable name
		// that contains 'SCORE' can be trounced by the replacement (e.g. IQ_SCORE
		// could be changed to IQ_12)
		//
		// - Byngl Dec 16, 2004

		aString = aString.replaceAll(Pattern.quote("SCORE"), Integer.toString(aNum));

		/////////////////////////////////////////////////////////////////////////

		return ownerPC.getVariableValue(aString, "").intValue();
	}

	public static int getStatModFor(PlayerCharacter ownerPC, PCStat stat)
	{
		return ownerPC.getVariableValue(stat.getStatMod(), "STAT:" + stat.getAbb()).intValue();
	}

	/**
	 * Calculate the total for the requested stat. If equipment or temporary
	 * bonuses should be excluded, getPartialStatFor should be used instead.
	 *
	 * @param aStat The abbreviation of the stat to be calculated
	 * @return The value of the stat
	 */
	public static int getTotalStatFor(PlayerCharacter ownerPC, PCStat stat)
	{
		int y = getBaseStatFor(ownerPC, stat);

		final PlayerCharacter aPC = ownerPC;
		// Only check for a lock if the stat hasn't been unlocked
		if (!aPC.hasUnlockedStat(stat))
		{
			Number val = aPC.getLockedStat(stat);
			if (val != null)
			{
				return val.intValue();
			}
		}

		y += aPC.getTotalBonusTo("STAT", stat.getAbb());

		return y;
	}

	/**
	 * Retrieve a correctly calculated attribute value where one or more
	 * types are excluded.
	 *
	 * @param aStat The abbreviation of the stat to be calculated
	 * @param useTemp Should temporary bonuses be included?
	 * @param useEquip Should equipment bonuses be included?
	 * @return The value of the stat
	 */
	public static int getPartialStatFor(PlayerCharacter aPC, PCStat stat, boolean useTemp, boolean useEquip)
	{
		// Only check for a lock if the stat hasn't been unlocked
		if (!aPC.hasUnlockedStat(stat))
		{
			Number val = aPC.getLockedStat(stat);
			if (val != null)
			{
				return val.intValue();
			}
		}

		int y = getBaseStatFor(aPC, stat);

		y += aPC.getPartialStatBonusFor(stat, useTemp, useEquip);

		return y;
	}

}
