/*
 * PreSkill.java
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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import pcgen.core.prereq.PrerequisiteOperator;

/**
 * @author wardc
 *
 */
public class PreSkillTester  extends AbstractPrerequisiteTest implements PrerequisiteTest {

	/* (non-Javadoc)
	 * @see pcgen.core.prereq.PrerequisiteTest#passes(pcgen.core.PlayerCharacter)
	 */
	public int passes(final Prerequisite prereq, final PlayerCharacter character) {
		final int requiredRanks = Integer.parseInt( prereq.getOperand());

		// Compute the skill name from the Prerequisite
		String requiredSkillName = prereq.getKey().toUpperCase();
		if (prereq.getSubKey()!=null) {
			requiredSkillName += " (" + prereq.getSubKey() + ")"; //$NON-NLS-1$ //$NON-NLS-2$
		}

		final boolean isType = (requiredSkillName.startsWith("TYPE.") || requiredSkillName.startsWith("TYPE=")); //$NON-NLS-1$ //$NON-NLS-2$
		if (isType)
		{
			requiredSkillName = requiredSkillName.substring(5).toUpperCase();
		}
		final String skillName = requiredSkillName.toUpperCase();


		// Now locate all instances of this skillname and test them
		final int percentageSignPosition = skillName.lastIndexOf('%');
		int runningTotal = 0;


		boolean foundMatch = false;
		boolean foundSkill = false;
		final List sList = (ArrayList) character.getSkillList().clone();
		for (Iterator e1 = sList.iterator(); e1.hasNext() && !foundMatch ;)
		{
			final Skill aSkill = (Skill) e1.next();

			final String aSkillName = aSkill.getName().toUpperCase();
			if (isType)
			{
				if (percentageSignPosition >= 0)
				{
					final int maxCount = aSkill.getMyTypeCount();
					for (int k=0; k < maxCount && !foundMatch; k++)
					{
						if (aSkill.getMyType(k).startsWith(skillName.substring(0, percentageSignPosition)))
						{
							foundMatch = true;
						}
					}
				}
				else if (aSkill.isType(skillName))
				{
					foundMatch=true;
				}

				if (foundMatch) {
					foundSkill = foundMatch;
					if (prereq.isTotalValues())
					{
						runningTotal += aSkill.getTotalRank(character).intValue();
					}
					else
					{
						if (prereq.getOperator().compare( aSkill.getTotalRank(character).intValue(), requiredRanks ) > 0)
						{
							runningTotal++;
						}
					}
					if (runningTotal==0) {
						foundMatch=false;
					}
				}
			}
			else if (aSkillName.equals(skillName) ||
					((percentageSignPosition >= 0) && aSkillName.startsWith(skillName.substring(0, percentageSignPosition))))
			{
				foundSkill = true;
				if (prereq.isTotalValues())
				{
					runningTotal += aSkill.getTotalRank(character).intValue();
				}
				else
				{
					if (prereq.getOperator().compare( aSkill.getTotalRank(character).intValue(), requiredRanks ) > 0)
					{
						runningTotal++;
					}
				}
				if (runningTotal>0) {
					foundMatch=true;
				}
			}

			if (prereq.isCountMultiples() || prereq.isTotalValues())
			{
				// For counted totals we want to count all occurances, not just the first
				foundMatch=false;
			}
		}

		// If we are looking for a negative test i.e. !PRESKILL and the PC
		// doesn't have the skill we have to return a match
		if (foundSkill == false)
		{
			if (prereq.getOperator() == PrerequisiteOperator.LT)
			{
				runningTotal++;
			}
		}
		return countedTotal(prereq, runningTotal);
	}

	/* (non-Javadoc)
	 * @see pcgen.core.prereq.PrerequisiteTest#kindsHandled()
	 */
	public String kindHandled() {
		return "SKILL"; //$NON-NLS-1$
	}

	/* (non-Javadoc)
	 * @see pcgen.core.prereq.PrerequisiteTest#toHtmlString(pcgen.core.prereq.Prerequisite)
	 */
	public String toHtmlString(final Prerequisite prereq) {
		String skillName = prereq.getKey();
		if (prereq.getSubKey() != null && !prereq.getSubKey().equals(""))  //$NON-NLS-1$
		{
			skillName += " (" + prereq.getSubKey() + ")"; //$NON-NLS-1$ //$NON-NLS-2$

		}

		final String foo = PropertyFactory.getFormattedString("PreSkill.toHtml", //$NON-NLS-1$
				new Object[] { prereq.getOperator().toDisplayString(),
						prereq.getOperand(),
						skillName } );
		return foo;
	}

}
