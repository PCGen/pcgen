/*
 * Copyright 2001 (C) Bryan McRoberts <merton_monk@yahoo.com>
 * Copyright 2003 (C) Chris Ward <frugal@purplewombat.co.uk>
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.	   See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */package plugin.pretokens.test;

import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.enumeration.Type;
import pcgen.core.PlayerCharacter;
import pcgen.core.Skill;
import pcgen.core.analysis.SkillRankControl;
import pcgen.core.display.CharacterDisplay;
import pcgen.core.prereq.AbstractPrerequisiteTest;
import pcgen.core.prereq.Prerequisite;
import pcgen.core.prereq.PrerequisiteTest;
import pcgen.system.LanguageBundle;

public class PreSkillMultTester extends AbstractPrerequisiteTest implements PrerequisiteTest
{

	@Override
	public int passes(final Prerequisite prereq, final PlayerCharacter character, CDOMObject source)
	{
		CharacterDisplay display = character.getDisplay();
		int runningTotal = 0;
		final int requiredRanks = Integer.parseInt(prereq.getOperand());

		String requiredSkillKey = prereq.getKey().toUpperCase();

		final boolean isType =
				(requiredSkillKey.startsWith("TYPE.") //$NON-NLS-1$
						|| requiredSkillKey.startsWith("TYPE=")); //$NON-NLS-1$
		if (isType)
		{
			requiredSkillKey = requiredSkillKey.substring(5);
		}
		final String skillKey = requiredSkillKey;

		final int percentageSignPosition = skillKey.lastIndexOf('%');

		boolean foundMatch = false;
		for (Skill aSkill : display.getSkillSet())
		{
			final String aSkillKey = aSkill.getKeyName().toUpperCase();
			if (isType)
			{
				if (percentageSignPosition >= 0)
				{
					for (Type type : aSkill.getTrueTypeList(false))
					{
						if (type.toString().toUpperCase().startsWith(skillKey.substring(0, percentageSignPosition)))
						{
							foundMatch = true;
							break;
						}
					}
				}
				else if (aSkill.isType(skillKey))
				{
					foundMatch = true;
				}

				if (foundMatch)
				{
					final int result = prereq.getOperator()
						.compare(SkillRankControl.getTotalRank(character, aSkill).intValue(), requiredRanks);
					if (result == 0)
					{
						foundMatch = false;
					}
					else
					{
						runningTotal = result;
					}
				}
			}
			else if (aSkillKey.equals(skillKey) || ((percentageSignPosition >= 0)
				&& aSkillKey.startsWith(skillKey.substring(0, percentageSignPosition))))
			{
				final int result = prereq.getOperator()
					.compare(SkillRankControl.getTotalRank(character, aSkill).intValue(), requiredRanks);
				if (result > 0)
				{
					foundMatch = true;
					runningTotal = result;
				}
			}
			if (foundMatch)
			{
				break;
			}
		}

		return countedTotal(prereq, runningTotal);
	}

	/**
	 * Get the type of prerequisite handled by this token.
	 * @return the type of prerequisite handled by this token.
	 */
	@Override
	public String kindHandled()
	{
		return "SKILLMULT"; //$NON-NLS-1$
	}

	@Override
	public String toHtmlString(final Prerequisite prereq)
	{
		String skillName = prereq.getKey();
		if (prereq.getSubKey() != null && !prereq.getSubKey().equals("")) //$NON-NLS-1$
		{
			skillName += " (" + prereq.getSubKey() + ')'; //$NON-NLS-1$ 

		}

        return LanguageBundle.getFormattedString("PreSkillMult.toHtml", //$NON-NLS-1$
            prereq.getOperator().toDisplayString(), prereq.getOperand(), skillName);
	}

}
