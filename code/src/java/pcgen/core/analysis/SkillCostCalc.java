/*
 * Copyright 2008 (C) Tom Parker <thpr@users.sourceforge.net>
 * Derived from Skill.java
 * Copyright 2001 (C) Bryan McRoberts <merton_monk@yahoo.com>
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.     See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 *
 * Created on June 9, 2008
 */
package pcgen.core.analysis;

import java.util.Collection;
import java.util.List;

import pcgen.cdom.base.AssociatedPrereqObject;
import pcgen.cdom.base.CDOMReference;
import pcgen.cdom.base.MasterListInterface;
import pcgen.cdom.enumeration.AssociationKey;
import pcgen.cdom.enumeration.AssociationListKey;
import pcgen.cdom.enumeration.ListKey;
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.cdom.enumeration.SkillCost;
import pcgen.cdom.list.ClassSkillList;
import pcgen.core.Ability;
import pcgen.core.Domain;
import pcgen.core.Equipment;
import pcgen.core.EquipmentModifier;
import pcgen.core.Globals;
import pcgen.core.PCClass;
import pcgen.core.PCTemplate;
import pcgen.core.PObject;
import pcgen.core.PlayerCharacter;
import pcgen.core.Race;
import pcgen.core.Skill;

public final class SkillCostCalc
{

	public static boolean isClassSkill(Skill sk, PCClass aClass,
			PlayerCharacter aPC)
	{
		if ((aPC == null) || (aClass == null))
		{
			return false;
		}

		if (SkillCostCalc.hasCSkill(aPC, aPC.getRace(), sk))
		{
			return true;
		}

		// hasSkill is a LevelAbility skill
		if (aClass.hasSkill(aPC, sk))
		{
			return true;
		}

		// hasCSkill is a class.lst loader skill
		if (SkillCostCalc.hasCSkill(aPC, aClass, sk))
		{
			return true;
		}

		// test for SKILLLIST skill
		if (aClass.hasClassSkill(aPC, sk))
		{
			return true;
		}

		if (aClass.isMonster())
		{
			if (hasMonsterClassSkill(aPC, aPC.getRace(), sk))
			{
				return true;
			}
		}

		for (Domain d : aPC.getDomainSet())
		{
			if (aClass.getKeyName().equals(
					aPC.getDomainSource(d).getPcclass().getKeyName())
					&& SkillCostCalc.hasCSkill(aPC, d, sk))
			{
				return true;
			}
		}

		if ((aPC.getDeity() != null) && SkillCostCalc.hasCSkill(aPC, aPC.getDeity(), sk))
		{
			return true;
		}

		for (Ability aFeat : aPC.getFullAbilitySet())
		{
			if (SkillCostCalc.hasCSkill(aPC, aFeat, sk))
			{
				return true;
			}
		}

		for (Skill aSkill : aPC.getSkillSet())
		{
			if (SkillCostCalc.hasCSkill(aPC, aSkill, sk))
			{
				return true;
			}
		}

		for (Equipment eq : aPC.getEquippedEquipmentSet())
		{
			if (SkillCostCalc.hasCSkill(aPC, eq, sk))
			{
				return true;
			}

			for (EquipmentModifier eqMod : eq.getEqModifierList(true))
			{
				if (SkillCostCalc.hasCSkill(aPC, eqMod, sk))
				{
					return true;
				}
			}

			for (EquipmentModifier eqMod : eq.getEqModifierList(false))
			{
				if (SkillCostCalc.hasCSkill(aPC, eqMod, sk))
				{
					return true;
				}
			}
		}

		for (PCTemplate aTemplate : aPC.getTemplateSet())
		{
			if (SkillCostCalc.hasCSkill(aPC, aTemplate, sk))
			{
				return true;
			}
		}

		List<ClassSkillList> skillLists = ClassSkillApplication
				.getClassSkillList(aPC, aClass);
		MasterListInterface masterLists = Globals.getMasterLists();
		for (CDOMReference<ClassSkillList> ref : masterLists.getActiveLists())
		{
			boolean found = false;
			for (ClassSkillList csl : skillLists)
			{
				if (ref.contains(csl))
				{
					found = true;
					break;
				}
			}
			if (found)
			{
				Collection<AssociatedPrereqObject> assoc = masterLists.getAssociations(ref, sk);
				if (assoc != null && !assoc.isEmpty())
				{
					return true;
				}
			}
		}
		return false;
	}

	public static SkillCost skillCostForPCClass(Skill sk, PCClass aClass,
			PlayerCharacter aPC)
	{
		if (isClassSkill(sk, aClass, aPC))
		{
			return SkillCost.CLASS;
		}
		else if (sk.getSafe(ObjectKey.EXCLUSIVE)
				&& !isCrossClassSkill(sk, aClass, aPC))
		{
			return SkillCost.EXCLUSIVE;
		}
		else
		{
			return SkillCost.CROSS_CLASS;
		}
	}

	private static boolean isCrossClassSkill(Skill sk, PCClass aClass,
			PlayerCharacter aPC)
	{
		if (isClassSkill(sk, aClass, aPC))
		{
			return false;
		}

		if ((aPC == null) || (aClass == null))
		{
			return false;
		}

		if (SkillCostCalc.hasCcSkill(aPC, aPC.getRace(), sk))
		{
			return true;
		}

		for (Domain d : aPC.getDomainSet())
		{
			if (aClass.getKeyName().equals(
					aPC.getDomainSource(d).getPcclass().getKeyName())
					&& SkillCostCalc.hasCcSkill(aPC, d, sk))
			{
				return true;
			}
		}

		if ((aPC.getDeity() != null) && SkillCostCalc.hasCcSkill(aPC, aPC.getDeity(), sk))
		{
			return true;
		}

		if (SkillCostCalc.hasCcSkill(aPC, aClass, sk))
		{
			return true;
		}

		if (aClass.isMonster())
		{
			if (hasMonsterCCSkill(aPC.getRace(), sk))
			{
				return true;
			}
		}

		for (Ability feat : aPC.getFullAbilitySet())
		{
			if (SkillCostCalc.hasCcSkill(aPC, feat, sk))
			{
				return true;
			}
		}

		for (Skill aSkill : aPC.getSkillSet())
		{
			if (SkillCostCalc.hasCcSkill(aPC, aSkill, sk))
			{
				return true;
			}
		}

		for (Equipment eq : aPC.getEquippedEquipmentSet())
		{
			if (SkillCostCalc.hasCcSkill(aPC, eq, sk))
			{
				return true;
			}

			for (EquipmentModifier eqMod : eq.getEqModifierList(true))
			{
				if (SkillCostCalc.hasCcSkill(aPC, eqMod, sk))
				{
					return true;
				}
			}

			for (EquipmentModifier eqMod : eq.getEqModifierList(false))
			{
				if (SkillCostCalc.hasCcSkill(aPC, eqMod, sk))
				{
					return true;
				}
			}
		}

		for (PCTemplate template : aPC.getTemplateSet())
		{
			if (SkillCostCalc.hasCcSkill(aPC, template, sk))
			{
				return true;
			}
		}

		return false;
	}

	public static boolean hasMonsterCCSkill(Race r, Skill s)
	{
		CDOMReference<ClassSkillList> mList = PCClass.MONSTER_SKILL_LIST;
		Collection<CDOMReference<Skill>> mods = r.getListMods(mList);
		if (mods == null)
		{
			return false;
		}
		for (CDOMReference<Skill> ref : mods)
		{
			for (AssociatedPrereqObject apo : r.getListAssociations(mList, ref))
			{
				if (SkillCost.CROSS_CLASS.equals(apo
						.getAssociation(AssociationKey.SKILL_COST)))
				{
					if (ref.contains(s))
					{
						return true;
					}
				}
			}
		}
		return false;
	}

	public static boolean hasMonsterClassSkill(PlayerCharacter pc, Race r, Skill s)
	{
		if (pc.getMonCSkills().contains(s))
		{
			return true;
		}
		CDOMReference<ClassSkillList> mList = PCClass.MONSTER_SKILL_LIST;
		Collection<CDOMReference<Skill>> mods = r.getListMods(mList);
		if (mods == null)
		{
			return false;
		}
		for (CDOMReference<Skill> ref : mods)
		{
			for (AssociatedPrereqObject apo : r.getListAssociations(mList, ref))
			{
				if (SkillCost.CLASS.equals(apo
						.getAssociation(AssociationKey.SKILL_COST)))
				{
					if (ref.contains(s))
					{
						return true;
					}
				}
			}
		}
		return false;
	}

	public static boolean hasCcSkill(PlayerCharacter pc, PObject po, Skill skill)
	{
		List<CDOMReference<Skill>> ccSkillList = po.getListFor(ListKey.CCSKILL);
		List<Skill> assocCCSkill = pc.getAssocList(po,
				AssociationListKey.CCSKILL);
		if (ccSkillList != null && !ccSkillList.isEmpty())
		{
			for (CDOMReference<Skill> ref : ccSkillList)
			{
				if (ref.contains(skill))
				{
					return true;
				}
			}
		}
		if (assocCCSkill != null && !assocCCSkill.isEmpty())
		{
			if (assocCCSkill.contains(skill))
			{
				return true;
			}
		}
		return false;
	}

	public static boolean hasCSkill(PlayerCharacter pc, PObject po, Skill skill)
	{
		List<CDOMReference<Skill>> cSkillList = po.getListFor(ListKey.CSKILL);
		if (cSkillList != null)
		{
			for (CDOMReference<Skill> ref : cSkillList)
			{
				if (ref.contains(skill))
				{
					return true;
				}
			}
		}
		List<Skill> assocCSkill = pc.getAssocList(po, AssociationListKey.CSKILL);
		if (assocCSkill != null)
		{
			if (assocCSkill.contains(skill))
			{
				return true;
			}
		}
		return false;
	}

}
