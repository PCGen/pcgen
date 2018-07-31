/*
 * Copyright 2001 (C) Bryan McRoberts <merton_monk@yahoo.com>
 * Copyright 2005 (C) Thomas Clegg <TN_Clegg@lycos.com>
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

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.base.CDOMReference;
import pcgen.cdom.enumeration.ListKey;
import pcgen.core.Globals;
import pcgen.core.PlayerCharacter;
import pcgen.core.Skill;
import pcgen.core.prereq.AbstractPrerequisiteTest;
import pcgen.core.prereq.Prerequisite;
import pcgen.core.prereq.PrerequisiteOperator;
import pcgen.core.prereq.PrerequisiteTest;
import pcgen.system.LanguageBundle;

public class PreCSkillTester extends AbstractPrerequisiteTest implements PrerequisiteTest
{
	@Override
	public int passes(final Prerequisite prereq, final PlayerCharacter character, CDOMObject source)
	{
		final int reqnumber = Integer.parseInt(prereq.getOperand());
		int runningTotal = 0;
		HashMap<Skill, HashSet<Skill>> serveAsSkills = new HashMap<>();
		Set<Skill> imitators = new HashSet<>();
		PreCSkillTester.getImitators(serveAsSkills, imitators);

		// Compute the skill name from the Prerequisite
		String requiredSkillKey = prereq.getKey().toUpperCase();

		if (prereq.getSubKey() != null)
		{
			requiredSkillKey += " (" + prereq.getSubKey().toUpperCase() + ')'; //$NON-NLS-1$ 
		}

		final boolean isType = (
				requiredSkillKey.startsWith("TYPE.") //$NON-NLS-1$
				|| requiredSkillKey.startsWith("TYPE=")); //$NON-NLS-1$
		if (isType)
		{
			requiredSkillKey = requiredSkillKey.substring(5);
		}
		final String skillKey = requiredSkillKey;
		Set<Skill> skillMatches = new HashSet<>();

		if (isType)
		{
			//Skill name is actually type to compare for

			//loop through skill list checking for type and class skill
			for (Skill skill : Globals.getContext().getReferenceContext().getConstructedCDOMObjects(Skill.class))
			{
				if (skill.isType(skillKey) && character.isClassSkill(skill))
				{
					skillMatches.add(skill);
					runningTotal++;
				}

			}
			if (runningTotal < reqnumber)
			{
				BREAKOUT: for (Skill fake : serveAsSkills.keySet())
				{
					if (character.isClassSkill(fake))
					{
						for (Skill mock : serveAsSkills.get(fake))
						{
							if (skillMatches.contains(mock))
							{
								// We already counted this skill in the above 
								// calculation.  We DONT want to match it 
								// a second time
								break BREAKOUT;
							}
							if (mock.isType(skillKey))
							{
								runningTotal++;
								break BREAKOUT;
							}
						}
					}
				}
			}
		}
		else
		{
			Skill skill =
					Globals.getContext().getReferenceContext().silentlyGetConstructedCDOMObject(Skill.class, skillKey);
			if (skill != null && character.isClassSkill(skill))
			{
				runningTotal++;
			}
			else
			{
				for (Skill mock : imitators)
				{
					if (character.isClassSkill(mock) && serveAsSkills.get(mock).contains(skill))
					{
						runningTotal++;
						break;
					}
				}
			}
		}

		runningTotal = prereq.getOperator().compare(runningTotal, reqnumber);
		return countedTotal(prereq, runningTotal);
	}

	/**
	 * @param serveAsSkills
	 * @param imitators
	 */
	private static void getImitators(HashMap<Skill, HashSet<Skill>> serveAsSkills, Set<Skill> imitators)
	{
		for (Skill aSkill : Globals.getContext().getReferenceContext().getConstructedCDOMObjects(Skill.class))
		{
			Set<Skill> servesAs = new HashSet<>();
			for (CDOMReference<Skill> ref : aSkill.getSafeListFor(ListKey.SERVES_AS_SKILL))
			{
				servesAs.addAll(ref.getContainedObjects());
			}

			if (!servesAs.isEmpty())
			{
				imitators.add(aSkill);
				serveAsSkills.put(aSkill, (HashSet<Skill>) servesAs);
			}
		}
	}

	/**
	 * Get the type of prerequisite handled by this token.
	 * @return the type of prerequisite handled by this token.
	 */
	@Override
	public String kindHandled()
	{
		return "CSKILL"; //$NON-NLS-1$
	}

	@Override
	public String toHtmlString(final Prerequisite prereq)
	{
		String skillName = prereq.getKey();
		if (prereq.getSubKey() != null && !prereq.getSubKey().equals("")) //$NON-NLS-1$
		{
			skillName += " (" + prereq.getSubKey() + ')'; //$NON-NLS-1$ 

		}

		String foo = "";
		if (prereq.getOperand().equals("1") && prereq.getOperator().equals(PrerequisiteOperator.GTEQ))
		{
			foo = LanguageBundle.getFormattedString("PreCSkill.single.toHtml", //$NON-NLS-1$
				skillName);
		}
		else
		{
			foo = LanguageBundle.getFormattedString("PreCSkill.toHtml", //$NON-NLS-1$
				prereq.getOperator().toDisplayString(), prereq.getOperand(), skillName);
		}
		return foo;
	}
}
