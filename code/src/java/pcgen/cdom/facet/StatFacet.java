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

import pcgen.cdom.enumeration.CharID;
import pcgen.cdom.enumeration.ListKey;
import pcgen.cdom.helper.StatLock;
import pcgen.core.PCStat;
import pcgen.core.PCTemplate;
import pcgen.core.PObject;

/**
 * StatFacet is a Facet that tracks the PCStat that have been granted to a
 * Player Character.
 */
public class StatFacet extends AbstractListFacet<PCStat>
{
	private TemplateFacet templateFacet = FacetLibrary
			.getFacet(TemplateFacet.class);
	private RaceFacet raceFacet = FacetLibrary.getFacet(RaceFacet.class);

	public boolean isNonAbility(CharID id, PCStat stat)
	{
		return !isUnlocked(id, stat) && isNonAbilityPrivate(id, stat);
	}

	private boolean isNonAbilityPrivate(CharID id, PCStat stat)
	{
		if (isNonAbilityForObject(stat, raceFacet.get(id)))
		{
			return true;
		}

		for (PCTemplate template : templateFacet.getSet(id))
		{
			if (isNonAbilityForObject(stat, template))
			{
				return true;
			}
		}
		return false;
	}

	private boolean isUnlocked(CharID id, PCStat stat)
	{
		if (isUnlockedForObject(stat, raceFacet.get(id)))
		{
			return true;
		}
		for (PCTemplate template : templateFacet.getSet(id))
		{
			if (isUnlockedForObject(stat, template))
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
	public static boolean isNonAbilityForObject(PCStat stat, PObject po)
	{
		// An unlock will always override a lock, so check it first
		if (isUnlockedForObject(stat, po))
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

	/**
	 * Test if that stat has been unlocked via a DEFINE|UNLOCK
	 * 
	 * @param stat
	 *            the stat in question
	 * 
	 * @return Whether this has been unlocked
	 */
	public static boolean isUnlockedForObject(PCStat stat, PObject po)
	{
		return po.containsInList(ListKey.UNLOCKED_STATS, stat);
	}

}
