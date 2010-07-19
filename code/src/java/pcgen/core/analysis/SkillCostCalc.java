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

import java.util.List;

import pcgen.cdom.enumeration.ObjectKey;
import pcgen.cdom.enumeration.SkillCost;
import pcgen.cdom.list.ClassSkillList;
import pcgen.core.PCClass;
import pcgen.core.PlayerCharacter;
import pcgen.core.Skill;

public final class SkillCostCalc
{

	public static boolean isClassSkill(Skill sk, PCClass aClass,
			PlayerCharacter aPC)
	{
		if (aClass == null)
		{
			return false;
		}

		List<ClassSkillList> classSkillList = ClassSkillApplication
				.getClassSkillList(aPC, aClass);
		return aPC.hasGlobalCost(sk, SkillCost.CLASS)
				|| aPC.hasLocalCost(aClass, sk, SkillCost.CLASS)
				|| aPC.hasLocalCost(classSkillList, sk, SkillCost.CLASS)
				||
				// test for SKILLLIST skill
				aClass.hasClassSkill(aPC, sk)
				|| aPC.hasMasterSkill(classSkillList, sk);
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
		if ((aClass == null) || isClassSkill(sk, aClass, aPC))
		{
			return false;
		}
		List<ClassSkillList> classSkillList = ClassSkillApplication
				.getClassSkillList(aPC, aClass);

		return aPC.hasGlobalCost(sk, SkillCost.CROSS_CLASS)
				|| aPC.hasLocalCost(aClass, sk, SkillCost.CROSS_CLASS)
				|| aPC.hasLocalCost(classSkillList, sk, SkillCost.CROSS_CLASS);
	}

}
