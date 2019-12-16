/*
 * Copyright 2008 (C) Tom Parker <thpr@users.sourceforge.net>
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
 */
package pcgen.core.analysis;

import java.util.ArrayList;
import java.util.regex.Pattern;

import pcgen.cdom.enumeration.ObjectKey;
import pcgen.cdom.enumeration.Type;
import pcgen.cdom.reference.CDOMSingleRef;
import pcgen.core.Globals;
import pcgen.core.PCStat;
import pcgen.core.PlayerCharacter;
import pcgen.core.SettingsHandler;
import pcgen.core.Skill;

public final class SkillModifier
{

	private SkillModifier()
	{
	}

	public static Integer modifier(Skill sk, PlayerCharacter aPC)
	{
		int bonus = 0;
		if (aPC == null)
		{
			return 0;
		}

		String keyName = sk.getKeyName();
		CDOMSingleRef<PCStat> statref = sk.get(ObjectKey.KEY_STAT);
		if (statref != null)
		{
			PCStat stat = statref.get();
			bonus = aPC.getStatModFor(stat);
			bonus += aPC.getTotalBonusTo("SKILL", "STAT." + stat.getKeyName());
		}
		bonus += aPC.getTotalBonusTo("SKILL", keyName);

		// loop through all current skill types checking for boni
		for (Type singleType : sk.getTrueTypeList(false))
		{
			bonus += aPC.getTotalBonusTo("SKILL", "TYPE." + singleType);
		}

		// now check for any lists of skills, etc
		bonus += aPC.getTotalBonusTo("SKILL", "LIST");

		// now check for ALL
		bonus += aPC.getTotalBonusTo("SKILL", "ALL");

		// these next two if-blocks try to get BONUS:[C]CSKILL|TYPE=xxx|y to
		// function
		if (aPC.isClassSkill(sk))
		{
			bonus += aPC.getTotalBonusTo("CSKILL", keyName);

			// loop through all current skill types checking for boni
			for (Type singleType : sk.getTrueTypeList(false))
			{
				bonus += aPC.getTotalBonusTo("CSKILL", "TYPE." + singleType);
			}

			bonus += aPC.getTotalBonusTo("CSKILL", "LIST");
		}

		if (!aPC.isClassSkill(sk) && !sk.getSafe(ObjectKey.EXCLUSIVE))
		{
			bonus += aPC.getTotalBonusTo("CCSKILL", keyName);

			// loop through all current skill types checking for boni
			for (Type singleType : sk.getTrueTypeList(false))
			{
				bonus += aPC.getTotalBonusTo("CCSKILL", "TYPE." + singleType);
			}

			bonus += aPC.getTotalBonusTo("CCSKILL", "LIST");
		}

		// the above two if-blocks try to get
		// BONUS:[C]CSKILL|TYPE=xxx|y to function
		int aCheckBonus = sk.getSafe(ObjectKey.ARMOR_CHECK).calculateBonus(aPC);
		bonus += aCheckBonus;

		String aString = SettingsHandler.getGame().getRankModFormula();
		if (!aString.isEmpty())
		{
			aString = aString.replaceAll(Pattern.quote("$$RANK$$"), SkillRankControl.getTotalRank(aPC, sk).toString());
			bonus += aPC.getVariableValue(aString, "").intValue();
		}

		return bonus;
	}

	/**
	 * Get the modifier to the skill granted by the key attribute
	 * 
	 * @param pc
	 * @return modifier
	 */
	public static int getStatMod(Skill sk, PlayerCharacter pc)
	{
		CDOMSingleRef<PCStat> stat = sk.get(ObjectKey.KEY_STAT);
		if (stat == null)
		{
			int statMod = 0;
			if (Globals.getGameModeHasPointPool())
			{
				ArrayList<Type> typeList = new ArrayList<>();
				SkillInfoUtilities.getKeyStatList(pc, sk, typeList);
                for (Type type : typeList)
                {
                    statMod += pc.getTotalBonusTo("SKILL", "TYPE." + type);
                }
			}
			return statMod;
		}
		else
		{
			return pc.getStatModFor(stat.get());
		}
	}
}
