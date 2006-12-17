/*
 * PreSkillMult.java
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
 *
 * Created on November 28, 2003
 *
 * Current Ver: $Revision$
 * Last Editor: $Author$
 * Last Edited: $Date$
 *
 */package plugin.pretokens.test;

import pcgen.core.PlayerCharacter;
import pcgen.core.Skill;
import pcgen.core.prereq.AbstractPrerequisiteTest;
import pcgen.core.prereq.Prerequisite;
import pcgen.core.prereq.PrerequisiteTest;
import pcgen.util.PropertyFactory;

/**
 * @author frugal@purplewombat.co.uk
 *
 */
public class PreSkillMultTester extends AbstractPrerequisiteTest implements
		PrerequisiteTest
{

	/* (non-Javadoc)
	 * @see pcgen.core.prereq.PrerequisiteTest#passes(pcgen.core.PlayerCharacter)
	 */
	@Override
	public int passes(final Prerequisite prereq, final PlayerCharacter character)
	{
		int runningTotal = 0;
		final int requiredRanks = Integer.parseInt(prereq.getOperand());

		String requiredSkillKey = prereq.getKey().toUpperCase();

		final boolean isType =
				(requiredSkillKey.startsWith("TYPE.") || requiredSkillKey.startsWith("TYPE=")); //$NON-NLS-1$ //$NON-NLS-2$
		if (isType)
		{
			requiredSkillKey = requiredSkillKey.substring(5).toUpperCase();
		}
		final String skillKey = requiredSkillKey.toUpperCase();

		final int percentageSignPosition = skillKey.lastIndexOf('%');

		boolean foundMatch = false;
		for (Skill aSkill : character.getSkillList())
		{
			final String aSkillKey = aSkill.getKeyName().toUpperCase();
			if (isType)
			{
				if (percentageSignPosition >= 0)
				{
					final int maxCount = aSkill.getMyTypeCount();
					for (int k = 0; k < maxCount && !foundMatch; k++)
					{
						if (aSkill.getMyType(k).startsWith(
							skillKey.substring(0, percentageSignPosition)))
						{
							foundMatch = true;
						}
					}
				}
				else if (aSkill.isType(skillKey))
				{
					foundMatch = true;
				}

				if (foundMatch)
				{
					final int result =
							prereq.getOperator().compare(
								aSkill.getTotalRank(character).intValue(),
								requiredRanks);
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
			else if (aSkillKey.equals(skillKey)
				|| ((percentageSignPosition >= 0) && aSkillKey
					.startsWith(skillKey.substring(0, percentageSignPosition))))
			{
				final int result =
						prereq.getOperator().compare(
							aSkill.getTotalRank(character).intValue(),
							requiredRanks);
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

	/* (non-Javadoc)
	 * @see pcgen.core.prereq.PrerequisiteTest#kindsHandled()
	 */
	public String kindHandled()
	{
		return "SKILLMULT"; //$NON-NLS-1$
	}

	/* (non-Javadoc)
	 * @see pcgen.core.prereq.PrerequisiteTest#toHtmlString(pcgen.core.prereq.Prerequisite)
	 */
	public String toHtmlString(final Prerequisite prereq)
	{
		String skillName = prereq.getKey();
		if (prereq.getSubKey() != null && !prereq.getSubKey().equals("")) //$NON-NLS-1$
		{
			skillName += " (" + prereq.getSubKey() + ")"; //$NON-NLS-1$ //$NON-NLS-2$

		}

		final String foo =
				PropertyFactory.getFormattedString("PreSkillMult.toHtml", //$NON-NLS-1$
					new Object[]{prereq.getOperator().toDisplayString(),
						prereq.getOperand(), skillName});
		return foo;
	}

}
