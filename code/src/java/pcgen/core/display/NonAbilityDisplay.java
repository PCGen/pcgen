/*
 * Copyright 2012 (C) Tom Parker <thpr@users.sourceforge.net>
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
package pcgen.core.display;

import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.enumeration.ListKey;
import pcgen.cdom.helper.StatLock;
import pcgen.core.PCStat;

public class NonAbilityDisplay
{

	/**
	 * Takes a StatLock. If that StatLock indicates a stat has been locked at 10
	 * then it is considered a non-ability.
	 * 
	 * TODO This is insanely bad design, it's completely arse about face. What
	 * should have been done was find a way to mark a stat as a non-ability and
	 * then have the stat checking code interpret that as "no bonus or penalty -
	 * treat like it was locked at 10". Doing it this way means there is no way
	 * to actually lock a stat at 10. TODO: Fix this mess! disparaging comments
	 * Andrew Wilson 20060308
	 * 
	 * @param stat
	 *            the stat in question
	 * 
	 * @return Whether this has been defined as a non-ability
	 */
	public static boolean isLockedStat(StatLock lock)
	{
		return lock.getLockValue().toString().equals("10");
	}

	/**
	 * Returns true if the given PCStat is not an ability as locked in the given
	 * CDOMObject.
	 * 
	 * @param stat
	 *            The PCStat to be checked to see if it is a non-ability as
	 *            locked in the the given CDOMObject
	 * @param po
	 *            The CDOMObject which is to be checked to see if the given
	 *            PCStat is locked as a non-ability
	 * 
	 * @return true if the given PCStat is not an ability as locked in the given
	 *         CDOMObject; false otherwise
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
				if (isLockedStat(sl))
				{
					return true;
				}
			}
		}
	
		return false;
	}

}
