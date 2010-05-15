/*
 * Copyright 2010 (C) Thomas Parker <thpr@users.sourceforge.net>
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
package plugin.primitive.weaponprof;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import pcgen.cdom.base.CDOMReference;
import pcgen.cdom.enumeration.GroupingState;
import pcgen.cdom.enumeration.ListKey;
import pcgen.core.Deity;
import pcgen.core.PlayerCharacter;
import pcgen.core.WeaponProf;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.PrimitiveToken;

public class DeityWeaponToken implements PrimitiveToken<WeaponProf>
{

	private static final Class<WeaponProf> WEAPONPROF_CLASS = WeaponProf.class;

	public boolean initialize(LoadContext context, String value, String args)
	{
		if (value != null || args != null)
		{
			return false;
		}
		return true;
	}

	public String getTokenName()
	{
		return "DEITYWEAPON";
	}

	public Class<WeaponProf> getReferenceClass()
	{
		return WEAPONPROF_CLASS;
	}

	public String getLSTformat()
	{
		return "DEITYWEAPON";
	}

	public boolean allow(PlayerCharacter pc, WeaponProf pcc)
	{
		Deity deity = pc.getDeity();
		if (deity == null)
		{
			return false;
		}
		List<CDOMReference<WeaponProf>> dwp = deity
				.getSafeListFor(ListKey.DEITYWEAPON);
		for (CDOMReference<WeaponProf> ref : dwp)
		{
			if (ref.contains(pcc))
			{
				return true;
			}
		}
		return false;
	}

	public Set<WeaponProf> getSet(PlayerCharacter pc)
	{
		Deity deity = pc.getDeity();
		if (deity == null)
		{
			return Collections.emptySet();
		}
		HashSet<WeaponProf> set = new HashSet<WeaponProf>();
		List<CDOMReference<WeaponProf>> dwp = deity
				.getSafeListFor(ListKey.DEITYWEAPON);
		for (CDOMReference<WeaponProf> ref : dwp)
		{
			set.addAll(ref.getContainedObjects());
		}
		return set;
	}

	public GroupingState getGroupingState()
	{
		return GroupingState.ANY;
	}

	@Override
	public boolean equals(Object obj)
	{
		return obj instanceof DeityWeaponToken;
	}

	@Override
	public int hashCode()
	{
		return 5783;
	}
}
