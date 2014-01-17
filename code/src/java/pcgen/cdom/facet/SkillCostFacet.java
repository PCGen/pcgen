/*
 * Copyright (c) Thomas Parker, 2012
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
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.cdom.enumeration.SkillCost;
import pcgen.cdom.facet.analysis.GlobalToSkillCostFacet;
import pcgen.cdom.facet.analysis.ListToSkillCostFacet;
import pcgen.cdom.facet.analysis.LocalSkillCostFacet;
import pcgen.cdom.facet.input.LocalAddedSkillCostFacet;
import pcgen.cdom.facet.input.MonsterCSkillFacet;
import pcgen.core.PCClass;
import pcgen.core.Skill;

/**
 * SkillCostFacet is a Facet that tracks costs of Skills for each PCClass
 * 
 * @author Thomas Parker (thpr [at] yahoo.com)
 */
public class SkillCostFacet
{
	private ListToSkillCostFacet listToSkillCostFacet;
	private LocalAddedSkillCostFacet localAddedSkillCostFacet;
	private LocalSkillCostFacet localSkillCostFacet;
	private SkillListToCostFacet skillListToCostFacet;
	private MonsterCSkillFacet monsterCSkillFacet;
	private GlobalToSkillCostFacet globalToSkillCostFacet;

	public SkillCost skillCostForPCClass(CharID id, Skill sk, PCClass aClass)
	{
		if (isClassSkill(id, aClass, sk))
		{
			return SkillCost.CLASS;
		}
		else if (sk.getSafe(ObjectKey.EXCLUSIVE)
			&& !isCrossClassSkill(id, aClass, sk))
		{
			return SkillCost.EXCLUSIVE;
		}
		else
		{
			return SkillCost.CROSS_CLASS;
		}
	}

	public boolean isClassSkill(CharID id, PCClass pcc, Skill skill)
	{
		if (pcc == null)
		{
			throw new IllegalArgumentException(
				"PCClass in isClassSkill cannot be null");
		}
		if (skill == null)
		{
			throw new IllegalArgumentException(
				"Skill in isClassSkill cannot be null");
		}
		return hasLocalCost(id, pcc, skill, SkillCost.CLASS)
			|| skillListToCostFacet.contains(id, pcc, SkillCost.CLASS, skill)
			|| monsterCSkillFacet.contains(id, skill);
	}

	public boolean isCrossClassSkill(CharID id, PCClass pcc, Skill skill)
	{
		if (pcc == null)
		{
			throw new IllegalArgumentException(
				"PCClass in isCrossClassSkill cannot be null");
		}
		if (skill == null)
		{
			throw new IllegalArgumentException(
				"Skill in isCrossClassSkill cannot be null");
		}
		if (isClassSkill(id, pcc, skill))
		{
			return false;
		}
		return hasLocalCost(id, pcc, skill, SkillCost.CROSS_CLASS);
	}

	private boolean hasLocalCost(CharID id, PCClass pcc, Skill skill,
		SkillCost sc)
	{
		return globalToSkillCostFacet.contains(id, pcc, sc, skill)
			|| localSkillCostFacet.contains(id, pcc, sc, skill)
			|| localAddedSkillCostFacet.contains(id, pcc, sc, skill)
			|| listToSkillCostFacet.contains(id, pcc, sc, skill);
	}

	public void setGlobalToSkillCostFacet(
		GlobalToSkillCostFacet globalToSkillCostFacet)
	{
		this.globalToSkillCostFacet = globalToSkillCostFacet;
	}

	public void setListToSkillCostFacet(ListToSkillCostFacet listToSkillCostFacet)
	{
		this.listToSkillCostFacet = listToSkillCostFacet;
	}

	public void setLocalAddedSkillCostFacet(
		LocalAddedSkillCostFacet localAddedSkillCostFacet)
	{
		this.localAddedSkillCostFacet = localAddedSkillCostFacet;
	}

	public void setLocalSkillCostFacet(LocalSkillCostFacet localSkillCostFacet)
	{
		this.localSkillCostFacet = localSkillCostFacet;
	}

	public void setSkillListToCostFacet(SkillListToCostFacet skillListToCostFacet)
	{
		this.skillListToCostFacet = skillListToCostFacet;
	}

	public void setMonsterCSkillFacet(MonsterCSkillFacet monsterCSkillFacet)
	{
		this.monsterCSkillFacet = monsterCSkillFacet;
	}
}
