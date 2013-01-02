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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import pcgen.cdom.enumeration.CharID;
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.cdom.enumeration.SkillCost;
import pcgen.cdom.facet.analysis.GlobalSkillCostFacet;
import pcgen.cdom.facet.analysis.ListSkillCostFacet;
import pcgen.cdom.facet.analysis.LocalSkillCostFacet;
import pcgen.cdom.facet.input.ClassSkillListFacet;
import pcgen.cdom.facet.input.GlobalAddedSkillCostFacet;
import pcgen.cdom.facet.input.LocalAddedSkillCostFacet;
import pcgen.cdom.facet.input.MonsterCSkillFacet;
import pcgen.cdom.list.ClassSkillList;
import pcgen.core.Globals;
import pcgen.core.PCClass;
import pcgen.core.Skill;
import pcgen.rules.context.ReferenceContext;

/**
 * SkillCostFacet is a Facet that tracks costs of Skills for each PCClass
 * 
 * @author Thomas Parker (thpr [at] yahoo.com)
 */
public class SkillCostFacet
{
	private ClassSkillListFacet classSkillListFacet;
	private GlobalAddedSkillCostFacet globalAddedSkillCostFacet;
	private GlobalSkillCostFacet globalSkillCostFacet;
	private ListSkillCostFacet listSkillCostFacet;
	private LocalAddedSkillCostFacet localAddedSkillCostFacet;
	private LocalSkillCostFacet localSkillCostFacet;
	private MasterSkillFacet masterSkillFacet;
	private SubClassFacet subClassFacet;
	private MonsterCSkillFacet monsterCSkillFacet;

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
		Collection<ClassSkillList> classSkillList = getClassSkillLists(id, pcc);
		return hasGlobalCost(id, skill, SkillCost.CLASS)
			|| hasLocalCost(id, pcc, skill, SkillCost.CLASS)
			|| hasLocalCost(id, classSkillList, skill, SkillCost.CLASS)
			|| hasMasterSkill(classSkillList, skill)
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
		Collection<ClassSkillList> classSkillList = getClassSkillLists(id, pcc);

		return hasGlobalCost(id, skill, SkillCost.CROSS_CLASS)
			|| hasLocalCost(id, pcc, skill, SkillCost.CROSS_CLASS)
			|| hasLocalCost(id, classSkillList, skill, SkillCost.CROSS_CLASS);
	}

	private final Collection<ClassSkillList> getClassSkillLists(CharID id,
		PCClass pcc)
	{
		Collection<ClassSkillList> classSkillList =
				classSkillListFacet.getSet(id, pcc);
		if (classSkillList.isEmpty())
		{
			List<ClassSkillList> returnList = new ArrayList<ClassSkillList>(2);
			ReferenceContext ref = Globals.getContext().ref;
			Class<ClassSkillList> csl = ClassSkillList.class;
			ClassSkillList l =
					ref.silentlyGetConstructedCDOMObject(csl, pcc.getKeyName());
			if (l != null)
			{
				returnList.add(l);
			}
			String subClassKey = subClassFacet.getSource(id, pcc);
			if (subClassKey != null)
			{
				l = ref.silentlyGetConstructedCDOMObject(csl, subClassKey);
				if (l != null)
				{
					returnList.add(l);
				}
			}
			return returnList;
		}
		else
		{
			return classSkillList;
		}
	}

	private boolean hasGlobalCost(CharID id, Skill skill, SkillCost sc)
	{
		return globalSkillCostFacet.contains(id, sc, skill)
			|| globalAddedSkillCostFacet.contains(id, skill, sc);
	}

	private boolean hasLocalCost(CharID id, PCClass pcc, Skill skill,
		SkillCost sc)
	{
		return localSkillCostFacet.contains(id, pcc, sc, skill)
			|| localAddedSkillCostFacet.contains(id, pcc, skill, sc);
	}

	private boolean hasLocalCost(CharID id,
		Collection<ClassSkillList> skillLists, Skill skill, SkillCost sc)
	{
		for (ClassSkillList csl : skillLists)
		{
			if (listSkillCostFacet.contains(id, csl, sc, skill))
			{
				return true;
			}
		}
		return false;
	}

	private boolean hasMasterSkill(Collection<ClassSkillList> skillLists,
		Skill skill)
	{
		for (ClassSkillList csl : skillLists)
		{
			if (masterSkillFacet.hasMasterSkill(csl, skill))
			{
				return true;
			}
		}
		return false;
	}

	public void setClassSkillListFacet(ClassSkillListFacet classSkillListFacet)
	{
		this.classSkillListFacet = classSkillListFacet;
	}

	public void setGlobalAddedSkillCostFacet(
		GlobalAddedSkillCostFacet globalAddedSkillCostFacet)
	{
		this.globalAddedSkillCostFacet = globalAddedSkillCostFacet;
	}

	public void setGlobalSkillCostFacet(
		GlobalSkillCostFacet globalSkillCostFacet)
	{
		this.globalSkillCostFacet = globalSkillCostFacet;
	}

	public void setListSkillCostFacet(ListSkillCostFacet listSkillCostFacet)
	{
		this.listSkillCostFacet = listSkillCostFacet;
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

	public void setMasterSkillFacet(MasterSkillFacet masterSkillFacet)
	{
		this.masterSkillFacet = masterSkillFacet;
	}

	public void setSubClassFacet(SubClassFacet subClassFacet)
	{
		this.subClassFacet = subClassFacet;
	}

	public void setMonsterCSkillFacet(MonsterCSkillFacet monsterCSkillFacet)
	{
		this.monsterCSkillFacet = monsterCSkillFacet;
	}
}
