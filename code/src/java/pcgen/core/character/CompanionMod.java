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

import pcgen.cdom.base.Constants;
import pcgen.cdom.enumeration.IntegerKey;
import pcgen.cdom.enumeration.ListKey;
import pcgen.cdom.enumeration.MapKey;
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.cdom.reference.CDOMSingleRef;
import pcgen.core.PCClass;
import pcgen.core.PObject;
import pcgen.core.Race;
import pcgen.core.utils.MessageType;
import pcgen.core.utils.ShowMessageDelegate;

/**
 * <code>CompanionMod</code>.
 *
 * @author Jayme Cox <jaymecox@users.sourceforge.net>
 * @version $Revision$
 */
public final class CompanionMod extends PObject
{
	/**
	 * Bog standard clone method
	 *
	 * @return  a copy of this Ability
	 */
	@Override
	public CompanionMod clone()
	{
		CompanionMod cmpMod = null;

		try
		{
			cmpMod = (CompanionMod) super.clone();
		}
		catch (CloneNotSupportedException e)
		{
			ShowMessageDelegate.showMessageDialog(
				e.getMessage(),
				Constants.s_APPNAME,
				MessageType.ERROR);
		}

		return cmpMod;
	}

	/**
	 * Compares classMap, type and level
	 * @param obj
	 * @return true if equal
	 */
	@Override
	public boolean equals(final Object obj)
	{
		if (obj instanceof CompanionMod)
		{
			final CompanionMod cmpMod = (CompanionMod) obj;

			List<CDOMSingleRef<Race>> car = cmpMod.getListFor(ListKey.APPLIED_RACE);
			List<CDOMSingleRef<Race>> ar = getListFor(ListKey.APPLIED_RACE);
			Map<CDOMSingleRef<? extends PCClass>, Integer> cac = cmpMod.getMapFor(MapKey.APPLIED_CLASS);
			Map<CDOMSingleRef<? extends PCClass>, Integer> ac = getMapFor(MapKey.APPLIED_CLASS);
			Map<String, Integer> cav = cmpMod.getMapFor(MapKey.APPLIED_VARIABLE);
			Map<String, Integer> av = getMapFor(MapKey.APPLIED_VARIABLE);
			if (ar == null && car != null || ac == null && cac != null
				|| av == null && cav != null)
			{
				return false;
			}
			return getSafe(IntegerKey.LEVEL) == cmpMod.getSafe(IntegerKey.LEVEL)
				&& ar.equals(car) && ac.equals(cac) && av.equals(cav);
		}
		return false;
	}

	/**
	 * Get Level
	 * @param className
	 * @return level
	 */
	public int getLevel(final String className)
	{
		int result = -1;

		List<CDOMSingleRef<Race>> list =
				getListFor(ListKey.APPLIED_RACE);
		if (list != null)
		{
			for (CDOMSingleRef<Race> ref : list)
			{
				Race race = ref.resolvesTo();
				String key = race.getKeyName();
				if(key.equalsIgnoreCase(className))
				{
					result = 1;
				}
			}
		}

		Map<CDOMSingleRef<? extends PCClass>, Integer> ac =
				getMapFor(MapKey.APPLIED_CLASS);
		if (ac != null)
		{
			for (Map.Entry<CDOMSingleRef<? extends PCClass>, Integer> me : ac.entrySet())
			{
				PCClass pcclass = me.getKey().resolvesTo();
				String key = pcclass.getKeyName();
				if (key.equalsIgnoreCase(className))
				{
					result = me.getValue();
				}
			}
		}

		Map<String, Integer> varmap = getMapFor(MapKey.APPLIED_VARIABLE);

		if (varmap != null && result == -1 && varmap.get(className) != null)
		{
			result = varmap.get(className);
		}

		return result;
	}

	/**
	 * Get use master skill
	 * @return true if you should use master skill
	 */
	public boolean getUseMasterSkill()
	{
		return getSafe(ObjectKey.USE_MASTER_SKILL);
	}

	/**
	 * Hashcode of the keyname
	 * @return hash code
	 */
	@Override
	public int hashCode()
	{
		return getSafe(IntegerKey.LEVEL);
	}
}
