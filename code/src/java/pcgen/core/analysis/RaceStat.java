/*
 * Copyright 2008 (C) Tom Parker <thpr@users.sourceforge.net>
 * Derived from Race.java
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
import pcgen.core.PObject;
import pcgen.core.SettingsHandler;

public class RaceStat
{

	public static boolean isUnlocked(final int statIdx, PObject po)
	{
		final List<PCStat> statList = SettingsHandler.getGame()
				.getUnmodifiableStatList();

		if ((statIdx < 0) || (statIdx >= statList.size()))
		{
			return false;
		}

		return po.containsInList(ListKey.UNLOCKED_STATS, statList.get(statIdx));
	}

	public static boolean isNonAbility(final int statIdx, PObject po)
	{
		final List<PCStat> statList = SettingsHandler.getGame()
				.getUnmodifiableStatList();

		if ((statIdx < 0) || (statIdx >= statList.size()))
		{
			return true;
		}

		// An unlock will always override a lock, so check it first
		if (isUnlocked(statIdx, po))
		{
			return false;
		}

		PCStat stat = statList.get(statIdx);
		for (StatLock sl : po.getSafeListFor(ListKey.STAT_LOCKS))
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

}
