/*
 * Copyright 2008 (C) Tom Parker <thpr@users.sourceforge.net>
 * Derived from PCTemplate.java
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

import pcgen.cdom.enumeration.ListKey;
import pcgen.cdom.helper.StatLock;
import pcgen.core.PCStat;
import pcgen.core.PCTemplate;
import pcgen.core.SettingsHandler;

public class TemplateStat
{

	/**
	 * Takes an integer input which it uses to access Games mode's "statlist"
	 * array. If that stat has been locked at 10 then it is considered a
	 * non-ability. 
	 * XXX This is insanely bad design, it's completely arse about
	 * face. What should have been done was find a way to mark a stat as a
	 * non-ability and then have the stat checking code interpret that as "no
	 * bonus or penalty - treat like it was locked at 10". Doing it this way
	 * means there is no way to actually lock a stat at 10. TODO: Fix this mess!
	 * disparaging comments Andrew Wilson 20060308
	 * 
	 * @param statIdx
	 *            index number of the stat in question
	 * 
	 * @return Whether this has been defined as a non-ability
	 */
	public static boolean isNonAbility(PCTemplate pct, final int statIdx)
	{
		final List<PCStat> statList =
				SettingsHandler.getGame().getUnmodifiableStatList();
	
		if ((statIdx < 0) || (statIdx >= statList.size()))
		{
			return true;
		}
	
		// An unlock will always override a lock, so check it first
		if (TemplateStat.isUnlocked(pct, statIdx))
		{
			return false;
		}
		
		PCStat stat = statList.get(statIdx);
		for (StatLock sl : pct.getSafeListFor(ListKey.STAT_LOCKS))
		{
			if (sl.getLockedStat().equals(stat))
			{
				if (sl.getLockValue().toString().equals("10"))
				{
					return true;
				}
			}
		}
	
		return false;
	}

	/**
	 * Takes an integer input which it uses to access Games mode's "statlist"
	 * array. Test if that stat has been unlocked via a DEFINE|UNLOCK 
	 * 
	 * @param statIdx
	 *            index number of the stat in question
	 * 
	 * @return Whether this has been unlocked
	 */
	public static boolean isUnlocked(PCTemplate pct, final int statIdx)
	{
		final List<PCStat> statList =
				SettingsHandler.getGame().getUnmodifiableStatList();
	
		if ((statIdx < 0) || (statIdx >= statList.size()))
		{
			return false;
		}

		return pct.containsInList(ListKey.UNLOCKED_STATS, statList.get(statIdx));
	}

}
