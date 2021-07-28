/*
 * Copyright (c) 2010 Tom Parker <thpr@users.sourceforge.net>
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
package plugin.lsttokens.testsupport;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import pcgen.base.util.DoubleKeyMap;
import pcgen.base.util.ListSet;
import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.base.Category;
import pcgen.cdom.content.CNAbility;
import pcgen.cdom.enumeration.CharID;
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.cdom.enumeration.SkillCost;
import pcgen.core.Ability;
import pcgen.core.Domain;
import pcgen.core.Language;
import pcgen.core.PCClass;
import pcgen.core.PCTemplate;
import pcgen.core.PlayerCharacter;
import pcgen.core.Race;
import pcgen.core.Skill;
import pcgen.core.WeaponProf;
import pcgen.core.display.CharacterDisplay;

public class TransparentPlayerCharacter extends PlayerCharacter
{

	public final TransparentCharacterDisplay display;
	public Set<WeaponProf> weaponProfSet = new ListSet<>();
	public Set<CNAbility> abilitySet = new ListSet<>();
	public Set<PCTemplate> templateSet = new ListSet<>();
	public Map<Skill, Integer> skillSet = new HashMap<>();
	public Race race;
	public int spellcastinglevel = -1;
	public Set<Race> qualifiedSet = new ListSet<>();
	public DoubleKeyMap<Skill, PCClass, SkillCost> skillCostMap =
			new DoubleKeyMap<>();
	public Map<PCClass, Integer> classMap = new LinkedHashMap<>();

	public TransparentPlayerCharacter()
	{
		display = new TransparentCharacterDisplay(getCharID());
	}

	public class TransparentCharacterDisplay extends CharacterDisplay
	{
		TransparentCharacterDisplay(CharID id)
		{
			super(id);
		}

		public Set<Domain> domainSet = new ListSet<>();
		public Set<Language> languageSet = new ListSet<>();

		@Override
		public Set<Domain> getDomainSet()
		{
			return domainSet;
		}

		@Override
		public Set<Language> getLanguageSet()
		{
			return languageSet;
		}

		@Override
		public Set<PCClass> getClassSet()
		{
			return classMap.keySet();
		}

		@Override
		public Race getRace()
		{
			return race;
		}
		
//		@Override
//		public SkillCost skillCostForPCClass(Skill sk, PCClass aClass)
//		{
//			SkillCost sc = skillCostMap.get(sk, aClass);
//			if (sc == null)
//			{
//				if (sk.getSafe(ObjectKey.EXCLUSIVE))
//				{
//					return SkillCost.EXCLUSIVE;
//				}
//				return SkillCost.CROSS_CLASS;
//			}
//			return sc;
//		}
//
		@Override
		public Set<Skill> getSkillSet()
		{
			return (skillSet == null) ? new ListSet<>() : skillSet.keySet();
		}

//		@Override
//		public boolean isClassSkill(PCClass aClass, Skill sk)
//		{
//			return SkillCost.CLASS.equals(skillCostMap.get(sk, aClass));
//		}
//
		@Override
		public Set<WeaponProf> getWeaponProfSet()
		{
			return weaponProfSet;
		}

		@Override
		public Set<PCTemplate> getTemplateSet()
		{
			return templateSet;
		}
		
		@Override
		public Float getRank(Skill sk)
		{
			return ((skillSet == null) || (skillSet.get(sk) == null)) ? 0.0f
				: Float.valueOf(skillSet.get(sk));
		}

	}

	@Override
	public Set<PCTemplate> getTemplateSet()
	{
		return templateSet;
	}

	@Override
	public Set<Skill> getSkillSet()
	{
		return (skillSet == null) ? new ListSet<>() : skillSet.keySet();
	}

	@Override
	public boolean isClassSkill(PCClass aClass, Skill sk)
	{
		return SkillCost.CLASS.equals(skillCostMap.get(sk, aClass));
	}

	@Override
	public SkillCost skillCostForPCClass(Skill sk, PCClass aClass)
	{
		SkillCost sc = skillCostMap.get(sk, aClass);
		if (sc == null)
		{
			if (sk.getSafe(ObjectKey.EXCLUSIVE))
			{
				return SkillCost.EXCLUSIVE;
			}
			return SkillCost.CROSS_CLASS;
		}
		return sc;
	}

	@Override
	public Race getRace()
	{
		return race;
	}

	@Override
	public boolean isSpellCaster(int level)
	{
		return (level <= spellcastinglevel);
	}

	@Override
	public Float getRank(Skill sk)
	{
		return ((skillSet == null) || (skillSet.get(sk) == null)) ? 0.0f
			: Float.valueOf(skillSet.get(sk));
	}

	@Override
	public boolean isQualified(CDOMObject po)
	{
		return qualifiedSet.contains(po);
	}

	@Override
	public CharacterDisplay getDisplay()
	{
		return display;
	}

	@Override
	public Set<PCClass> getClassSet()
	{
		return classMap.keySet();
	}

	@Override
	public ArrayList<PCClass> getClassList()
	{
		return new ArrayList<>(classMap.keySet());
	}
	
	@Override
	public Float getMaxRank(Skill sk, PCClass cl)
	{
		return (float) (classMap.get(cl) + 3);
	}

	@Override
	public List<CNAbility> getCNAbilities(Category<Ability> cat)
	{
		return abilitySet.stream()
		                 .filter(cna -> cna.getAbilityCategory().equals(cat))
		                 .collect(Collectors.toList());
	}
}
