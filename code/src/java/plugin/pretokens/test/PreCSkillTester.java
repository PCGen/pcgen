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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

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

		if (isType)
		{
			//Skill name is actually type to compare for

			//get list of skills available
			List skillList = Globals.getSkillList();

			//loop through skill list checking for type and class skill
			for (Iterator i = skillList.iterator(); i.hasNext();)
			{
				Skill aSkill = (Skill) i.next();
				if ( aSkill.isType(skillName) && isClassSkill(aSkill,character) )
				{
					runningTotal++;
				}
			}
		}
		else
		{
			Skill aSkill = Globals.getSkillNamed(skillName);
			if ( isClassSkill(aSkill,character) )
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

	private boolean isClassSkill(Skill aSkill, PlayerCharacter aPC)
	{
		if (aSkill != null)
		{
			//create list of character classes
//			final List cList = (ArrayList) aPC.getClassList().clone();

//			for (Iterator i = cList.iterator(); i.hasNext();)
			for (Iterator i = aPC.getClassList().iterator(); i.hasNext();)
			{
				final PCClass aClass = (PCClass) i.next();
				if (aSkill.isClassSkill(aClass,aPC))
				{
					return true;
				}
			}
		}
		return false;
	}

	public ArrayList getCompiledCSkillList(PlayerCharacter character)
	{
		/*
		 * not used at moment
		 */

		//get the class skills from Classes, Templates, Feats, Skills, Domain and Race

		new ArrayList(); //feats
		new ArrayList(); //skills
		new ArrayList(); //domains
		new ArrayList(); //race

		ArrayList oList = new ArrayList(); //Compiled

		/*
		 * Check templates for CSkills
		 */
		final List tempList = character.getTemplateList();
		if  ( (tempList == null) || tempList.isEmpty() )
		{
			// TODO Do Nothing?
		}
		else
		{
			ArrayList tList = new ArrayList(); //create list for template cskills

			for (Iterator i = tempList.iterator(); i.hasNext();)
			{
				final PCTemplate aTemplate = (PCTemplate) i.next();
				final List tempList2 = aTemplate.getCSkillList();

				if (tempList2 == null)
				{
					// do nothing if no CSKILL: found in template
				}
				else
				{
					tList.addAll(tempList2);
				}
			}
			oList.addAll(tList); //add all the cskills from the templates to compiled
		}

		/*
		 * return compiled list
		 */
		return oList;
	}

}
