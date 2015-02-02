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
import pcgen.cdom.reference.CDOMSingleRef;
import pcgen.core.PCStat;

public class NonAbilityDisplay
{

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
		if (po == null)
		{
			//why is this check necessary (and what errors does it suppress??)
			return false;
		}
		// An unlock will always override a lock, so check it first
		for (CDOMSingleRef<PCStat> ref : po
			.getSafeListFor(ListKey.NONSTAT_TO_STAT_STATS))
		{
			if (ref.resolvesTo().equals(stat))
			{
				return false;
			}
		}
	
		for (CDOMSingleRef<PCStat> nsStat : po.getSafeListFor(ListKey.NONSTAT_STATS))
		{
			if (nsStat.resolvesTo().equals(stat))
			{
				return true;
			}
		}
	
		return false;
	}

}
