/*
 * Copyright 2008 (C) Tom Parker <thpr@users.sourceforge.net>
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
package pcgen.cdom.inst;

import pcgen.base.util.DoubleKeyMap;
import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.enumeration.SkillCost;
import pcgen.core.PCClass;
import pcgen.core.PlayerCharacter;
import pcgen.core.Skill;

/**
 * An ObjectCache is a CDOMObject designed to serve as a cache of information
 * for a PlayerCharacter. It is therefore somewhat simple (non-typed) and
 * contains some specialized caching functions.
 */
public class ObjectCache extends CDOMObject
{

	/**
	 * Returns false, as ObjectCache never has a type.
	 */
	@Override
	public boolean isType(String type)
	{
		return false;
	}

	/**
	 * Stores a cache of the cost of each Skill based on the Skill's key and
	 * PCClass
	 */
	private final DoubleKeyMap<Skill, PCClass, SkillCost> skillCostMap = new DoubleKeyMap<Skill, PCClass, SkillCost>();

	/**
	 * Returns the cost of a given Skill for the given PlayerCharacter and
	 * PCClass.
	 * 
	 * @param pc
	 *            The PlayerCharacter for which the SkillCost is being
	 *            calculated.
	 * @param skill
	 *            The Skill for which the SkillCost is being calculated.
	 * @param pcc
	 *            The PCClass for which the SkillCost is being calculated.
	 * @return The cost of a given Skill for the given PlayerCharacter and
	 *         PCClass.
	 */
	public SkillCost getSkillCost(PlayerCharacter pc, Skill skill, PCClass pcc)
	{
		SkillCost cost = skillCostMap.get(skill, pcc);
		if (cost == null)
		{
			cost = pc.skillCostForPCClass(skill, pcc);
			skillCostMap.put(skill, pcc, cost);
		}
		return cost;
	}

}
