/*
 * StatList.java
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
 *
 * Created on August 10, 2002, 11:45 PM
 *
 * Current Ver: $Revision: 1.38 $
 * Last Editor: $Author: binkley $
 * Last Edited: $Date: 2005/10/20 23:40:10 $
 *
 */
package pcgen.core;

import pcgen.core.utils.CoreUtility;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * <code>StatList</code>.
 *
 * @author Bryan McRoberts <merton_monk@yahoo.com>
 * @version $Revision: 1.38 $
 */
public final class StatList
{
	private List stats = new ArrayList();
	private PlayerCharacter ownerPC;

	public StatList(final PlayerCharacter pc)
	{
		ownerPC = pc;
	}

	public int getBaseStatFor(final String aStat)
	{
		final int x = getIndexOfStatFor(aStat);

		if (x == -1)
		{
			return 0;
		}

		final PCStat stat = (PCStat) stats.get(x);
		final PlayerCharacter aPC = ownerPC;
		int z = aPC.getVariableValue("LOCK." + stat.getAbb(), "").intValue();

		if ((z != 0) || ((z == 0) && aPC.hasVariable("LOCK." + stat.getAbb())))
		{
			return z;
		}

		z = aPC.getVariableValue("BASE." + stat.getAbb(), "").intValue();

		if (z != 0)
		{
			return z;
		}

		return stat.getBaseScore();
	}

	public List getBonusListOfType(final String aType, final String aName)
	{
		final List aList = new ArrayList();

		for (Iterator e = stats.iterator(); e.hasNext();)
		{
			final PCStat aStat = (PCStat) e.next();
			aList.addAll(aStat.getBonusListOfType(aType, aName));
		}

		return aList;
	}

	/**
	 * @param aNum
	 * @return the MOD for any given number
	 */
	public int getModForNumber(final int aNum)
	{
		// Use the formula from stat #1
		// (they all use the same formula)
		return getModForNumber(aNum, 1);
	}

	public int getModForNumber(final int aNum, final int statIndex)
	{
		final PCStat stat = (PCStat) stats.get(statIndex);
		String aString = stat.getStatMod();

		/////////////////////////////////////////////////////////////////////////
		// Need to replace all occurances of 'SCORE' in the formula, not just the first.
		// For some systems (High Adventure Role Playing for example), it is necessary to
		// have multiple 'SCORE' values in the formula.
		//
		// This whole method should probably be revisited as a valid variable name that
		// contains 'SCORE' can be trounced by the replacement (e.g. IQ_SCORE could be changed to IQ_12)
		// - Byngl Dec 16, 2004
		//
		//aString = aString.replaceFirst("SCORE", Integer.toString(aNum)); //Only works on jdk 1.4
//		aString = CoreUtility.replaceFirst(aString, "SCORE", Integer.toString(aNum));
		aString = CoreUtility.replaceAll(aString, "SCORE", Integer.toString(aNum));
		/////////////////////////////////////////////////////////////////////////

		return ownerPC.getVariableValue(aString, "").intValue();
	}

	public String getPenaltyVar(final String aStat)
	{
		final int x = getIndexOfStatFor(aStat);

		if (x == -1)
		{
			return "";
		}

		final PCStat stat = (PCStat) stats.get(x);

		return stat.getPenaltyVar();
	}

	public int getStatModFor(final String aStat)
	{
		final int x = getIndexOfStatFor(aStat);

		if (x == -1)
		{
			return 0;
		}

		final PCStat stat = (PCStat) stats.get(x);

		return ownerPC.getVariableValue(stat.getStatMod(), "STAT:" + stat.getAbb()).intValue();
	}

	public List getStats()
	{
		return stats;
	}

	/**
	 * Calculate the total for the requested stat. If equipment or temporary
	 * bonuses should be excluded, getPartialStatFor should be used instead.
	 *
	 * @param aStat The abbreviation of the stat to be calculated
	 * @return The value of the stat
	 */
	public int getTotalStatFor(final String aStat)
	{
		int y = getBaseStatFor(aStat);
		int x = getIndexOfStatFor(aStat);

		if (x == -1)
		{
			return y;
		}

		final PCStat stat = (PCStat) stats.get(x);
		final PlayerCharacter aPC = ownerPC;
		x = aPC.getVariableValue("LOCK." + stat.getAbb(), "").intValue();

		if ((x != 0) || ((x == 0) && aPC.hasVariable("LOCK." + stat.getAbb())))
		{
			return x;
		}

		y += aPC.getTotalBonusTo("STAT", stat.getAbb());

		return y;
	}

	public int getIndexOfStatFor(final String aStat)
	{
		// see if it starts with STATx where x is a number
		if (aStat.startsWith("STAT"))
		{
			final int x = Integer.parseInt(aStat.substring(4));

			if ((x < 0) || (x >= stats.size()))
			{
				return -1;
			}

			return x;
		}

		// otherwise it must be an abbreviation
		return SettingsHandler.getGame().getStatFromAbbrev(aStat);
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
	public int getPartialStatFor(String aStat, boolean useTemp, boolean useEquip)
	{
		int y = getBaseStatFor(aStat);
		int x = getIndexOfStatFor(aStat);

		if (x == -1)
		{
			return y;
		}

		final PCStat stat = (PCStat) stats.get(x);
		final PlayerCharacter aPC = ownerPC;
		x = aPC.getVariableValue("LOCK." + stat.getAbb(), "").intValue();

		if ((x != 0) || ((x == 0) && aPC.hasVariable("LOCK." + stat.getAbb())))
		{
			return x;
		}

		y += aPC.getPartialStatBonusFor(stat.getAbb(), useTemp, useEquip);

		return y;
	}
}
