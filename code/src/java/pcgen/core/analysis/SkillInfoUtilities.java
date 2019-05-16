/*
 * Copyright 2009 (C) Tom Parker <thpr@users.sourceforge.net>
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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Supplier;

import pcgen.cdom.enumeration.ListKey;
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.cdom.enumeration.Type;
import pcgen.cdom.reference.CDOMSingleRef;
import pcgen.core.PCStat;
import pcgen.core.PlayerCharacter;
import pcgen.core.Skill;

public final class SkillInfoUtilities
{

	private SkillInfoUtilities()
	{
	}

	/**
	 * Get the key attribute's description
	 * 
	 * @return description
	 */
	public static String getKeyStatFromStats(PlayerCharacter pc, Skill sk)
	{
		Supplier<PCStat> stat = sk.get(ObjectKey.KEY_STAT);
		return stat == null ? "" : stat.get().getKeyName();
	}

	/**
	 * Get a list of PCStat's that apply a SKILL bonus to this skill. Generates
	 * (optionally, if typeList is non-null) a list of String's types
	 *
	 * @param typeList
	 * @return List of stats that apply
	 */
	public static List<PCStat> getKeyStatList(PlayerCharacter pc, Skill sk, List<Type> typeList)
	{
		return new ArrayList<>();
	}

	/**
	 * Get an iterator for the sub types
	 *
	 * @return iterator for the sub types
	 */
	public static Iterator<Type> getSubtypeIterator(Skill sk)
	{
		List<Type> ret = sk.getSafeListFor(ListKey.TYPE);
		CDOMSingleRef<PCStat> keystat = sk.get(ObjectKey.KEY_STAT);
		if (keystat == null)
		{
			ret.remove(Type.NONE);
		}
		else
		{
			// skip the keystat
			ret.remove(Type.getConstant(keystat.get().getDisplayName()));
			/*
			 * TODO This is magic, and makes tremendous assumptions about the
			 * DATA - BAD BAD BAD
			 */
		}
		return ret.iterator();
	}

}
