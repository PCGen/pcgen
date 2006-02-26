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
 * Current Ver: $Revision: 1.10 $
 * Last Editor: $Author: binkley $
 * Last Edited: $Date: 2005/10/18 20:23:37 $
 *
 */package pcgen.core.prereq;

import pcgen.core.PlayerCharacter;
import pcgen.core.Skill;
import pcgen.util.PropertyFactory;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @author frugal@purplewombat.co.uk
 *
 */
public class PreSkillMult  extends AbstractPrerequisiteTest implements PrerequisiteTest {

	/* (non-Javadoc)
	 * @see pcgen.core.prereq.PrerequisiteTest#passes(pcgen.core.PlayerCharacter)
	 */
	public int passes(final Prerequisite prereq, final PlayerCharacter character) {
		int runningTotal=0;
		final int requiredRanks = Integer.parseInt( prereq.getOperand());

		String requiredSkillName = prereq.getKey().toUpperCase();


		final boolean isType = (requiredSkillName.startsWith("TYPE.") || requiredSkillName.startsWith("TYPE=")); //$NON-NLS-1$ //$NON-NLS-2$
		if (isType)
		{
			requiredSkillName = requiredSkillName.substring(5).toUpperCase();
		}
		final String skillName = requiredSkillName.toUpperCase();

		final int percentageSignPosition = skillName.lastIndexOf('%');

		boolean foundMatch = false;
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
					final int result = prereq.getOperator().compare( aSkill.getTotalRank(character).intValue(), requiredRanks );
					if (result==0) {
						foundMatch=false;
					}
					else {
						runningTotal = result;
					}
				}
			}
			else if (aSkillName.equals(skillName) ||
					((percentageSignPosition >= 0) && aSkillName.startsWith(skillName.substring(0, percentageSignPosition))))
			{
				final int result = prereq.getOperator().compare(aSkill.getTotalRank(character).intValue() , requiredRanks);
				if (result>0) {
					foundMatch=true;
					runningTotal = result;
				}
			}
		}

		return countedTotal(prereq, runningTotal);
	}

	/* (non-Javadoc)
	 * @see pcgen.core.prereq.PrerequisiteTest#kindsHandled()
	 */
	public String kindHandled() {
		return "SKILLMULT"; //$NON-NLS-1$
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

		final String foo = PropertyFactory.getFormattedString("PreSkillMult.toHtml", //$NON-NLS-1$
				new Object[] { prereq.getOperator().toDisplayString(),
						prereq.getOperand(),
						skillName } );
		return foo;
	}

}
