/*
 * Copyright (c) Thomas Parker, 2009.
 * 
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA
 */
package pcgen.cdom.facet;

import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.enumeration.CharID;
import pcgen.cdom.enumeration.ListKey;
import pcgen.cdom.helper.StatLock;
import pcgen.core.PCStat;

/**
 * NonAbilityFacet is a Facet that tracks the Non-Abilities (PCStat objects)
 * that have been set on a Player Character.
 */
public class NonAbilityFacet
{
	private UnlockedStatFacet unlockedStatFacet;
	private StatLockFacet statLockFacet;

	public boolean isNonAbility(CharID id, PCStat stat)
	{
		if (unlockedStatFacet.contains(id, stat))
		{
			return false;
		}
		for (StatLock lock : statLockFacet.getSet(id))
		{
			if ((lock.getLockedStat().equals(stat))
					&& (lock.getLockValue().toString().equals("10")))
			{
				return true;
			}
		}
		return false;
	}

	/**
	 * Takes a stat. If that stat has been locked at 10 then it is considered a
	 * non-ability. XXX This is insanely bad design, it's completely arse about
	 * face. What should have been done was find a way to mark a stat as a
	 * non-ability and then have the stat checking code interpret that as "no
	 * bonus or penalty - treat like it was locked at 10". Doing it this way
	 * means there is no way to actually lock a stat at 10. TODO: Fix this mess!
	 * disparaging comments Andrew Wilson 20060308
	 * 
	 * @param stat
	 *            the stat in question
	 * 
	 * @return Whether this has been defined as a non-ability
	 */
	public static boolean isNonAbilityForObject(PCStat stat, CDOMObject po)
	{
		// An unlock will always override a lock, so check it first
		if (po == null || po.containsInList(ListKey.UNLOCKED_STATS, stat))
		{
			return false;
		}

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

	public void setUnlockedStatFacet(UnlockedStatFacet unlockedStatFacet)
	{
		this.unlockedStatFacet = unlockedStatFacet;
	}

	public void setStatLockFacet(StatLockFacet statLockFacet)
	{
		this.statLockFacet = statLockFacet;
	}

}
