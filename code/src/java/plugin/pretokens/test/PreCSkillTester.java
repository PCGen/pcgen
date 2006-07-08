/*
 * PreCSkill.java
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
 *
 * Created on November 28, 2003
 *
 */package plugin.pretokens.test;

import pcgen.core.*;
import pcgen.core.prereq.AbstractPrerequisiteTest;
import pcgen.core.prereq.Prerequisite;
import pcgen.core.prereq.PrerequisiteTest;
import pcgen.util.PropertyFactory;

/**
 * @author arknight
 *
 */
public class PreCSkillTester  extends AbstractPrerequisiteTest implements PrerequisiteTest
{
	/* (non-Javadoc)
	 * @see pcgen.core.prereq.PrerequisiteTest#passes(pcgen.core.PlayerCharacter)
	 */
	public int passes(final Prerequisite prereq, final PlayerCharacter character)
	{
		final int reqnumber = Integer.parseInt(prereq.getOperand());
		int runningTotal = 0;

		// Compute the skill name from the Prerequisite
		String requiredSkillKey = prereq.getKey().toUpperCase();

		if (prereq.getSubKey()!=null) {
			requiredSkillKey += " (" + prereq.getSubKey() + ")"; //$NON-NLS-1$ //$NON-NLS-2$
		}

		final boolean isType = (requiredSkillKey.startsWith("TYPE.") || requiredSkillKey.startsWith("TYPE=")); //$NON-NLS-1$ //$NON-NLS-2$
		if (isType)
		{
			requiredSkillKey = requiredSkillKey.substring(5).toUpperCase();
		}
		final String skillKey = requiredSkillKey.toUpperCase();

		if (isType)
		{
			//Skill name is actually type to compare for

			//loop through skill list checking for type and class skill
			for ( Skill skill : Globals.getSkillList() )
			{
				if ( skill.isType(skillKey) && skill.isClassSkill(character) )
				{
					runningTotal++;
				}
			}
		}
		else
		{
			Skill skill = Globals.getSkillKeyed(skillKey);
			if ( skill.isClassSkill( character ) )
			{
				runningTotal++;
			}
		}

		runningTotal = prereq.getOperator().compare(runningTotal, reqnumber);
		return countedTotal(prereq, runningTotal);
	}

	/* (non-Javadoc)
	 * @see pcgen.core.prereq.PrerequisiteTest#kindsHandled()
	 */
	public String kindHandled()
	{
		return "CSKILL"; //$NON-NLS-1$
	}

	/* (non-Javadoc)
	 * @see pcgen.core.prereq.PrerequisiteTest#toHtmlString(pcgen.core.prereq.Prerequisite)
	 */
	public String toHtmlString(final Prerequisite prereq)
	{
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
