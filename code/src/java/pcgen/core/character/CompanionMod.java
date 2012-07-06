/*
 * CompanionMod.java
 * Copyright 2001 (C) Bryan McRoberts <merton_monk@yahoo.com>
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
 *************************************************************************
 *
 * @author Jayme Cox <jaymecox@users.sourceforge.net>
 * @Created on July 10th, 2002, 3:55 PM
 *
 * Current Ver: $Revision$
 * Last Editor: $Author$
 * Last Edited: $Date$
 *
 *************************************************************************/
package pcgen.core.character;

import java.util.List;
import java.util.Map;

import pcgen.cdom.enumeration.ListKey;
import pcgen.cdom.enumeration.MapKey;
import pcgen.cdom.reference.CDOMSingleRef;
import pcgen.core.PCClass;
import pcgen.core.PObject;
import pcgen.core.Race;

/**
 * <code>CompanionMod</code>.
 *
 * @author Jayme Cox <jaymecox@users.sourceforge.net>
 * @version $Revision$
 */
public final class CompanionMod extends PObject
{
	/**
	 * Get Level
	 * @param var
	 * @return level
	 */
	public int getVariableApplied(final String var)
	{
		int result = -1;

		Map<String, Integer> varmap = getMapFor(MapKey.APPLIED_VARIABLE);

		if (varmap != null && result == -1 && varmap.get(var) != null)
		{
			result = varmap.get(var);
		}

		return result;
	}

	public boolean appliesToRace(Race r)
	{
		List<CDOMSingleRef<Race>> list = getListFor(ListKey.APPLIED_RACE);
		if (list != null)
		{
			for (CDOMSingleRef<Race> ref : list)
			{
				Race race = ref.resolvesTo();
				if (race.equals(r))
				{
					return true;
				}
			}
		}

		return false;
	}

	public int getLevelApplied(PCClass cl)
	{
		int result = -1;

		Map<CDOMSingleRef<? extends PCClass>, Integer> ac = getMapFor(MapKey.APPLIED_CLASS);
		if (ac != null)
		{
			for (Map.Entry<CDOMSingleRef<? extends PCClass>, Integer> me : ac
					.entrySet())
			{
				PCClass pcclass = me.getKey().resolvesTo();
				if (pcclass.getKeyName().equalsIgnoreCase(cl.getKeyName()))
				{
					result = me.getValue();
				}
			}
		}

		return result;
	}
}
